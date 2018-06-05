package derek.project;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.text.similarity.JaccardSimilarity;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import derek.project.algorithm.DiceCoefficient;
import derek.project.algorithm.RemovalStopwords;
import derek.project.algorithm.TFIDF;
import derek.project.algorithm.WordNet;
import derek.project.config.AppConfig;
import derek.project.dao.ApiMethodDao;
import derek.project.model.ApiMethod;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.WuPalmer;

/**
 * Main Class for the similarity measurement
 * @author Derek
 */
public class App {

	// WS4J
	private static WordNet wn = new WordNet();
	static ILexicalDatabase db = new NictWordNet();
	private static RelatednessCalculator rcs = new WuPalmer(db);

	// Syntax-based Similarity
	private static JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
	private static DiceCoefficient diceCoefficient = new DiceCoefficient();
	private static final int DICE = 0;
	private static final int JACCARD = 1;

	// Stemming library
	private static SnowballStemmer snowballStemmer = new englishStemmer();
	
	private static boolean logging = false; 
	
	public static void main(String[] args) {
		realMeasurement();
		//SimilarityExperiment();
	}
	
	/**
	 * Calculate the similarity of two vectors from DB
	 */
	public static void realMeasurement() {
		List<List<String>> d = DocumentGroup.getDocumentGroup(false, false);
		List<List<String>> ds = DocumentGroup.getDocumentGroup(true, false);
		
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		ApiMethodDao apiMethodDao = context.getBean(ApiMethodDao.class);
		
		List<ApiMethod> list = apiMethodDao.list();
		
		ApiMethod api1; 
		String[] method1;
		String[] description1;
		List<String[]> list1;
		
		ApiMethod api2; 
		String[] method2;
		String[] description2;
		List<String[]> list2;
		
		for(int i=2500; i<list.size(); i++) {
			api1 = list.get(i);
			
			method1 = api1.getMethod().split(" ");
			description1 = api1.getMethod().split(" ");
					
			list1 = new ArrayList<>();
			list1.add(method1);
			list1.add(description1);
			
			for(int j=i+1; j<list.size(); j++) {
				api2 = list.get(j);
				
				method2 = api2.getMethod().split(" ");
				description2 = api2.getMethod().split(" ");
				
				list2 = new ArrayList<>();
				list2.add(method2);
				list2.add(description2);
				
				calSimilarity(DocumentGroup.mergeLists(list1), DocumentGroup.mergeLists(list2), d, ds, i, j);
			}
		}
	}
	
	/**
	 * Experimentalise the similarity measurement between some data
	 */
	public static void SimilarityExperiment() {
		// Make arrays
		String[] resource1 = {"flickr"};
		String[] method1 = {"flickr","photos","geo","photos","for","location"};
		String[] description1 = {"return","a","list","of","photos","for","the","calling","user","at","a","specific","latitude","longitude","and","accuracy"};
		String[] parameters1 = {"api","key","","rest","","lat","","lon","","accuracy","","extras","","per","page","","page"};
		
		String[] resource2 = {"flickr"};
		String[] method2 = {"flickr","photos","notes","edit"};
		String[] description2 = {"Edit","a","note","on","a","photo.","Coordinates","and","sizes","are","in","pixels,","based","on","the","500px","image","size","shown","on","individual","photo","pages."};
		String[] parameters2 = {"api_key","note_id","note_x","note_y","note_w","note_h","note_text"};
		
		String[] resource3 = {"onenote"};
		String[] method3 = {"get", "notebook", "sections"};
		String[] description3 = {"returns","a","collection","of","sections","within","a","specific","notebook"};
		String[] parameters3 = {"count","","filter","","orderby","","select","","top","","expand","","skip"};
		
		String[] resource4 = {"flickr"};
		String[] method4 = {"flickr","photos","geo","getlocation"};
		String[] description4 = {"get","the","geo","data","latitude","and","longitude","and","the","accuracy","level","for","a","photo"};
		String[] parameters4 = {"api","key","","rest","","photo","id"};
		
		List<String[]> list1 = new ArrayList<>();
		List<String[]> list2 = new ArrayList<>();
		List<String[]> list3 = new ArrayList<>();
		List<String[]> list4 = new ArrayList<>();
		//list1.add(resource1);	list2.add(resource2);	list3.add(resource3);	list4.add(resource4);
		list1.add(method1);		list2.add(method2);		list3.add(method3);		list4.add(method4);
		list1.add(description1);list2.add(description2);list3.add(description3);list4.add(description4);
		list1.add(parameters1);	list2.add(parameters2);	list3.add(parameters3);	list4.add(parameters4);
		
		List<List<String>> d = DocumentGroup.getDocumentGroup(false, true);
		List<List<String>> ds = DocumentGroup.getDocumentGroup(true, true);
		
		calSimilarity(DocumentGroup.mergeLists(list1), DocumentGroup.mergeLists(list2), d, ds);
		calSimilarity(DocumentGroup.mergeLists(list1), DocumentGroup.mergeLists(list3), d, ds);
		calSimilarity(DocumentGroup.mergeLists(list1), DocumentGroup.mergeLists(list4), d, ds);
	}
	
