package com.ebanx.ofx.parser.statement;

import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Transaction {
    private String id;
    private String code;
    private String type;
    private LocalDate date;
    private BigDecimal amount;
    private String description;

    public void calculateId() {
        if (code == null || date == null) {
            return;
        }

        this.setId(DigestUtils.sha1Hex((code + date).getBytes()));
    }

    public void setCode(String code) {
        this.code = code;
        calculateId();
    }

    public void setDate(LocalDate date) {
        this.date = date;
        calculateId();
    }
}
