package com.lineacademy.travelforexspring.dto.wallet.response;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.wallet.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class WalletResponse {
    private Long id;
    private CurrencyCode currency;
    private BigDecimal balance;

    public static WalletResponse from(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .build();
    }
}
