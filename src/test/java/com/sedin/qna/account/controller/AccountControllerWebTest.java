package com.sedin.qna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.AccountDto;
import com.sedin.qna.account.model.Gender;
import com.sedin.qna.account.service.AccountService;
import com.sedin.qna.exception.DuplicatedException;
import com.sedin.qna.exception.NotFoundException;
import com.sedin.qna.util.ApiDocumentUtil;
import com.sedin.qna.util.DocumentFormatGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    private static final Long EXISTED_ID = 1L;
    private static final Long NOT_EXISTED_ID = 9999L;
    private static final String PREFIX = "prefix";
    private static final String LOGIN_ID = "sedin";
    private static final String EXISTED_LOGIN_ID = "sedin";
    private static final String PASSWORD = "12341234";
    private static final String NAME = "LeeSeJin";
    private static final LocalDate BORN_DATE = LocalDate.of(1994, 8, 30);
    private static final String EMAIL = "sejin@email.com";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private AccountDto.Create createDto;
    private AccountDto.Create registeredDto;
    private AccountDto.Create createDtoWithEmptyArgument;
    private AccountDto.Update updateDto;
    private AccountDto.Update updateDtoWithEmptyArgument;
    private AccountDto.Response response;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
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
                        .loginId(LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(BORN_DATE)
                        .gender(Gender.MALE)
                        .email(EMAIL)
                        .build();

                response = AccountDto.Response.builder()
                        .id(EXISTED_ID)
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
    @DisplayName("PATCH /api/accounts/{id} 는")
    class DescribeUpdateAccount {

        @Nested
        @DisplayName("존재하는 accountId와 정보로 요청이 들어오면")
        class ContextWithExistedAccountIdAndAccountUpdateDto {

            @BeforeEach
            void prepareExistedAccountIdAndAccountUpdateDto() {
                updateDto = AccountDto.Update.builder()
                        .originalPassword(PASSWORD)
                        .newPassword(PREFIX + PASSWORD)
                        .email(PREFIX + EMAIL)
                        .build();

                response = AccountDto.Response.builder()
                        .id(EXISTED_ID)
                        .loginId(LOGIN_ID)
                        .name(NAME)
                        .bornDate(BORN_DATE)
                        .gender(Gender.MALE)
                        .email(PREFIX + EMAIL)
                        .build();

                given(accountService.update(eq(EXISTED_ID), any(AccountDto.Update.class))).willReturn(response);
            }

            @Test
            @DisplayName("HttpStatus 200 OK를 응답한다")
            void it_returns_httpStatus_OK() throws Exception {
                String requestBody = objectMapper.writeValueAsString(updateDto);

                ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/accounts/{id}", EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                        .andDo(MockMvcResultHandlers.print());

                // Patch Account RestDocs
                result.andExpect(status().isOk())
                        .andDo(document("update-account",
                                ApiDocumentUtil.getDocumentRequest(),
                                ApiDocumentUtil.getDocumentResponse(),
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

                verify(accountService, times(1)).update(eq(EXISTED_ID), any(AccountDto.Update.class));
            }
        }

        @Nested
        @DisplayName("존재하는 accountId와 유효하지 않은 정보로 요청이 들어오면")
        class ContextWithExistedAccountIdAndInvalidAccountUpdateDto {

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

                mockMvc.perform(patch("/api/accounts/" + EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, never())
                        .update(eq(EXISTED_ID), any(AccountDto.Update.class));
            }
        }

        @Nested
        @DisplayName("존재하지 않은 accountId로 요청이 들어오면")
        class ContextWithNotExistedAccountId {

            @BeforeEach
            void prepareNotExistedAccountId() {
                updateDto = AccountDto.Update.builder()
                        .originalPassword(PASSWORD)
                        .newPassword(PREFIX + PASSWORD)
                        .email(PREFIX + EMAIL)
                        .build();

                given(accountService.update(eq(NOT_EXISTED_ID), any(AccountDto.Update.class)))
                        .willThrow(new NotFoundException("accountId"));
            }

            @Test
            @DisplayName("HttpStatus 400 Bad Request를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                String requestBody = objectMapper.writeValueAsString(updateDto);

                mockMvc.perform(patch("/api/accounts/" + NOT_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, times(1))
                        .update(eq(NOT_EXISTED_ID), any(AccountDto.Update.class));
            }
        }
    }

    @Nested
    @DisplayName("DELETE /api/accounts/{id} 는")
    class DescribeDeleteAccount {

        @Nested
        @DisplayName("존재하는 accountId로 요청이 들어오면")
        class ContextWithExistedAccountId {

            @BeforeEach
            void prepareExistedAccountId() {
                doNothing().when(accountService).delete(EXISTED_ID);
            }

            @Test
            @DisplayName("HttpStatus 200 OK를 응답한다")
            void it_returns_httpStatus_OK() throws Exception {
                ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/accounts/{id}", EXISTED_ID));

                // Delete Account RestDocs
                result.andExpect(status().isOk())
                        .andDo(document("delete-account",
                                ApiDocumentUtil.getDocumentRequest(),
                                ApiDocumentUtil.getDocumentResponse(),
                                pathParameters(parameterWithName("id").description("삭제할 사용자 id"))
                                ));

                verify(accountService, times(1)).delete(EXISTED_ID);
            }
        }

        @Nested
        @DisplayName("존재하지 않은 accountId로 요청이 들어오면")
        class ContextWithNotExistedAccountId {

            @BeforeEach
            void prepareNotExistedAccountId() {
                doThrow(new NotFoundException("accountId")).when(accountService).delete(NOT_EXISTED_ID);
            }

            @Test
            @DisplayName("HttpStatus 400 BadRequest를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                mockMvc.perform(delete("/api/accounts/" + NOT_EXISTED_ID))
                        .andExpect(status().isBadRequest());

                verify(accountService, times(1)).delete(NOT_EXISTED_ID);
            }
        }
    }
}
