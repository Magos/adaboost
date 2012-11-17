package adaboost;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/** The object responsible for the boosting algorithm itself,
 * performing ensemble learning from data sets.*/
public class Adaboost {

	private static final String NAMESPACE = "adaboost.";
	private static final String CLASSIFIERS_COUNT = NAMESPACE + "classifiers.count";

	public static void main(String[] args) {
		if(args.length != 1){
			System.err.println("Please give a properties file argument.");
			System.exit(1);
		}
		Properties props = loadProperties(args);
		int classifierCount = Integer.parseInt((String) props.get(CLASSIFIERS_COUNT));
		for (int i = 0; i < classifierCount; i++) {
			
		}
		

	}

	private static Properties loadProperties(String[] args) {
		Properties props = new Properties();
		try {
			FileInputStream stream = new FileInputStream(args[0]);
			props.load(stream);
			stream.close();
		} catch (IOException e) {
			System.err.println("Error reading properties");
			e.printStackTrace();
		}
		return props;
	}

}
