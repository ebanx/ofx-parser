package com.ebanx.ofx.parser.statement;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Statement {

    private final List<Transaction> transactions;

    public Statement() {
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}

