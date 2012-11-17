package adaboost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class CSVLoader {
	private static final String SEPARATOR = ",";
	public static final String DATA_SOURCE_PROPERTY = "adaboost.dataSource";
	public static final String DATA_SOURCE_CLASS = "adaboost.dataClass";

	public static <T extends Enum<?>> Set<Instance<T>> load(Properties props) throws IOException, InstantiationException, IllegalAccessException{
		//Check existence and readability of data source
		String location = props.getProperty(DATA_SOURCE_PROPERTY);
		File source = new File(location);
		if(!(source.exists() && source.canRead())){
			throw new RuntimeException("Unable to read data source at " + location + ".");
		}
		//Prepare for instantiation of appropriate instance class.
		Class<? extends Instance<T>> instanceClass = null;
		try {
			instanceClass = (Class<? extends Instance<T>>) Class.forName(props.getProperty(DATA_SOURCE_CLASS)) ;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to load requested instance class.",e);
		}
		//Start loading.
		Set<Instance<T>> ret = new HashSet<Instance<T>>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
		while(reader.ready()){
			String line = reader.readLine();
			String[] values = line.split(SEPARATOR);
			Instance<T> instance = instanceClass.newInstance();
			instance.initialize(values);
			ret.add(instance);
		}
		double weight = 1d / ret.size();
		for (Instance<T> instance : ret) {
			instance.setWeight(weight);
		}
		return ret;
	}

}
