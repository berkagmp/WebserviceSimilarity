package derek.project;

import static java.lang.System.out;

import org.apache.commons.text.similarity.JaccardSimilarity;

import derek.project.algorithm.DiceCoefficient;
import derek.project.algorithm.LevenshteinDistance;

/**
 * Test for Dice, Jaccard Coefficient and Levenshtein Distance
 * @author Derek
 */
public class SyntaxBasedTest {
	public static void main(String[] args) {
		String[] str1 = { "lat", "latitude" };
		String[] str2 = { "getLocation", "search.stations.lat.long" };
		String[] str3 = { "live.weather.lat.long", "weather.get" };

		DiceCoefficient diceCoefficient = new DiceCoefficient();
		out.println(diceCoefficient.diceCoefficientOptimized(str1[0], str1[1]));
		out.println(diceCoefficient.diceCoefficientOptimized(str2[0], str2[1]));
		out.println(diceCoefficient.diceCoefficientOptimized(str3[0], str3[1]));

		LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		out.println(levenshteinDistance.levenshteinDistance(str1[0], str1[1]));
		out.println(levenshteinDistance.levenshteinDistance(str2[0], str2[1]));
		out.println(levenshteinDistance.levenshteinDistance(str3[0], str3[1]));

		JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
		out.println(jaccardSimilarity.apply(str1[0], str1[1]));
		out.println(jaccardSimilarity.apply(str2[0], str2[1]));
		out.println(jaccardSimilarity.apply(str3[0], str3[1]));
	}
}
