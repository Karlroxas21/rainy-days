package com.rainydaysengine.rainydays.domain.service.user;

import com.rainydaysengine.rainydays.domain.port.auth.Session;
import com.rainydaysengine.rainydays.domain.port.user.IUserPort;
import com.rainydaysengine.rainydays.infra.postgres.repository.UserRepository;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    // UserService_UserRegister_ReturnsUserRegisterResponse needs this
    @Mock
    private Validator validator;

    @Mock
    private IUserPort iUserPort;

    @InjectMocks
    private User userService;

    @Test
    public void UserService_UserRegister_ReturnsUserRegisterResponse() {
        UserRequestDto userRequestDto = UserRequestDto
                .builder()
                .email("email@email.com")
                .username("ycath")
                .first_name("Ney")
                .last_name("Potinez")
                .password("Password@1")
                .build();

        when(iUserPort.userRegister(Mockito.any(UserRequestDto.class)))
                .thenReturn(CompletableFuture.completedFuture(UUID.randomUUID().toString()));

        CompletableFuture<UserRegisterResponse> userRegistered = userService.userRegister(userRequestDto);

        Assertions.assertThat(userRegistered).isNotNull();
        Assertions.assertThat(userRegistered).isCompleted();
    }

    @Test
    public void UserService_UserLogin_ReturnsUserLoginResponse() {
        String identifier = "test@email.com";
        String password = "Password@1";

        String sampleToken = "ory_st_VhQBZogVRiaVG0SAO4bjnt4WQi4CL0p7";
        OffsetDateTime sampleExpiry = OffsetDateTime.parse("2025-09-13T10:32:48.935724071Z");

        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("first", "Karl");
        nameMap.put("last", "Roxas");

        Map<String, Object> traits = new HashMap<>();
        traits.put("name", nameMap);
        traits.put("email", "karl@email.com");
        traits.put("username", "kroxas");

        String id = UUID.randomUUID().toString();
        Session session = new Session(
                id,
                sampleToken,
                sampleExpiry,
                null,
                null,
                traits
        );

        when(iUserPort.userLogin(identifier, password))
                .thenReturn(session);

        UserLoginResponse userLogin = userService.userLogin(identifier, password);

        Assertions.assertThat(userLogin).isNotNull();
        Assertions.assertThat(userLogin).isInstanceOf(UserLoginResponse.class);
    }

    @Test
    public void UserService_WhoAmI_ReturnsSession() {
        String identifier = "test@email.com";
        String password = "Password@1";

        String sampleToken = "ory_st_VhQBZogVRiaVG0SAO4bjnt4WQi4CL0p7";
        OffsetDateTime sampleExpiry = OffsetDateTime.parse("2025-09-13T10:32:48.935724071Z");

        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("first", "Karl");
        nameMap.put("last", "Roxas");

        Map<String, Object> traits = new HashMap<>();
        traits.put("name", nameMap);
        traits.put("email", "karl@email.com");
        traits.put("username", "kroxas");

        String id = UUID.randomUUID().toString();
        Session session = new Session(
                id,
                sampleToken,
                sampleExpiry,
                null,
                null,
                traits
        );

        when(iUserPort.getSessionFromToken(sampleToken))
                .thenReturn(session);

        Session whoAmI = userService.whoAmI(sampleToken);

        Assertions.assertThat(whoAmI).isNotNull();
        Assertions.assertThat(whoAmI).isInstanceOf(Session.class);

    }

    @Test
    public void UserService_ResetPassword_ReturnsVoid() {
        String mockIamId = UUID.randomUUID().toString();
        String identifier = "test@email.com";
        String newPassword = "Password@3";

        when(userRepository.findByEmailAddress(identifier))
                .thenReturn(Optional.ofNullable(mockIamId));

        assertAll(() -> userService.resetPassword(identifier,newPassword));
    }
}

