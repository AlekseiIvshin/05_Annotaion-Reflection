package mapper;

import classexamples.FromClass;
import classexamples.ToClass;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class OrikaMapper implements Mapper{

	
	@Override
	public Object format(Object fromClass) {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		
		mapperFactory.classMap(FromClass.class,ToClass.class )
				.fieldAToB("id", "userId")
				.fieldAToB("name", "userName")
				.fieldAToB("lastName", "userLastName")
				.fieldAToB("fa.id", "ta.id")
				.register();
		
		MapperFacade mapper = mapperFactory.getMapperFacade();
		
		FromClass sourse = (FromClass) fromClass;
		
		ToClass result = mapper.map(sourse, ToClass.class);
		
		return result;
	}
	

}
