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
		if(checkFieldsHaveThisClass(fromClass)){
			logger.error("Class {} contains itself",fromClass.getName());
			return null;
		}
		
		Class<?> toClass = getTargetClass(fromClass);
		Object result;
		try {
			result = toClass.newInstance();
		} catch (InstantiationException | IllegalAccessException | NullPointerException e1) {
			logger.error("Error on create new instance of {}: {}",toClass.getName(), e1.toString());
			return null;
		}
		
		Field[] fields = fromClass.getDeclaredFields();

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
				
//				if(isMapped(fromField.getType())){
//					logger.info("Field is mapped {}.{} -> {}.{}",fromClass.getName(),fromField.getName(),
//							toClass.getName(),toField.getName());
//				} else {
//					if(toField.getType() != fromField.getType()){
//						logger.error("{} type of field not equals {}",toField.getType(),fromField.getType());
//						return null;
//					}
//				}
				
				
				if(isMapped(fromField.getType())){
					logger.info("Field is mapped {}.{} -> {}.{}",fromClass.getName(),fromField.getName(),
							toClass.getName(),toField.getName());
					Object value;
					try {
						value = getData(fromField,fromObj);
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
							StackTraceElement[] trace = e.getStackTrace();
							for(StackTraceElement ste: trace){
								logger.trace(ste.toString());
							}
							logger.error(e.toString());
							return null;
					}
					if(value == null){
						logger.error("Value of {} equals NULL",fromField.getName());
						return null;
					}
					Object r = format(value);
					if(r != null ){
						boolean setResult = setData(toField, result, r);
						if(!setResult){
							logger.error("Set to {}.{} value = {}",result.getClass(),fromField.getName(),value);
						}
					}
					
				} else {
					if(toField.getType() != fromField.getType()){
						logger.error("{} type of field not equals {}",toField.getType(),fromField.getType());
						return null;
					} else {
						Object value;
						try {
							value = getData(fromField,fromObj);
						} catch (IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
								StackTraceElement[] trace = e.getStackTrace();
								for(StackTraceElement ste: trace){
									logger.trace(ste.toString());
								}
								logger.error(e.toString());
								return null;
						}
						if(value == null){
							logger.error("Value of {} equals NULL",fromField.getName());
							return null;
						}
						boolean setResult = setData(toField,result,value);
						
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

		logger.info("Mapping done: class {}",fromClass.getName());
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
	
	private static Object getData(Field field,Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		logger.info("Get data {}.{}",obj.getClass(),field.getName());
				
		try {
			return getDataFromField(field, obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.info("Field is inaccessible");
		}
		
		return getDataUseGetter(field, obj);
	}
	
	private static Object getDataFromField(Field field,Object obj) throws IllegalArgumentException, IllegalAccessException{

		logger.info("Get from field",obj.getClass(),field.getName());
		return  field.get(obj);
	}
	
	private static Object getDataUseGetter(Field field,Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		logger.info("Get data (use getter) from field {}",field.getName());
		Class<?> c = obj.getClass();
		String getterName = "get"+field.getName();
		Method[] methods = c.getDeclaredMethods();
				
		for(Method m: methods){
			if(m.getName().equalsIgnoreCase(getterName)){
				return m.invoke(obj);
			}
		}
		
		return null;
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
	
	private static boolean checkFieldsHaveThisClass(Class<?> thisClass){
		Field[] toClassFields = thisClass.getDeclaredFields();
		for(Field tf: toClassFields){
			if(tf.getType().equals(thisClass)){
				return true;
			}
		}
		return false;
	}
}