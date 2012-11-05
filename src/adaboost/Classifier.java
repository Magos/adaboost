package adaboost;

import java.util.Set;

public interface Classifier<T extends Enum<?>> {
	public T classify(Instance<T> instance);
	public void train(Set<Instance<T>> trainingset);
}
