package es.codevert;

import java.io.IOException;

public class OpenFileController {

    // Application to open files
    private static final String APP = "notepad";

    public static void openFile(String filePath) {
        ProcessBuilder pb = new ProcessBuilder(APP, filePath); // Create a process to open the file

        try {
            pb.start(); // Start the process
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
