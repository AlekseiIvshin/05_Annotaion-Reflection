package main.java.classexamples;

public class TA {

	private String id;
	
	public double dNumber;
	
	public void setId(String id){
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "["+id+","+dNumber+"]";
	}
}
