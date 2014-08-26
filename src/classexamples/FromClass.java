package classexamples;

import annotation.ClassTarget;
import annotation.FieldName;

@ClassTarget("ToClass")
public class FromClass {

	@FieldName("userId")
	public String id;

	@FieldName("userName")
	public String name;

	@FieldName("userLastName")
	public String lastName;
	
	@Override
	public String toString() {
		return super.toString()+": ["+id+", "+name+", "+lastName+"]";
	}
}