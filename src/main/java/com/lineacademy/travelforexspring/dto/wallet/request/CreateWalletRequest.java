package com.lineacademy.travelforexspring.dto.wallet.request;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class CreateWalletRequest {

    @NotNull(message = "통화 코드를 선택해주세요.")
    private CurrencyCode currency;

    private BigDecimal balance;
}