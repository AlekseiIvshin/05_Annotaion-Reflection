package com.epam.examples;

import com.epam.annotation.FieldModifier;
import com.epam.annotation.FieldModifiers;
import com.epam.annotation.FieldName;
import com.epam.annotation.FieldType;

public class FromClass {

	@FieldName("userId")
	public String id;

	@FieldName("userName")
	public String name;

	@FieldName("userLastName")
	public String lastName;
}