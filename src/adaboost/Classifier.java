package adaboost;

import java.util.Set;

/** A classifier of data instances into enumerated classes. Templated with the class enumeration. */
public interface Classifier<T extends Enum<?>> {
	/** Attempt to classify this instance.*/
	public T classify(Instance<Enum<?>> instance);
	/** Use this (labeled) instance set to train on.*/
	public void train(Set<Instance<Enum<?>>> trainingSet);
	/** Retrieve and use any configuration properties necessary for the implementation from these Properties.
	 * Any relevant property should have the prefix indicated. 
	 * The properties "count" and "class" are reserved within this name space.*/
	public void configure(Properties props, String prefix);
}
