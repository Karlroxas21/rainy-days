package com.rainydaysengine.rainydays.application.service.user;

import com.rainydaysengine.rainydays.application.port.user.IUserService;
import com.rainydaysengine.rainydays.application.service.jwt.Jwt;
import com.rainydaysengine.rainydays.errors.ApplicationError;
import com.rainydaysengine.rainydays.infra.postgres.entity.UsersEntity;
import com.rainydaysengine.rainydays.infra.postgres.repository.UserRepository;
import com.rainydaysengine.rainydays.utils.CallResult;
import com.rainydaysengine.rainydays.utils.CallWrapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class User implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    private final Validator validator;
    private final UserRepository userRepository;
    private final Jwt jwtService;

    @Autowired
    AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public UserRegisterResponse userRegister(UserRequestDto userRequestDto) {
        // Validator
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        if (!violations.isEmpty()) {
            logger.error("User#violations: userRequestDto has violation. {}",
                    violations);
            throw new ConstraintViolationException(violations);
        }

        String password = encoder.encode(userRequestDto.getPassword());

        UsersEntity usersEntity = getUsersEntity(userRequestDto, password);

        CallResult<UsersEntity> saveUser = CallWrapper.syncCall(() -> userRepository.save(usersEntity));
        if (saveUser.isFailure()) {
            logger.error("User#userRegister(): Error saving user. User: {}", usersEntity.getEmailAddress());
            throw ApplicationError.InternalError(usersEntity.getEmailAddress());
        }
        logger.info("User#userRegister(): User saved successfully. ID: {}", usersEntity.getId());

        return new UserRegisterResponse(
                saveUser.getResult().getEmailAddress(),
                saveUser.getResult().getUsername(),
                saveUser.getResult().getFirstName(),
                Optional.ofNullable(saveUser.getResult().getMiddleName()),
                saveUser.getResult().getLastName()
        );

    }

    @Override
    public String verify(UserLoginRequest loginRequest) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getIdentifier(),
                        loginRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loginRequest.getIdentifier());
        }
        // Spring will automatically returns
        // "code": 500, "message": "Bad credentials",
        return "";
    }

    @Override
    public void resetPassword(String identity, String password) {
        Optional<String> userId = userRepository.findByEmailAddressString(identity);

        if (!userId.isPresent()) {
            logger.info("User#resetPassword(): userRepository.findByEmail() no user found", identity);
        }

        if (userId.isPresent()) {
            String newPassword = encoder.encode(password);
            this.userRepository.updatePassword(UUID.fromString(userId.get()), newPassword);
            logger.info("User#resetPassword(): iUserPort.resetPassword() Reset password Success {}", userId.get());
        }
    }

    @Override
    public UserWhoAmIResponse whoAmI(String sessionToken) {
        String token = sessionToken.substring(7);
        String identity = jwtService.extractUsername(token);

        CallResult<UsersEntity> userEntity = CallWrapper.syncCall(() -> userRepository.findByEmailAddress(identity));
        if (userEntity.isFailure()) {
            logger.error("User#whoAmI(): Error getting data.", userEntity.getError());
            throw ApplicationError.InternalError(userEntity.getError());
        }

        return new UserWhoAmIResponse(
                userEntity.getResult().getId(),
                userEntity.getResult().getEmailAddress(),
                userEntity.getResult().getFirstName(),
                Optional.ofNullable(userEntity.getResult().getMiddleName()),
                userEntity.getResult().getLastName(),
                userEntity.getResult().getProfileUrl()
        );
    }

    @NotNull
    private static UsersEntity getUsersEntity(UserRequestDto userRequestDto, String password) {
        UsersEntity usersEntity = new UsersEntity();

        usersEntity.setPassword(password);
        usersEntity.setEmailAddress(userRequestDto.getEmailAddress());
        usersEntity.setUsername(userRequestDto.getUsername());
        usersEntity.setFirstName(userRequestDto.getFirstName());
        usersEntity.setMiddleName(userRequestDto.getMiddleName());
        usersEntity.setLastName(userRequestDto.getLastName());
        usersEntity.setSuffix(userRequestDto.getSuffix());
        usersEntity.setProfileUrl(userRequestDto.getProfileUrl());

        return usersEntity;
    }
}
