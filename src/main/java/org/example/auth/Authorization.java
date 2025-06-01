package org.example.auth;

import org.example.Main;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Authorization {
    private static boolean addUser = false;
    private static boolean wrongPass = false;
    private static boolean login = false;

    private static void readFile(String username, String password) {
        try {
            Path filePath = Path.of(Main.filePath);
            List<String> lines = Files.readAllLines(filePath);
            String[][] matrix = new String[lines.size()][];

            boolean userFound = false;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i) == null) continue;

                matrix[i] = lines.get(i).split(";");
                if (matrix[i].length == 0) continue;

                if (matrix[i][0].equalsIgnoreCase(username)) {
                    userFound = true;

                    if (matrix[i].length > 1 && Objects.equals(password, matrix[i][1])) {
                        login = true;
                    } else {
                        wrongPass = true;
                    }
                    break;
                }
            }

            if (!userFound) {
                addUser = true;
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            addUser = true;
        }
    }

    public static boolean userLogin(String username, String password) {
        resetStates();

        if (username == null || username.isBlank() || password == null) {
            addUser = true;
            return false;
        }

        readFile(username, password);

        if (addUser) {
            System.out.println("User not found! Please add user!");
        } else if (login) {
            System.out.println("User authorized successfully!");
        } else if (wrongPass) {
            System.out.println("Wrong password! Please try again!");
        }

        return login;
    }

    private static void resetStates() {
        addUser = false;
        wrongPass = false;
        login = false;
    }
}

// метод регистрации пользователя (логин и пароль)

// метод автогизации пользователь (логин и пароль)

