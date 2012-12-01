package adaboost.classifiers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import adaboost.Classifier;
import adaboost.Instance;
import adaboost.Properties;

/** A framework class for classifiers whose algorithms rely on finite, discrete attribute spaces.
 * It presents an alternate view of a dataset where any real-valued attributes are discretized into a configurable number of bins, adding O(n) preprocessing time.*/
abstract class DiscreteClassifier<T extends Enum<T>> implements Classifier<T> {
	/** The property used for how many bins this classifier should use on real-valued attributes.*/
	private static final String BINS = "bins";
	private int binCount;
	/** Mappers for translating real-valued attributes into ints. */
	private Map<Integer,Mapper> mappers;

	protected DiscreteClassifier() {
		super();
	}
	
	protected DiscreteClassifier(DiscreteClassifier parent){
		this.mappers = parent.mappers;
	}

	/** Preprocess a training set to create mappers that discretize any real-valued attributes. */
	protected void preprocess(Set<Instance<Enum<?>>> trainingSet) {
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
			for (Iterator<Object> it = instance.getAttributes(); it.hasNext();) {
				Object next = it.next();
				attribute++;
				if(next instanceof Double){
					ret.get(attribute).update((double)next);
				}
			}
		}
		mappers = ret;
	}

	@Override
	public void configure(Properties props, String prefix) {
		binCount = Math.abs(props.getIntProperty(prefix+BINS));
	}
	
	protected Iterator<Integer> getAttributes(final Instance<Enum<?>> instance){
		return new Iterator<Integer>() {
			private Iterator<Object> it = instance.getAttributes();
			private int i = 0;

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public Integer next() {
				Object next = it.next();
				i++;
				if(next instanceof Integer){
					return (int) next;
				}else if(next instanceof Double){
					Mapper mapper = mappers.get(i);
					return mapper.map((double) next);
				}
				return null;
			}

			@Override
			public void remove() {
				it.remove();
			}
		};
	}

	/** A support class for discretizing real-valued attributes. */
	protected class Mapper{
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