package com.ebanx.ofx.parser.lexer;

import lombok.Data;

@Data
public class CloseTag implements Token {

    private final String tagName;
}
