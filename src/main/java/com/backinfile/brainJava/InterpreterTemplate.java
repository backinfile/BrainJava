package com.backinfile.brainJava;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InterpreterTemplate {
    public static void main(String[] args) throws IOException {
        exec(System.in, System.out);
    }

    public static void exec(InputStream in, OutputStream out) throws IOException {
        throw new RuntimeException("not implement");
    }
}
