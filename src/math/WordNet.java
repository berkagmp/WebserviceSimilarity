package math;

import java.util.List;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class WordNet {
	// private static ILexicalDatabase db = new NictWordNet();

	// available options of metrics
	// private static RelatednessCalculator[] rcs = { new HirstStOnge(db), new
	// LeacockChodorow(db), new Lesk(db), new WuPalmer(db), new Resnik(db), new
	// JiangConrath(db), new Lin(db), new Path(db) };

	private static double compute(RelatednessCalculator rcs, String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
		double s = rcs.calcRelatednessOfWords(word1, word2);
		return s;
	}

	private static double computeByMax(RelatednessCalculator rc, String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
		ILexicalDatabase db = new NictWordNet();
		// double s = rcs.calcRelatednessOfWords(word1, word2);

		List<POS[]> posPairs = rc.getPOSPairs();
		double maxScore = -1D;

		for (POS[] posPair : posPairs) {
			List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(word1, posPair[0].toString());
			List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(word2, posPair[1].toString());

			for (Concept synset1 : synsets1) {
				for (Concept synset2 : synsets2) {
					Relatedness relatedness = rc.calcRelatednessOfSynset(synset1, synset2);
					double score = relatedness.getScore();
					if (score > maxScore) {
						maxScore = score;
					}
				}
			}
		}

		if (maxScore == -1D) {
			maxScore = 0.0;
		}

		return maxScore;
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
	
	public double calByMax(RelatednessCalculator rcs, String w1, String w2) {
		return computeByMax(rcs, w1, w2);
	}
}
