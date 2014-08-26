package mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSetter {

	final static Logger logger = LoggerFactory.getLogger(DataGetter.class);
		
	public static boolean setData(Field field, Object obj,Object value){
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
			result = setDataToSetter(field,obj,value);
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
	
	private static boolean setDataToSetter(Field field, Object obj,Object value) {
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
				break;
			}
		}
		
		return result;
	}
}
