package classexamples;

public abstract class TA {

	private String id;
	
	public void setId(String id){
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id;
	}
}
