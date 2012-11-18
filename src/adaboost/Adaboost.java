package adaboost;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/** The object responsible for the boosting algorithm itself,
 * performing ensemble learning from data sets.*/
public class Adaboost {

	/** A prefix for adaboost-related properties. */
	public static final String NAMESPACE = "adaboost.";
	/** How many different classes of classifiers should this run contain?*/
	private static final String CLASSIFIERS_COUNT = NAMESPACE + "classifiers.count";
	/** What proportion of the data set should be used for testing? (Real value between 0 and 1)*/
	private static final String TEST_SET_PROPORTION = NAMESPACE + "testSetProportion";

	public static void main(String[] args) {
		if(args.length != 1){
			System.err.println("Please give a properties file argument.");
			System.exit(1);
		}
		
		Properties props = null;
		try {
			props = loadProperties(args);
		} catch (IOException e1) {
			System.err.println("Could not read properties file.");
			e1.printStackTrace();
			System.exit(1);
		}
		
		Set<Instance<Enum<?>>> dataSet = null;
		try {
			dataSet = CSVLoader.load(props);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			System.err.println("Error while loading data set.");
			e.printStackTrace();
			System.exit(1);
		}
		Set<Instance<Enum<?>>> trainingSet = new HashSet<Instance<Enum<?>>>();
		Set<Instance<Enum<?>>> testingSet = new HashSet<Instance<Enum<?>>>();
		double proportion = Double.parseDouble((String) props.get(TEST_SET_PROPORTION));
		proportion = (proportion < 0 ? 0 : (proportion > 1 ? 1 : proportion));
		partitionDataSet(dataSet, trainingSet, testingSet, proportion);


		int classifierCount = Integer.parseInt((String) props.get(CLASSIFIERS_COUNT));
		for (int i = 0; i < classifierCount; i++) {

		}


	}

	/** Non-destructively partition a data set into training and testing sets based on property values.
	 * @param
	 * dataSet The data-set to be partitioned. It will still contain all its instances.
	 * trainingSet The training set to fill. Must be an empty set at start. Afterward will be a proper subset of dataSet.
	 * testingSet The testing set to fill. Must be an empty set at start. Afterward will be a proper subset of dataSet.
	 * proportion The proportion of data to put into the testing set. Real value in [0,1], traditionally smaller than 0.5
	 * */
	static void partitionDataSet(Set<Instance<Enum<?>>> dataSet,
			Set<Instance<Enum<?>>> trainingSet,
			Set<Instance<Enum<?>>> testingSet, double proportion) {
		assert dataSet.size() > 0;
		assert trainingSet.size() == 0;
		assert testingSet.size() == 0;
		int testingCount = (int) Math.floor(proportion*dataSet.size());
		int skipCount = dataSet.size() / testingCount;
		int i = 0;
		for (Instance<Enum<?>> instance : dataSet) {
			if(i++ % skipCount == 0){
				testingSet.add(instance);
			}else{
				trainingSet.add(instance);
			}
		}
	}

	static Properties loadProperties(String[] args) throws IOException{
		Properties props = new Properties();
		FileInputStream stream = new FileInputStream(args[0]);
		props.load(stream);
		stream.close();
		return props;
	}

}
