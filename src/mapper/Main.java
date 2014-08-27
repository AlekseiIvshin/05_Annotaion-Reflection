package mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import classexamples.FA;
import classexamples.FromClass;



public class Main {

	final static Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);

		logger.info("Entering application.");
		Object example = initExampleClass();
		useMyMapper(example);
		useOrika(example);
		logger.info("Exiting application.");
	}
	
	
	private static void useMyMapper(Object fromClass){
		logger.info("*** Start use my mapper ***");
		
		
		logger.info("In: "+fromClass.toString());
		Object tc = MyMapper.format(fromClass);
		if(tc != null){
			logger.info("Out: "+tc.toString());
		} else {
			logger.info("Mapping has some errors.");
		}
		logger.info("*** End use my mapper ***");	
	}
	
	private static void useOrika(Object fromClass){
		logger.info("*** Start use orika ***");
		
		
		
		logger.info("*** End use orika ***");	
	}
	
	private static Object initExampleClass(){
		logger.info("*** Init example class ***");
		FromClass example = new FromClass();
		example.setId("rootId");
		example.name = "Test";
		example.lastName = "Elephant";
		example.fa = new FA();
		example.fa.setId("fa.id");
		logger.info(example.toString());
		return example;
	}

}
