package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.domain.wallet.Wallet;
import com.lineacademy.travelforexspring.dto.wallet.request.CreateWalletRequest;
import com.lineacademy.travelforexspring.repository.UserRepository;
import com.lineacademy.travelforexspring.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Transactional
    public Wallet createWallet(Long userId, CreateWalletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        if (walletRepository.existsByUserIdAndCurrency(userId, request.getCurrency())) {
            throw new RuntimeException("ALREADY_EXISTS_WALLET");
        }
        Wallet wallet = Wallet.builder()
                .user(user)
                .currency(request.getCurrency())
                .balance(BigDecimal.ZERO)
                .build();

        return walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public List<Wallet> getMyWallets(Long userId) {
        return walletRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Wallet getWalletDetail(Long userId, Long walletId) {
        return walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new RuntimeException("WALLET_NOT_FOUND"));
    }

    @Transactional
    public void deleteWallet(Long userId, Long walletId) {
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new RuntimeException("WALLET_NOT_FOUND"));
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("BALANCE_NOT_ZERO");
        }

        wallet.softDeleteData();
    }
}