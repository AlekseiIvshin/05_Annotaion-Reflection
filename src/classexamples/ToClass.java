package classexamples;

public class ToClass {

	public String userId;
	
	public String userName;
	public String userLastName;
	

	@Override
	public String toString() {
		return super.toString()+": ["+userId+", "+userName+", "+userLastName+"]";
	}
}
