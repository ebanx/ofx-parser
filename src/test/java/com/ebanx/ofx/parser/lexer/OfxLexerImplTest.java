package com.ebanx.ofx.parser.lexer;

import com.ebanx.ofx.parser.OfxParseException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class OfxLexerImplTest {

    @Test
    public void textTest() throws IOException {

        String data = "HELLO:WORLD\n" +
                "HEADER:OFX_SUCKS\n" +
                "HEY YOU MY FRIEND 10 20 30\n" +
                "       ";

        OfxLexer lexer = new OfxLexerImpl(new ByteArrayInputStream(data.getBytes()));

        Token token1 = lexer.nextToken();
        assertEquals(Text.class, token1.getClass());
        assertEquals("HELLO:WORLD", ((Text) token1).getContent());

        Token token2 = lexer.nextToken();
        assertEquals(Text.class, token2.getClass());
        assertEquals("HEADER:OFX_SUCKS", ((Text) token2).getContent());

        Token token3 = lexer.nextToken();
        assertEquals(Text.class, token3.getClass());
        assertEquals("HEY YOU MY FRIEND 10 20 30", ((Text) token3).getContent());

        Token token4 = lexer.nextToken();
        assertEquals(Text.class, token4.getClass());
        assertEquals("       ", ((Text) token4).getContent());

        assertNull(lexer.nextToken());
    }

    @Test
    public void openTagTest() throws IOException {

        String data = "<HELLO>\n" +
                "<STMTRAN>\n" +
                "<     >\n";

        OfxLexer lexer = new OfxLexerImpl(new ByteArrayInputStream(data.getBytes()));

        Token token1 = lexer.nextToken();
        assertEquals(OpenTag.class, token1.getClass());
        assertEquals("HELLO", ((OpenTag) token1).getTagName());

        Token token2 = lexer.nextToken();
        assertEquals(OpenTag.class, token2.getClass());
        assertEquals("STMTRAN", ((OpenTag) token2).getTagName());

        Token token3 = lexer.nextToken();
        assertEquals(OpenTag.class, token3.getClass());
        assertEquals("     ", ((OpenTag) token3).getTagName());

        assertNull(lexer.nextToken());
    }

    @Test
    public void closeTagTest() throws IOException {

        String data = "</HELLO>\n" +
                "</STMTRAN>\n" +
                "</     >\n";

        OfxLexer lexer = new OfxLexerImpl(new ByteArrayInputStream(data.getBytes()));

        Token token1 = lexer.nextToken();
        assertEquals(CloseTag.class, token1.getClass());
        assertEquals("HELLO", ((CloseTag) token1).getTagName());

        Token token2 = lexer.nextToken();
        assertEquals(CloseTag.class, token2.getClass());
        assertEquals("STMTRAN", ((CloseTag) token2).getTagName());

        Token token3 = lexer.nextToken();
        assertEquals(CloseTag.class, token3.getClass());
        assertEquals("     ", ((CloseTag) token3).getTagName());

        assertNull(lexer.nextToken());
    }

    @Test
    public void openTagWithContentTest() throws IOException {

        String data = "<HELLO>\n" +
                "asd bla bla<STMTRAN>\n" +
                "<     >hellooou\n";

        OfxLexer lexer = new OfxLexerImpl(new ByteArrayInputStream(data.getBytes()));

        Token token1 = lexer.nextToken();
        assertEquals(OpenTag.class, token1.getClass());
        assertEquals("HELLO", ((OpenTag) token1).getTagName());

        Token token2 = lexer.nextToken();
        assertEquals(Text.class, token2.getClass());
        assertEquals("asd bla bla", ((Text) token2).getContent());

        Token token3 = lexer.nextToken();
        assertEquals(OpenTag.class, token3.getClass());
        assertEquals("STMTRAN", ((OpenTag) token3).getTagName());

        Token token4 = lexer.nextToken();
        assertEquals(OpenTag.class, token4.getClass());
        assertEquals("     ", ((OpenTag) token4).getTagName());

        Token token5 = lexer.nextToken();
        assertEquals(Text.class, token5.getClass());
        assertEquals("hellooou", ((Text) token5).getContent());

        assertNull(lexer.nextToken());
    }

    @Test
    public void failOnUnclosedTagTest() throws IOException {
        String data = "<HELLO";

        OfxLexer lexer = new OfxLexerImpl(new ByteArrayInputStream(data.getBytes()));

        try {
            lexer.nextToken();
            fail();
        } catch (OfxParseException e) {
            assertEquals("Unclosed tag: <HELLO", e.getMessage());
        }
    }

    @Test
    public void skipEmptyLineTest() throws IOException {

        String data = "<HELLO>\n" +
                "asd bla bla<STMTRAN>\n" +
                "\n" +
                "<     >hellooou\n";

        OfxLexer lexer = new OfxLexerImpl(new ByteArrayInputStream(data.getBytes()));

        Token token1 = lexer.nextToken();
        assertEquals(OpenTag.class, token1.getClass());
        assertEquals("HELLO", ((OpenTag) token1).getTagName());

        Token token2 = lexer.nextToken();
        assertEquals(Text.class, token2.getClass());
        assertEquals("asd bla bla", ((Text) token2).getContent());

        Token token3 = lexer.nextToken();
        assertEquals(OpenTag.class, token3.getClass());
        assertEquals("STMTRAN", ((OpenTag) token3).getTagName());

        Token token4 = lexer.nextToken();
        assertEquals(OpenTag.class, token4.getClass());
        assertEquals("     ", ((OpenTag) token4).getTagName());

        Token token5 = lexer.nextToken();
        assertEquals(Text.class, token5.getClass());
        assertEquals("hellooou", ((Text) token5).getContent());

        assertNull(lexer.nextToken());
    }
}