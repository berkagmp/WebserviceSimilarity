package derek.project;
import java.util.Arrays;

import derek.project.algorithm.WordNet;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;

public class WordNetTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ILexicalDatabase db = new NictWordNet();
		RelatednessCalculator[] rcs = {new WuPalmer(db), new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db)
				, new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)};
		
		WordNet wn = new WordNet();
		
		String[] str = {"pag", "page"};
		for(int i=0; i<rcs.length; i++)
			wn.cal(rcs[i], str);
		
		/*System.out.println(Arrays.asList().);
		System.out.println(wn.cal(rcs[6], str));*/
		
	}

}
