package adaboost.instance;

import java.util.Iterator;

import adaboost.Instance;

/** An Instance class for the Glass dataset.*/
public class NurseryInstance extends Instance<NurseryEnum> {
	private int[] values;
	
	public NurseryInstance(){
		values = new int[8];
	}

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
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = Integer.parseInt(values[i]);
		}
		this.classification = NurseryEnum.fromString(values[values.length-1]);
	}

}
