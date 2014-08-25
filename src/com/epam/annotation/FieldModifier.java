package com.epam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Target field modifiers
 * @author Aleksei_Ivshin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(FieldModifiers.class)
public @interface FieldModifier {
	
	String value();
}