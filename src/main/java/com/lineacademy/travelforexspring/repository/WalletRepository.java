package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    boolean existsByUserIdAndCurrency(Long userId, CurrencyCode currency);

    List<Wallet> findAllByUserId(Long userId);

    Optional<Wallet> findByIdAndUserId(Long id, Long userId);

}