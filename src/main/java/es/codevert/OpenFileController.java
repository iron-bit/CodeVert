package es.codevert;

import java.io.IOException;

public class OpenFileController {

    private static final String APP = "subl";


    public static void openFile(String filePath) {
        ProcessBuilder pb = new ProcessBuilder(APP, filePath);

        try {
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
