package org.example.utils;
import org.example.Main;
import java.nio.file.Path;
import java.util.List;
import java.nio.file.Files;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtils {
    public static void processFile() {
        try {
            Path filePath = Path.of(Main.filePath);
            List<String> allLines = Files.readAllLines(filePath);
            List<String> firstElements = new ArrayList<>();

            for (String line : allLines) {
                String[] parts = line.split(";");
                System.out.println(parts[0]);
                firstElements.add(parts[0]);
            }
            System.out.println("---");
            System.out.println(firstElements);

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}