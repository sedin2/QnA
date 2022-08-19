package com.sedin.qna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedin.qna.account.model.dto.AccountSignUpDto;
import com.sedin.qna.account.model.response.AccountApiResponse;
import com.sedin.qna.account.service.AccountService;
import com.sedin.qna.error.DuplicatedException;
import com.sedin.qna.network.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerWebTest {

    private static final Long EXISTED_ID = 1L;
    private static final String LOGIN_ID = "sedin";
    private static final String EXISTED_LOGIN_ID = "sedin";
    private static final String PASSWORD = "12341234";
    private static final String NAME = "LeeSeJin";
    private static final String SEX = "M";
    private static final String EMAIL = "sejin@email.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private AccountSignUpDto accountSignUpDto;
    private Header<AccountApiResponse> response;

    @BeforeEach
    void prepare() {
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
                given(accountService.signUp(any(AccountSignUpDto.class)))
                        .willReturn(response);
            }

            @Test
            @DisplayName("HttpStatus 201 Created를 응답한다")
            void it_returns_httpStatus_created() throws Exception {
                accountSignUpDto = AccountSignUpDto.builder()
                        .loginId(LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(LocalDateTime.now())
                        .sex(SEX)
                        .email(EMAIL)
                        .build();

                String requestBody = objectMapper.writeValueAsString(accountSignUpDto);

                mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(containsString(LOGIN_ID)));

                verify(accountService).signUp(any(AccountSignUpDto.class));
            }
        }

        @Nested
        @DisplayName("이미 등록된 정보로 요청이 들어오면")
        class ContextWithAlreadyRegisteredAccountSignUpDto {

            @BeforeEach
            void prepareRegisteredDto() {
                given(accountService.signUp(any(AccountSignUpDto.class)))
                        .willThrow(new DuplicatedException(EXISTED_LOGIN_ID));
            }

            @Test
            @DisplayName("HttpStatus 400 BadRequest를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                accountSignUpDto = AccountSignUpDto.builder()
                        .loginId(EXISTED_LOGIN_ID)
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(LocalDateTime.now())
                        .sex(SEX)
                        .email(EMAIL)
                        .build();

                String requestBody = objectMapper.writeValueAsString(accountSignUpDto);

                mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, times(1)).signUp(any(AccountSignUpDto.class));
            }
        }

        @Nested
        @DisplayName("비어있는 인자값으로 요청이 들어오면")
        class ContextWithEmptyArgumentInAccountSignUpDto {

            @Test
            @DisplayName("HttpStatus 400 BadRequest를 응답한다")
            void it_returns_httpStatus_badRequest() throws Exception {
                accountSignUpDto = AccountSignUpDto.builder()
                        .loginId("")
                        .password(PASSWORD)
                        .name(NAME)
                        .bornDate(LocalDateTime.now())
                        .sex(SEX)
                        .email(EMAIL)
                        .build();

                String requestBody = objectMapper.writeValueAsString(accountSignUpDto);

                mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest());

                verify(accountService, times(0)).signUp(accountSignUpDto);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /api/accounts/{id} 는")
    class DescribeUpdateAccount {

    }

    @Nested
    @DisplayName("DELETE /api/accounts/{id} 는")
    class DescribeDeleteAccount {

    }
}
