package com.ebanx.ofx.parser.lexer;

import lombok.Data;

@Data
public class OpenTag implements Token {
    private final String tagName;
}
