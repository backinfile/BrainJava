package com.backinfile.brainJava;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class JavaByteCodeInterpreterTest {

    @Test
    void parse() {
        for (var list : BrainFuckCodes.codeLists) {
            assert list.size() == 3;
            String code = list.get(0);
            String input = list.get(1);
            String output = list.get(2);

            var outputStream = new ByteArrayOutputStream(1024);
            JavaByteCodeInterpreter.exec(code, new ByteArrayInputStream(input.getBytes()), outputStream);
            assert output.equals(outputStream.toString());
        }
    }
}