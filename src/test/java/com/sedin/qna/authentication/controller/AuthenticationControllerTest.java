package com.sedin.qna.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.authentication.model.AuthenticationDto;
import com.sedin.qna.authentication.service.AuthenticationService;
import com.sedin.qna.common.exception.NotFoundException;
import com.sedin.qna.common.exception.PasswordIncorrectException;
import com.sedin.qna.util.ApiDocumentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static com.sedin.qna.common.response.ApiResponseCode.NOT_FOUND;
import static com.sedin.qna.common.response.ApiResponseCode.UNAUTHORIZED;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@DisplayName("AuthenticationController WebMVCTest")
class AuthenticationControllerTest {

    private final String EMAIL = "cafe@mocha.com";
    private final String PASSWORD = "password";
    private final String INCORRECT_PASSWORD = "incorrect";
    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9." +
            "LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";

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
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        response = AuthenticationDto.Response.builder()
                .accessToken(ACCESS_TOKEN)
                .build();
    }

    @Test
    @DisplayName("로그인 요청 - Success")
    void When_Login_Account_Expect_OK() throws Exception {
        // given
        login = AccountDto.Login.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        given(authenticationService.authenticate(login)).willReturn(response);

        // when
        String requestBody = objectMapper.writeValueAsString(login);

        ResultActions result = this.mockMvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                .andDo(document("login-account",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("Access Token")
                        )
                ));

        verify(authenticationService).authenticate(login);
    }

    @Test
    @DisplayName("로그인 요청 - Fail (패스워드 불일치)")
    void When_Login_Account_With_Incorrect_Password_Expect_Unauthorized() throws Exception {
        // given
        login = AccountDto.Login.builder()
                .email(EMAIL)
                .password(INCORRECT_PASSWORD)
                .build();

        given(authenticationService.authenticate(login))
                .willThrow(new PasswordIncorrectException());

        // when
        String requestBody = objectMapper.writeValueAsString(login);

        ResultActions result = this.mockMvc.perform(
                post("/api/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(UNAUTHORIZED.getId()));
    }

    @Test
    @DisplayName("로그인 요청 - Fail (미등록 이메일)")
    void When_Login_Account_With_Unregistered_Email_Expect_BadRequest() throws Exception {
        // given
        login = AccountDto.Login.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        given(authenticationService.authenticate(login)).willThrow(new NotFoundException(EMAIL));

        // when
        String requestBody = objectMapper.writeValueAsString(login);

        ResultActions result = this.mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.getId()));
    }

}
