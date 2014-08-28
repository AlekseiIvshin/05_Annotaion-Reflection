package main.java.classexamples;

import main.java.annotation.ClassTarget;
import main.java.annotation.FieldName;

@ClassTarget("main.java.classexamples.TA")
public class FA {

	@FieldName("id")
	private String id;
	
	@FieldName("dNumber")
	public int number;
	
	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "["+id+","+number+"]";
	}
}
