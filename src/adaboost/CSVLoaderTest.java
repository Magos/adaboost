package adaboost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import adaboost.instance.GlassEnum;
import adaboost.instance.GlassInstance;
import adaboost.instance.NurseryInstance;

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
		props.put(CSVLoader.DATA_SOURCE_PROPERTY, "res/nursery.csv");
		props.put(CSVLoader.DATA_SOURCE_CLASS, NurseryInstance.class.getCanonicalName());
		instances = CSVLoader.load(props);
		assertTrue("CSV loaded instance set should be nonempty.", instances.size() > 0);
		expectedWeight = (1d/12960d);
		for (Instance<GlassEnum> instance : instances) {
			assertEquals("Freshly loaded instances should be weighted equally.",instance.getWeight(),expectedWeight,DELTA);
		}
	}
	
	@Test (expected = RuntimeException.class)
	public void testUnavailableFile() throws InstantiationException, IllegalAccessException, IOException{
		Properties props = new Properties();
		props.put(CSVLoader.DATA_SOURCE_PROPERTY, "nonexistentFile.csv");
		props.put(CSVLoader.DATA_SOURCE_CLASS, GlassInstance.class.getCanonicalName());
		CSVLoader.load(props);
	}

	@Test (expected = RuntimeException.class)
	public void testUnavailableClass() throws InstantiationException, IllegalAccessException, IOException{
		Properties props = new Properties();
		props.put(CSVLoader.DATA_SOURCE_PROPERTY, "res/glass.csv");
		props.put(CSVLoader.DATA_SOURCE_CLASS, "adaboost.classifiers.NonExistentClass");
		CSVLoader.load(props);
	}
}
