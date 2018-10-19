package picocli.codegen.aot.graalvm;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ReflectionConfigGeneratorTest {
    @Test
    public void testMainStdOut() throws IOException {
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            ReflectionConfigGenerator.main(Example.class.getName());
        } finally {
            System.setOut(old);
        }
        String expected = read("/example-reflect.json");
        expected = expected.replace("\r\n", "\n");
        expected = expected.replace("\n", System.getProperty("line.separator"));

        assertEquals(expected, baos.toString());
    }

    @Test
    public void testMainOutputFile() throws IOException {
        File file = File.createTempFile("picocli-codegen", ".json");

        ReflectionConfigGenerator.main("--output", file.getAbsolutePath(), Example.class.getName());

        String expected = read("/example-reflect.json");
        expected = expected.replace("\r\n", "\n");
        expected = expected.replace("\n", System.getProperty("line.separator"));

        String actual = readAndClose(new FileInputStream(file));

        assertEquals(expected, actual);
    }

    private String read(String resource) throws IOException {
        return readAndClose(getClass().getResourceAsStream(resource));
    }

    private String readAndClose(InputStream in) throws IOException {
        try {
            byte[] buff = new byte[15000];
            int size = in.read(buff);
            return new String(buff, 0, size);
        } finally {
            in.close();
        }
    }
}
