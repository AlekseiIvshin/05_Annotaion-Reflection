package com.epam.examples;

import com.epam.annotation.FieldName;

public class FromClass {

	@FieldName("userId")
	public String id;

	@FieldName("userName")
	public String name;

	@FieldName("userLastName")
	public String lastName;
}