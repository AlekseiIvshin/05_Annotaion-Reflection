package main.java.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.annotation.ClassTarget;
import main.java.annotation.FieldName;

public class MyMapper implements Mapper{

	final static Logger logger = LoggerFactory.getLogger(MyMapper.class);
	
	public Object map(Object fromObj){
		Class<?> fromClass = fromObj.getClass();
		
		logger.info("Starting mapping: class {}",fromClass);
		
		if(checkFieldsHaveThisClass(fromClass)){
			logger.error("Class {} contains itself in fields",fromClass.getName());
			return null;
		}
		
		Class<?> toClass = getTargetClass(fromClass);
		if(toClass == null){
			logger.error("Error on create new instance type of class NULL");
			return null;
		}
		// Get result class instance
		Object result;
		try {
			result = toClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			logger.error("Error on create new instance of {}: {}",toClass.getName(), e1.toString());
			return null;
		}
		
		Field[] fields = fromClass.getDeclaredFields();

		for(Field fromField: fields){
			
			if(!isMapped(fromField)){
				continue;
			}
			
			String targetFieldName = getTargetFieldName(fromField);

			Field toField = getTargetField(toClass,targetFieldName);
			
			if(toField == null){
				logger.error("Field not found: {}.{}",toClass,targetFieldName);
				return null;
			}
			
			boolean fieldClassIsMapped = isMapped(fromField.getType());
			
			if(fieldClassIsMapped){
				logger.info("Field is mapped {}.{} -> {}.{}",
						fromClass.getName(),fromField.getName(),
						toClass.getName(),toField.getName());
			} else {
				if(!toField.getType().equals(fromField.getType())){
					logger.error("{} type of field not equals {}",
							toField.getType(),fromField.getType());
					return null;
				}
			}
			
			Object valueForTargetField;
			try {
				valueForTargetField = DataGetter.getData(fromField,fromObj);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
					StackTraceElement[] trace = e.getStackTrace();
					for(StackTraceElement ste: trace){
						logger.trace(ste.toString());
					}
					logger.error(e.toString());
					return null;
			}
			
			if(valueForTargetField == null){
				logger.warn("Value of {} equals NULL",fromField.getName());
				continue;
			}
			
			boolean setResult = setData(toField,result,valueForTargetField,fieldClassIsMapped);
			
			if(!setResult){
				logger.error("Set to {}.{} value = {}",result.getClass(),fromField.getName(),valueForTargetField);
			}
			
			
		}

		logger.info("Mapping done: class {}",fromClass.getName());
		return result;
	}
	
	/**
	 * Get target filed name from annotation
	 * @param fromField field that have annotation FieldName
	 * @return target field name
	 */
	private static String getTargetFieldName(Field fromField){
		FieldName fieldName = (FieldName) fromField.getAnnotation(FieldName.class);
		return fieldName != null? fieldName.value(): fromField.getName();
	}

	/**
	 * Check class has annotation for mapping
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
	
	/** 
	 * Check field has annotation for mapping
	 * @param field
	 * @return
	 */
	private static boolean isMapped(Field field){
		FieldName fieldName = (FieldName) field.getAnnotation(FieldName.class);
		return fieldName != null;
	}

	/**
	 * Get target class from annotation
	 * @param fromClass
	 * @return target class
	 */
	private static Class<?> getTargetClass(Class<?> fromClass){
		Annotation[] annotations = fromClass.getAnnotations();
		for(Annotation a: annotations){
			if(a.annotationType().equals(ClassTarget.class)){
				ClassTarget ct = (ClassTarget) a;
				try {
					return Class.forName(ct.value());
				} catch (ClassNotFoundException e) {
					logger.error("Target class {} for {} not founded",ct.value(),fromClass);
					return null;
				}
			}
		}
		return null;
	}
	
	/**
	 * Check on class has itself in the field (anti recursive)
	 * @param thisClass
	 * @return
	 */
	private static boolean checkFieldsHaveThisClass(Class<?> thisClass){
		Field[] toClassFields = thisClass.getDeclaredFields();
		for(Field tf: toClassFields){
			if(tf.getType().equals(thisClass)){
				return true;
			}
		}
		return false;
	}
	
	private boolean setData(Field toField,Object targetObject,Object value,boolean fromFieldClassIsMapped){
		
		if(fromFieldClassIsMapped){
			Object r = map(value);
			if(r != null ){
				return DataSetter.setData(toField, targetObject, r);
			}
		} else {
			return DataSetter.setData(toField,targetObject,value);
		}
		
		return false;
	}
	
	private  static Field getTargetField(Class<?> targetClass,String targetFieldName){
		Field[] toClassFields = targetClass.getDeclaredFields();
		for(Field tf: toClassFields){
			if(tf.getName().equals(targetFieldName)){
				return  tf;
			}
		}
		return null;
	}
}