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
		
		Object myMapper = useMyMapper(example);
		Object orikaMapper = useOrika(example);
		
		logger.info("*** Compare results ***");
		if(myMapper != null){
			logger.info("My Mapper Out: "+myMapper.toString());
		} else {
			logger.info("My Mapping has some errors.");
		}
		
		if(orikaMapper != null){
			logger.info("Orika Mapper Out: "+orikaMapper.toString());
		} else {
			logger.info("Orika Mapping has some errors.");
		}
		
		
		logger.info("Exiting application.");
	}
	
	
	private static Object useMyMapper(Object fromClass){
		logger.info("*** Start use my mapper ***");
		logger.info("In: "+fromClass.toString());
		Mapper m = new MyMapper();
		Object result = m.map(fromClass);
		logger.info("*** End use my mapper ***");	
		return result;
	}
	
	private static Object useOrika(Object fromClass){
		logger.info("*** Start use orika ***");
		logger.info("In: "+fromClass.toString());
		Mapper m = new OrikaMapper();
		Object result = m.map(fromClass);
		logger.info("*** End use orika ***");
		return result;
	}
	
	private static Object initExampleClass(){
		logger.info("*** Init example class ***");
		FromClass example = new FromClass();
		example.setId("rootId");
		example.name = "Test";
		example.lastName = "Elephant";
		example.fa = new FA();
		example.fa.setId("fa.id");
		example.fa.number = 100;
		logger.info(example.toString());
		return example;
	}

}
