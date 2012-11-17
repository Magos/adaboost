package adaboost;

import java.util.Iterator;

/** An instance of a classification problem, templated with the problems class. */
public abstract class Instance<T extends Enum<?>> {
	private double weight;
	protected T classification;
	
	/** Get an iterator over the attributes of the instance.
	 * Within a data set, every instance must provide iterators that use the same ordering of attributes.
	 * Missing attributes should be null.*/
	public abstract Iterator<Object> getAttributes();
	
	/** Initialize the instance given this set of serialized values. For use by loaders. */
	public abstract void initialize(String[] values);
	
	public T getClassification(){
		return classification;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = (weight < 0 ? 0 : (weight > 1 ? 1 : weight));
	}
	
}
