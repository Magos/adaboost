package adaboost.classifiers;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private static int binCount;
	/** Mappers for translating real-valued attributes into ints. */
	private Map<Integer,Mapper> mappers = new HashMap<Integer,Mapper>();

	@SuppressWarnings("unchecked")
	@Override
	public T classify(Instance<Enum<?>> instance) {
		if(trainingSet.contains(instance)){
			return (T) instance.getClassification();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
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
		//First: Divide training set into per-class sets.
		Map<T,Set<Instance<Enum<?>>>> partition = new HashMap<T,Set<Instance<Enum<?>>>>();
		for (Instance<Enum<?>> instance : trainingSet) {
			Set<Instance<Enum<?>>> correspondingSet = partition.get(instance.getClassification());
			if(correspondingSet == null){
				correspondingSet = new HashSet<Instance<Enum<?>>>();
				partition.put((T) instance.getClassification(), correspondingSet);
			}
			correspondingSet.add(instance);
			
		}
		//Preprocess to identify attribute ranges and create discretization scheme.
		preprocess(trainingSet);
		//For each per-class set, count instances of each discrete attribute value.
		for (Entry<T, Set<Instance<Enum<?>>>> group : partition.entrySet()) {
			T classification = group.getKey();
			
			for (Instance<Enum<?>> instance : group.getValue()) {
				
			}
		}
	}

	/** Preprocess a training set to set up mappers that discretize any real-valued attributes. */
	private void preprocess(Set<Instance<Enum<?>>> trainingSet) {
		Iterator<Object> attributes = trainingSet.iterator().next().getAttributes();
		int attributeCount = 0;
		Map<Integer,Class<?>> activeClasses = new HashMap<Integer,Class<?>>();
		while(attributes.hasNext()){
			attributeCount++;
			Object next = attributes.next();
			Class<?> activeClass= next.getClass();
			activeClasses.put(attributeCount,activeClass);
			if(next instanceof Double){
				mappers.put(attributeCount, new Mapper((double)next, (double)next));
			}
		}
		for (Instance<Enum<?>> instance : trainingSet) {
			int attribute = 0;
			for (Iterator it = instance.getAttributes(); it.hasNext();) {
				Object next = it.next();
				attribute++;
				if(next instanceof Double){
					mappers.get(attribute).update((double)next);
				}
			}
		}
	}

	@Override
	public void configure(Properties props, String prefix) {
		binCount = Math.abs(props.getIntProperty(prefix+"bins"));
	}
	
	/** A collection class for holding the information needed to look up an attribute probability.*/
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
	
	/** A support class for discretizing real-valued attributes. */
	private class Mapper{
		double max, min;
		
		Mapper(double max, double min){
			this.max = max;
			this.min = min;
		}
		
		int map(double in){
			return (int) ((in - min)*binCount/max);
		}
		
		void update(double in){
			if(in < min) min = in;
			if(in > max) max = in;
		}
	}
}
