package com.backinfile.brainJava;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JavaInterpreter {
    public static Pattern CODE_PATTERN = Pattern.compile("^[.,<>+\\-\\[\\]]+$");

    public static JavaInterpreter parse(String code) {
//        if (!CODE_PATTERN.matcher(code).matches()) {
//            throw new RuntimeException("parse error");
//        }

        var interpreter = new JavaInterpreter();
        interpreter.code = code;
        interpreter.jumpTable = parseJumpTable(code);
        return interpreter;
    }

    private static int[] parseJumpTable(String code) {
        Stack<Integer> indexStack = new Stack<>();
        int[] jumpTable = new int[code.length()];
        for (int i = 0; i < code.length(); i++) {
            switch (code.charAt(i)) {
                case '[':
                    indexStack.push(i);
                    break;
                case ']':
                    if (indexStack.empty()) {
                        throw new RuntimeException("miss match of ], index=" + i);
                    }
                    var beforeIndex = indexStack.pop();
                    jumpTable[i] = beforeIndex;
                    jumpTable[beforeIndex] = i;
                    break;
            }
        }

        if (!indexStack.empty()) {
            throw new RuntimeException("miss match of [, index=" + code.length());
        }
        return jumpTable;
    }

    private String code;
    private int[] jumpTable;

    private JavaInterpreter() {
    }

    public void exec() {
        this.exec(System.in, System.out);
    }

    public void exec(InputStream inputStream, OutputStream outputStream) {
        try {
            execCode(inputStream, outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void execCode(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] stack = new byte[256];
        int pointer = 0;

        for (int i = 0; i < code.length(); i++) {
            switch (code.charAt(i)) {
                case '.':
                    outputStream.write(stack[pointer]);
                    break;
                case ',':
                    stack[pointer] = (byte) inputStream.read();
                    break;
                case '>':
                    pointer++;
                    break;
                case '<':
                    pointer--;
                    break;
                case '+':
                    stack[pointer]++;
                    break;
                case '-':
                    stack[pointer]--;
                    break;
                case '[':
                    if (stack[pointer] == 0) {
                        i = jumpTable[i];
                    }
                    break;
                case ']':
                    if (stack[pointer] != 0) {
                        i = jumpTable[i];
                    }
                    break;
            }
        }
        outputStream.flush();
    }

    public static void main(String[] args) throws IOException {
        JavaInterpreter.parse("++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.").exec();
    }
}
