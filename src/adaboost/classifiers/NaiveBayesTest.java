package adaboost.classifiers;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import adaboost.CSVLoader;
import adaboost.Instance;
import adaboost.instance.GlassEnum;

public class NaiveBayesTest {
	private Set<Instance<Enum<?>>> instances;
	private NaiveBayes<GlassEnum> classifier;
	
	@Before
	public void setUp() throws Exception {
		Properties props =  new Properties();
		props.load(new FileInputStream("properties/test.properties"));
		instances = CSVLoader.load(props);
		classifier = new NaiveBayes<GlassEnum>();
	}

	@Test
	public void testClassify() {
		classifier.train(instances);
		Instance<Enum<?>> temp = instances.iterator().next();
		GlassEnum correct = (GlassEnum) temp.getClassification();
		assertEquals(correct, classifier.classify(temp));
	}

}
