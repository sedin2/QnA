package com.sedin.qna.account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountDto {

    @Getter
    @EqualsAndHashCode
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

        public Account toEntity(String encodingPassword) {
            return Account.builder()
                    .email(email)
                    .password(encodingPassword)
                    .name(name)
                    .role(Role.ROLE_USER)
                    .build();
        }
    }

    @Getter
    @EqualsAndHashCode
    public static class Update {

        @NotBlank
        private String originalPassword;

        @NotBlank
        private String newPassword;

        @NotBlank
        private String name;

        private Update() {

        }

        @Builder
        private Update(String originalPassword, String newPassword, String name) {
            this.originalPassword = originalPassword;
            this.newPassword = newPassword;
            this.name = name;
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

        @Builder
        private Response(Long id, String email, String password, String name,
                         Role role) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.name = name;
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
