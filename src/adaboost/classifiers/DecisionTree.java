package adaboost.classifiers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import adaboost.Instance;
import adaboost.Properties;

public class DecisionTree<T extends Enum<T>> extends DiscreteClassifier<T> {
	/** The property for what depth is allowed. If not specified, defaults to -1: as many levels as there are attributes.*/
	public static final String DEPTH_LIMIT = "depthLimit";
	/** This node's height, counting from layer, the last layer allowed.*/
	private int height = -1;
	/** Is this a leaf?*/
	private boolean leaf = false;
	/** If this is a leaf, what classification does it belong to?*/
	private T classification = null;
	/** If this isn't a leaf, what attribute does it test? */
	private int attributePosition = -1;
	/** If this isn't a leaf, these are its children. */
	private Map<Integer,DecisionTree<T>> children = null;
	
	public DecisionTree(){
		
	}
	
	protected DecisionTree(Set<Instance<Enum<?>>> value, Set<Integer> newAttributes, int i, final Map<Integer,Mapper> mappers) {
		this.height = i;
		this.mappers = mappers;
		if(i == 0 || newAttributes.size() == 0 || getEntropy(value) == 0d){
			leaf = true;
			classification = getBiggestClassification(value);
		}else{
			buildSubtree(value,newAttributes);
		}
	}

	@SuppressWarnings("unchecked")
	public T getBiggestClassification(Set<Instance<Enum<?>>> value) {
		Map<T,Double> map = new HashMap<T,Double>();
		for (Instance<Enum<?>> instance : value) {
			T classification = (T) instance.getClassification();
			Double weight = map.get(classification);
			if(weight == null){
				weight = new Double(0);
			}
			weight += instance.getWeight();
			map.put(classification, weight);
		}
		double max = 0;
		T ret = null;
		for (Map.Entry<T,Double> entry : map.entrySet()) {
			if(entry.getValue() > max){
				max = entry.getValue();
				ret = entry.getKey();
			}
		}
		return ret;
	}

	@Override
	public T classify(Instance<Enum<?>> instance) {
		if(leaf){
			return classification;
		}else{
			int value = getAttributeValue(attributePosition, instance);
			DecisionTree<T> child = children.get(value);
			if(child == null){
				return null;
			}
			return child.classify(instance);
		}
	}

	@Override
	public void train(Set<Instance<Enum<?>>> trainingSet) {
		super.preprocess(trainingSet);
		int attribute = 0;
		Set<Integer> attributes = new HashSet<Integer>();
		for (Iterator<Integer> iterator = super.getAttributes(trainingSet.iterator().next()); iterator.hasNext();) {
			iterator.next();
			attributes.add(attribute++);
		}
		buildSubtree(trainingSet,attributes);
		
	}

	int getBestPartition(Map<Integer, Map<Integer, Set<Instance<Enum<?>>>>> partitionMap, double setWeight) {
		double min = Double.MAX_VALUE;
		int ret = -1;
		for (Map.Entry<Integer, Map<Integer,Set<Instance<Enum<?>>>>> entry : partitionMap.entrySet()) {
			double partitionEntropy = getPartitionEntropy(entry.getValue(), setWeight);
			if(partitionEntropy < min){
				min = partitionEntropy;
				ret = entry.getKey();
			}
		}
		return ret;
	}

	public double getPartitionEntropy(Map<Integer, Set<Instance<Enum<?>>>> partition, double setWeight) {
		double ret = 0d;
		for (Set<Instance<Enum<?>>> set : partition.values()) {
			double weightFactor = getWeight(set)/setWeight;
			ret += (getEntropy(set)*weightFactor);
		}
		return ret;
	}

	Map<Integer, Map<Integer, Set<Instance<Enum<?>>>>> partitionByAttributes(Set<Instance<Enum<?>>> trainingSet, Set<Integer> attributes) {
		Map<Integer,Map<Integer,Set<Instance<Enum<?>>>>> ret = new HashMap<Integer,Map<Integer,Set<Instance<Enum<?>>>>>();
		for (Integer attributePosition : attributes) {
			Map<Integer,Set<Instance<Enum<?>>>> partition = new HashMap<Integer,Set<Instance<Enum<?>>>>();
			for (Instance<Enum<?>> instance : trainingSet) {
				int attributeValue = getAttributeValue(attributePosition,instance);
				Set<Instance<Enum<?>>> corresponding = partition.get(attributeValue);
				if(corresponding == null){
					corresponding = new HashSet<Instance<Enum<?>>>();
					partition.put(attributeValue, corresponding);
				}
				corresponding.add(instance);
			}
			ret.put(attributePosition,partition);
		}
		return ret;
	}

	private void buildSubtree(Set<Instance<Enum<?>>> instances, Set<Integer> attributes){
		double setWeight = getWeight(instances);
		Map<Integer,Map<Integer,Set<Instance<Enum<?>>>>> partitionMap = partitionByAttributes(instances, attributes);
		int bestChoice = getBestPartition(partitionMap, setWeight);
		this.attributePosition = bestChoice;
		Set<Integer> newAttributes = new HashSet<Integer>(attributes);
		newAttributes.remove(bestChoice);
		children = new HashMap<Integer,DecisionTree<T>>();
		for (Map.Entry<Integer, Set<Instance<Enum<?>>>> partitionSet : partitionMap.get(bestChoice).entrySet()) {
			Set<Instance<Enum<?>>> instanceSet = partitionSet.getValue();
			DecisionTree<T> child = new DecisionTree<T>(instanceSet,newAttributes,height-1,mappers);
			children.put(partitionSet.getKey(), child);
		}
	}
	
	
	private double getWeight(Set<Instance<Enum<?>>> instances) {
		double ret = 0d;
		for (Instance<Enum<?>> instance : instances) {
			ret += instance.getWeight();
		}
		return ret;
	}

	@Override
	public void configure(Properties props, String prefix) {
		int limit = props.getIntProperty(prefix + DEPTH_LIMIT);
		if(limit == -1){
			height = Integer.MAX_VALUE;
		}else{
			height = Math.abs(limit);
		}
	}

	private int getAttributeValue(int position, Instance<Enum<?>> instance) {
		int i = 0;
		Iterator<Integer> it = super.getAttributes(instance);
		while(i++ < position){
			it.next();
		}
		return it.next();
	}
	
	@SuppressWarnings("unchecked")
	public double getEntropy(Set<Instance<Enum<?>>> trainingSet){
		Map<T,Integer> counts = new HashMap<T,Integer>();
		for (Instance<?> instance : trainingSet) {
			T instanceClass = (T) instance.getClassification();
			Integer count = counts.get(instanceClass);
			if(count == null){
				count = new Integer(0);
			}
			counts.put(instanceClass, count+1);
		}
		double ret = 0;
		for (Map.Entry<T, Integer> entry : counts.entrySet()) {
			double p = (((double)entry.getValue()) / ((double)trainingSet.size()));
			ret -= p*(Math.log(p)/Math.log(2));//Log2(x) = log(x)/log(2)
		}
		return ret;
	}
}
