# ofx-parser
OFX Lexer and Parser

### Usage

#### Add gradle dependency:
```
repositories {
    maven {
        url  "https://dl.bintray.com/jonhkr/maven" 
    }
}

dependencies {
    compile 'com.ebanx:ofx-parser:1.0.0-RELEASE'
}
```

#### Using the lexer

The lexer ca be used to build custom parsers, the following is an example of how it works:

```Java

String data = "<TAG1>Content</TAG1><TAG2>Content";

OfxLexer lexer = new OfxLexerImpl(new ByteArrayInputStream(data.getBytes()));

Token token;

while ((token = lexer.nextToken()) != null) {
     // do something...
}

```

#### Using the statement parser

The statement parser can be used to parse the transactions of a statement from an OFX file.

This is an example of usage:

```Java
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

statement.getTransactions().forEach(txn -> {
    // do something
});

```