	/**
	 * Make a standard vector including two vectors
	 * @param v1 The first vector
	 * @param v2 The second vector
	 * @param sort Descending sort
	 * @return Vector
	 */
	public static List<String> makeStandardVector(List<String> v1, List<String> v2, Descending sort){
		List<String> vector = new ArrayList<String>();
		vector.addAll(v1);
		vector.addAll(v2);
		
		// Remove redundancy
		HashSet<String> hashSet = new HashSet<>(vector);
		vector = new ArrayList<String>(hashSet);

		// Sorting
		Collections.sort(vector, sort);
		
		return vector;
	}
	
	/**
	 * Real calculation the similarity with various conditions
	 * @param list1 The first vector
	 * @param list2 The second vector
	 * @param d Document group for IDF
	 * @param ds Stemmed document group for IDF
	 * @param i Sequence
	 * @param j Sequence
	 */
	public static void calSimilarity(List<String> list1, List<String> list2, List<List<String>> d, List<List<String>> ds, int i, int j) {
		// Change array to List
		List<String> v1 = list1;
		List<String> v2 = list2;

		// Stop words removal
		v1.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
		v2.removeIf(RemovalStopwords.predicateForStopwordsRemoval);

		// Sorting
		Descending descending = new Descending();
		Collections.sort(v1, descending);
		Collections.sort(v2, descending);

		// Make standard vector
		List<String> v3 = makeStandardVector(v1, v2, descending);
		
		// Make document set for TF-IDF
		d.add(v1);
		d.add(v2);

		// Make and set TF-IDF array
		double[] t1 = new double[v1.size()];
		double[] t2 = new double[v2.size()];
		int c = 0;
		for (String w : v1) t1[c++] = TFIDF.tfIdf(v1, d, w);
		c = 0;
		for (String w : v2)	t2[c++] = TFIDF.tfIdf(v2, d, w);

		// Make final vectors
		boolean tfidfYn = true;
		double semanticRatio = 50;
		
		double[] d1 = getVector(v1, v3, t1, tfidfYn, DICE, semanticRatio);
		double[] d2 = getVector(v2, v3, t2, tfidfYn, DICE, semanticRatio);

		out.format("\n%d/%d \t %f", i, j, cosineSimilarity(d1, d2));
	}
	
	/**
	 * Experimentalise calculation the similarity with various conditions
	 * @param list1 The first vector
	 * @param list2 The second vector
	 * @param d Document group for IDF
	 * @param ds Stemmed document group for IDF
	 */
	public static void calSimilarity(List<String> list1, List<String> list2, List<List<String>> d, List<List<String>> ds) {
		// Change array to List
		List<String> v1 = list1;
		List<String> v2 = list2;

		// Stop words removal
		v1.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
		v2.removeIf(RemovalStopwords.predicateForStopwordsRemoval);

		// Sorting
		Descending descending = new Descending();
		Collections.sort(v1, descending);
		Collections.sort(v2, descending);

		// Stemming
		List<String> vs1 = stemmingForList(v1);
		List<String> vs2 = stemmingForList(v2);

		// Make standard vector
		List<String> v3 = makeStandardVector(v1, v2, descending);
		// Make stemming standard vector
		List<String> vs3 = makeStandardVector(vs1, vs2, descending);

		// Make document set for TF-IDF
		d.add(v1);
		d.add(v2);

		// Make stemming document set for TF-IDF
		ds.add(vs1);
		ds.add(vs2);

		if(logging) {
			out.println("Vectors without Stemming");
			out.println(Arrays.toString(v1.toArray()));
			out.println(Arrays.toString(v2.toArray()));
			out.println(Arrays.toString(v3.toArray()));
	
			out.println("Vectors with Stemming");
			out.println(Arrays.toString(vs1.toArray()));
			out.println(Arrays.toString(vs2.toArray()));
			out.println(Arrays.toString(vs3.toArray()));
		}

		// Make TF-IDF array
		double[] t1 = new double[v1.size()];
		double[] t2 = new double[v2.size()];
		double[] ts1 = new double[vs1.size()];
		double[] ts2 = new double[vs2.size()];

		// Set TF-IDF array
		int c = 0;
		for (String w : v1) t1[c++] = TFIDF.tfIdf(v1, d, w);
		c = 0;
		for (String w : vs1) ts1[c++] = TFIDF.tfIdf(vs1, ds, w);
		c = 0;
		for (String w : v2) t2[c++] = TFIDF.tfIdf(v2, d, w);
		c = 0;
		for (String w : vs2) ts2[c++] = TFIDF.tfIdf(vs2, ds, w);

		if(logging) {
			out.println();
			out.println("TF-IDF Vector");
			out.println(Arrays.toString(t1));
			out.println(Arrays.toString(t2));
			
			out.println("TF-IDF Vector after stemming");
			out.println(Arrays.toString(ts1));
			out.println(Arrays.toString(ts2));
		}

		// Make final vectors
		double[] d1, d2, ds1, ds2;
		boolean tfidfYn = false;

		for (double semanticRatio = 0; semanticRatio < 110; semanticRatio += 10) {

			tfidfYn = false;
			d1 = getVector(v1, v3, t1, tfidfYn, DICE, semanticRatio);
			d2 = getVector(v2, v3, t2, tfidfYn, DICE, semanticRatio);
			ds1 = getVector(vs1, vs3, ts1, tfidfYn, DICE, semanticRatio);
			ds2 = getVector(vs2, vs3, ts2, tfidfYn, DICE, semanticRatio);

			// Result
			if(logging) {
				out.println(semanticRatio + "\td\t" + cosineSimilarity(d1, d2));
				out.println(semanticRatio + "\tds\t" + cosineSimilarity(ds1, ds2));
			}

			// Make final vectors with TF-IDF
			tfidfYn = true;
			d1 = getVector(v1, v3, t1, tfidfYn, DICE, semanticRatio);
			d2 = getVector(v2, v3, t2, tfidfYn, DICE, semanticRatio);
			ds1 = getVector(vs1, vs3, ts1, tfidfYn, DICE, semanticRatio);
			ds2 = getVector(vs2, vs3, ts2, tfidfYn, DICE, semanticRatio);

			// Result
			if(logging) {
				out.println(semanticRatio + "\tdt\t" + cosineSimilarity(d1, d2));
				out.println(semanticRatio + "\tdst\t" + cosineSimilarity(ds1, ds2));
			}
		}
	}

