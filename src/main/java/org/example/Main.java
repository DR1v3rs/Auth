package org.example;

import org.example.auth.Authorization;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static String filePath = "C:\\Users\\DikushinAS\\IdeaProjects\\Auth_1.0\\Users.txt";

    public static void main(String[] args) {
        Authorization.userLogin("user05", "Pass01");
    }
}


//