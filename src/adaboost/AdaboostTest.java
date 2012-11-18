package adaboost;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


public class AdaboostTest {

	private static final String[] TEST_ARGS = new String[]{"properties/test.properties"};

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPartitionDataSet() throws IOException, InstantiationException, IllegalAccessException {
		Properties props = Adaboost.loadProperties(TEST_ARGS);
		Set<Instance<Enum<?>>> set = CSVLoader.load(props);
		Set<Instance<Enum<?>>> trainingSet = new HashSet<Instance<Enum<?>>>();
		Set<Instance<Enum<?>>> testingSet = new HashSet<Instance<Enum<?>>>();
		double proportion = 0.2d;
		Adaboost.partitionDataSet(set, trainingSet, testingSet, proportion);
		double realProportion = ((double) testingSet.size())/((double) set.size());
		assertEquals(proportion,realProportion,0.001d);
	}

	@Test
	public void testLoadProperties() throws IOException {
		Properties props = Adaboost.loadProperties(TEST_ARGS);
		assertNotNull(props);
		assertTrue("Loaded properties should be nonempty", props.size() > 0);
	}
	
	@Test (expected = IOException.class)
	public void testLoadUnavailableProperties() throws IOException {
		Adaboost.loadProperties(new String[]{"nonexistent.properties"});
	}
	
	@Test
	public void testMain(){
		Adaboost.main(TEST_ARGS);
	}
	
	@Test (expected = RuntimeException.class)
	public void testMainEmpty(){
		Adaboost.main(new String[0]);
	}

}
