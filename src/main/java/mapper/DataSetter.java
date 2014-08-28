package mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for hidden process of setting data to field
 * @author Aleksei_Ivshin
 *
 */
public class DataSetter {

	final static Logger logger = LoggerFactory.getLogger(DataGetter.class);
		
	/**
	 * Set data to field
	 * @param field Target field
	 * @param obj object where set data
	 * @param value value of data
	 * @return 
	 */
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
			logger.info("Field {}.{} is inaccessible for 'set' operations",obj.getClass(),field.getName());
			// if can't set to field, use setter method
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
	
	/**
	 * Set data to field using setter
	 * @param field
	 * @param obj
	 * @param value
	 * @return
	 */
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
