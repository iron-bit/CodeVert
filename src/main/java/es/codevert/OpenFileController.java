package es.codevert;

import java.io.IOException;

public class OpenFileController {

    private static final String APP = "notepad";


    public static void openFile(String filePath) {
        ProcessBuilder pb = new ProcessBuilder(APP, filePath);

        try {
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
