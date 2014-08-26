package mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataGetter   {

	final static Logger logger = LoggerFactory.getLogger(DataGetter.class);
	
	public static Object getData(Field field,Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
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

}
