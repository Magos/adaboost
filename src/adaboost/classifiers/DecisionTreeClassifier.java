package adaboost.classifiers;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import adaboost.Classifier;
import adaboost.Instance;
import adaboost.Properties;

/** A classifier that builds a tree of questions about instance attributes to separate classes from one another. */
public class DecisionTreeClassifier<T extends Enum<T>> extends DiscreteClassifier<T> {
	private DecisionTree root;
	@Override
	public T classify(Instance<Enum<?>> instance) {
		return root.classify(instance);
	}

	@Override
	public void train(Set<Instance<Enum<?>>> trainingset) {
		super.preprocess(trainingset);
		DecisionTree root = new DecisionTree(trainingset);

	}

	@Override
	public void configure(Properties props, String prefix) {
		super.configure(props, prefix);
		
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
		
		public DecisionTree(Set<Instance<Enum<?>>> trainingset) {
			//Attempt to partition set by every attribute.
			//Calculate entropies of the partitions.
			//Recursively construct children.
		}
	}
	
}
