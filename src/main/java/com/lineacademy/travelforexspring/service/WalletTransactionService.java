package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.enums.TransactionType;
import com.lineacademy.travelforexspring.domain.wallet.Wallet;
import com.lineacademy.travelforexspring.domain.wallettransaction.WalletTransaction;
import com.lineacademy.travelforexspring.dto.wallettransaction.request.CreateTransactionRequest;
import com.lineacademy.travelforexspring.dto.wallettransaction.request.UpdateTransactionRequest;
import com.lineacademy.travelforexspring.repository.WalletRepository;
import com.lineacademy.travelforexspring.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public WalletTransaction createTransaction(Long userId, Long walletId, CreateTransactionRequest request) {
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId) // 파라미터 순서 (walletId, userId) 맞춤
                .orElseThrow(() -> new RuntimeException("WALLET_NOT_FOUND"));

        if (request.getTransactionType() == TransactionType.DEPOSIT) {
            wallet.addBalance(request.getAmount());
        } else if (request.getTransactionType() == TransactionType.WITHDRAWAL) {
            wallet.subtractBalance(request.getAmount());
        }

        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .transactionType(request.getTransactionType())
                .amount(request.getAmount())
                .appliedExchangeRate(request.getAppliedExchangeRate())
                .convertedKrwAmount(request.getConvertedKrwAmount())
                .transactionMethod(request.getTransactionMethod())
                .memo(request.getMemo())
                .transactionDate(request.getTransactionDate())
                .build();

        return walletTransactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<WalletTransaction> getTransactionList(Long userId, Long walletId, Long cursorId, int limit) {
        walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new RuntimeException("WALLET_NOT_FOUND"));

        PageRequest pageRequest = PageRequest.of(0, limit);

        if (cursorId == null) {
            return walletTransactionRepository.findByWalletIdOrderByIdDesc(walletId, pageRequest);
        }

        return walletTransactionRepository.findByWalletIdAndIdLessThanOrderByIdDesc(walletId, cursorId, pageRequest);
    }
    @Transactional(readOnly = true)
    public WalletTransaction getTransaction(Long userId, Long walletId, Long transactionId) {
        walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new RuntimeException("WALLET_NOT_FOUND"));

        WalletTransaction transaction = walletTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("TRANSACTION_NOT_FOUND"));

        if (!transaction.getWallet().getId().equals(walletId)) {
            throw new RuntimeException("TRANSACTION_NOT_FOUND");
        }

        return transaction;
    }

    @Transactional
    public WalletTransaction updateTransaction(Long userId, Long walletId, Long transactionId, UpdateTransactionRequest request) {
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new RuntimeException("WALLET_NOT_FOUND"));

        WalletTransaction transaction = walletTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("TRANSACTION_NOT_FOUND"));

        if (!transaction.getWallet().getId().equals(walletId)) {
            throw new RuntimeException("TRANSACTION_NOT_FOUND");
        }

        if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
            wallet.subtractBalance(transaction.getAmount());
        } else {
            wallet.addBalance(transaction.getAmount());
        }

        if (request.getTransactionType() == TransactionType.DEPOSIT) {
            wallet.addBalance(request.getAmount());
        } else {
            wallet.subtractBalance(request.getAmount());
        }

        transaction.updateTransaction(
                request.getTransactionType(),
                request.getAmount(),
                request.getAppliedExchangeRate(),
                request.getConvertedKrwAmount(),
                request.getTransactionMethod(),
                request.getMemo(),
                request.getTransactionDate()
        );

        return transaction;
    }

    @Transactional
    public void deleteTransaction(Long userId, Long walletId, Long transactionId) {
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new RuntimeException("WALLET_NOT_FOUND"));

        WalletTransaction transaction = walletTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("TRANSACTION_NOT_FOUND"));

        if (!transaction.getWallet().getId().equals(walletId)) {
            throw new RuntimeException("TRANSACTION_NOT_FOUND");
        }

        if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
            wallet.subtractBalance(transaction.getAmount());
        } else {
            wallet.addBalance(transaction.getAmount());
        }

        transaction.softDeleteData();
    }
}