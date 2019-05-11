package me.victor.npaw.utils;

import java.util.UUID;

public class ViewCodeUtility {



    public static String generateViewCode(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
