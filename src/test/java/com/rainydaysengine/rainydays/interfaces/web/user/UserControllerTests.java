package com.rainydaysengine.rainydays.interfaces.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainydaysengine.rainydays.application.port.auth.Session;
import com.rainydaysengine.rainydays.application.port.user.IUserPort;
import com.rainydaysengine.rainydays.application.service.entry.DepositEntryDto;
import com.rainydaysengine.rainydays.application.service.entry.Entry;
import com.rainydaysengine.rainydays.application.service.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private User userService;

    @Autowired
    private Entry entry;

    @Mock
    private IUserPort iUserPort;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegisterResponse userRegisterResponse;

    private UserRequestDto userRequestDto;

    private UserLoginRequest userLoginRequest;

    private UserLoginResponse userLoginResponse;

    private UserWhoAmIRequest userWhoAmIRequest;

    private UserResetPasswordRequest userResetPasswordRequest;

    private DepositEntryDto depositEntryDto;

    private MockMultipartFile mockMultipartFile;

    private Session session;

    @BeforeEach
    public void init() {
        userRequestDto = UserRequestDto
                .builder()
                .email("email@email.com")
                .username("ycath")
                .first_name("Ney")
                .last_name("John")
                .password("Password@1")
                .build();

        userRegisterResponse = UserRegisterResponse
                .builder()
                .iamId(UUID.randomUUID().toString())
                .email("email@email.com")
                .username("ycath")
                .first_name("Ney")
                .last_name("John")
                .build();

        userLoginRequest = UserLoginRequest
                .builder()
                .identifier("email@email.com")
                .password("Password@1")
                .build();

        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("first", "Karl");
        nameMap.put("last", "Roxas");

        Map<String, Object> traits = new HashMap<>();
        traits.put("name", nameMap);
        traits.put("email", "karl@email.com");
        traits.put("username", "kroxas");

        String sampleToken = "ory_st_VhQBZogVRiaVG0SAO4bjnt4WQi4CL0p7";
        OffsetDateTime sampleExpiry = OffsetDateTime.parse("2025-09-13T10:32:48.935724071Z");

        userLoginResponse = UserLoginResponse
                .builder()
                .traits(traits)
                .token(sampleToken)
                .expiry(sampleExpiry)
                .build();

        userWhoAmIRequest = UserWhoAmIRequest
                .builder()
                .session_token(sampleToken)
                .build();

        userResetPasswordRequest = UserResetPasswordRequest
                .builder()
                .identity("email@email.com")
                .password("Password@2")
                .build();

        mockMultipartFile = new MockMultipartFile(
                "photo",
                "test-image.jpg",
                "image/jpeg",
                "dummy-image-content".getBytes()
        );
        depositEntryDto = DepositEntryDto
                .builder()
                .userId(UUID.randomUUID())
                .amount(10000)
                .note("Test")
                .photo(mockMultipartFile)
                .groupId(UUID.randomUUID())
                .build();
    }

    @Test
    public void UserController_Register_ReturnsCreated() throws Exception{

        String mockUuid = UUID.randomUUID().toString();

        when(userService.userRegister(Mockito.any(UserRequestDto.class)))
                .thenReturn(CompletableFuture.completedFuture(userRegisterResponse));

        MvcResult mvcResult = mockMvc.perform(post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated());
    }

    @Test
    public void UserController_Login_ReturnsOk() throws Exception{
        String mockEmail = "email@email.com";
        String mockPassword = "Pssword@1";
        when(userService.userLogin(
                "email@email.com",
                "Password@1"))
                .thenReturn(userLoginResponse);

        ResultActions response = mockMvc.perform(post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequest)));

        response.andExpect(status().isOk());
    }

    @Test
    public void UserController_WhoAmI_ReturnsOk() throws Exception{
        String sampleToken = "ory_st_VhQBZogVRiaVG0SAO4bjnt4WQi4CL0p7";
        OffsetDateTime sampleExpiry = OffsetDateTime.parse("2025-09-13T10:32:48.935724071Z");

        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("first", "Karl");
        nameMap.put("last", "Roxas");

        Map<String, Object> traits = new HashMap<>();
        traits.put("name", nameMap);
        traits.put("email", "email@email.com");
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

        when(userService.whoAmI(
                userWhoAmIRequest.getSession_token()))
                .thenReturn(session);

        ResultActions response = mockMvc.perform(post("/v1/user/whoami")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWhoAmIRequest)));

        response.andExpect(status().isOk());
    }

    @Test
    public void UserController_ResetPassword_ReturnsVoid() throws Exception {
        Mockito.doNothing().when(userService).resetPassword(
                "email@email.com",
                "Password@1");
        mockMvc.perform(post("/v1/user/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResetPasswordRequest)))
                .andExpect(status().isNoContent());

    }

    @Test
    public void UserController_AddEntry_ReturnsString() throws Exception {
        when(this.entry.addEntry(Mockito.any(DepositEntryDto.class)))
                .thenReturn(String.valueOf(UUID.randomUUID()));

        ResultActions response = mockMvc.perform(multipart("/v1/user/add-entry")
                .file(this.mockMultipartFile) // photo
                // Use .param() for simple string/primitive fields
                .param("userId", depositEntryDto.getUserId().toString())
                .param("amount", String.valueOf(depositEntryDto.getAmount()))
                .param("groupId", String.valueOf(depositEntryDto.getGroupId()))
                .param("note", depositEntryDto.getNote())
               );

        response.andExpect(status().isOk());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        User userService() {
            return Mockito.mock(User.class);
        }

        @Bean
        Entry entryService() {
            return Mockito.mock(Entry.class);
        }
    }
}
