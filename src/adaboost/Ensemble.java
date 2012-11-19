package adaboost;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**A Classifier built upon an ensemble of other classifiers. It produces classifications by means of weighted voting.
 * It does not train; Constructors have the responsibility to weight their classifiers before inputting them.*/
public class Ensemble<T extends Enum<T>> implements Classifier<T> {
	private Map<Classifier<T>,Double> map;
	private double totalWeight;
	
	public Ensemble(Set<Classifier<T>> classifiers){
		map = new HashMap<Classifier<T>, Double>();
		totalWeight = 0;
	}
	
	public void addClassifier(Classifier<T> classifier, double weight){
		map.put(classifier,weight);
		totalWeight += weight;
	}
	
	@Override
	public T classify(Instance<Enum<?>> instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void train(Set<Instance<Enum<?>>> trainingSet) {
		//This classifier does not train.
	}

	@Override
	public void configure(Properties props, String prefix) {
		//No configuration needed.
	}

}
