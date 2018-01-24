package com.ebanx.ofx.parser.statement;

import com.ebanx.ofx.parser.OfxParseException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class StatementParserTest {

    @Test
    public void parseStatementTest() throws IOException {
        String data = "<STMTTRN>\n" +
                "\t  <TRNTYPE>XFER \n" +
                "\t  <DTPOSTED>20171117100000[-03:EST] \n" +
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
        System.out.println(txn1);
        assertEquals("XFER", txn1.getType());
        assertEquals(LocalDate.of(2017, 11, 17), txn1.getDate());
        assertEquals(new BigDecimal("68.25"), txn1.getAmount());
        assertEquals("2017110106825", txn1.getCode());
        assertEquals("TRANSFERÊNCIA RECEBIDA", txn1.getDescription());
        assertNotNull(txn1.getId());

        Transaction txn2 = statement.getTransactions().get(1);
        assertEquals("XFER", txn2.getType());
        assertEquals(LocalDate.of(2017, 11, 9), txn2.getDate());
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

    @Test
    public void parseFullStatementTest() throws IOException {
        String data = "<OFX>\n" +
                "    <SIGNONMSGSRSV1>\n" +
                "        <SONRS>\n" +
                "            <STATUS>\n" +
                "                <CODE>0\n" +
                "                <SEVERITY>INFO\n" +
                "            </STATUS>\n" +
                "            <DTSERVER>20180108112911[-3:GMT]\n" +
                "            <LANGUAGE>ENG\n" +
                "            <FI>\n" +
                "                <ORG>SANTANDER\n" +
                "                <FID>SANTANDER\n" +
                "            </FI>\n" +
                "        </SONRS>\n" +
                "    </SIGNONMSGSRSV1>\n" +
                "    <BANKMSGSRSV1>\n" +
                "        <STMTTRNRS>\n" +
                "            <TRNUID>1\n" +
                "            <STATUS>\n" +
                "                <CODE>0\n" +
                "                <SEVERITY>INFO\n" +
                "            </STATUS>\n" +
                "            <STMTRS>\n" +
                "                <CURDEF>BRL\n" +
                "                <BANKACCTFROM>\n" +
                "                    <BANKID>033\n" +
                "                    <ACCTID>3415130040326\n" +
                "                    <ACCTTYPE>CHECKING\n" +
                "                </BANKACCTFROM>\n" +
                "                <BANKTRANLIST>\n" +
                "                    <DTSTART>20180108112911[-3:GMT]\n" +
                "                    <DTEND>20180108112911[-3:GMT]\n" +
                "                    <STMTTRN>\n" +
                "                        <TRNTYPE>OTHER\n" +
                "                        <DTPOSTED>20180105000000[-3:GMT]\n" +
                "                        <TRNAMT>           1690,70\n" +
                "                        <FITID>00020801\n" +
                "                        <CHECKNUM>00020801\n" +
                "                        <PAYEEID>0\n" +
                "                        <MEMO>TED DIFERENTE TITULARIDADE CIP     048.796.089-04           \n" +
                "                    </STMTTRN>\n" +
                "                    <STMTTRN>\n" +
                "                        <TRNTYPE>OTHER\n" +
                "                        <DTPOSTED>20180105000000[-3:GMT]\n" +
                "                        <TRNAMT>             16,90\n" +
                "                        <FITID>00093938\n" +
                "                        <CHECKNUM>00093938\n" +
                "                        <PAYEEID>0\n" +
                "                        <MEMO>DOC E RECEBIDO-TIT DISTINTA        093.120.939-08           \n" +
                "                    </STMTTRN>\n" +
                "                </BANKTRANLIST>\n" +
                "                <LEDGERBAL>\n" +
                "                    <BALAMT>          88485,41\n" +
                "                    <DTASOF>20180108112911[-3:GMT]\n" +
                "                </LEDGERBAL>\n" +
                "            </STMTRS>\n" +
                "        </STMTTRNRS>\n" +
                "    </BANKMSGSRSV1>\n" +
                "</OFX>";

        Statement statement = StatementParser.parseStatement(new ByteArrayInputStream(data.getBytes()));

        assertNotNull(statement);
        assertEquals(2, statement.getTransactions().size());

        assertEquals(LocalDate.of(2018, 1, 8), statement.getOfxDate());

        Transaction txn1 = statement.getTransactions().get(0);
        System.out.println(txn1);
        assertEquals("OTHER", txn1.getType());
        assertEquals(LocalDate.of(2018, 1, 5), txn1.getDate());
        assertEquals(new BigDecimal("1690.70"), txn1.getAmount());
        assertEquals("00020801", txn1.getCode());
        assertEquals("TED DIFERENTE TITULARIDADE CIP     048.796.089-04", txn1.getDescription());
        assertNotNull(txn1.getId());

        Transaction txn2 = statement.getTransactions().get(1);
        assertEquals("OTHER", txn2.getType());
        assertEquals(LocalDate.of(2018, 1, 5), txn2.getDate());
        assertEquals(new BigDecimal("16.90"), txn2.getAmount());
        assertEquals("00093938", txn2.getCode());
        assertEquals("DOC E RECEBIDO-TIT DISTINTA        093.120.939-08", txn2.getDescription());
        assertNotNull(txn2.getId());
    }

}