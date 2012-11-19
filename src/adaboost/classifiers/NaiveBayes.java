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
	private static int binCount;
	/** Mappers for translating real-valued attributes into ints. */
	private Map<Integer,Mapper> mappers = new HashMap<Integer,Mapper>();

	@SuppressWarnings("unchecked")
	@Override
	public T classify(Instance<Enum<?>> instance) {
		ValueClassPair key = new ValueClassPair();
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void train(Set<Instance<Enum<?>>> trainingSet) {
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
		//For each per-class set, count instances of each attribute value and tabulate probabilities.
		aPosteriori = new HashMap<ValueClassPair,Double>();
		for (Entry<T, Set<Instance<Enum<?>>>> group : partition.entrySet()) {
			T classification = group.getKey();
			double totalClassWeight = 0;
			Map<ValueClassPair,Double> tempMap = new HashMap<ValueClassPair,Double>();
			//Make counts.
			for (Instance<Enum<?>> instance : group.getValue()) {
				int attribute = 0;
				for (Iterator<Object> it = instance.getAttributes(); it.hasNext();) {
					Object next = it.next();
					attribute++;
					double instanceWeight = instance.getWeight();
					totalClassWeight += instanceWeight;

					ValueClassPair vcPair = new ValueClassPair();
					vcPair.attribute = attribute;
					vcPair.classification = classification;
					
					if(next instanceof Double){
						vcPair.attribute = mappers.get(attribute).map((double) next);
					}else{
						vcPair.attribute = (int) next;
					}
					
					if(tempMap.containsKey(vcPair)){
						double updated = tempMap.get(vcPair) + instanceWeight;
						tempMap.put(vcPair,updated);
					}else{
						tempMap.put(vcPair,instanceWeight);
					}
				}
			}
			//Produce probabilities from counts.
			for (Entry<ValueClassPair,Double> entry : tempMap.entrySet()) {
				entry.setValue(entry.getValue() / totalClassWeight);
			}
			//Update permanent storage.
			aPosteriori.putAll(tempMap);
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
		int attribute;
		T classification;
		
		@Override
		public int hashCode() {
			return position + 31*attribute + classification.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof NaiveBayes.ValueClassPair){
				ValueClassPair other = (ValueClassPair) obj;
				return position == other.position &&
						attribute == other.attribute &&
						classification == other.classification;
			}else return false;
		}
		
		@Override
		public String toString() {
			return "position " + position + " attribute "  + attribute + " class " + classification;
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
