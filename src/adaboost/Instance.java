package adaboost;

import java.util.Iterator;

/** An instance of a classification problem, templated with the problems class. */
public abstract class Instance<T extends Enum<?>> {
	private double weight;
	private T classification;
	
	/** Get an iterator over the attributes of the instance.
	 * Within a data set, every instance must provide iterators that use the same ordering of attributes.
	 * Missing attributes should be null.*/
	public abstract Iterator<Object> getAttributes();
	
	public T getClassification(){
		return classification;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
}
