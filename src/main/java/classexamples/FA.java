package classexamples;

import annotation.ClassTarget;
import annotation.FieldName;

@ClassTarget("classexamples.TA")
public class FA {

	@FieldName("id")
	private String id;
	
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
