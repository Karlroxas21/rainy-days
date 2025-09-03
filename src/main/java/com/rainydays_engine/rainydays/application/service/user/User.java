package com.rainydays_engine.rainydays.application.service.user;

import com.rainydays_engine.rainydays.application.port.user.IUserPort;
import com.rainydays_engine.rainydays.application.port.user.IUserService;
import com.rainydays_engine.rainydays.domain.repository.UserRepository;
import com.rainydays_engine.rainydays.infra.kratos.Kratos;
import com.rainydays_engine.rainydays.infra.postgres.entity.Users;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public CompletableFuture<UserResponse> userRegister(UserRequestDto userRequestDto) {
        // Validator
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        if (!violations.isEmpty()) {
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

        return futureIamId.thenApply(iamId -> new UserResponse(
                iamId,
                userRequestDto.getEmail(),
                userRequestDto.getUsername(),
                userRequestDto.getFirst_name(),
                Optional.ofNullable(userRequestDto.getMiddle_name()),
                userRequestDto.getLast_name()));
    }
}
