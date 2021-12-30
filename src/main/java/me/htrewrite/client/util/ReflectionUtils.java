package me.htrewrite.client.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static void setDeclaredField(Class<?> clazz, Object instance, String name, Object value) {
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception exception) { exception.printStackTrace(); }
    }

    public static Object getObjectDeclaredField(Class<?> clazz, Object instance, String name) {
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(instance);
        } catch (Exception exception) { exception.printStackTrace(); }

        return null;
    }

    public static boolean getBooleanDeclaredField(Class<?> clazz, Object instance, String name) {
        Object o = getObjectDeclaredField(clazz, instance, name);
        return o!=null?(boolean)o:false;
    }

    public static Double getDoubleDeclaredField(Class<?> clazz, Object instance, String name) {
        Object o = getObjectDeclaredField(clazz, instance, name);
        return o!=null?(Double)o:-1D;
    }

    public static int getIntDeclaredField(Class<?> clazz, Object instance, String name) {
        Object o = getObjectDeclaredField(clazz, instance, name);
        return o!=null?(int)o:-1;
    }

    public static float getFloatDeclaredField(Class<?> clazz, Object instance, String name) {
        Object o = getObjectDeclaredField(clazz, instance, name);
        return o!=null?(float)o:-1;
    }
}