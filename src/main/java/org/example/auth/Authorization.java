package org.example.auth;

import org.example.Main;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Authorization {
    private static boolean addUser = false;   //если True то запрос на добавление пользователя
    private static boolean wrongPass = false; //если True то пароль неправильный
    private static boolean login = false;     //если True то всё хорошо

    private static void readFile(String user, String pass) {
        try {
            Path filePath = Path.of(Main.filePath);
            List<String> lines = Files.readAllLines(filePath);
            String[][] matrix = new String[lines.size()][];

            for (int i = 0; i < lines.size(); i++) {
                matrix[i] = lines.get(i).split(";");
                if (lines.get(i) != null && matrix[i].length > 0 && matrix[i][0].equalsIgnoreCase(user)) {
                    if (lines.get(i) != null && matrix[i].length > 0 && matrix[i][1].equals(pass)) {
                        login = true;
                        break;
                    } else {
                        wrongPass = true;
                        }
                    break;
                } else {
                    addUser = true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error read file: " + e.getMessage());
        }
    }

    public static boolean userLogin(String user, String pass) {
        readFile(user, pass);
        if (addUser) {
            System.out.println("User not found! Please add user!");
        } else if (login) {
            System.out.println("User authorizated!!!");
        } else if (wrongPass) {
            System.out.println("Wrong password! Please try again!");
        }

        return addUser;
    }
}



//public class Authorization {
//    public static boolean checkLogin(String login) {
//        String[][] credentials = FileUtils.getCredentials();

//        for (String[] row : credentials) {
//            System.out.println(Arrays.toString(row));
//            if (row.length > 0 && row[0].equals(login)) {
//                //System.out.println(credentials.toString(row));
//                return true; // логин найден в первом столбце
//            }
//        }
//        return false; // логин не найден
//    }
//}

// метод регистрации пользователя (логин и пароль)

// метод автогизации пользователь (логин и пароль)

