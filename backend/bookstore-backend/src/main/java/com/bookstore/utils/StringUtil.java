package com.bookstore.utils;

public class StringUtil {
    public static void printNumber(String name){
        System.out.println("The length of name is: " + name.length());
    }


    public static int getMaxIndex(String data){
        if(data == null) {
            return -1;
        }

        return data.length();
    }
}
