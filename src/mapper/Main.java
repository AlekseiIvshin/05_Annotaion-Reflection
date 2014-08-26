package mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import classexamples.FromClass;
import classexamples.ToClass;



public class Main {

	final static Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		logger.info("Entering application.");
		
		FromClass example = new FromClass();
		example.id = "22";
		example.name = "Test";
		example.lastName = "Elephant";
		logger.info("In: "+example.toString());
		ToClass tc = Mapper.format(example);
		if(tc != null){
			logger.info("Out: "+tc.toString());
		} else {
			logger.info("Mapping has some errors.");
		}
		
		logger.info("Exiting application.");	
	}
	
	private static String getParam(String[] args, String key){
		for(int i = 0; i < args.length-1; i+=2){
			if(args[i].equalsIgnoreCase(key)){
				return args[i+1];
			}
		}
		return "";
	}

}
