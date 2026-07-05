package dev.skidfuscator.test.util;

import dev.skidfuscator.obfuscator.util.ClassUtil;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassUtilJavaVersionTest {
    @Test
    public void supportsJava25ClassFiles() {
        assertSupportedClassVersion(Opcodes.V25, "Java25Sample");
    }

    @Test
    public void supportsJava26ClassFiles() {
        assertSupportedClassVersion(Opcodes.V26, "Java26Sample");
    }

    private static void assertSupportedClassVersion(int version, String name) {
        byte[] bytes = createClass(version, name);

        assertTrue(ClassUtil.isValidClass(bytes));
        assertEquals(version, ClassUtil.getVersion(bytes));

        ClassNode node = ClassUtil.getNode(new ClassReader(bytes), 0);
        assertEquals(version, node.version);
        assertEquals(version, ClassUtil.getVersion(ClassUtil.toCode(node, 0)));
    }

    private static byte[] createClass(int version, String name) {
        ClassWriter writer = new ClassWriter(0);
        writer.visit(
                version,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
                "dev/skidfuscator/testclasses/version/" + name,
                null,
                "java/lang/Object",
                null
        );

        MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(1, 1);
        init.visitEnd();

        writer.visitEnd();
        return writer.toByteArray();
    }
}