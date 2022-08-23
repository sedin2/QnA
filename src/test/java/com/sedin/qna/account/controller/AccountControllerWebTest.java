package com.sedin.qna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.request.AccountSignUpDto;
import com.sedin.qna.account.model.request.AccountUpdateDto;
import com.sedin.qna.account.model.response.AccountApiResponse;
import com.sedin.qna.account.service.AccountService;
import com.sedin.qna.error.DuplicatedException;
import com.sedin.qna.error.NotFoundException;
import com.sedin.qna.network.Header;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static com.sedin.qna.util.ApiDocumentUtil.getDocumentRequest;
import static com.sedin.qna.util.ApiDocumentUtil.getDocumentResponse;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
    private static final String SEX = "M";
    private static final String EMAIL = "sejin@email.com";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private AccountSignUpDto accountSignUpDto;
    private AccountUpdateDto accountUpdateDto;
    private Header<AccountApiResponse> response;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();

        AccountApiResponse accountApiResponse = AccountApiResponse.builder()
                .id(EXISTED_ID)
                .loginId(LOGIN_ID)
                .name(NAME)
                .bornDate(LocalDateTime.now())
                .sex(SEX)
                .email(EMAIL)
                .build();

        response = Header.OK(accountApiResponse);
    }

    @Nested
    @DisplayName("POST /api/accounts 는")
    class DescribeSignUpAccount {

        @Nested
        @DisplayName("사용자 등록 요청이 들어오면")
        class ContextWithAccountSignUpDto {

            @BeforeEach
            void prepareAccountSignUpDto() {
                accountSignUpDto = AccountSignUpDto.builder()
                        .loginId(LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(LocalDateTime.now())
                        .sex(SEX)
                        .email(EMAIL)
                        .build();

                given(accountService.signUp(any(AccountSignUpDto.class)))
                        .willReturn(response);
            }

            @Test
            @DisplayName("HttpStatus 201 Created를 응답한다")
            void it_returns_httpStatus_created() throws Exception {
                String requestBody = objectMapper.writeValueAsString(accountSignUpDto);

                ResultActions result = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON));

                result.andExpect(status().isCreated())
                        .andExpect(content().string(containsString(LOGIN_ID)))
                        .andDo(document("create-account",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("loginId").type(JsonFieldType.STRING).description("아이디"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("bornDate").type(JsonFieldType.STRING).description("생년월일"),
                                        fieldWithPath("sex").type(JsonFieldType.STRING).description("성별"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                ),
                                responseFields(
                                        fieldWithPath("resultCode").type(JsonFieldType.STRING).description("응답 코드"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("상태 설명"),
                                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 id"),
                                        fieldWithPath("data.loginId").type(JsonFieldType.STRING).description("사용자 로그인 id"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자 이름"),
                                        fieldWithPath("data.bornDate").type(JsonFieldType.STRING).description("사용자 생년월일"),
                                        fieldWithPath("data.sex").type(JsonFieldType.STRING).description("사용자 성별"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("사용자 이메일")
                                )));

                verify(accountService).signUp(any(AccountSignUpDto.class));
            }
        }

        @Nested
        @DisplayName("이미 등록된 정보로 요청이 들어오면")
        class ContextWithAlreadyRegisteredAccountSignUpDto {

            @BeforeEach
            void prepareRegisteredDto() {
                accountSignUpDto = AccountSignUpDto.builder()
                        .loginId(EXISTED_LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(LocalDateTime.now())
                        .sex(SEX)
                        .email(EMAIL)
                        .build();

                given(accountService.signUp(any(AccountSignUpDto.class)))
                        .willThrow(new DuplicatedException(EXISTED_LOGIN_ID));
            }

            @Test
            @DisplayName("HttpStatus 400 BadRequest를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                String requestBody = objectMapper.writeValueAsString(accountSignUpDto);

                mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, times(1)).signUp(any(AccountSignUpDto.class));
            }
        }

        @Nested
        @DisplayName("비어있는 인자값으로 요청이 들어오면")
        class ContextWithEmptyArgumentInAccountSignUpDto {

            @BeforeEach
            void prepareAccountSignUpDtoWithEmptyArgument() {
                accountSignUpDto = AccountSignUpDto.builder()
                        .loginId("")
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(LocalDateTime.now())
                        .sex(SEX)
                        .email(EMAIL)
                        .build();
            }

            @Test
            @DisplayName("HttpStatus 400 BadRequest를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                String requestBody = objectMapper.writeValueAsString(accountSignUpDto);

                mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, times(0)).signUp(any(AccountSignUpDto.class));
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
                accountUpdateDto = AccountUpdateDto.builder()
                        .originalPassword(PASSWORD)
                        .newPassword(PREFIX + PASSWORD)
                        .email(PREFIX + EMAIL)
                        .build();

                given(accountService.update(eq(EXISTED_ID), any(AccountUpdateDto.class))).willReturn(response);
            }

            @Test
            @DisplayName("HttpStatus 200 OK를 응답한다")
            void it_returns_httpStatus_OK() throws Exception {
                String requestBody = objectMapper.writeValueAsString(accountUpdateDto);

                mockMvc.perform(patch("/api/accounts/" + EXISTED_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(requestBody))
                        .andExpect(status().isOk());

                verify(accountService, times(1)).update(eq(EXISTED_ID), any(AccountUpdateDto.class));
            }

        }

        @Nested
        @DisplayName("존재하는 accountId와 유효하지 않은 정보로 요청이 들어오면")
        class ContextWithExistedAccountIdAndInvalidAccountUpdateDto {

            @BeforeEach
            void prepareInvalidAccountUpdateDto() {
                accountUpdateDto = AccountUpdateDto.builder()
                        .originalPassword("")
                        .newPassword(PREFIX + PASSWORD)
                        .email(PREFIX + EMAIL)
                        .build();
            }

            @Test
            @DisplayName("HttpStatus 400 Bad Request를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                String requestBody = objectMapper.writeValueAsString(accountUpdateDto);

                mockMvc.perform(patch("/api/accounts/" + EXISTED_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, times(0))
                        .update(eq(EXISTED_ID), any(AccountUpdateDto.class));
            }
        }

        @Nested
        @DisplayName("존재하지 않은 accountId로 요청이 들어오면")
        class ContextWithNotExistedAccountId {

            @BeforeEach
            void prepareNotExistedAccountId() {
                accountUpdateDto = AccountUpdateDto.builder()
                        .originalPassword(PASSWORD)
                        .newPassword(PREFIX + PASSWORD)
                        .email(PREFIX + EMAIL)
                        .build();

                given(accountService.update(eq(NOT_EXISTED_ID), any(AccountUpdateDto.class)))
                        .willThrow(new NotFoundException("accountId"));
            }

            @Test
            @DisplayName("HttpStatus 400 Bad Request를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                String requestBody = objectMapper.writeValueAsString(accountUpdateDto);
                System.out.println(requestBody);

                mockMvc.perform(patch("/api/accounts/" + NOT_EXISTED_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, times(1))
                        .update(eq(NOT_EXISTED_ID), any(AccountUpdateDto.class));
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
            @DisplayName("HttpStatus 204 No Content를 응답한다")
            void it_returns_httpStatus_noContent() throws Exception {
                mockMvc.perform(delete("/api/accounts/" + EXISTED_ID))
                        .andExpect(status().isNoContent());

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
