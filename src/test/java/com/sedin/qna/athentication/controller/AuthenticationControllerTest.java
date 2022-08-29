package com.sedin.qna.athentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.athentication.model.AuthenticationDto;
import com.sedin.qna.athentication.service.AuthenticationService;
import com.sedin.qna.exception.NotFoundException;
import com.sedin.qna.exception.PasswordIncorrectException;
import com.sedin.qna.network.ApiResponseCode;
import com.sedin.qna.util.ApiDocumentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class AuthenticationControllerTest {

    private final String UNREGISTERED_LOGIN_ID = "not register login id";
    private final String LOGIN_ID = "loginId";
    private final String PASSWORD = "password";
    private final String INCORRECT_PASSWORD = "incorrect";
    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9.LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private AccountDto.Login login;

    private AuthenticationDto.Response response;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        response = AuthenticationDto.Response.builder()
                .accessToken(ACCESS_TOKEN)
                .build();
    }

    @Test
    void When_Login_Account_Expect_OK() throws Exception {
        // given
        login = AccountDto.Login.builder()
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .build();

        given(authenticationService.checkValidAuthentication(any(AccountDto.Login.class))).willReturn(response);

        // when
        String requestBody = objectMapper.writeValueAsString(login);

        ResultActions result = this.mockMvc.perform(post("/api/auth/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                .andDo(document("login-account",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestFields(
                                fieldWithPath("loginId").type(JsonFieldType.STRING).description("아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("Access Token")
                        )
                ));

        verify(authenticationService, times(1))
                .checkValidAuthentication(any(AccountDto.Login.class));
    }

    @Test
    void When_Login_Account_With_Incorrect_Password_Expect_Unauthorized() throws Exception {

        // given
        login = AccountDto.Login.builder()
                .loginId(LOGIN_ID)
                .password(INCORRECT_PASSWORD)
                .build();

        given(authenticationService.checkValidAuthentication(any(AccountDto.Login.class)))
                .willThrow(new PasswordIncorrectException("password"));

        // when
        String requestBody = objectMapper.writeValueAsString(login);

        ResultActions result = this.mockMvc.perform(
                post("/api/auth/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(ApiResponseCode.UNAUTHORIZED.toString()));
    }

    @Test
    void When_Login_Account_With_Unregistered_LoginId_Expect_BadRequest() throws Exception {

        // given
        login = AccountDto.Login.builder()
                .loginId(UNREGISTERED_LOGIN_ID)
                .password(PASSWORD)
                .build();

        given(authenticationService.checkValidAuthentication(any(AccountDto.Login.class)))
                .willThrow(new NotFoundException("loginId"));
        // when
        String requestBody = objectMapper.writeValueAsString(login);

        ResultActions result = this.mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(ApiResponseCode.NOT_FOUND.toString()));
    }
}
