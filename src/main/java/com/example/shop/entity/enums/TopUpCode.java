package com.example.shop.entity.enums;

import java.math.BigDecimal;


public enum TopUpCode {

    CODE_50("1111222233334444", BigDecimal.valueOf(10)),
    CODE_100("5555666677778888", BigDecimal.valueOf(100)),
    CODE_1000("9999000011112222", BigDecimal.valueOf(1000)),
    CODE_10000("9999999999999999", BigDecimal.valueOf(10000));

    private final String code;
    private final BigDecimal amount;

    TopUpCode(String code, BigDecimal amount) {
        this.code = code;
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public static TopUpCode findByCode(String code) {
        for (TopUpCode topUpCode : values()) {
            if (topUpCode.code.equals(code)) {
                return topUpCode;
            }
        }
        return null;
    }
}
