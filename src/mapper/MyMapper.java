package mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import annotation.ClassTarget;
import annotation.FieldName;

public class MyMapper implements Mapper{

	final static Logger logger = LoggerFactory.getLogger(MyMapper.class);
	
	public Object map(Object fromObj){
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
			if(!isMapped(fromField)){
				continue;
			}
			
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
				
				boolean isMapped = isMapped(fromField.getType());
				
				if(isMapped){
					logger.info("Field is mapped {}.{} -> {}.{}",fromClass.getName(),fromField.getName(),
							toClass.getName(),toField.getName());
				} else {
					if(!toField.getType().equals(fromField.getType())){
						logger.error("{} type of field not equals {}",toField.getType(),fromField.getType());
						return null;
					}
				}
				
				Object valueForField;
				try {
					valueForField = DataGetter.getData(fromField,fromObj);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
						StackTraceElement[] trace = e.getStackTrace();
						for(StackTraceElement ste: trace){
							logger.trace(ste.toString());
						}
						logger.error(e.toString());
						return null;
				}
				if(valueForField == null){
					logger.warn("Value of {} equals NULL",fromField.getName());
				}
				
				if(isMapped){
					Object r = map(valueForField);
					if(r != null ){
						boolean setResult = DataSetter.setData(toField, result, r);
						if(!setResult){
							logger.error("Set to {}.{} value = {}",result.getClass(),fromField.getName(),valueForField);
						}
					}
				} else {
					boolean setResult = DataSetter.setData(toField,result,valueForField);
					
					if(!setResult){
						logger.error("Set to {}.{} value = {}",result.getClass(),fromField.getName(),valueForField);
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
	
	private static boolean isMapped(Field field){
		FieldName fieldName = (FieldName) field.getAnnotation(FieldName.class);
		return fieldName != null;
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