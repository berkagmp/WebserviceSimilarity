package derek.project;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.text.similarity.JaccardSimilarity;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import derek.project.algorithm.DiceCoefficient;
import derek.project.algorithm.RemovalStopwords;
import derek.project.algorithm.TFIDF;
import derek.project.algorithm.WordNet;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.WuPalmer;

public class StringSimilarity {

	private static WordNet wn = new WordNet();
	static ILexicalDatabase db = new NictWordNet();
	private static RelatednessCalculator[] rcs = {new WuPalmer(db)};
	/////new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db), new Resnik(db), new JiangConrath(db), , new Path(db) };
	
	private static JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
	private static DiceCoefficient diceCoefficient = new DiceCoefficient();
	
	private static SnowballStemmer snowballStemmer = new englishStemmer();
	
	private static final int DICE = 0;
	private static final int JACCARD = 1;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] str1 = { "lat", "latitude" };
		String[] str2 = { "getLocation", "search.stations.lat.long" };
		String[] str3 = { "live.weather.lat.long", "weather.get" };

		/*
		 * DiceCoefficient diceCoefficient = new DiceCoefficient();
		 * out.println(diceCoefficient.diceCoefficientOptimized(str1[0], str1[1]));
		 * out.println(diceCoefficient.diceCoefficientOptimized(str2[0], str2[1]));
		 * out.println(diceCoefficient.diceCoefficientOptimized(str3[0], str3[1]));
		 * 
		 * LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		 * out.println(levenshteinDistance.levenshteinDistance(str1[0], str1[1]));
		 * out.println(levenshteinDistance.levenshteinDistance(str2[0], str2[1]));
		 * out.println(levenshteinDistance.levenshteinDistance(str3[0], str3[1]));
		 * 
		 * out.println(jaccardSimilarity.apply(str1[0], str1[1]));
		 * out.println(jaccardSimilarity.apply(str2[0], str2[1]));
		 * out.println(jaccardSimilarity.apply(str3[0], str3[1]));
		 */

		TFIDF tfidf = new TFIDF();
		String[] strarr1 = {"flickr","photos","geo","photos", "For", "Location", "Return","list","photos","call","user","specific","latitude","longitude","accuracy","api_key","format","lat","lon","accuracy","extras","per_page","page"}; 
		String[] strarr2 = {"street", "view", "publish","photo","get","photoId","view","Get","metadata","specify","Photo","method","return","following","error","codes"};
		strarr1 = strarr2;
		List<String> doc1 = Arrays.asList(strarr1);
		List<String> doc2 = Arrays.asList(strarr2);
		doc1.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
		doc2.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
		
		// Stemming
		strarr1 = stemmingForArr(strarr1);
		strarr2 = stemmingForArr(strarr2);
		List<String> doc1WithStemming = Arrays.asList(strarr1);
		List<String> doc2WithStemming = Arrays.asList(strarr2);
		doc1WithStemming.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
		doc2WithStemming.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
		
		// Ordering
		Descending descending = new Descending();
        Collections.sort(doc1WithStemming, descending);
        Collections.sort(doc2WithStemming, descending); 
		
	    // Make standard vector
		//List<List<String>> documents = Arrays.asList(doc1, doc2);
		List<List<String>> documents = DocumentGroup.getDocumentGroup(false);
		out.println(documents.size());
				
		List<String> doc3 = new ArrayList<String>();
		doc3.addAll(doc1);
		doc3.addAll(doc2);
		Collections.sort(doc3, descending);
		// Remove redundancy
		HashSet<String> hashSet = new HashSet<>(doc3);
		doc3 = new ArrayList<String>(hashSet);
	
        // Make standard vector with Stemming
		//List<List<String>>  documentsWithStemming = Arrays.asList(doc1WithStemming, doc2WithStemming);
		List<List<String>>  documentsWithStemming = DocumentGroup.getDocumentGroup(true);
		List<String> doc3WithStemming = new ArrayList<String>();
		doc3WithStemming.addAll(doc1WithStemming);
		doc3WithStemming.addAll(doc2WithStemming);
		Collections.sort(doc3WithStemming, descending);
		// Remove redundancy
		hashSet = new HashSet<>(doc3WithStemming);
		doc3WithStemming = new ArrayList<String>(hashSet);

		out.println("Vectors without Stemming");
		out.println(Arrays.toString(doc1.toArray()));
		out.println(Arrays.toString(doc2.toArray()));
		out.println(Arrays.toString(doc3.toArray()));
		
		out.println("Vectors with Stemming");
		out.println(Arrays.toString(doc1WithStemming.toArray()));
		out.println(Arrays.toString(doc2WithStemming.toArray()));
		out.println(Arrays.toString(doc3WithStemming.toArray()));

		double[] v1WithStemming = new double[doc1WithStemming.size()];
		double[] v2WithStemming = new double[doc2WithStemming.size()];

		double[] v1 = new double[doc1.size()];
		double[] v2 = new double[doc2.size()];

		int c = 0;
		for (String w : doc1WithStemming) v1WithStemming[c++] = tfidf.tfIdf(doc1WithStemming, documentsWithStemming, w);
		c = 0;
		for (String w : doc1) v1[c++] = tfidf.tfIdf(doc1, documents, w);
		c = 0;
		for (String w : doc2WithStemming) v2WithStemming[c++] = tfidf.tfIdf(doc2WithStemming, documentsWithStemming, w);
		c = 0;
		for (String w : doc2) v2[c++] = tfidf.tfIdf(doc2, documents, w);
	
		out.println();
		out.println("TF-IDF Vector");
		out.println(Arrays.toString(v1));
		out.println(Arrays.toString(v2));
		out.println("TF-IDF Vector after stemming");
		out.println(Arrays.toString(v1WithStemming));
		out.println(Arrays.toString(v2WithStemming));

		boolean tfidfYn = false;
		double[] d1WithStemmingJ = getVector(doc1WithStemming, doc3WithStemming, v1WithStemming, tfidfYn, JACCARD);
		double[] d2WithStemmingJ = getVector(doc2WithStemming, doc3WithStemming, v2WithStemming, tfidfYn, JACCARD);
		double[] d1J = getVector(doc1, doc3, v1, tfidfYn, JACCARD);
		double[] d2J = getVector(doc2, doc3, v2, tfidfYn, JACCARD);
		double[] d1WithStemmingD = getVector(doc1WithStemming, doc3WithStemming, v1WithStemming, tfidfYn, DICE);
		double[] d2WithStemmingD = getVector(doc2WithStemming, doc3WithStemming, v2WithStemming, tfidfYn, DICE);
		double[] d1D = getVector(doc1, doc3, v1, tfidfYn, DICE);
		double[] d2D = getVector(doc2, doc3, v2, tfidfYn, DICE);

		out.println();
		out.println("Vectors with JACCARD after stemming");
		out.println(Arrays.toString(d1WithStemmingJ));
		out.println(Arrays.toString(d2WithStemmingJ));
		out.println("Vectors with DICE after stemming");
		out.println(Arrays.toString(d1WithStemmingD));
		out.println(Arrays.toString(d2WithStemmingD));
		out.println("Vectors with JACCARD");
		out.println(Arrays.toString(d1J));
		out.println(Arrays.toString(d2J));
		out.println("Vectors with DICE");
		out.println(Arrays.toString(d1D));
		out.println(Arrays.toString(d2D));
		
		out.println("\nCosine JACCARD after stemming: " + cosineSimilarity(d1WithStemmingJ, d2WithStemmingJ));
		out.println("Cosine DICE after stemming: " + cosineSimilarity(d1WithStemmingD, d2WithStemmingD));
		out.println("Cosine JACCARD: " + cosineSimilarity(d1J, d2J));
		out.println("Cosine DICE: " + cosineSimilarity(d1D, d2D));
	
		tfidfYn = true;
		d1WithStemmingJ = getVector(doc1WithStemming, doc3WithStemming, v1WithStemming, tfidfYn, JACCARD);
		d2WithStemmingJ = getVector(doc2WithStemming, doc3WithStemming, v2WithStemming, tfidfYn, JACCARD);
		d1J = getVector(doc1, doc3, v1, tfidfYn, JACCARD);
		d2J = getVector(doc2, doc3, v2, tfidfYn, JACCARD);
		d1WithStemmingD = getVector(doc1WithStemming, doc3WithStemming, v1WithStemming, tfidfYn, DICE);
		d2WithStemmingD = getVector(doc2WithStemming, doc3WithStemming, v2WithStemming, tfidfYn, DICE);
		d1D = getVector(doc1, doc3, v1, tfidfYn, DICE);
		d2D = getVector(doc2, doc3, v2, tfidfYn, DICE);
		out.println("\nTfidf Cosine JACCARD after stemming: " + cosineSimilarity(d1WithStemmingJ, d2WithStemmingJ));
		out.println("Tfidf Cosine DICE after stemming: " + cosineSimilarity(d1WithStemmingD, d2WithStemmingD));
		out.println("Tfidf Cosine JACCARD: " + cosineSimilarity(d1J, d2J));
		out.println("Tfidf Cosine DICE: " + cosineSimilarity(d1D, d2D));
	}
	
	public static String[] stemmingForArr(String[] args) {
		String[] result = new String[args.length];
		
		for(int i=0; i<args.length; i++) {
			snowballStemmer.setCurrent(args[i]);
	        snowballStemmer.stem();
			result[i] = snowballStemmer.getCurrent();
		}
        
        return result;
	}
	
	public static List<String> stemmingForList(List<String> args) {
		List<String> result = new ArrayList<String>();
		
		for(int i=0; i<args.size(); i++) {
			snowballStemmer.setCurrent(args.get(i));
	        snowballStemmer.stem();
	        result.add(snowballStemmer.getCurrent());
		}
        
        return result;
	}

	public static double[] getVector(List<String> doc1, List<String> target, double[] tfidf, boolean tfidfYn, int syntax) {
		double[] result = new double[target.size()];
		int i = 0;
		double temp = 0;
		double wordnet = 0;
		
		for (String s1 : target) {
			temp = 0;

			for (int k = 0; k < doc1.size(); k++) {
				//out.println(s1 + "/" + doc1.get(k));
				if (s1.equals(doc1.get(k))) {
					temp = (tfidfYn) ? tfidf[k] : 1;
					break;
				} else {
					for (int w = 0; w < rcs.length; w++) {
						//wordnet = wn.cal(rcs[w], s1, doc1.get(k));
						
						if(syntax == DICE) {
							wordnet = (wn.calByMax(rcs[w], s1, doc1.get(k)) * 0.7) + (diceCoefficient.diceCoefficientOptimized(s1, doc1.get(k)) * 0.3);
						}else if(syntax == JACCARD){
							wordnet = (wn.calByMax(rcs[w], s1, doc1.get(k)) * 0.7) + (jaccardSimilarity.apply(s1, doc1.get(k)) * 0.3);
						}
						
						if (temp < wordnet) {
							temp = wordnet;
						}
					}

					//out.println("temp-" + temp);
					if (k == doc1.size() - 1)
						temp *= (tfidfYn) ? tfidf[k] : 1;
				}
			}
			//out.println();
			result[i++] = temp;
		}

		return result;
	}

	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;

		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}

		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

}

class Descending implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return o2.compareTo(o1);
    }
}