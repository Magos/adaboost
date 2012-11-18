package adaboost.classifiers;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import adaboost.Classifier;
import adaboost.Instance;
import adaboost.Properties;

public class NaiveBayes<T extends Enum<T>> implements Classifier<T> {
	private Map<T,Double> aPrioriProbability;
	private Set<Instance<T>> trainingSet;

	@Override
	public T classify(Instance<T> instance) {
		if(trainingSet.contains(instance)){
			return instance.getClassification();
		}
		return null;
	}

	@Override
	public void train(Set<Instance<T>> trainingSet) {
		this.trainingSet = trainingSet;
		//For naive bayes we need to obtain two probabilities by counting:
		//Weighted probability that any instance belongs to a classification, and
		//weighted probability that a set of attributes correspond to a classification.
		//Obtain the former at train-time, find the latter on request.
		Map<T,Double> temp = new HashMap<T,Double>();
		double totalWeight = 0;
		for (Instance<T> instance : trainingSet) {
			T value = instance.getClassification();
			if(temp.get(value) == null) temp.put(value,  0d);
			temp.put(value, temp.get(value) + instance.getWeight());
			totalWeight += instance.getWeight();
		}
		for (Entry<T,Double> group : temp.entrySet()) {
			Double d = group.getValue() / totalWeight;
			group.setValue(d);
		}
		aPrioriProbability = new EnumMap<T,Double>(temp);
	}

	@Override
	public void configure(Properties props, String prefix) {
		
	}
}
