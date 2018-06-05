package derek.project.algorithm;

import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

/**
 * 
 * @author
 *
 */
public class WordNet {
	private static double compute(RelatednessCalculator rcs, String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
		double s = rcs.calcRelatednessOfWords(word1, word2);
		return s;
	}

	public void cal(RelatednessCalculator rcs, String[] words) {
		for (int i = 0; i < words.length - 1; i++) {
			for (int j = i + 1; j < words.length; j++) {
				double distance = compute(rcs, words[i], words[j]);
				System.out.println(words[i] + " -  " + words[j] + " = " + distance);
			}
		}
	}

	public double cal(RelatednessCalculator rcs, String w1, String w2) {
		return compute(rcs, w1, w2);
	}

}
