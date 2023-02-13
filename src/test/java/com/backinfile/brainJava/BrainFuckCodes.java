package com.backinfile.brainJava;

import java.util.List;

public class BrainFuckCodes {
    public static final List<List<String>> codeLists = List.of(
            // write all input text
            List.of(",.,.,.,.,.",
                    "hello", "hello"),

            // toUpperCase
            List.of(",----------[----------------------.,----------]",
                    "hello\n", "HELLO"),

            // sum
            List.of(",>++++++[<-------->-],,[<+>-],<.>.",
                    "1\n2\n", "3\n"),

            // print hello world
            List.of("++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.",
                    "", "Hello World!\n")
    );
}
