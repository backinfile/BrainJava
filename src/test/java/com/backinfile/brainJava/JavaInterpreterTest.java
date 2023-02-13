package com.backinfile.brainJava;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JavaInterpreterTest {

    @Test
    void pattern() {
        assert JavaInterpreter.CODE_PATTERN.matcher(",.[]+-<>").matches();
        assert !JavaInterpreter.CODE_PATTERN.matcher(",.[]+-<>aa").matches();
    }

    @Test
    void parse() {
        for (var list : BrainFuckCodes.codeLists) {
            assert list.size() == 3;
            String code = list.get(0);
            String input = list.get(1);
            String output = list.get(2);

            var interpreter = JavaInterpreter.parse(code);
            var outputStream = new ByteArrayOutputStream(1024);
            interpreter.exec(new ByteArrayInputStream(input.getBytes()), outputStream);
            assert output.equals(outputStream.toString());
        }
    }
}