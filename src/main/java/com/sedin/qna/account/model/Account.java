package com.sedin.qna.account.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "Account")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false, length = 24)
    private String loginId;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 8)
    private String name;

    @Column(name = "born_date", nullable = false)
    private LocalDate bornDate;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Column(unique = true, nullable = false, length = 45)
    private String email;

    @Builder
    private Account(Long id, String loginId, String password, String name, LocalDate bornDate, Gender gender, String email) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.bornDate = bornDate;
        this.gender = gender;
        this.email = email;
    }

    public Account updatePasswordAndEmail(String newPassword, String email) {
        this.password = newPassword;
        this.email = email;

        return this;
    }
}

