package adaboost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import adaboost.instance.GlassEnum;
import adaboost.instance.GlassInstance;

public class CSVLoaderTest {

	private static final double DELTA = 0.0001d;

	@Test
	public void testLoad() throws InstantiationException, IllegalAccessException, IOException {
		Properties props = new Properties();
		props.put(CSVLoader.DATA_SOURCE_PROPERTY, "res/glass.csv");
		props.put(CSVLoader.DATA_SOURCE_CLASS, GlassInstance.class.getCanonicalName());
		Set<Instance<GlassEnum>> instances = CSVLoader.load(props);
		assertTrue("CSV loaded instance set should be nonempty.", instances.size() > 0);
		double expectedWeight = (1d/214d);
		for (Instance<GlassEnum> instance : instances) {
			assertEquals("Freshly loaded instances should be weighted equally.",instance.getWeight(),expectedWeight,DELTA);
		}
		
	}

}
