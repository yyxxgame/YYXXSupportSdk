package cn.yyxx.support;

import java.lang.reflect.Method;

/**
 * @author #Suyghur.
 * Created on 2020/7/8
 */
public class ReflectUtils {


    public static Object callInstanceMethod(Object instance, String methodName, Class<?>[] types, Object[] valuse) {
        return callMethod(instance, methodName, types, valuse);
    }

    /**
     * 调用该对象所有可调用的公有方法,包括父类方法
     *
     * @param obj        调用者对象
     * @param methodName 调用的方法名，与obj合在一起即为 obj.methodName
     * @param types      调用方法的参数类型
     * @param values     调用方法的参数值
     * @return methodName所返回的对象
     */
    public static Object callMethod(Object obj, String methodName, Class<?>[] types, Object[] values) {
        // 注：数组类型为:基本类型+[].class,如String[]写成 new Class<?>[]{String[].class}
        if (obj == null) {
            return null;
        }
        Class<?> classz = obj.getClass();
        Method method = null;
        Object retValue = null;
        try {
            method = classz.getMethod(methodName, types);
            retValue = method.invoke(obj, values);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * 调用该类的静态方法，包括静态方法
     *
     * @param clz        类类对象
     * @param methodName 调用的方法名，与obj合在一起即为 obj.methodName
     * @param types      调用方法的参数类型,数组类型为:基本类型+[].class,如String[]写成
     *                   new Class<?>[]{String[].class},int 类型是为int.class
     * @param values     调用方法的参数值
     * @return methodName所返回的对象
     */
    public static Object callStaticMethod(Class<?> clz, String methodName, Class<?>[] types, Object[] values) {
        Method method = null;
        Object retValue = null;
        try {
            method = clz.getDeclaredMethod(methodName, types);
            method.setAccessible(true);// 设置安全检查，设为true使得可以访问私有方法
            retValue = method.invoke(null, values);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * 调用该类的静态方法,包括静态方法
     *
     * @param className  调用的方法名，与obj合在一起即为 obj.methodname
     * @param methodName 调用的方法名，与obj合在一起即为 obj.methodname
     * @param types      调用方法的参数类型,数组类型为:基本类型+[].class,如String[]写成
     *                   new Class<?>[]{String[].class},int 类型是为int.class
     * @param values     调用方法的参数值
     * @return
     */
    public static Object callStaticMethod(String className, String methodName, Class<?>[] types, Object[] values) {
        Class<?> clz;
        try {
            clz = Class.forName(className);
            return callStaticMethod(clz, methodName, types, values);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
