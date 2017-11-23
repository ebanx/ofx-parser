package com.ebanx.ofx.parser.lexer;

import lombok.Data;

@Data
public class Text implements Token {

    private final String content;
}
