package adaboost.classifiers;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import adaboost.Classifier;
import adaboost.Instance;

public class NaiveBayes<T extends Enum<T>> implements Classifier<T> {
	private Map<T,Double> probability;

	@Override
	public T classify(Instance<T> instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void train(Set<Instance<T>> trainingset) {
		//Need two probabilities for naive bayes:
		//Weighted probability that any instance belongs to a classification
		Map<T,Double> temp = new HashMap<T,Double>();
		for (Instance<T> instance : trainingset) {
			T value = instance.getClassification();
			if(temp.get(value) == null) temp.put(value,  0d);
			temp.put(value, temp.get(value) + instance.getWeight());
		}
		for (Entry<T,Double> group : temp.entrySet()) {
			Double d = group.getValue() / trainingset.size();
			group.setValue(d);
		}
		probability = new EnumMap<T,Double>(temp);
		//And weighted probability that a set of attributes correspond to a classification. 
	}
}
