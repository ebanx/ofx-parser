package com.ebanx.ofx.parser.statement;

import com.ebanx.ofx.parser.OfxParseException;
import com.ebanx.ofx.parser.lexer.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class StatementParser {

    private final OfxLexer lexer;

    private StatementParser(InputStream ofx) {
        this.lexer = new OfxLexerImpl(ofx);
    }

    private Statement getStatement() throws IOException {
        Token token;

        Statement statement = new Statement();
        Transaction currTransaction = null;
        String currentOpenTag = null;

        while ((token = lexer.nextToken()) != null) {

            if (token instanceof OpenTag) {
                OpenTag openTag = (OpenTag) token;

                switch (openTag.getTagName()) {
                    case "STMTTRN":
                        currTransaction = new Transaction();
                        statement.addTransaction(currTransaction);
                        break;
                    case "TRNTYPE":
                    case "DTPOSTED":
                    case "TRNAMT":
                    case "FITID":
                    case "CHECKNUM":
                    case "PAYEEID":
                    case "MEMO":
                        if (currTransaction == null) {
                            throw new OfxParseException("Transaction field out of context");
                        }

                        currentOpenTag = openTag.getTagName();
                        break;
                    default:
                        break;
                }
            }

            if (token instanceof Text) {
                Text text = (Text) token;

                if (currTransaction != null && currentOpenTag != null) {
                    addContent(currTransaction, currentOpenTag, text.getContent().trim());
                }
            }

            if (token instanceof CloseTag) {
                CloseTag closeTag = (CloseTag) token;

                if (closeTag.getTagName().equals("STMTTRN")) {
                    currTransaction = null;
                    currentOpenTag = null;
                }
            }
        }

        return statement;
    }

    private void addContent(Transaction tx, String tag, String content) {
        switch (tag) {
            case "TRNTYPE":
                tx.setType(content);
                break;
            case "DTPOSTED":
                tx.setDate(content);
                break;
            case "TRNAMT":
                String amt = content.replace(",", ".");
                tx.setAmount(new BigDecimal(amt));
                break;
            case "FITID":
                tx.setCode(content);
                break;
            case "MEMO":
                tx.setDescription(concat(tx.getDescription(), content));
                break;
        }
    }

    private String concat(String... data) {
        StringBuilder v = new StringBuilder();
        for (String datum : data) {
            if (datum != null) {
                v.append(datum);
            }
        }

        return v.toString();
    }

    public static Statement parseStatement(InputStream ofx) throws IOException {
        StatementParser parser = new StatementParser(ofx);
        return parser.getStatement();
    }

}
