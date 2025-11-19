package com.rainydaysengine.rainydays.application.service.user;

import com.rainydaysengine.rainydays.application.port.user.IUserPort;
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

@RequiredArgsConstructor
@Service
public class User implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    private final IUserPort iUserPort;
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

    public String verify(UserLoginRequest loginRequest) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getIdentifier(),
                        loginRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loginRequest.getIdentifier());
        }
        return "";
        // Spring will automatically returns
        // "code": 500, "message": "Bad credentials",
    }

//    @Override
//    public UserLoginResponse userLogin(String identifier, String password) {
//        CallResult<Session> session = CallWrapper.syncCall(() -> this.iUserPort.userLogin(identifier, password));
//
//        if (session.isFailure()) {
//            logger.error("User#userLogin(): frontendApi.createNativeLoginFlow() failed", session.getError());
//            throw ApplicationError.Unauthorized(null);
//        }
//
//        Map<String, Object> traits = session.getResult().traits();
//        String token = session.getResult().token();
//        OffsetDateTime expiry = session.getResult().expiresAt();
//
//        UserLoginResponse userLoginResponse = new UserLoginResponse(
//                traits,
//                token,
//                expiry
//        );
//
//        return userLoginResponse;
//    }

//    @Override
//    public Session whoAmI(String token) {
//        CallResult<Session> sessionFromToken = CallWrapper.syncCall(() -> this.iUserPort.getSessionFromToken(token));
//
//        if (sessionFromToken.isFailure()) {
//            logger.error("User#whoAmI(): iUserPort.getSessionFromToken() failed", sessionFromToken.getError());
//            throw ApplicationError.Unauthorized(null);
//        }
//
//        Session session = new Session(
//                sessionFromToken.getResult().id(),
//                sessionFromToken.getResult().token(),
//                sessionFromToken.getResult().expiresAt(),
//                sessionFromToken.getResult().devices(),
//                sessionFromToken.getResult().identity(),
//                sessionFromToken.getResult().traits()
//        );
//
//        return session;
//    }

//    @Override
//    public void resetPassword(String identity, String password) {
//        Optional<String> user = userRepository.findByEmailAddress(identity);
//
//        if (!user.isPresent()) {
//            logger.info("User#resetPassword(): userRepository.findByEmail() no user found", identity);
//        }
//
//        String iamId;
//
//        if (user.isPresent()) {
//            iamId = user.get();
//
//            this.iUserPort.resetPassword(iamId, password);
//            logger.info("User#resetPassword(): iUserPort.resetPassword() Reset password Success", iamId);
//        }
//    }

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
