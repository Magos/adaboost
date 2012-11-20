package adaboost.classifiers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import adaboost.Classifier;
import adaboost.Instance;
import adaboost.Properties;

/** A classifier that builds a tree of questions about instance attributes to separate classes from one another. */
public class DecisionTreeClassifier<T extends Enum<T>> extends DiscreteClassifier<T> {
	/** The property for what depth is allowed. If not specified, defaults to -1: as many levels as there are attributes.*/
	private static final String DEPTH_LIMIT = "depthLimit";
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

	private class DecisionTree{
		private int attributePosition;
		private T classification;
		private Map<Integer,DecisionTree> children;
		
		public T classify(Instance<Enum<?>> instance){
			if(classification != null){
				return classification;
			}else{
				int position = 0;
				Iterator<Integer> it = getAttributes(instance);
				while(position < attributePosition){
					it.next();
				}
				return children.get(it.next()).classify(instance);
			}
		}
		
		public DecisionTree(Set<Instance<Enum<?>>> trainingSet, Set<Integer> attributes, int depth) {
			//Are we at the depth limit? 
			if(depth == depthLimit){
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
				//Attempt to partition set by every attribute available.
				
				//Calculate entropies of the partitions.
				//Recursively construct children.
			}
		}
		
		private double getEntropy(Set<Instance<?>> set){
			Map<T,Double> weights = new HashMap<T,Double>();
			double totalWeight = 0;
			for (Instance<?> instance : set) {
				Object instanceClass = instance.getClassification();
				double weight = instance.getWeight() + 
						(weights.get(instanceClass != null ? weights.get(instanceClass) : 0d ));
				weights.put((T) instanceClass, weight);
				totalWeight += instance.getWeight();
			}
			double ret = 0;
			for (Map.Entry<T, Double> entry : weights.entrySet()) {
				double p = entry.getValue()/totalWeight;
				ret -= p*(Math.log(p)/Math.log(2));//Log2(x) = log(x)/log(2)
			}
			return ret;
		}
	}
	
}
