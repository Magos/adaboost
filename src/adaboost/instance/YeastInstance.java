package adaboost.instance;

import java.util.Iterator;

import adaboost.Instance;

public class YeastInstance extends Instance<YeastEnum> {
	private Object[] values = new Object[9];

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
			double temp = Double.parseDouble(values[i]);
			if(temp == (int)temp){
				this.values[i] = (int) temp;
			}else{
				this.values[i] = temp;
			}
		}
		this.classification = YeastEnum.fromString(values[values.length-1]);
	}

	@Override
	public Class<? extends Enum<?>> getClassEnum() {
		return YeastEnum.class;
	}

}
