package adaboost.classifiers;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import adaboost.CSVLoader;
import adaboost.Instance;
import adaboost.Properties;
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
		classifier.configure(props, "adaboost.classifiers.0.");
	}

	@Test
	public void testClassify() {
		classifier.train(instances);
		int correctAmount = 0;
		for (Instance<Enum<?>> instance : instances) {
			GlassEnum correct = (GlassEnum) instance.getClassification();
			correctAmount += (correct == classifier.classify(instance) ? 1 : 0);
		}
		double correctProportion = (((double)correctAmount )/ ((double)instances.size()));
		assertTrue("Proportion correctly classified : " + correctProportion,correctProportion > 0.5d);
	}

}
