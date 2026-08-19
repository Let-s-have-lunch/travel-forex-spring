package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.wallettransaction.WalletTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletIdOrderByIdDesc(Long walletId, Pageable pageable);

    List<WalletTransaction> findByWalletIdAndIdLessThanOrderByIdDesc(Long walletId, Long id, Pageable pageable);

}