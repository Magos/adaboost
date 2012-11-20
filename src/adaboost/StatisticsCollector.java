package adaboost;

import java.util.Map;

import adaboost.classifiers.DecisionTreeClassifier;
import adaboost.classifiers.NaiveBayes;
import adaboost.instance.GlassInstance;
import adaboost.instance.NurseryInstance;
import adaboost.instance.PageInstance;
import adaboost.instance.PenInstance;
import adaboost.instance.YeastInstance;


public class StatisticsCollector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] dataSets = new String[]{
				"res/glass.csv", "res/nursery.csv","res/page-blocks.csv","res/pen-digits.csv","res/yeast.csv"
		};
		String[] classes = new String[]{
				GlassInstance.class.getCanonicalName(), NurseryInstance.class.getCanonicalName(), PageInstance.class.getCanonicalName(), PenInstance.class.getCanonicalName(), YeastInstance.class.getCanonicalName()
		};
		for (int i = 0; i < classes.length; i++) {
			System.out.println("Collecting for data set: " + dataSets[i]);
			Properties props = new Properties();
			props.put(Adaboost.LOG_TRAIN_ERRORS, "true");
			props.put(Adaboost.TEST_SET_PROPORTION,"0.2");
			props.put(Adaboost.CLASSIFIERS_COUNT, "2");
			props.put(Adaboost.CLASSIFIERS_NS + "0.class", NaiveBayes.class.getCanonicalName());
			props.put(Adaboost.CLASSIFIERS_NS + "0.bins", "10");
			props.put(Adaboost.CLASSIFIERS_NS + "1.class", DecisionTreeClassifier.class.getCanonicalName());
			props.put(Adaboost.CLASSIFIERS_NS + "1.bins", "10");
			props.put(Adaboost.CLASSIFIERS_NS + "1." + DecisionTreeClassifier.DEPTH_LIMIT, "-1");
			props.put(CSVLoader.DATA_SOURCE_PROPERTY, dataSets[i]);
			props.put(CSVLoader.DATA_SOURCE_CLASS, classes[i]);
			
			performTest(1,0, props);
			performTest(0, 1, props);
			performTest(5, 0, props);
			performTest(10, 0, props);
			performTest(20, 0, props);
			performTest(0, 5, props);
			props.put(Adaboost.CLASSIFIERS_NS + "1." + DecisionTreeClassifier.DEPTH_LIMIT, "1");
			performTest(0, 10, props);
			props.put(Adaboost.CLASSIFIERS_NS + "1." + DecisionTreeClassifier.DEPTH_LIMIT, "2");
			performTest(0, 10, props);
			props.put(Adaboost.CLASSIFIERS_NS + "1." + DecisionTreeClassifier.DEPTH_LIMIT, "-1");
			performTest(0, 10, props);
			performTest(0, 20, props);
			props.put(Adaboost.CLASSIFIERS_NS + "1." + DecisionTreeClassifier.DEPTH_LIMIT, "2");
			performTest(5, 5, props);
			performTest(10, 10, props);
			performTest(20, 20, props);
		}
	}

	private static void performTest(int i, int j, Properties props) {
		props.put(Adaboost.CLASSIFIERS_NS + "0.count", Integer.toString(i));
		props.put(Adaboost.CLASSIFIERS_NS + "1.count", Integer.toString(j));
		Map<Classifier<?>,Double> result = Adaboost.runTest(props);
		System.out.println("Ensemble performance was " + result.get(null));
	}

	
}
