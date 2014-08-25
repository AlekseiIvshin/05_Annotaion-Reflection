package com.epam.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.examples.FromClass;
import com.epam.examples.ToClass;

public class Main {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger("com.epam.mapper.Main");
		logger.debug("Main");
		
		FromClass example = new FromClass();
		example.id = "22";
		
		ToClass tc = Mapper.format(example);
		example.id= "3213";
		System.out.println(tc.userId);
		System.out.println(example.id);
			
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
