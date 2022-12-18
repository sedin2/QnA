package com.sedin.qna.account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountDto {

    @Getter
    public static class Create {

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        private String name;

        private Create() {

        }

        @Builder
        private Create(String email, String password, String name) {
            this.email = email;
            this.password = password;
            this.name = name;
        }

        public void setEncodingPassword(String encodedPassword) {
            this.password = encodedPassword;
        }

        public Account toEntity() {
            return Account.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .role(Role.ROLE_USER)
                    .build();
        }
    }

    @Getter
    public static class Update {

        @NotBlank
        private String originalPassword;

        @NotBlank
        private String newPassword;

        @Email
        @NotBlank
        private String email;

        private Update() {

        }

        @Builder
        private Update(String originalPassword, String newPassword, String email) {
            this.originalPassword = originalPassword;
            this.newPassword = newPassword;
            this.email = email;
        }
    }

    @Getter
    public static class Login {

        @NotBlank
        private String email;

        @NotBlank
        private String password;

        private Login() {

        }

        @Builder
        private Login(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    @Getter
    public static class Response {

        private final Long id;

        private final String email;

        @JsonIgnore
        private final String password;

        private final String name;

        private final Role role;

        @Builder
        private Response(Long id, String email, String password, String name,
                         Role role) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.name = name;
            this.role = role;
        }

        public static Response of(Account account) {
            return Response.builder()
                    .id(account.getId())
                    .email(account.getEmail())
                    .password(account.getPassword())
                    .name(account.getName())
                    .role(account.getRole())
                    .build();
        }
    }

    @Getter
    public static class ResponseOne {

        private final Response account;

        public ResponseOne(Response account) {
            this.account = account;
        }
    }

    @Getter
    public static class ResponseList {

        private final List<Response> accounts;

        public ResponseList(List<Response> accounts) {
            this.accounts = accounts;
        }
    }
}
