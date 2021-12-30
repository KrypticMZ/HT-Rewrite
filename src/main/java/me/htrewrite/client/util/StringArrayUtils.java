package me.htrewrite.client.util;

public class StringArrayUtils {
    public static boolean contains(String[] array, String string, boolean ignoreCase) {
        for(String element : array)
            if(ignoreCase?element.equalsIgnoreCase(string):element.contentEquals(string))
                return true;
        return false;
    }

    public static String[] addElement(String[] array, String string) {
        String newArray[] = new String[array.length+1];
        for(int i = 0; i < array.length; i++)
            newArray[i] = array[i];
        newArray[array.length] = string;

        return newArray;
    }
    
    public static String[] removeElement(String[] array, String string, boolean ignoreCase) {
        String newArray[] = new String[array.length-1];
        for(int i = 0, k = 0; i < array.length; i++)
            if((ignoreCase?array[i].equalsIgnoreCase(string):array[i].contentEquals(string)))
                newArray[k++] = array[i];
        return newArray;
    }
}