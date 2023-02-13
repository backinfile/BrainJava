package com.backinfile.brainJava;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InterpreterTemplate {
    public static void main(String[] args) throws IOException {
        exec(System.in, System.out);
        System.out.println();
    }

    public static void exec(InputStream in, OutputStream out) throws IOException {
        byte[] stack = new byte[10];
        stack[0] += 1;
        stack[0] -= 1;
        throw new RuntimeException("not implement");
    }
}
