package com.upitis.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by upitis on 13.04.16.
 */
public class ConsoleHelper {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message){
        System.out.println(message);
    }

    public static String readString(){
        while (true){
            try {
                return bufferedReader.readLine();
            } catch (IOException e) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
                continue;
            }
        }
    }

    public static int readInt(){
        while (true) {
            try {
                return Integer.valueOf(readString());
            } catch (NumberFormatException ex) {
                System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
                continue;
            }
        }
    }
}
