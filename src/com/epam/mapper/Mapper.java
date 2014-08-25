package com.epam.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import com.epam.annotation.FieldName;
import com.epam.examples.FromClass;
import com.epam.examples.ToClass;

public class Mapper {

	public static void showClassMap(Class<?> toMap){
		// Get class name and modifiers
		System.out.printf("Class:%n%s%n",toMap.getCanonicalName());
		System.out.printf("Modifiers:%n%s%n",Modifier.toString(toMap.getModifiers()));
		System.out.println();
		
		// Get class parameters type 
		TypeVariable[] tv = toMap.getTypeParameters();
		System.out.printf("Type Parameters:%n");
		if(tv.length != 0){
			for(TypeVariable<?> t: tv){
				System.out.printf("%s ",t.getName());
			}
			System.out.println();
		}else{
			System.out.println("No Type Parameters");
		}
		
		// Get interfaces
		Type[] interfaces = toMap.getGenericInterfaces();
		System.out.printf("Implemented Interfaces:%n");
		if(interfaces.length != 0){
			for(Type t: interfaces){
				System.out.printf("%s%n",t.toString());
			}
			System.out.println();
		}else{
			System.out.println("No Implemented Interfaces");
		}
		

		// Get super classes 
		List<Class<?>> list = new ArrayList<>(); 
		pringAncestor(toMap, list);
		System.out.printf("Super classes:%n");
		if(list.size() != 0){
			for(Class c: list){
				System.out.printf("%s%n",c.toString());
			}
			System.out.println();
		}else{
			System.out.println("No Super Classes");
		}
		
		System.out.println("Annotations");
		Annotation[] annotations = toMap.getAnnotations();
		if(annotations.length != 0){
			for(Annotation a: annotations){
				System.out.printf("%s%n",a.toString());
			}
			System.out.println();
		}else{
			System.out.println("No Annotations");
		}
	}
	
	private static void pringAncestor(Class<?> c,List<Class<?>> l){
		Class<?> ancestor = c.getSuperclass();
		if(ancestor != null){
			l.add(ancestor);
			pringAncestor(ancestor, l);
		}
	}
	
	public static ToClass format(FromClass fromObj){
		
		Class<?> fromClass = fromObj.getClass();
		Class<?> toClass = ToClass.class;
		
		
		ToClass result = new ToClass();
		
		Field[] fields =fromClass.getDeclaredFields();

		for(Field f: fields){
			FieldName fieldName = (FieldName) f.getAnnotation(FieldName.class);
			String name = fieldName != null? fieldName.value(): f.getName();
			System.out.println(name);
			Field fr = null;
			try {
				fr = toClass.getField(name);
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
			if(fr.getType() != f.getType()){
				return null;
			}
			
			try {
				fr.set(result, getData(f,fromObj));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		return result;
	}
	
	private static Object getData(Field field,Object obj){
		
		if(Modifier.isPrivate(field.getModifiers())){
			return "0";
		} else{
			try {
				return field.get(obj);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
	
	
}