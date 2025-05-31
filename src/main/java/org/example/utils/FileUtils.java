
package org.example.utils;
import org.example.Main;
import java.nio.file.Path;
import java.util.List;
import java.nio.file.Files;
import java.io.IOException;

public class FileUtils {
    public static void processFile() {
        try {
            Path filePath = Path.of(Main.filePath);
            List<String> lines = Files.readAllLines(filePath);
            String[][] matrix = new String[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                matrix[i] = lines.get(i).split(";");
            }
            //System.out.println(matrix[0][0] + " | " + matrix[0][1]);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}
