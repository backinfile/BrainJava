package com.backinfile.brainJava;


import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Stack;

import static org.objectweb.asm.Opcodes.*;

public class JavaByteCodeInterpreter {
    private static final String CLASS_NAME = "BrainJavaRunner";


    public static void main(String[] args) throws IOException {
        var bytes = getByteCodes(",.,.,.", CLASS_NAME);
        Files.write(Path.of("D://test.class"), bytes);
    }

    public static byte[] getByteCodes(String brainFuckCode, String javaClassName) throws IOException {
        var classReader = new ClassReader("com.backinfile.brainJava.InterpreterTemplate");
        var classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = new ByteCodeClassVisitor(ASM5, classWriter, javaClassName, brainFuckCode);
        classReader.accept(visitor, 0);
        return classWriter.toByteArray();
    }

    public static void execBytes(byte[] bytes, String javaClassName, InputStream inputStream, OutputStream outputStream) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var clazz = BrainFuckClassLoader.Instance.defineClassFromClassFile(javaClassName, bytes);
        var exec = clazz.getDeclaredMethod("exec", InputStream.class, OutputStream.class);
        exec.invoke(null, inputStream, outputStream);
    }

    public static void exec(String brainFuckCode, InputStream inputStream, OutputStream outputStream) {
        try {
            String className = CLASS_NAME + "_" + getRandomName();
            execBytes(getByteCodes(brainFuckCode, className), className, inputStream, outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getRandomName() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private static class BrainFuckClassLoader extends ClassLoader {
        public static final BrainFuckClassLoader Instance = new BrainFuckClassLoader();

        public Class<?> defineClassFromClassFile(String className, byte[] classFile) throws ClassFormatError {
            return defineClass(className, classFile, 0, classFile.length);
        }
    }

    private static class ByteCodeClassVisitor extends ClassVisitor {
        private final String className;
        private final String code;

        protected ByteCodeClassVisitor(int api, ClassVisitor classVisitor, String className, String code) {
            super(api, classVisitor);
            this.className = className;
            this.code = code;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if (name.equals("exec")) {
                return new ExecMethodVisitor(ASM5, super.visitMethod(access, name, descriptor, signature, exceptions), code);
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, className, signature, superName, interfaces);
        }
    }

    private static class ExecMethodVisitor extends MethodVisitor {
        private final MethodVisitor target;
        private final String code;

        protected ExecMethodVisitor(int api, MethodVisitor target, String code) {
            super(api, null);
            this.target = target;
            this.code = code;
        }

        @Override
        public void visitCode() {
//            target.visitVarInsn(ALOAD, 1);
//            target.visitLdcInsn("in visitor");
//            target.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "()[B", false);
//            target.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V", false);
//
//            target.visitVarInsn(ALOAD, 1);
//            target.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "flush", "()V", false);


            // 0-OutputStream out
            // 1-InputStream in
            // 2-byte[] stack
            // 3-int cur

            // byte[] stack = new byte[256];
            target.visitIntInsn(SIPUSH, 256);
            target.visitIntInsn(NEWARRAY, T_BYTE);
            target.visitVarInsn(ASTORE, 2);

            // int cur = 0;
            target.visitInsn(ICONST_0);
            target.visitVarInsn(ISTORE, 3);


            Stack<Label> firstLabelStack = new Stack<>();
            Stack<Label> secondLabelStack = new Stack<>();

            for (int i = 0; i < code.length(); i++) {
                switch (code.charAt(i)) {
                    case '.': {
                        // out.write(stack[cur]);
                        target.visitVarInsn(ALOAD, 1);
                        target.visitVarInsn(ALOAD, 2);
                        target.visitVarInsn(ILOAD, 3);
                        target.visitInsn(BALOAD);
                        target.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "(I)V", false);
                        break;
                    }
                    case ',': {
                        // stack[cur] = (byte) in.read();
                        target.visitVarInsn(ALOAD, 2);
                        target.visitVarInsn(ILOAD, 3);
                        target.visitVarInsn(ALOAD, 0);
                        target.visitMethodInsn(INVOKEVIRTUAL, "java/io/InputStream", "read", "()I", false);
                        target.visitInsn(I2B);
                        target.visitInsn(BASTORE);
                        break;
                    }
                    case '>': {
                        // cur += 1
                        target.visitIincInsn(3, 1);
                        break;
                    }
                    case '<': {
                        // cur -= 1
                        target.visitIincInsn(3, -1);
                        break;
                    }
                    case '+': {
                        // stack[cur] += 1
                        target.visitVarInsn(ALOAD, 2);
                        target.visitVarInsn(ILOAD, 3);
                        target.visitInsn(DUP2);
                        target.visitInsn(BALOAD);
                        target.visitInsn(ICONST_1);
                        target.visitInsn(IADD);
                        target.visitInsn(I2B);
                        target.visitInsn(BASTORE);
                        break;
                    }
                    case '-': {
                        // stack[cur] -= 1
                        target.visitVarInsn(ALOAD, 2);
                        target.visitVarInsn(ILOAD, 3);
                        target.visitInsn(DUP2);
                        target.visitInsn(BALOAD);
                        target.visitInsn(ICONST_1);
                        target.visitInsn(ISUB);
                        target.visitInsn(I2B);
                        target.visitInsn(BASTORE);
                        break;
                    }
                    case '[': {
                        Label second = new Label();
                        secondLabelStack.add(second);

                        // if (stack[cur] == 0) jump to :second
                        target.visitVarInsn(ALOAD, 2);
                        target.visitVarInsn(ILOAD, 3);
                        target.visitInsn(BALOAD);
                        target.visitInsn(ICONST_0);
                        target.visitJumpInsn(IF_ICMPEQ, second);

                        // first:
                        Label first = new Label();
                        firstLabelStack.push(first);
                        target.visitLabel(first);

                        break;
                    }
                    case ']': {
                        if (firstLabelStack.empty()) {
                            throw new RuntimeException("miss match of ], index=" + i);
                        }
                        var first = firstLabelStack.pop();
                        var second = secondLabelStack.pop();


                        // if (stack[cur] != 0) jump to :first
                        target.visitVarInsn(ALOAD, 2);
                        target.visitVarInsn(ILOAD, 3);
                        target.visitInsn(BALOAD);
                        target.visitInsn(ICONST_0);
                        target.visitJumpInsn(IF_ICMPNE, first);

                        // second:
                        target.visitLabel(second);
                        break;
                    }
                }
            }
            if (!firstLabelStack.empty()) {
                throw new RuntimeException("miss match of [, index=" + code.length());
            }

            // out.flush();
            target.visitVarInsn(ALOAD, 1);
            target.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "flush", "()V", false);

            // return;
            target.visitInsn(RETURN);
            target.visitMaxs(4, 4);
            target.visitEnd();
        }
    }
}

