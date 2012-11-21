package adaboost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import adaboost.instance.GlassInstance;
import adaboost.instance.NurseryInstance;
import adaboost.instance.PageInstance;
import adaboost.instance.PenInstance;
import adaboost.instance.YeastInstance;

public class CSVLoaderTest {

	private static final double DELTA = 0.0001d;
	private static final String[] SOURCES = new String[]{
		"res/glass.csv", "res/nursery.csv","res/page-blocks.csv","res/pen-digits.csv","res/yeast.csv"
	};
	private static final String[] CLASSES = new String[]{
		GlassInstance.class.getCanonicalName(), NurseryInstance.class.getCanonicalName(), PageInstance.class.getCanonicalName(), PenInstance.class.getCanonicalName(), YeastInstance.class.getCanonicalName()
	};

	@Test 
	public void testLoad() throws InstantiationException, IllegalAccessException, IOException {
		
		Properties props = new Properties();
		for (int i = 0; i < SOURCES.length; i++) {
			props.put(CSVLoader.DATA_SOURCE_PROPERTY, SOURCES[i]);
			props.put(CSVLoader.DATA_SOURCE_CLASS, CLASSES[i]);
			Set<Instance<Enum<?>>>instances = CSVLoader.load(props);
			assertTrue("CSV loaded instance set should be nonempty.", instances.size() > 0);
			double expectedWeight = (1d/instances.size());
			for (Instance<Enum<?>> instance : instances) {
				assertEquals("Freshly loaded instances should be weighted equally.",instance.getWeight(),expectedWeight,DELTA);
			}
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