	/**
	 * Stemming method for Array data
	 * @param args
	 * @return
	 */
	public static String[] stemmingForArr(String[] args) {
		String[] result = new String[args.length];

		for (int i = 0; i < args.length; i++) {
			snowballStemmer.setCurrent(args[i]);
			snowballStemmer.stem();
			result[i] = snowballStemmer.getCurrent();
		}

		return result;
	}

	/**
	 * Stemming method for List data
	 * @param args
	 * @return
	 */
	public static List<String> stemmingForList(List<String> args) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < args.size(); i++) {
			snowballStemmer.setCurrent(args.get(i));
			snowballStemmer.stem();
			result.add(snowballStemmer.getCurrent());
		}

		return result;
	}

	/**
	 * Calculate cosine similarity
	 * @param vectorA The first vector
	 * @param vectorB The second vector
	 * @return similarity value (0 ~ 1)
	 */
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
	
	/**
	 * Make the vector for cosine similarity with syntax and semantic-based similarity and TF-IDF weighing
	 * @param vectorA The first vector
	 * @param vectorB The second vector
	 * @return similarity value (0 ~ 1)
	 */
	public static double[] getVector(List<String> doc1, List<String> target, double[] tfidf, boolean tfidfYn, int syntax, double semanticRatio) {
		final double ratioForSemantic = semanticRatio / 100;
		final double ratioForSyntax = (100 - semanticRatio) / 100;
		double[] result = new double[target.size()];
		int i = 0;
		double temp = 0;
		double value = 0;
		
		for (String s1 : target) {
			temp = 0;

			for (int k = 0; k < doc1.size(); k++) {
				if (s1.equals(doc1.get(k))) {
					temp = (tfidfYn) ? tfidf[k] : 1;
					break;
				} else {
					for (int w = 0; w < 1; w++) {
						if (syntax == DICE) {
							value = (wn.cal(rcs, s1, doc1.get(k)) * ratioForSemantic)
									+ (diceCoefficient.diceCoefficientOptimized(s1, doc1.get(k)) * ratioForSyntax);
						} else if (syntax == JACCARD) {
							value = (wn.cal(rcs, s1, doc1.get(k)) * ratioForSemantic)
									+ (jaccardSimilarity.apply(s1, doc1.get(k)) * ratioForSyntax);
						}
						
						if (temp < value) {
							temp = value;
						}
					}

					if (k == doc1.size() - 1)
						temp *= (tfidfYn) ? tfidf[k] : 1;
				}
			}
			result[i++] = temp;
		}

		return result;
	}
	
}

class Descending implements Comparator<String> {
	@Override
	public int compare(String o1, String o2) {
		return o2.compareTo(o1);
	}
}