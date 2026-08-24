package com.lineacademy.travelforexspring.repository;


import java.math.BigDecimal;

public interface TripExpenseSumProjection {
    Long getTripId();
    BigDecimal getTotal();
}