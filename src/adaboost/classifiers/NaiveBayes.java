package adaboost.classifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	private Map<Integer,Mapper> mappers;
	/** Workaround for not being able to retrieve the .values() of an enum T that is unbound at compile time. Instead, use all T values observed at train time.*/
	private Set<Enum<?>> observedTs;

	public NaiveBayes(){
		aPosteriori = new HashMap<ValueClassPair,Double>();
		observedTs = new HashSet<Enum<?>>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T classify(Instance<Enum<?>> instance) {
		//Calculate probabilities using naive bayesian assumption.
		Map<T,Double> probabilities = new HashMap<T,Double>(aPriori); //(non-normalized) probabilites are derived as a priori multiplied by by every attribute value probability.
		int attributePosition = 0;
		for (Iterator<Object> it = instance.getAttributes(); it.hasNext();) {
			attributePosition++;
			Object next = it.next();
			ValueClassPair vc = new ValueClassPair();
			vc.position = attributePosition;
			int attributeValue;
			if(next instanceof Double){
				attributeValue = mappers.get(attributePosition).map((double) next);
			}else{
				attributeValue = (int) next;
			}
			vc.attribute = attributeValue;
			for (Enum<?> classification : observedTs) {
				vc.classification = (T) classification;
				double update = probabilities.get(classification);
				double attrProb = (aPosteriori.containsKey(vc) ? aPosteriori.get(vc) : 0d); //Assume any not previously observed have 0 probability.
				update *= attrProb;
				probabilities.put((T) classification,update);
			}
		}
		//Find the most likely class and return it.
		double min = 0d;
		T ret = null;
		for (Map.Entry<T, Double> entry : probabilities.entrySet()) {
			if(entry.getValue() > min){
				min = entry.getValue();
				ret = entry.getKey();
			}
		}
		
		return ret;
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
			observedTs.add(instance.getClassification());
		}
		//Preprocess to identify attribute ranges and create discretization scheme.
		mappers = preprocess(trainingSet);
		//For each per-class set, count instances of each attribute value and tabulate probabilities.

		for (Entry<T, Set<Instance<Enum<?>>>> group : partition.entrySet()) {
			T classification = group.getKey();
			double totalClassWeight = 0;
			Map<ValueClassPair,Double> tempMap = new HashMap<ValueClassPair,Double>();
			//Make counts.
			for (Instance<Enum<?>> instance : group.getValue()) {
				int attributePosition = 0;
				for (Iterator<Object> it = instance.getAttributes(); it.hasNext();) {
					Object next = it.next();
					attributePosition++;
					double instanceWeight = instance.getWeight();
					totalClassWeight += instanceWeight;

					ValueClassPair vcPair = new ValueClassPair();
					vcPair.position = attributePosition;
					vcPair.classification = classification;
					
					if(next instanceof Double){
						vcPair.attribute = mappers.get(attributePosition).map((double) next);
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

	/** Preprocess a training set to create mappers that discretize any real-valued attributes. */
	private Map<Integer, Mapper> preprocess(Set<Instance<Enum<?>>> trainingSet) {
		Map<Integer, Mapper> ret = new HashMap<Integer,Mapper>();
		Iterator<Object> attributes = trainingSet.iterator().next().getAttributes();
		int attributeCount = 0;
		Map<Integer,Class<?>> activeClasses = new HashMap<Integer,Class<?>>();
		while(attributes.hasNext()){
			attributeCount++;
			Object next = attributes.next();
			Class<?> activeClass= next.getClass();
			activeClasses.put(attributeCount,activeClass);
			if(next instanceof Double){
				ret.put(attributeCount, new Mapper((double)next, (double)next));
			}
		}
		for (Instance<Enum<?>> instance : trainingSet) {
			int attribute = 0;
			for (Iterator it = instance.getAttributes(); it.hasNext();) {
				Object next = it.next();
				attribute++;
				if(next instanceof Double){
					ret.get(attribute).update((double)next);
				}
			}
		}
		return ret;
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
			int ret = 0;
			ret = ret*31 + attribute;
			ret = ret*31 + position;
			ret = ret*31 + classification.hashCode();
			return ret;
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
