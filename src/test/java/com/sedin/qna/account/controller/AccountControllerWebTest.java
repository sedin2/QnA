package com.sedin.qna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.service.AccountService;
import com.sedin.qna.authentication.service.JwtTokenProvider;
import com.sedin.qna.common.configuration.SecurityConfiguration;
import com.sedin.qna.common.exception.DuplicatedException;
import com.sedin.qna.common.exception.NotFoundException;
import com.sedin.qna.configuration.WithCustomMockUser;
import com.sedin.qna.util.ApiDocumentUtil;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static com.sedin.qna.common.response.ApiResponseCode.BAD_PARAMETER;
import static com.sedin.qna.common.response.ApiResponseCode.DUPLICATED_ERROR;
import static com.sedin.qna.common.response.ApiResponseCode.NOT_FOUND;
import static com.sedin.qna.common.response.ApiResponseCode.OK;
import static com.sedin.qna.common.response.ApiResponseCode.UNAUTHORIZED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@Import({
        SecurityConfiguration.class,
        JwtTokenProvider.class
})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class AccountControllerWebTest {

    private static final Long AUTHORIZED_ID = 1L;
    private static final String PREFIX = "prefix";
    private static final String PASSWORD = "12341234";
    private static final String ORIGINAL_PASSWORD = "12341234";
    private static final String NEW_PASSWORD = "new12345";
    private static final String NAME = "Mocha";
    private static final String NEW_NAME = "CafeMocha";
    private static final String EMAIL = "cafe@mocha.com";
    private static final String EXISTED_EMAIL = "cafe@mocha.com";
    private static final String NOT_EXISTED_EMAIL = "noob@mail.com";
    private static final String INVALID_EMAIL = "invalid";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_SCHEME = "Bearer ";
    private static final String VALID_TOKEN = "header.payload.verify-signature";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private AccountDto.Create createDto;
    private AccountDto.Create createDtoWithInvalidEmail;
    private AccountDto.Create alreadyRegisteredEmail;
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
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentation))
                .build();
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
                        .email(EMAIL)
                        .password(PASSWORD)
                        .name(NAME)
                        .build();

                response = AccountDto.Response.builder()
                        .email(EMAIL)
                        .id(AUTHORIZED_ID)
                        .name(NAME)
                        .build();

                given(accountService.signUp(createDto))
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
                        .andExpect(jsonPath("$.code").value(OK.getId()))
                        .andExpect(jsonPath("$.data.account.email").value(EMAIL))
                        .andDo(document("create-account",
                                ApiDocumentUtil.getDocumentRequest(),
                                ApiDocumentUtil.getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                ),
                                responseFields(
                                        beneathPath("data").withSubsectionId("data"),
                                        fieldWithPath("account.id").type(JsonFieldType.NUMBER).description("아이디"),
                                        fieldWithPath("account.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("account.name").type(JsonFieldType.STRING).description("이름")
                                )));

                verify(accountService).signUp(createDto);
            }
        }

        @Nested
        @DisplayName("이메일 형식이 잘못된 요청이 들어오면")
        class ContextWithInvalidEmailInCreateDto {

            @BeforeEach
            void prepareAccountSignUpDtoWithEmptyArgument() {
                createDtoWithInvalidEmail = AccountDto.Create.builder()
                        .email(INVALID_EMAIL)
                        .password(PASSWORD)
                        .name(NAME)
                        .build();
            }

            @Test
            @DisplayName("HttpStatus 400 BadRequest를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                String requestBody = objectMapper.writeValueAsString(createDtoWithInvalidEmail);

                mockMvc.perform(post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(requestBody))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").value(BAD_PARAMETER.getId()));

                verify(accountService, never()).signUp(createDtoWithInvalidEmail);
            }
        }


        @Nested
        @DisplayName("이미 등록된 이메일로 요청이 들어오면")
        class ContextWithAlreadyRegisteredEmail {

            @BeforeEach
            void prepareRegisteredDto() {
                alreadyRegisteredEmail = AccountDto.Create.builder()
                        .email(EXISTED_EMAIL)
                        .password(PASSWORD)
                        .name(NAME)
                        .build();

                given(accountService.signUp(alreadyRegisteredEmail))
                        .willThrow(new DuplicatedException(EMAIL));
            }

            @Test
            @DisplayName("HttpStatus 400 BadRequest를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                String requestBody = objectMapper.writeValueAsString(alreadyRegisteredEmail);

                mockMvc.perform(post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(requestBody))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").value(DUPLICATED_ERROR.getId()));

                verify(accountService).signUp(alreadyRegisteredEmail);
            }
        }

        @Nested
        @DisplayName("비어있는 인자값으로 요청이 들어오면")
        class ContextWithEmptyArgumentInCreateDto {

            @BeforeEach
            void prepareAccountSignUpDtoWithEmptyArgument() {
                createDtoWithEmptyArgument = AccountDto.Create.builder()
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
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").value(BAD_PARAMETER.getId()));

                verify(accountService, never()).signUp(createDtoWithEmptyArgument);
            }
        }
    }

    @Nested
    @DisplayName("Access Token으로 인증/인가에 성공한 경우")
    class ContextWithValidAccessToken {

        @Nested
        @DisplayName("PATCH /api/accounts 는")
        class DescribeUpdateAccount {

            @Nested
            @DisplayName("유효한 수정 정보로 요청이 들어오면")
            class ContextWithValidUpdateDto {

                @BeforeEach
                void prepareValidUpdateDto() {
                    updateDto = AccountDto.Update.builder()
                            .name(NEW_NAME)
                            .originalPassword(ORIGINAL_PASSWORD)
                            .newPassword(NEW_PASSWORD)
                            .build();

                    response = AccountDto.Response.builder()
                            .id(AUTHORIZED_ID)
                            .email(EMAIL)
                            .name(NEW_NAME)
                            .build();

                    given(accountService.update(EMAIL, updateDto))
                            .willReturn(response);
                }

                @Test
                @WithCustomMockUser
                @DisplayName("HttpStatus 200 OK를 응답한다")
                void it_returns_httpStatus_OK() throws Exception {
                    String requestBody = objectMapper.writeValueAsString(updateDto);

                    ResultActions result = mockMvc.perform(patch("/api/accounts")
                            .header(AUTHORIZATION, BEARER_SCHEME + VALID_TOKEN)
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
                                    requestFields(
                                            fieldWithPath("originalPassword").type(JsonFieldType.STRING).description("기존 비밀번호"),
                                            fieldWithPath("newPassword").type(JsonFieldType.STRING).description("변경 할 비밀번호"),
                                            fieldWithPath("name").type(JsonFieldType.STRING).description("변경 할 닉네임")
                                    ),
                                    responseFields(
                                            beneathPath("data").withSubsectionId("data"),
                                            fieldWithPath("account.id").type(JsonFieldType.NUMBER).description("아이디"),
                                            fieldWithPath("account.email").type(JsonFieldType.STRING).description("이메일"),
                                            fieldWithPath("account.name").type(JsonFieldType.STRING).description("이름")
                                    )));

                    verify(accountService).update(EMAIL, updateDto);
                }
            }

            @Nested
            @DisplayName("유효하지 않은 정보로 요청이 들어오면")
            class ContextWithInvalidAccountUpdateDto {

                @BeforeEach
                void prepareInvalidAccountUpdateDto() {
                    updateDtoWithEmptyArgument = AccountDto.Update.builder()
                            .originalPassword("")
                            .newPassword(PREFIX + PASSWORD)
                            .build();
                }

                @Test
                @WithCustomMockUser
                @DisplayName("HttpStatus 400 Bad Request를 응답한다")
                void it_returns_httpStatus_badRequest() throws Exception {
                    String requestBody = objectMapper.writeValueAsString(updateDtoWithEmptyArgument);

                    mockMvc.perform(patch("/api/accounts")
                                    .header(AUTHORIZATION, BEARER_SCHEME + VALID_TOKEN)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.code").value(BAD_PARAMETER.getId()));

                    verify(accountService, never())
                            .update(any(String.class), any(AccountDto.Update.class));
                }
            }

            @Nested
            @DisplayName("존재하지 않은 이메일로 요청이 들어오면")
            class ContextWithNotExistedEmail {

                @BeforeEach
                void prepare() {
                    updateDto = AccountDto.Update.builder()
                            .originalPassword(PASSWORD)
                            .newPassword(NEW_PASSWORD)
                            .name(NEW_NAME)
                            .build();

                    given(accountService.update(eq(NOT_EXISTED_EMAIL), any(AccountDto.Update.class)))
                            .willThrow(new NotFoundException(NOT_EXISTED_EMAIL));
                }

                @Test
                @WithCustomMockUser(username = NOT_EXISTED_EMAIL)
                @DisplayName("HttpStatus 400 Bad Request를 응답한다")
                void it_returns_httpStatus_badRequest() throws Exception {
                    String requestBody = objectMapper.writeValueAsString(updateDto);

                    mockMvc.perform(patch("/api/accounts")
                                    .header(AUTHORIZATION, VALID_TOKEN)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.code").value(NOT_FOUND.getId()));

                    verify(accountService).update(eq(NOT_EXISTED_EMAIL), any(AccountDto.Update.class));
                }
            }
        }

        @Nested
        @DisplayName("DELETE /api/accounts/{id} 는")
        class DescribeDeleteAccount {

            @Nested
            @DisplayName("요청이 오면")
            class Context {

                @BeforeEach
                void prepareAuthorizedAccountId() {
                    doNothing().when(accountService).delete(EMAIL);
                }

                @Test
                @WithCustomMockUser
                @DisplayName("HttpStatus 200 OK를 응답한다")
                void it_returns_httpStatus_OK() throws Exception {
                    ResultActions result = mockMvc.perform(
                            RestDocumentationRequestBuilders.delete("/api/accounts")
                                    .header(AUTHORIZATION, VALID_TOKEN));

                    // Delete Account RestDocs
                    result.andExpect(status().isOk())
                            .andDo(document("delete-account",
                                    ApiDocumentUtil.getDocumentRequest(),
                                    ApiDocumentUtil.getDocumentResponse(),
                                    requestHeaders(headerWithName(AUTHORIZATION).description("Basic auth credentials"))
                            ));

                    verify(accountService).delete(EMAIL);
                }
            }

            @Nested
            @DisplayName("존재하지 않은 이메일로 요청이 들어오면")
            class ContextWithNotExistedAccountId {

                @BeforeEach
                void prepareNotExistedAccountId() {
                    doThrow(new NotFoundException(NOT_EXISTED_EMAIL))
                            .when(accountService)
                            .delete(NOT_EXISTED_EMAIL);
                }

                @Test
                @WithCustomMockUser(username = NOT_EXISTED_EMAIL)
                @DisplayName("HttpStatus 400 BadRequest를 응답한다")
                void it_returns_httpStatus_badRequest() throws Exception {
                    mockMvc.perform(delete("/api/accounts")
                                    .header(AUTHORIZATION, VALID_TOKEN))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.code").value(NOT_FOUND.getId()));

                    verify(accountService).delete(NOT_EXISTED_EMAIL);
                }
            }
        }
    }

    @Nested
    @DisplayName("Access Token이 유효 하지 않을 경우")
    class ContextWithInvalidAccessToken {

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
                        .build();

                String requestBody = objectMapper.writeValueAsString(updateDto);

                mockMvc.perform(patch("/api/accounts/" + AUTHORIZED_ID)
                                .header(AUTHORIZATION, authorization)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.code").value(UNAUTHORIZED.getId()));

                verify(accountService, never()).update(any(String.class), any(AccountDto.Update.class));
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
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.code").value(UNAUTHORIZED.getId()));

                verify(accountService, never()).delete(any(String.class));
            }
        }
    }

}
