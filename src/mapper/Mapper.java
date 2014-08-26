package mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import annotation.ClassTarget;
import annotation.FieldName;

public class Mapper {

	final static Logger logger = LoggerFactory.getLogger(Mapper.class);
	
	public static Object format(Object fromObj){
		Class<?> fromClass = fromObj.getClass();
		
		logger.info("Starting mapping: class {}",fromClass);
		
		Class<?> toClass = getTargetClass(fromClass);
		
		Object result;
		try {
			result = toClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			logger.error("Error on create new instance of {}",toClass);
			return null;
		}
		
		Field[] fields =fromClass.getDeclaredFields();

		for(Field fromField: fields){
			String name = getTargetFieldName(fromField);

			Field toField = null;
			try {
				Field[] toClassFields = toClass.getDeclaredFields();
				for(Field tf: toClassFields){
					if(tf.getName().equals(name)){
						toField = tf;
						break;
					}
				}
				if(toField == null){
					logger.error("Not founded {}.{}",toClass,name);
					return null;
				}
				
				if(isMapped(fromField.getType())){
					logger.info("Field is mapped {}.{} -> {}.{}",fromClass,fromField.getName(),
							toClass,toField.getName());
					Object value = getData(fromField,fromObj);
					Object r = format(value);
					boolean setResult = setData(toField, result, r);
					
				} else {
					if(toField.getType() != fromField.getType()){
						logger.error("{} type of field not equals {}",toField.getType(),fromField.getType());
						return null;
					} else {
						Object value = getData(fromField,fromObj);
						boolean setResult = setData(toField,result,value);
						if(value == null){
							logger.warn("Value of {} equals NULL",toField.getName());
						}
						if(!setResult){
							logger.error("Set to {}.{} value = {}",result.getClass(),fromField.getName(),value);
						}
					}
				}
			} catch (SecurityException e) {
				StackTraceElement[] trace = e.getStackTrace();
				for(StackTraceElement ste: trace){
					logger.trace(ste.toString());
				}
				logger.error(e.toString());
				return null;
			}
			
		}

		logger.info("Mapping done: class {}",fromClass);
		return result;
	}
	
	private static boolean setData(Field field, Object obj,Object value){
		logger.info("Set data to {}.{}",obj.getClass(),field.getName());
		boolean result = false;
		if(Modifier.isFinal(field.getModifiers())){
			logger.error("Field {}.{} is final",obj.getClass(),field.getName());
			return false;
		}
		try {
			field.set(obj, value);
			result = true;
		} catch (IllegalAccessException | IllegalArgumentException e) {
			result = setDataUseSetter(field,obj,value);
			if(!result){
				StackTraceElement[] trace = e.getStackTrace();
				for(StackTraceElement ste: trace){
					logger.trace(ste.toString());
				}
				logger.error(e.toString());
			}
		}
		logger.info("Set result {}", result? "SUCCESS": "FAIL");
		return result;
	}
	
	private static boolean setDataUseSetter(Field field, Object obj,Object value) {
		logger.info("Set data (use setter) to {}.{} with value = {}",obj.getClass(),field.getName(),value);
		Class<?> c = obj.getClass();
		String setterName = "set"+field.getName();
		boolean result = false;
		Method[] methods = c.getDeclaredMethods();
		for(Method m: methods){
			if(m.getName().equalsIgnoreCase(setterName)){
				try {
					m.invoke(obj, value);
					result = true;
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					StackTraceElement[] trace = e.getStackTrace();
					for(StackTraceElement ste: trace){
						logger.trace(ste.toString());
					}
					logger.error(e.toString());
				}
			}
		}
		
		return result;
	}
	
	
	private static Object getData(Field field,Object obj){
		logger.info("Get data {}.{}",obj.getClass(),field.getName());
		Object result = null;
		
		try {
			result = field.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			result = getDataUseGetter(field,obj);
			if(result == null){
				StackTraceElement[] trace = e.getStackTrace();
				for(StackTraceElement ste: trace){
					logger.trace(ste.toString());
				}
				logger.error(e.toString());
			}
		}
		
		logger.info("Result = "+result);
		return result;
	}
	
	private static Object getDataUseGetter(Field field,Object obj){
		logger.info("Get data (use getter) from field {}",field.getName());
		Class<?> c = obj.getClass();
		String getterName = "get"+field.getName();
		Method[] methods = c.getDeclaredMethods();
		
		Object result = null; 
		
		for(Method m: methods){
			if(m.getName().equalsIgnoreCase(getterName)){
				try {
					result = m.invoke(obj);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					StackTraceElement[] trace = e.getStackTrace();
					for(StackTraceElement ste: trace){
						logger.trace(ste.toString());
					}
					logger.error(e.toString());
				}
			}
		}
		return result;
	}
	
	private static String getTargetFieldName(Field fromField){
		FieldName fieldName = (FieldName) fromField.getAnnotation(FieldName.class);
		return fieldName != null? fieldName.value(): fromField.getName();
	}

	/**
	 * Check class on mapping to some class
	 * @param Checked class
	 * @return Mapping target class
	 */
	private static boolean isMapped(Class<?> obj){
		Annotation[] annotations = obj.getAnnotations();
		for(Annotation a: annotations){
			if(a.annotationType().equals(ClassTarget.class)){
				return true;
			}
		}
		return false;
	}

	private static Class<?> getTargetClass(Class<?> fromClass){
		Class<?> c = null;
		Annotation[] annotations = fromClass.getAnnotations();
		for(Annotation a: annotations){
			if(a.annotationType().equals(ClassTarget.class)){
				ClassTarget ct = (ClassTarget) a;
				try {
					c = Class.forName(ct.value());
				} catch (ClassNotFoundException e) {
					logger.error("Target class {} for {} not founded",ct.value(),fromClass);
				}
				break;
			}
		}
		return c;
	}
}