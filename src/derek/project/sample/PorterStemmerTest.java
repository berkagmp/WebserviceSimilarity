package derek.project.sample;
import static java.lang.System.out;

import org.tartarus.snowball.ext.porterStemmer;

import edu.cmu.lti.ws4j.util.PorterStemmer;

public class PorterStemmerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
		PorterStemmer porterStemmer = new PorterStemmer();
		String result = porterStemmer.stemSentence("a getting cats caresses conflated troubling probably latitude lat");
		
		out.println(result);
	}

}
