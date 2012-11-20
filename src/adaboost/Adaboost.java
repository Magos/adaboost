package adaboost;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
	private static final String LOG_TRAIN_ERRORS = NAMESPACE + "logTrainingErrors";
	
	private static double BETA = 1.5d;

	/** Run a single test, from a properties file.*/
	public static void main(String[] args) {
		//Read args
		if(args.length != 1){
			throw new RuntimeException("Please give a properties file argument.");
		}
		//Load properties
		Properties props = null;
		try {
			props = loadProperties(args);
		} catch (IOException e1) {
			throw new RuntimeException("Could not read properties file.", e1);
		}
		Map<Classifier<?>, Double> performance = runTest(props);
		System.out.println(performance);
	}

	/** Perform the run described by the properties in props and return the results of testing. */
	public static Map<Classifier<?>, Double> runTest(Properties props) {
		//Load and prepare data set.
		Set<Instance<Enum<?>>> dataSet = null;
		try {
			dataSet = CSVLoader.load(props);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			throw new RuntimeException("Error while loading data set.",e);
		}
		Set<Instance<Enum<?>>> trainingSet = new HashSet<Instance<Enum<?>>>();
		Set<Instance<Enum<?>>> testingSet = new HashSet<Instance<Enum<?>>>();
		double proportion = props.getDoubleProperty(TEST_SET_PROPORTION);
		proportion = (proportion < 0 ? 0 : (proportion > 1 ? 1 : proportion));
		partitionDataSet(dataSet, trainingSet, testingSet, proportion);
		//Train classifiers.
		Map<Classifier<?>,Double> ensemble = adaBoostTraining(props, trainingSet);
		//Test classifiers.
		Map<Classifier<?>,Double> performance = testEnsemble(ensemble,testingSet);
		return performance;
	}

	/** Test the ensemble on the given test set and return its proportion of correct classifications, both on an individual basis and as a whole.
	 * The performance of the ensemble is indicated by the special key null.*/
	private static Map<Classifier<?>, Double> testEnsemble(
			Map<Classifier<?>, Double> ensemble,
			Set<Instance<Enum<?>>> testingSet) {
		Map<Classifier<?>,Double> performance = new HashMap<Classifier<?>,Double>();
		performance.put(null,0d);
		for (Classifier<?> classifier : ensemble.keySet()) {
			performance.put(classifier, 0d);
		}
		double ensembleTotalWeight = 0;
		for (Double classifierWeight : ensemble.values()) {
			ensembleTotalWeight += classifierWeight;
		}
		for (Instance<Enum<?>> instance : testingSet) {
			double correctVote = 0;
			for (Classifier<?> classifier : ensemble.keySet()) {
				boolean correct = classifier.classify(instance) == instance.getClassification();
				if(correct){
					performance.put(classifier, performance.get(classifier)+1);
					correctVote += ensemble.get(classifier);
				}
			}
			if(correctVote*2 >= ensembleTotalWeight){//If at least half of the weight of the ensemble voted for this, it was a hit.
				performance.put(null, performance.get(null) +1);
			}
		}
		for (Map.Entry<Classifier<?>, Double> classifierPerformance : performance.entrySet()) {
			classifierPerformance.setValue(classifierPerformance.getValue() / testingSet.size());
		}
		return performance;
	}

	/** The adaboost algorithm itself, producing the ensemble described in properties using the training set provided.*/
	private static Map<Classifier<?>,Double> adaBoostTraining(Properties props, Set<Instance<Enum<?>>> trainingSet) {
		Map<Classifier<?>,Double> ensemble = new HashMap<Classifier<?>,Double>();
		int classifierCount = props.getIntProperty(CLASSIFIERS_COUNT);
		boolean logging = props.getBooleanProperty(LOG_TRAIN_ERRORS,false);

		for (int i = 0; i < classifierCount; i++) {
			String prefix = CLASSIFIERS_NS + i + ".";
			Class<? extends Classifier<?>> classifier = null;
			String className = props.getProperty(prefix + "class");
			try {
				classifier = (Class<? extends Classifier<?>>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Unable to load classifier " + className,e);
			}
			int count = props.getIntProperty(prefix + COUNT);
			if(logging) System.out.println("Logging training errors for " + className);
			for (int j = 0; j < count; j++) {
				boolean accepted = false;
				while(!accepted){
					Classifier<?> x = null;
					try {
						x = classifier.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
					x.configure(props, prefix);
					x.train(trainingSet);
					accepted = updateWeights(x,trainingSet, ensemble, logging);
				}
			}
		}
		return ensemble;


	}

	/** Consider a classifier for inclusion in the ensemble and update weights. */
	private static boolean updateWeights(Classifier<?> x, Set<Instance<Enum<?>>> trainingSet, Map<Classifier<?>,Double> ensemble, boolean log) {
		double error = 0;
		for (Instance<Enum<?>> instance : trainingSet) {
			if(x.classify(instance) != instance.getClassification()){
				error += instance.getWeight();
			}
		}
		int l = countAttributes(trainingSet);
		double limit = (l-1d)/l;
		if(error > limit){
			jiggleWeights(trainingSet);
			return false;//Reject this classifier
		}else if(error > 0){
			for (Instance<Enum<?>> instance : trainingSet) {
				if(x.classify(instance) != instance.getClassification()){
					double weight = instance.getWeight() * ((1d-error)/error)*(l-1);
					instance.setWeight(weight);
				}
			}
			normalizeWeights(trainingSet);
			ensemble.put(x, Math.log(((1d-error)/error)*(l-1)));
		}else if(error == 0){
			jiggleWeights(trainingSet);
			ensemble.put(x, 10 + Math.log(l-1));
		}
		if(log){
			System.out.println(error);
		}
		return true;		
	}

	private static void jiggleWeights(Set<Instance<Enum<?>>> trainingSet) {
		for (Instance<Enum<?>> instance : trainingSet) {
			double weight = instance.getWeight();
			double plusMinus = Math.round(Math.random()-0.5d);
			assert plusMinus == 1d || plusMinus == -1d;
			weight = Math.max(0d, weight + (plusMinus*Math.pow(1/trainingSet.size(),BETA)));
			instance.setWeight(weight);
		}
		normalizeWeights(trainingSet);
	}
	private static void normalizeWeights(Set<Instance<Enum<?>>> trainingSet) {
		double totalWeight = 0;
		for (Instance<Enum<?>> instance : trainingSet) {
			totalWeight += instance.getWeight();
		}
		for (Instance<Enum<?>> instance : trainingSet) {
			instance.setWeight(instance.getWeight()/totalWeight);
		}
	}

	private static int countAttributes(Set<Instance<Enum<?>>> trainingSet) {
		int ret = 0;
		for (Iterator iterator = trainingSet.iterator().next().getAttributes(); iterator.hasNext();) {
			iterator.next();
			ret++;
		}
		return ret;
	}

	/** Non-destructively partition a data set into training and testing sets.
	 * Attempts to sample the test data evenly from throughout the data set, to ensure it is representative.
	 * @param dataSet The data-set to be partitioned. No alterations are performed on this set.
	 * @param trainingSet The training set to fill. Must be an empty set at start. Afterward will be a proper subset of dataSet.
	 * @param testingSet The testing set to fill. Must be an empty set at start. Afterward will be a proper subset of dataSet.
	 * @param  proportion The proportion of data to put into the testing set. Real value in [0,1], traditionally smaller than 0.5
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
