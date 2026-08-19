package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.enums.TransactionType;
import com.lineacademy.travelforexspring.domain.trip.Trip;
import com.lineacademy.travelforexspring.domain.tripexpense.TripExpense;
import com.lineacademy.travelforexspring.domain.wallet.Wallet;
import com.lineacademy.travelforexspring.domain.wallettransaction.WalletTransaction;
import com.lineacademy.travelforexspring.dto.tripexpense.request.CreateTripExpenseRequest;
import com.lineacademy.travelforexspring.dto.tripexpense.request.UpdateTripExpenseRequest;
import com.lineacademy.travelforexspring.repository.TripExpenseRepository;
import com.lineacademy.travelforexspring.repository.TripRepository;
import com.lineacademy.travelforexspring.repository.WalletRepository;
import com.lineacademy.travelforexspring.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TripExpenseService {
    private final TripExpenseRepository tripExpenseRepository;
    private final TripRepository tripRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Transactional
    public TripExpense createExpense(Long userId, Long tripId, CreateTripExpenseRequest request) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        WalletTransaction walletTransaction = null;

        // 지갑 연동 결제일 경우
        if (Boolean.TRUE.equals(request.getIsWalletLinked())) {
            if (request.getWalletId() == null) throw new RuntimeException("WALLET_ID_REQUIRED");

            Wallet wallet = walletRepository.findByIdAndUserId(request.getWalletId(), userId)
                    .orElseThrow(() -> new RuntimeException("WALLET_NOT_FOUND"));

            if (wallet.getCurrency() != request.getCurrency()) {
                throw new RuntimeException("CURRENCY_MISMATCH");
            }

            // 지갑 잔액 차감 (부족하면 INSUFFICIENT_BALANCE 예외 발생)
            wallet.subtractBalance(request.getAmount());

            // 출금 내역 생성
            walletTransaction = WalletTransaction.builder()
                    .wallet(wallet)
                    .transactionType(TransactionType.WITHDRAWAL)
                    .amount(request.getAmount())
                    .appliedExchangeRate(request.getConvertedKrwAmount().divide(request.getAmount(), 4, RoundingMode.HALF_UP))
                    .convertedKrwAmount(request.getConvertedKrwAmount())
                    .transactionMethod(request.getPaymentMethod().name())
                    .memo(trip.getTitle() + " 지출 연동")
                    .transactionDate(request.getExpenseDate())
                    .build();

            walletTransaction = walletTransactionRepository.save(walletTransaction);
        }

        TripExpense expense = TripExpense.builder()
                .trip(trip)
                .currency(request.getCurrency())
                .amount(request.getAmount())
                .convertedKrwAmount(request.getConvertedKrwAmount())
                .category(request.getCategory())
                .merchant(request.getMerchant())
                .paymentMethod(request.getPaymentMethod())
                .isWalletLinked(request.getIsWalletLinked())
                .walletTransaction(walletTransaction)
                .memo(request.getMemo())
                .expenseDate(request.getExpenseDate())
                .build();

        return tripExpenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Page<TripExpense> getExpenseList(Long userId, Long tripId, Pageable pageable) {
        tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        return tripExpenseRepository.findAllByTripIdOrderByExpenseDateDesc(tripId, pageable);
    }

    @Transactional(readOnly = true)
    public TripExpense getExpenseDetail(Long userId, Long tripId, Long expenseId) {
        tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        return tripExpenseRepository.findByIdAndTripId(expenseId, tripId)
                .orElseThrow(() -> new RuntimeException("EXPENSE_NOT_FOUND"));
    }

    @Transactional
    public TripExpense updateExpense(Long userId, Long tripId, Long expenseId, UpdateTripExpenseRequest request) {
        tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        TripExpense expense = tripExpenseRepository.findByIdAndTripId(expenseId, tripId)
                .orElseThrow(() -> new RuntimeException("EXPENSE_NOT_FOUND"));

        // 지갑 연동된 내역의 금액 변경 시 롤백 및 재적용 처리
        if (expense.isWalletLinked() && expense.getWalletTransaction() != null) {
            WalletTransaction wt = expense.getWalletTransaction();
            Wallet wallet = wt.getWallet();

            // 기존 출금액 복구
            wallet.addBalance(expense.getAmount());
            // 새로운 금액 차감
            wallet.subtractBalance(request.getAmount());

            wt.updateTransaction(
                    TransactionType.WITHDRAWAL,
                    request.getAmount(),
                    request.getConvertedKrwAmount().divide(request.getAmount(), 4, RoundingMode.HALF_UP),
                    request.getConvertedKrwAmount(),
                    request.getPaymentMethod().name(),
                    wt.getMemo(),
                    request.getExpenseDate()
            );
        }

        expense.updateExpense(
                request.getCurrency(),
                request.getAmount(),
                request.getConvertedKrwAmount(),
                request.getCategory(),
                request.getMerchant(),
                request.getPaymentMethod(),
                request.getMemo(),
                request.getExpenseDate()
        );

        return expense;
    }

    @Transactional
    public void deleteExpense(Long userId, Long tripId, Long expenseId) {
        tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        TripExpense expense = tripExpenseRepository.findByIdAndTripId(expenseId, tripId)
                .orElseThrow(() -> new RuntimeException("EXPENSE_NOT_FOUND"));

        // 지갑 연동된 경우 삭제 시 잔액 환불 및 출금 내역 삭제
        if (expense.isWalletLinked() && expense.getWalletTransaction() != null) {
            WalletTransaction wt = expense.getWalletTransaction();
            wt.getWallet().addBalance(expense.getAmount());
            wt.softDeleteData();
        }

        expense.softDeleteData();
    }
}
