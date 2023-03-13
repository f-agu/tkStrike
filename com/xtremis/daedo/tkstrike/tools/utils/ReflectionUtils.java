package com.xtremis.daedo.tkstrike.tools.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;

public final class ReflectionUtils {
  private static final String NESTED_PROPERTY_SEPARATOR = ".";
  
  public static BeanUtilsBean getBeanUtilsBean() {
    return beanUtilsBean;
  }
  
  private static BeanUtilsBean beanUtilsBean = new BeanUtilsBean() {
    
    };
  
  public static <T, U> List<U> copyProperties(List<T> source, Class<U> destType) {
    List<U> dest = new ArrayList<>();
    try {
      if (source != null) {
        for (T element : source) {
          if (element == null)
            continue; 
          U destElem = destType.newInstance();
          getBeanUtilsBean().copyProperties(destElem, element);
          dest.add(destElem);
        } 
        List<U> s1 = new ArrayList<>();
        s1.add(null);
        dest.removeAll(s1);
      } 
    } catch (InvocationTargetException|InstantiationException|IllegalAccessException e) {
      throw new RuntimeException(e);
    } 
    return dest;
  }
  
  public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
    fields.addAll(Arrays.asList(type.getDeclaredFields()));
    if (type.getSuperclass() != null)
      fields = getAllFields(fields, type.getSuperclass()); 
    return fields;
  }
  
  public static Class<?> getClass(Type type) {
    if (type instanceof Class)
      return (Class)type; 
    if (type instanceof ParameterizedType)
      return getClass(((ParameterizedType)type).getRawType()); 
    return null;
  }
  
  public static <T> Class<T> getGenericClass(Class<?> classToInspect) {
    return getGenericClass(classToInspect, 0);
  }
  
  public static <T> Class<T> getGenericClass(Class<?> classToInspect, int numGeneric) {
    Type type = getGenericType(classToInspect, numGeneric);
    return (Class)getClass(type);
  }
  
  public static Type getGenericType(Class<?> classToInspect) {
    return getGenericType(classToInspect, 0);
  }
  
  public static Type getGenericType(Class<?> classToInspect, int numGeneric) {
    return ((ParameterizedType)classToInspect.getGenericSuperclass()).getActualTypeArguments()[numGeneric];
  }
  
  public static Field getPropertyField(Class<?> beanClass, String propertyPath) throws NoSuchFieldException {
    if (beanClass == null)
      throw new IllegalArgumentException("beanClass cannot be null"); 
    if (propertyPath.indexOf("[") != -1)
      propertyPath = propertyPath.substring(0, propertyPath.indexOf("[")); 
    if (propertyPath.indexOf(".") == -1) {
      Field field1 = null;
      try {
        field1 = beanClass.getDeclaredField(propertyPath);
      } catch (NoSuchFieldException e) {
        if (beanClass.getSuperclass() == null)
          throw e; 
        field1 = getPropertyField(beanClass.getSuperclass(), propertyPath);
      } 
      return field1;
    } 
    String propertyName = propertyPath.substring(0, propertyPath.indexOf("."));
    Field field = getPropertyField(beanClass, propertyName);
    return getPropertyField(getTargetType(field), propertyPath.substring(propertyPath.indexOf(".") + 1));
  }
  
  public static Object getPropertyValue(Object bean, String propertyPath) throws IllegalArgumentException, NoSuchFieldException {
    if (bean == null)
      throw new IllegalArgumentException("bean cannot be null"); 
    Field field = getPropertyField(bean.getClass(), propertyPath);
    field.setAccessible(true);
    try {
      return field.get(bean);
    } catch (IllegalAccessException e) {
      return null;
    } 
  }
  
  public static void setAncestorField(Object object, String fieldName, Object managerMock) throws NoSuchFieldException, IllegalAccessException {
    int MAX_PARENT_LEVEL = 5;
    Class<?> ancestorClass = object.getClass().getSuperclass();
    for (int i = 1; i <= 5; i++) {
      try {
        Field field = ancestorClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, managerMock);
        break;
      } catch (NoSuchFieldException ex) {
        ancestorClass = ancestorClass.getSuperclass();
        if (ancestorClass == null || i == 5)
          throw ex; 
      } 
    } 
  }
  
  public static void setPropertyField(Object object, String fieldName, Object managerMock) throws NoSuchFieldException, IllegalAccessException {
    Field field = object.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(object, managerMock);
  }
  
  private static Class<?> getTargetType(Field field) {
    if (field.getGenericType() instanceof ParameterizedType) {
      ParameterizedType type = (ParameterizedType)field.getGenericType();
      if ((type.getActualTypeArguments()).length == 1 && type.getActualTypeArguments()[0] instanceof Class)
        return (Class)type.getActualTypeArguments()[0]; 
    } 
    return field.getType();
  }
}
