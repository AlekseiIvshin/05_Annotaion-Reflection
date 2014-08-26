package mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import classexamples.FA;
import classexamples.FromClass;
import classexamples.ToClass;



public class Main {

	final static Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);
		
		logger.info("Entering application.");
		
		FromClass example = new FromClass();
		example.setId("rootId");
		example.name = "Test";
		example.lastName = "Elephant";
		example.fa = new FA();
		example.fa.setId("fa.id");
		logger.info("In: "+example.toString());
		Object tc = Mapper.format(example);
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
