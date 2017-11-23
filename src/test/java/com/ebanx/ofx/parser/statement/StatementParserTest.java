package com.ebanx.ofx.parser.statement;

import com.ebanx.ofx.parser.OfxParseException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class StatementParserTest {

    @Test
    public void parseStatementTest() throws IOException {
        String data = "<STMTTRN>\n" +
                "\t  <TRNTYPE>XFER \n" +
                "\t  <DTPOSTED>20171101 \n" +
                "\t  <TRNAMT>68.25 \n" +
                "\t  <FITID>2017110106825 \n" +
                "\t  <CHECKNUM>00042051 \n" +
                "\t  <MEMO>TRANSFERÊNCIA RECEBIDA    \n" +
                "\t </STMTTRN>\n" +
                "\t <STMTTRN>\n" +
                "\t  <TRNTYPE>XFER \n" +
                "\t  <DTPOSTED>20171109 \n" +
                "\t  <TRNAMT>3.39 \n" +
                "\t  <FITID>201711090339 \n" +
                "\t  <CHECKNUM>00031668 \n" +
                "\t  <MEMO>TRANSFERÊNCIA RECEBIDA    \n" +
                "\t </STMTTRN>";

        Statement statement = StatementParser.parseStatement(new ByteArrayInputStream(data.getBytes()));

        assertNotNull(statement);
        assertEquals(2, statement.getTransactions().size());

        Transaction txn1 = statement.getTransactions().get(0);
        assertEquals("XFER", txn1.getType());
        assertEquals("20171101", txn1.getDate());
        assertEquals(new BigDecimal("68.25"), txn1.getAmount());
        assertEquals("2017110106825", txn1.getCode());
        assertEquals("TRANSFERÊNCIA RECEBIDA", txn1.getDescription());
        assertNotNull(txn1.getId());

        Transaction txn2 = statement.getTransactions().get(1);
        assertEquals("XFER", txn2.getType());
        assertEquals("20171109", txn2.getDate());
        assertEquals(new BigDecimal("3.39"), txn2.getAmount());
        assertEquals("201711090339", txn2.getCode());
        assertEquals("TRANSFERÊNCIA RECEBIDA", txn2.getDescription());
        assertNotNull(txn1.getId());
    }

    @Test
    public void failOnOutOfContextTransactionFieldTest() throws IOException {
        String data = "" +
                "\t  <TRNTYPE>XFER \n" +
                "\t  <DTPOSTED>20171101 \n" +
                "\t  <TRNAMT>68.25 \n" +
                "\t  <FITID>2017110106825 \n" +
                "\t  <CHECKNUM>00042051 \n" +
                "\t  <MEMO>TRANSFERÊNCIA RECEBIDA    \n" +
                "\t </STMTTRN>\n";

        try {
            StatementParser.parseStatement(new ByteArrayInputStream(data.getBytes()));
            fail();
        } catch (OfxParseException e) {
            assertEquals("Transaction field out of context", e.getMessage());
        }
    }

}