package com.example.dan_p.nonogrammaker.utils;


public class Utils {

    public static double generateRandom(int min , int max) {
        int range = (max - min) + 1;
        return (Math.random() * range) + min;
    }

    public static int[] parseStringToIntegerArray(String string) {
        int[] integerArray = new int[string.length()];
        char[] charArray = string.toCharArray();

        for (int i = 0 ; i < integerArray.length ; i++)
            integerArray[i] = Integer.parseInt(String.valueOf(charArray[i]));

        return integerArray;
    }

    public static String integerArrayToString(int[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int a : array)
            stringBuilder.append(a);
        return stringBuilder.toString();
    }

    public static String replaceChar(String str, int index, char replace){
        if(str==null){
            return str;
        }else if(index<0 || index>=str.length()){
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }
}
