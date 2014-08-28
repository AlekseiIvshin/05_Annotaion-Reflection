package classexamples;

public class ToClass {

	public String userId;
	
	private String userName;
	public String userLastName;
	
	public String getUserName(){
		return userName;
	}
	
	public TA ta;
	
	public void setUserName(String userName){
		this.userName = userName;
	}
		
	@Override
	public String toString() {
		return super.toString()+": ["+userId+", "+userName+", "+userLastName+","+(ta ==null? "null":ta.toString())+"]";
	}
}
