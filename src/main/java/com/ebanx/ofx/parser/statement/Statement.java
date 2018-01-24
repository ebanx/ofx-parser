package com.ebanx.ofx.parser.statement;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Statement {

    private LocalDate ofxDate;

    private final List<Transaction> transactions;

    public Statement() {
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void addOfxDate(LocalDate date) {
        this.ofxDate = date;
    }
}

