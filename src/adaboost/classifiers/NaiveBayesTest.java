package adaboost.classifiers;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import adaboost.CSVLoader;
import adaboost.Instance;
import adaboost.instance.GlassEnum;
import adaboost.instance.GlassInstance;

public class NaiveBayesTest {
	private Set<Instance<GlassEnum>> instances;
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
		Instance<GlassEnum> temp = instances.iterator().next();
		GlassEnum correct = temp.getClassification();
		assertEquals(correct, classifier.classify(temp));
	}

	@Test
	public void testTrain() {
		classifier.train(instances);
		
	}

}
