package com.sedin.qna.account.repository;

import com.sedin.qna.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByLoginIdAndPassword(String loginId, String password);

    Optional<Account> findByIdAndPassword(Long id, String password);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

}
