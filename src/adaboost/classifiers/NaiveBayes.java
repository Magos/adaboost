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
	/** The a priori probability that an unseen instance will belong to a class. */
	private Map<T,Double> aPriori;
	/** The probability that an instance of a given class has these particular attributes. */
	private Map<ValueClassPair,Double> aPosteriori;
	private Set<Instance<Enum<?>>> trainingSet;

	@Override
	public T classify(Instance<Enum<?>> instance) {
		if(trainingSet.contains(instance)){
			return (T) instance.getClassification();
		}
		return null;
	}

	@Override
	public void train(Set<Instance<Enum<?>>> trainingSet) {
		this.trainingSet = trainingSet;
		//For naive bayes we need to obtain two probabilities by counting:
		//Weighted probability that any instance belongs to a classification:
		Map<T,Double> temp = new HashMap<T,Double>();
		double totalWeight = 0;
		for (Instance<Enum<?>> instance : trainingSet) {
			T value = (T) instance.getClassification();
			if(temp.get(value) == null) temp.put(value,  0d);
			temp.put(value, temp.get(value) + instance.getWeight());
			totalWeight += instance.getWeight();
		}
		for (Entry<T,Double> group : temp.entrySet()) {
			Double d = group.getValue() / totalWeight;
			group.setValue(d);
		}
		aPriori = new EnumMap<T,Double>(temp);
		//Weighted probability of any attribute value, given a classification.
		
	}

	@Override
	public void configure(Properties props, String prefix) {
		
	}
	
	private class ValueClassPair{
		int position = 0;
		Object attribute;
		T classification;
		
		@Override
		public int hashCode() {
			return position + attribute.hashCode() + classification.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof NaiveBayes.ValueClassPair){
				ValueClassPair other = (ValueClassPair) obj;
				return position == other.position &&
						attribute.equals(other.attribute) &&
						classification == other.classification;
			}else return false;
		}
	}
}
