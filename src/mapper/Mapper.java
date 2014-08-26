package mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import classexamples.FromClass;
import classexamples.ToClass;
import annotation.FieldName;

public class Mapper {

	final static Logger logger = LoggerFactory.getLogger(Mapper.class);
	
	public static ToClass format(FromClass fromObj){
		logger.info("Start mapping");
		
		Class<?> fromClass = fromObj.getClass();
		Class<?> toClass = ToClass.class;
		
		ToClass result = new ToClass();
		
		Field[] fields =fromClass.getDeclaredFields();

		for(Field f: fields){
			FieldName fieldName = (FieldName) f.getAnnotation(FieldName.class);
			String name = fieldName != null? fieldName.value(): f.getName();
			Field fr = null;
			try {
				fr = toClass.getField(name);
				if(fr.getType() != f.getType()){
					logger.error("{} type of field not equals {}",fr.getType(),f.getType());
					return null;
				} else {
					fr.set(result, getData(f,fromObj));
				}
			} catch (NoSuchFieldException | SecurityException |IllegalAccessException e) {
				logger.error(e.toString());
				return null;
			}
			
		}

		logger.info("Mapping done");
		return result;
	}
	
	private static Object getData(Field field,Object obj){
		
		if(Modifier.isPrivate(field.getModifiers())){
			getDataFromMethods(field,obj);
		} else{
			try {
				return field.get(obj);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error(e.toString());
			}
		}
		return null;
	}
	
	private static Object getDataFromMethods(Field field,Object obj){
		
		System.err.println(obj.getClass());
		
		return null;
	}
	
	
}