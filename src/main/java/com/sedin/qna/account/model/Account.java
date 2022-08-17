package com.sedin.qna.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "Account")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false, length = 24)
    private String loginId;

    @Column(nullable = false, length = 30)
    private String password;

    @Column(nullable = false, length = 8)
    private String name;

    @Column(name = "born_date", nullable = false)
    private LocalDateTime bornDate;

    @Column(nullable = false, length = 2)
    private String sex;

    @Column(unique = true, nullable = false, length = 45)
    private String email;

    public void updatePasswordAndEmail(String newPassword, String email) {
        this.password = newPassword;
        this.email = email;
    }
}
