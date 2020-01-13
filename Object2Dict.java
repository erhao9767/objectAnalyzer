import java.util.Arrays;
import java.util.ArrayList;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.AccessibleObject;


public final class Object2Dict {

    private static ArrayList<Object> visited = new ArrayList<>();

    private final static String[] wrapType = {"java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double",
            "java.lang.Short", "java.lang.String", "java.lang.Byte", "java.lang.Character",
            "java.lang.Void", "java.lang.Boolean"};

    private static String startSymbol = "[";
    private static String endSymbol = "]";
    private static String allotSymbol = "=";
    private static String delimiter = ", ";


    /**
     * 将对象所有字段以字符串展示
     *
     * @param obj 任意对象
     * @return String 传入对象的所有字段
     */
    static String toString(Object obj) {
        if (obj == null) {
            return "null"; }

        Class objClass = obj.getClass();
        String objClassName = objClass.getName();

        if (Arrays.asList(wrapType).contains(objClassName)) {
            return obj.toString();}
        if (objClass.isArray()) {
            return handleArray(obj);}

        String echoObj = searchClassField(obj);
        return formatRet(echoObj);
    }

    private static String searchClassField(Object obj) {
        Class objClass = obj.getClass();
        StringBuilder ret = new StringBuilder();

        ret.append(objClass.getName());
        while (objClass != null) {
            ret.append(startSymbol);
            Field[] fieldArray = objClass.getDeclaredFields();
            AccessibleObject.setAccessible(fieldArray, true);

            for (Field field : fieldArray) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    if (!ret.toString().endsWith(startSymbol)) {
                        ret.append(delimiter);}
                    ret.append(field.getName()).append(allotSymbol);
                    try {
                        Class fieldType = field.getType();
                        Object fieldVal = field.get(obj);
                        if (fieldType.isPrimitive()) {
                            ret.append(fieldVal);}
                        else {
                            ret.append(toString(fieldVal)); }
                    } catch (Exception e) {
                        e.printStackTrace();}
                }
            }
            ret.append(endSymbol);
            objClass = objClass.getSuperclass();
        }
        return ret.toString();
    }

    private static String formatRet(String ret) {
        return ret.replace("[]", "")
                .replace("][", ", ");
    }

    private static String handleArray(Object obj) {
        Class objClass = obj.getClass();
        StringBuilder ret = new StringBuilder(String.format("%s[%s]{", objClass.getComponentType().toString(),
                Array.getLength(obj)));

        for (int i = 0; i < Array.getLength(obj); i++) {
            if (i > 0) {
                ret.append(delimiter);}
            Object val = Array.get(obj, i);
            if (objClass.getComponentType().isPrimitive()) {
                ret.append(val);}
            else {
                ret.append(toString(val));}
        }
        return ret.append(endSymbol).toString();
    }
}
