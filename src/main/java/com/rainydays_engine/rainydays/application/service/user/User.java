package com.rainydays_engine.rainydays.application.service.user;

import com.rainydays_engine.rainydays.application.port.user.IUserPort;
import com.rainydays_engine.rainydays.application.port.user.IUserService;
import com.rainydays_engine.rainydays.domain.repository.UserRepository;
import com.rainydays_engine.rainydays.errors.ApplicationError;
import com.rainydays_engine.rainydays.infra.kratos.Kratos;
import com.rainydays_engine.rainydays.infra.postgres.entity.Users;
import com.rainydays_engine.rainydays.utils.CallResult;
import com.rainydays_engine.rainydays.utils.CallWrapper;
import com.rainydays_engine.rainydays.application.port.auth.Session;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class User implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(Kratos.class);

    private IUserPort iUserPort;
    private Validator validator;
    private final UserRepository userRepository;

    public User(IUserPort iUserPort, Validator validator, UserRepository userRepository) {
        this.iUserPort = iUserPort;
        this.validator = validator;
        this.userRepository = userRepository;
    }

    @Override
    public CompletableFuture<UserRegisterResponse> userRegister(UserRequestDto userRequestDto) {
        // Validator
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        if (!violations.isEmpty()) {
            logger.error("User#violations: userRequestDto has violation. {}",
                    violations);
            throw new ConstraintViolationException(violations);
        }

        CompletableFuture<String> futureIamId = this.iUserPort.userRegister(userRequestDto);

        futureIamId
                .thenApply(iamId -> {
                    Users user = new Users();
                    user.setIamId(iamId);
                    return userRepository.save(user);
                })
                .thenAccept(savedUser -> logger.info("User#userRegister(): User saved successfully. ID: {}",
                        savedUser.getId()));

        return futureIamId.thenApply(iamId -> new UserRegisterResponse(
                iamId,
                userRequestDto.getEmail(),
                userRequestDto.getUsername(),
                userRequestDto.getFirst_name(),
                Optional.ofNullable(userRequestDto.getMiddle_name()),
                userRequestDto.getLast_name()));
    }

    @Override
    public UserLoginResponse userLogin(String identifier, String password) {
        CallResult<Session> session = CallWrapper.syncCall(() -> this.iUserPort.userLogin(identifier, password));

        if(session.isFailure()) {
            logger.error("User#userLogin(): frontendApi.createNativeLoginFlow() failed", session.getError());
            throw ApplicationError.Unauthorized(null);
        }
        
        Map<String, Object> traits =  session.getResult().traits();
        String token = session.getResult().token();
        OffsetDateTime expiry = session.getResult().expiresAt();

        UserLoginResponse userLoginResponse = new UserLoginResponse(
                traits,
                token,
                expiry
        );

        return userLoginResponse;
    }
}
