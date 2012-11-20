package adaboost.classifiers;

import java.util.Set;

import adaboost.Classifier;
import adaboost.Instance;
import adaboost.Properties;

/** A classifier that builds a tree of questions about instance attributes to separate classes from one another. */
public class DecisionTree<T extends Enum<T>> extends DiscreteClassifier<T> {

	@Override
	public T classify(Instance<Enum<?>> instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void train(Set<Instance<Enum<?>>> trainingset) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(Properties props, String prefix) {
		super.configure(props, prefix);
		
	}

}
