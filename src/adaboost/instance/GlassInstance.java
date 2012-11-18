package adaboost.instance;

import java.util.Iterator;

import adaboost.Instance;
/** An Instance class for the Glass dataset.*/
public class GlassInstance extends Instance<GlassEnum> {
	private double[] values;
	
	public GlassInstance(){
		values = new double[8];
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
			this.values[i] = Double.parseDouble(values[i]);
		}
		this.classification = GlassEnum.fromString(values[values.length-1]);
	}

	@Override
	public Class<? extends Enum<?>> getClassEnum() {
		return GlassEnum.class;
	}

}
