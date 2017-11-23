package com.ebanx.ofx.parser.statement;

import lombok.Data;
import org.apache.commons.codec.binary.Hex;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Data
public class Transaction {
    private String id;
    private String code;
    private String type;
    private String date;
    private BigDecimal amount;
    private String description;

    public void calculateId() {
        if (code == null || date == null) {
            return;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update((code + date).getBytes());
            this.setId(Hex.encodeHexString(digest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void setCode(String code) {
        this.code = code;
        calculateId();
    }

    public void setDate(String date) {
        this.date = date;
        calculateId();
    }
}
