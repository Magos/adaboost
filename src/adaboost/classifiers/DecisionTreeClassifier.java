package adaboost.classifiers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import adaboost.Instance;
import adaboost.Properties;

/** A classifier that builds a tree of questions about instance attributes to separate classes from one another. */
public class DecisionTreeClassifier<T extends Enum<T>> extends DiscreteClassifier<T> {
	/** The property for what depth is allowed. If not specified, defaults to -1: as many levels as there are attributes.*/
	public static final String DEPTH_LIMIT = "depthLimit";
	private DecisionTree root;
	private int depthLimit = -1;
	
	@Override
	public T classify(Instance<Enum<?>> instance) {
		return root.classify(instance);
	}

	@Override
	public void train(Set<Instance<Enum<?>>> trainingset) {
		super.preprocess(trainingset);
		int attribute = 0;
		Set<Integer> attributes = new HashSet<Integer>();
		for (Iterator<Integer> iterator = getAttributes(trainingset.iterator().next()); iterator.hasNext();) {
			iterator.next();
			attributes.add(attribute++);
		}
		root = new DecisionTree(trainingset,attributes,0);
	}

	@Override
	public void configure(Properties props, String prefix) {
		super.configure(props, prefix);
		String depthLim = (String) props.get(prefix + DEPTH_LIMIT);
		if(depthLim != null){
			try{
				int temp = Integer.parseInt(depthLim);
				if(temp == -1){
					depthLimit = temp;
				}else{
					depthLimit = Math.abs(temp);
				}
			}catch(NumberFormatException e){
				throw new RuntimeException("Bad key for decision tree classifier's depth limit.",e);
			}
		}
	}

	class DecisionTree{
		private int attributePosition;
		private T classification;
		private boolean isUnknown = false;
		private Map<Integer,DecisionTree> children;

		public T classify(Instance<Enum<?>> instance){
			if(classification != null || isUnknown){
				return classification;
			}else{
				int position = 0;
				Iterator<Integer> it = getAttributes(instance);
				while(position < attributePosition){
					it.next();
				}
				DecisionTree child = children.get(it.next());
				if(child == null){
					return null;
				}else{
					return child.classify(instance);
				}
			}
		}

		public DecisionTree(Set<Instance<Enum<?>>> trainingSet, Set<Integer> attributes, int depth) {
			double trainingSetWeight = getWeight(trainingSet);
			if(trainingSet == null || trainingSet.isEmpty()){
				this.classification = null;
				this.isUnknown = true;
			}
			//Are we at the depth limit? 
			if(depth == depthLimit || attributes.isEmpty()){
				//If so, we answer with a weighted majority vote of our instances.
				Map<T,Double> weights = new HashMap<T,Double>();
				for (Instance<Enum<?>> instance : trainingSet) {
					Double classificationWeight = weights.get(instance.getClassification());
					double weight = instance.getWeight() + (classificationWeight == null ? 0 : classificationWeight);
					weights.put((T) instance.getClassification(), weight);
				}
				double max = 0d;
				T chosen = null;
				for (Map.Entry<T, Double> entry : weights.entrySet()) {
					if(max < entry.getValue()){
						max = entry.getValue();
						chosen = entry.getKey();
					}
				}
				classification = chosen;
			}else{
				double entropy = getEntropy(trainingSet);
				if(entropy == 0){
					//Consider naive case: Entropy 0 means perfect separation.
					classification = (T) trainingSet.iterator().next().getClassification();
					return;
				}
				//Attempt to partition set by every attribute available.
				Map<Integer,Map<Integer,Set<Instance<Enum<?>>>>> partitions = partitionByAttributes(trainingSet, attributes);
				//Calculate entropy changes of the partitions.
				int chosen = -1;
				double chosenEntropy = Double.POSITIVE_INFINITY;
				Map<Integer,Double> entropies = new HashMap<Integer,Double>();
				for (Map.Entry<Integer,Map<Integer,Set<Instance<Enum<?>>>>> partition : partitions.entrySet()) {
					double partitionEntropy = 0;
					for (Set<Instance<Enum<?>>> subset : partition.getValue().values()){
						double setEntropy = getEntropy(subset);
						double weightFactor = getWeight(subset)/trainingSetWeight;
						partitionEntropy += (setEntropy * weightFactor);
					}
					entropies.put(partition.getKey(),partitionEntropy);
					if(partitionEntropy < chosenEntropy){
						chosenEntropy = partitionEntropy;
						chosen = partition.getKey();
					}
				}
				assert (chosen > -1);
				if(chosen == -1){
//					System.out.println("Chosen was -1! There were " + entropies );
					chosen = attributes.iterator().next();
				}
				//Recursively construct children.
				children = new HashMap<Integer, DecisionTree>();
				Map<Integer, Set<Instance<Enum<?>>>> chosenPartition = partitions.get(chosen);
				Set<Map.Entry<Integer, Set<Instance<Enum<?>>>>> entrySet = chosenPartition.entrySet();
				for (Entry<Integer, Set<Instance<Enum<?>>>>  partition : entrySet) {
					Set<Integer> newAttributes = new HashSet<Integer>(attributes);
					newAttributes.remove(chosen);
					DecisionTree child = new DecisionTree(partition.getValue(), newAttributes, depth+1);
					children.put(partition.getKey(), child);
				}
			}
		}

		private double getWeight(Set<Instance<Enum<?>>> subset) {
			double ret = 0;
			for (Instance<Enum<?>> instance : subset) {
				ret += instance.getWeight();
			}
			return ret;
		}

		private Map<Integer, Map<Integer, Set<Instance<Enum<?>>>>> partitionByAttributes(Set<Instance<Enum<?>>> trainingSet, Set<Integer> attributes) {
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

		private int getAttributeValue(int position, Instance<Enum<?>> instance) {
			int i = 0;
			Iterator<Integer> it = getAttributes(instance);
			while(i++ < position){
				it.next();
			}
			return it.next();
		}

		private double getEntropy(Set<Instance<Enum<?>>> trainingSet){
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
				double p = (((double)entry.getValue()) / trainingSet.size());
				ret -= p*(Math.log(p)/Math.log(2));//Log2(x) = log(x)/log(2)
			}
			return ret;
		}
	}

}
