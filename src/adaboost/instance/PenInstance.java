package adaboost.instance;

import java.util.Iterator;

import adaboost.Instance;

public class PenInstance extends Instance<PenEnum>{
	private int[] values = new int[17];

	@Override
	public Iterator<Object> getAttributes() {
		return new Iterator<Object>() {
			private int i;

			@Override
			public boolean hasNext() {
				return i < values.length;
			}

			@Override
			public Object next() {
				return values[i++];
			}

			@Override
			public void remove() {
				//Non-removing implementation.
			}
			
		};
	}

	@Override
	public void initialize(String[] values) {
		for (int i = 0; i < values.length; i++) {
			this.values[i] = Integer.parseInt(values[i]);
		}
		this.classification = PenEnum.fromString(values[values.length-1]);
	}

	@Override
	public Class<? extends Enum<?>> getClassEnum() {
		return PenEnum.class;
	}
}