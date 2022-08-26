package com.sedin.qna.account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountDto {

    @Getter
    public static class Create {

        @NotBlank
        private String loginId;
        @NotBlank
        private String password;
        @NotBlank
        private String name;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate bornDate;
        @NotNull
        private Gender gender;
        @Email
        @NotBlank
        private String email;

        private Create() {

        }

        @Builder
        private Create(@NotBlank String loginId, @NotBlank String password, @NotBlank String name,
                       LocalDate bornDate, @NotNull Gender gender, @Email @NotBlank String email) {
            this.loginId = loginId;
            this.password = password;
            this.name = name;
            this.bornDate = bornDate;
            this.gender = gender;
            this.email = email;
        }

        public void setEncodingPassword(String encodedPassword) {
            this.password = encodedPassword;
        }

        public Account toEntity() {
            return Account.builder()
                    .loginId(loginId)
                    .password(password)
                    .name(name)
                    .bornDate(bornDate)
                    .gender(gender)
                    .email(email)
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
        private Update(@NotBlank String originalPassword, @NotBlank String newPassword, @Email @NotBlank String email) {
            this.originalPassword = originalPassword;
            this.newPassword = newPassword;
            this.email = email;
        }

        public Account complete(Account account) {
            return account.updatePasswordAndEmail(newPassword, email);
        }
    }

    @Getter
    public static class Response {

        private Long id;
        private String loginId;
        @JsonIgnore
        private String password;
        private String name;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate bornDate;
        private Gender gender;
        private String email;

        @Builder
        private Response(Long id, String loginId, String password, String name, LocalDate bornDate, Gender gender, String email) {
            this.id = id;
            this.loginId = loginId;
            this.password = password;
            this.name = name;
            this.bornDate = bornDate;
            this.gender = gender;
            this.email = email;
        }

        public static Response of(Account account) {
            return Response.builder()
                    .id(account.getId())
                    .loginId(account.getLoginId())
                    .password(account.getPassword())
                    .name(account.getName())
                    .bornDate(account.getBornDate())
                    .gender(account.getGender())
                    .email(account.getEmail())
                    .build();
        }
    }

    @Getter
    public static class ResponseOne {

        private Response account;

        public ResponseOne(Response account) {
            this.account = account;
        }
    }

    @Getter
    public static class ResponseList {

        private List<Response> accounts;

        public ResponseList(List<Response> accounts) {
            this.accounts = accounts;
        }
    }
}
