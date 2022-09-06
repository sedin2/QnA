package com.sedin.qna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.Account;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.model.Gender;
import com.sedin.qna.account.service.AccountService;
import com.sedin.qna.exception.DuplicatedException;
import com.sedin.qna.exception.InvalidTokenException;
import com.sedin.qna.exception.NotFoundException;
import com.sedin.qna.exception.PermissionToAccessException;
import com.sedin.qna.interceptor.AuthenticationInterceptor;
import com.sedin.qna.util.ApiDocumentUtil;
import com.sedin.qna.util.DocumentFormatGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class AccountControllerWebTest {

    private static final Long AUTHORIZED_ID = 1L;
    private static final Long THE_OTHER_ID = 2L;
    private static final Long NOT_EXISTED_ID = 9999L;
    private static final String PREFIX = "prefix";
    private static final String LOGIN_ID = "sedin";
    private static final String EXISTED_LOGIN_ID = "sedin";
    private static final String PASSWORD = "12341234";
    private static final String NAME = "LeeSeJin";
    private static final LocalDate BORN_DATE = LocalDate.of(1994, 8, 30);
    private static final String EMAIL = "sejin@email.com";
    private static final String AUTHORIZATION = "Authorization";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjF9." +
            "LwF0Ms-3xGGJX9JBIrc7bzGl1gYUAq3R3gesg35BA1w";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor;

    @MockBean
    private AuthenticationInterceptor interceptor;

    private Account authenticatedAccount;
    private AccountDto.Create createDto;
    private AccountDto.Create registeredDto;
    private AccountDto.Create createDtoWithEmptyArgument;
    private AccountDto.Update updateDto;
    private AccountDto.Update updateDtoWithEmptyArgument;
    private AccountDto.Response response;

    static class InvalidAccessToken implements ArgumentsProvider {

        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("", "비어 있고 공백을 포함 하지 않을 때"),
                    Arguments.of("  ", "비어 있고 공백을 포함 할 때"),
                    Arguments.of("invalid", "Jwt 형식이 아닐 때"),
                    Arguments.of(PREFIX + VALID_TOKEN, "Jwt 형식 이지만 유효하지 않을 때")
            );
        }
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        authenticatedAccount = Account.builder()
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .name(NAME)
                .email(EMAIL)
                .gender(Gender.MALE)
                .bornDate(BORN_DATE)
                .build();

        when(interceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Nested
    @DisplayName("POST /api/accounts 는")
    class DescribeSignUpAccount {

        @Nested
        @DisplayName("사용자 등록 요청이 들어오면")
        class ContextWithAccountCreateDto {

            @BeforeEach
            void prepareCreate() {
                createDto = AccountDto.Create.builder()
                        .loginId(LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(BORN_DATE)
                        .gender(Gender.MALE)
                        .email(EMAIL)
                        .build();

                response = AccountDto.Response.builder()
                        .id(AUTHORIZED_ID)
                        .loginId(LOGIN_ID)
                        .name(NAME)
                        .bornDate(BORN_DATE)
                        .gender(Gender.MALE)
                        .email(EMAIL)
                        .build();

                given(accountService.signUp(any(AccountDto.Create.class)))
                        .willReturn(response);
            }

            @Test
            @DisplayName("HttpStatus 201 Created를 응답한다")
            void it_returns_httpStatus_created() throws Exception {
                String requestBody = objectMapper.writeValueAsString(createDto);

                ResultActions result = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON));

                // Post Account RestDocs
                result.andExpect(status().isCreated())
                        .andExpect(content().string(containsString(LOGIN_ID)))
                        .andDo(document("create-account",
                                ApiDocumentUtil.getDocumentRequest(),
                                ApiDocumentUtil.getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("loginId").type(JsonFieldType.STRING).description("아이디"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("bornDate").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("생년월일"),
                                        fieldWithPath("gender").type(JsonFieldType.STRING).description("성별"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                ),
                                responseFields(
                                        beneathPath("data").withSubsectionId("data"),
                                        fieldWithPath("account.id").type(JsonFieldType.NUMBER).description("아이디"),
                                        fieldWithPath("account.loginId").type(JsonFieldType.STRING).description("로그인 아이디"),
                                        fieldWithPath("account.name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("account.bornDate").type(JsonFieldType.STRING)
                                                .attributes(DocumentFormatGenerator.getDateFormat())
                                                .description("생년월일"),
                                        fieldWithPath("account.gender").type(JsonFieldType.STRING).description("성별"),
                                        fieldWithPath("account.email").type(JsonFieldType.STRING).description("이메일")
                                )));

                verify(accountService, times(1)).signUp(any(AccountDto.Create.class));
            }
        }

        @Nested
        @DisplayName("이미 등록된 정보로 요청이 들어오면")
        class ContextWithAlreadyRegisteredAccountSignUpDto {

            @BeforeEach
            void prepareRegisteredDto() {
                registeredDto = AccountDto.Create.builder()
                        .loginId(EXISTED_LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(BORN_DATE)
                        .gender(Gender.MALE)
                        .email(EMAIL)
                        .build();

                given(accountService.signUp(any(AccountDto.Create.class)))
                        .willThrow(new DuplicatedException(EXISTED_LOGIN_ID));
            }

            @Test
            @DisplayName("HttpStatus 400 BadRequest를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                String requestBody = objectMapper.writeValueAsString(registeredDto);

                mockMvc.perform(post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, times(1)).signUp(any(AccountDto.Create.class));
            }
        }

        @Nested
        @DisplayName("비어있는 인자값으로 요청이 들어오면")
        class ContextWithEmptyArgumentInAccountCreateDto {

            @BeforeEach
            void prepareAccountSignUpDtoWithEmptyArgument() {
                createDtoWithEmptyArgument = AccountDto.Create.builder()
                        .loginId("")
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(BORN_DATE)
                        .gender(Gender.MALE)
                        .email(EMAIL)
                        .build();
            }

            @Test
            @DisplayName("HttpStatus 400 BadRequest를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                String requestBody = objectMapper.writeValueAsString(createDtoWithEmptyArgument);

                mockMvc.perform(post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, never()).signUp(any(AccountDto.Create.class));
            }
        }
    }

    @Nested
    @DisplayName("Access Token이 유효 할 경우")
    class ContextWithValidAccessToken {

        @BeforeEach
        void prepareValidAccessToken() throws Exception {
            when(interceptor.preHandle(any(), any(), any()))
                    .then(invocation -> {
                        HttpServletRequest source = invocation.getArgument(0);
                        source.setAttribute("account", authenticatedAccount);
                        return true;
                    });
        }

        @Nested
        @DisplayName("PATCH /api/accounts/{id} 는")
        class DescribeUpdateAccount {

            @Nested
            @DisplayName("인가된 accountId와 정보로 요청이 들어오면")
            class ContextWithAuthorizedAccountIdAndAccountUpdateDto {

                @BeforeEach
                void prepareExistedAccountIdAndAccountUpdateDto() {
                    updateDto = AccountDto.Update.builder()
                            .originalPassword(PASSWORD)
                            .newPassword(PREFIX + PASSWORD)
                            .email(PREFIX + EMAIL)
                            .build();

                    response = AccountDto.Response.builder()
                            .id(AUTHORIZED_ID)
                            .loginId(LOGIN_ID)
                            .name(NAME)
                            .bornDate(BORN_DATE)
                            .gender(Gender.MALE)
                            .email(PREFIX + EMAIL)
                            .build();

                    given(accountService.update(eq(authenticatedAccount), eq(AUTHORIZED_ID), any(AccountDto.Update.class)))
                            .willReturn(response);
                }

                @Test
                @DisplayName("HttpStatus 200 OK를 응답한다")
                void it_returns_httpStatus_OK() throws Exception {
                    String requestBody = objectMapper.writeValueAsString(updateDto);

                    ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/accounts/{id}", AUTHORIZED_ID)
                                    .header(AUTHORIZATION, VALID_TOKEN)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                            );

                    // Patch Account RestDocs
                    result.andExpect(status().isOk())
                            .andDo(document("update-account",
                                    ApiDocumentUtil.getDocumentRequest(),
                                    ApiDocumentUtil.getDocumentResponse(),
                                    requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                                    pathParameters(parameterWithName("id").description("업데이트할 사용자 id")),
                                    requestFields(
                                            fieldWithPath("originalPassword").type(JsonFieldType.STRING).description("기존 비밀번호"),
                                            fieldWithPath("newPassword").type(JsonFieldType.STRING).description("변경 할 비밀번호"),
                                            fieldWithPath("email").type(JsonFieldType.STRING).description("변경 할 이메일")
                                    ),
                                    responseFields(
                                            beneathPath("data").withSubsectionId("data"),
                                            fieldWithPath("account.id").type(JsonFieldType.NUMBER).description("아이디"),
                                            fieldWithPath("account.loginId").type(JsonFieldType.STRING).description("로그인 아이디"),
                                            fieldWithPath("account.name").type(JsonFieldType.STRING).description("이름"),
                                            fieldWithPath("account.bornDate").type(JsonFieldType.STRING)
                                                    .attributes(DocumentFormatGenerator.getDateFormat())
                                                    .description("생년월일"),
                                            fieldWithPath("account.gender").type(JsonFieldType.STRING).description("성별"),
                                            fieldWithPath("account.email").type(JsonFieldType.STRING).description("이메일")
                                    )));

                    verify(accountService, times(1)).update(eq(authenticatedAccount), eq(AUTHORIZED_ID), any(AccountDto.Update.class));
                }
            }

            @Nested
            @DisplayName("인가된 accountId와 유효하지 않은 정보로 요청이 들어오면")
            class ContextWithAuthorizedAccountIdAndInvalidAccountUpdateDto {

                @BeforeEach
                void prepareInvalidAccountUpdateDto() {
                    updateDtoWithEmptyArgument = AccountDto.Update.builder()
                            .originalPassword("")
                            .newPassword(PREFIX + PASSWORD)
                            .email(PREFIX + EMAIL)
                            .build();
                }

                @Test
                @DisplayName("HttpStatus 400 Bad Request를 응답한다")
                void it_returns_httpStatus_badRequest() throws Exception {
                    String requestBody = objectMapper.writeValueAsString(updateDtoWithEmptyArgument);

                    mockMvc.perform(patch("/api/accounts/" + AUTHORIZED_ID)
                                    .header(AUTHORIZATION, VALID_TOKEN)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8))
                            .andExpect(status().isBadRequest());

                    verify(accountService, never())
                            .update(any(Account.class), anyLong(), any(AccountDto.Update.class));
                }
            }

            @Nested
            @DisplayName("인가된 accountId와 accountId가 틀린 요청이 들어오면")
            class ContextWithNotSameBetweenAuthorizedAccountIdAndTheOtherAccountId {

                @BeforeEach
                void prepareTheOtherAccountId() {
                    updateDtoWithEmptyArgument = AccountDto.Update.builder()
                            .originalPassword(PASSWORD)
                            .newPassword(PREFIX + PASSWORD)
                            .email(PREFIX + EMAIL)
                            .build();

                    given(accountService.update(eq(authenticatedAccount), eq(THE_OTHER_ID), any(AccountDto.Update.class)))
                            .willThrow(new PermissionToAccessException());
                }

                @Test
                @DisplayName("HttpStatus 401 Unauthorized 응답한다")
                void it_returns_httpStatus_unauthorized() throws Exception {
                    String requestBody = objectMapper.writeValueAsString(updateDtoWithEmptyArgument);

                    mockMvc.perform(patch("/api/accounts/" + THE_OTHER_ID)
                                    .header(AUTHORIZATION, VALID_TOKEN)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8))
                            .andExpect(status().isUnauthorized());

                    verify(accountService, times(1))
                            .update(any(Account.class), anyLong(), any(AccountDto.Update.class));
                }
            }

            @Nested
            @DisplayName("인가된 accountId와 존재하지 않은 accountId로 요청이 들어오면")
            class ContextWithAuthorizedAccountIdAndNotExistedAccountId {

                @BeforeEach
                void prepareNotExistedAccountId() {
                    updateDto = AccountDto.Update.builder()
                            .originalPassword(PASSWORD)
                            .newPassword(PREFIX + PASSWORD)
                            .email(PREFIX + EMAIL)
                            .build();

                    given(accountService.update(eq(authenticatedAccount), eq(NOT_EXISTED_ID), any(AccountDto.Update.class)))
                            .willThrow(new NotFoundException(NOT_EXISTED_ID.toString()));
                }

                @Test
                @DisplayName("HttpStatus 400 Bad Request를 응답한다")
                void it_returns_httpStatus_badRequest() throws Exception {
                    String requestBody = objectMapper.writeValueAsString(updateDto);

                    mockMvc.perform(patch("/api/accounts/" + NOT_EXISTED_ID)
                                    .header(AUTHORIZATION, VALID_TOKEN)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8))
                            .andExpect(status().isBadRequest());

                    verify(accountService, times(1))
                            .update(any(Account.class), anyLong(), any(AccountDto.Update.class));
                }
            }
        }

        @Nested
        @DisplayName("DELETE /api/accounts/{id} 는")
        class DescribeDeleteAccount {

            @Nested
            @DisplayName("인가된 accountId로 요청이 들어오면")
            class ContextWithAuthorizedAccountId {

                @BeforeEach
                void prepareAuthorizedAccountId() {
                    doNothing().when(accountService).delete(authenticatedAccount, AUTHORIZED_ID);
                }

                @Test
                @DisplayName("HttpStatus 200 OK를 응답한다")
                void it_returns_httpStatus_OK() throws Exception {
                    ResultActions result = mockMvc.perform(
                            RestDocumentationRequestBuilders.delete("/api/accounts/{id}", AUTHORIZED_ID)
                                    .header(AUTHORIZATION, VALID_TOKEN));

                    // Delete Account RestDocs
                    result.andExpect(status().isOk())
                            .andDo(document("delete-account",
                                    ApiDocumentUtil.getDocumentRequest(),
                                    ApiDocumentUtil.getDocumentResponse(),
                                    requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                                    pathParameters(parameterWithName("id").description("삭제할 사용자 id"))
                            ));

                    verify(accountService, times(1)).delete(authenticatedAccount, AUTHORIZED_ID);
                }
            }

            @Nested
            @DisplayName("인가된 accountId와 accountId가 틀린 요청이 들어오면")
            class ContextWithNotSameBetweenAuthorizedAccountIdAndTheOtherAccountId {

                @BeforeEach
                void prepareTheOtherAccountId() {
                    doThrow(new PermissionToAccessException())
                            .when(accountService).delete(authenticatedAccount, THE_OTHER_ID);
                }

                @Test
                @DisplayName("HttpStatus 401 Unauthorized를 응답한다")
                void it_returns_httpStatus_badRequest() throws Exception {
                    mockMvc.perform(delete("/api/accounts/" + THE_OTHER_ID)
                                    .header(AUTHORIZATION, VALID_TOKEN))
                            .andExpect(status().isUnauthorized());

                    verify(accountService, times(1)).delete(any(Account.class), anyLong());
                }
            }

            @Nested
            @DisplayName("존재하지 않은 accountId로 요청이 들어오면")
            class ContextWithNotExistedAccountId {

                @BeforeEach
                void prepareNotExistedAccountId() {
                    doThrow(new NotFoundException(NOT_EXISTED_ID.toString()))
                            .when(accountService)
                            .delete(authenticatedAccount, NOT_EXISTED_ID);
                }

                @Test
                @DisplayName("HttpStatus 400 BadRequest를 응답한다")
                void it_returns_httpStatus_badRequest() throws Exception {
                    mockMvc.perform(delete("/api/accounts/" + NOT_EXISTED_ID)
                                    .header(AUTHORIZATION, VALID_TOKEN))
                            .andExpect(status().isBadRequest());

                    verify(accountService, times(1)).delete(any(Account.class), anyLong());
                }
            }
        }
    }

    @Nested
    @DisplayName("Access Token이 유효 하지 않을 경우")
    class ContextWithInvalidAccessToken {

        @BeforeEach
        void prepare() throws Exception {
            when(interceptor.preHandle(any(), any(), any())).thenThrow(new InvalidTokenException());
        }

        @Nested
        @DisplayName("PATCH /api/accounts/{id} 는")
        class DescribeUpdateAccount {

            @ParameterizedTest(name = "[{index}] {1}")
            @ArgumentsSource(InvalidAccessToken.class)
            @DisplayName("HttpStatus 401 Unauthorized를 응답한다")
            void it_returns_httpStatus_Unauthorized(String authorization, String message) throws Exception {
                updateDto = AccountDto.Update.builder()
                        .originalPassword(PASSWORD)
                        .newPassword(PREFIX + PASSWORD)
                        .email(PREFIX + EMAIL)
                        .build();

                String requestBody = objectMapper.writeValueAsString(updateDto);

                mockMvc.perform(patch("/api/accounts/" + AUTHORIZED_ID)
                                .header(AUTHORIZATION, authorization)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                        .andExpect(status().isUnauthorized());

                verify(accountService, never()).update(any(Account.class), anyLong(), any(AccountDto.Update.class));
            }
        }

        @Nested
        @DisplayName("DELETE /api/accounts/{id} 는")
        class DescribeDeleteAccount {

            @ParameterizedTest(name = "[{index}] {1}")
            @ArgumentsSource(InvalidAccessToken.class)
            @DisplayName("HttpStatus 401 Unauthorized를 응답한다")
            void it_returns_httpStatus_Unauthorized(String authorization, String message) throws Exception {
                mockMvc.perform(delete("/api/accounts/" + AUTHORIZED_ID)
                                .header(AUTHORIZATION, authorization)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                        .andExpect(status().isUnauthorized());

                verify(accountService, never()).delete(any(Account.class), anyLong());
            }
        }
    }
}
