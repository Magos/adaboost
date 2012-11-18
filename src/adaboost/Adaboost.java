package adaboost;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/** The object responsible for the boosting algorithm itself,
 * performing ensemble learning from data sets.*/
public class Adaboost {

	/** A prefix for adaboost-related properties. */
	public static final String NAMESPACE = "adaboost.";
	/** Part of several property keys.*/
	private static final String COUNT = "count";
	private static final String CLASSIFIERS_NS = NAMESPACE + "classifiers.";
	/** How many different classes of classifiers should this run contain?*/
	private static final String CLASSIFIERS_COUNT = CLASSIFIERS_NS + COUNT;
	/** What proportion of the data set should be used for testing? (Real value between 0 and 1)*/
	private static final String TEST_SET_PROPORTION = NAMESPACE + "testSetProportion";

	public static void main(String[] args) {
		if(args.length != 1){
			throw new RuntimeException("Please give a properties file argument.");
		}
		
		Properties props = null;
		try {
			props = loadProperties(args);
		} catch (IOException e1) {
			throw new RuntimeException("Could not read properties file.", e1);
		}
		Set<Instance<Enum<?>>> dataSet = null;
		try {
			dataSet = CSVLoader.load(props);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			throw new RuntimeException("Error while loading data set.",e);
		}
		Set<Instance<Enum<?>>> trainingSet = new HashSet<Instance<Enum<?>>>();
		Set<Instance<Enum<?>>> testingSet = new HashSet<Instance<Enum<?>>>();
		double proportion = Double.parseDouble((String) props.get(TEST_SET_PROPORTION));
		proportion = (proportion < 0 ? 0 : (proportion > 1 ? 1 : proportion));
		partitionDataSet(dataSet, trainingSet, testingSet, proportion);


		int classifierCount = Integer.parseInt((String) props.get(CLASSIFIERS_COUNT));
		for (int i = 0; i < classifierCount; i++) {
			int count = props.getIntProperty(CLASSIFIERS_NS + i + "." + COUNT);
			
			
		}


	}

	/** Non-destructively partition a data set into training and testing sets based on property values.
	 * Attempts to sample the test data evenly from throughout the data set, to ensure it is representative.
	 * @param dataSet The data-set to be partitioned. No alterations are performed on this set.
	 * @param trainingSet The training set to fill. Must be an empty set at start. Afterward will be a proper subset of dataSet.
	 * @param testingSet The testing set to fill. Must be an empty set at start. Afterward will be a proper subset of dataSet.
	 *@param  proportion The proportion of data to put into the testing set. Real value in [0,1], traditionally smaller than 0.5
	 * */
	static void partitionDataSet(Set<Instance<Enum<?>>> dataSet,
			Set<Instance<Enum<?>>> trainingSet,
			Set<Instance<Enum<?>>> testingSet, double proportion) {
		assert dataSet != null && 		dataSet.size() > 0;
		assert trainingSet != null && 	trainingSet.size() == 0;
		assert testingSet != null && 	testingSet.size() == 0;
		assert proportion >= 0 && proportion <= 1; 
		
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
