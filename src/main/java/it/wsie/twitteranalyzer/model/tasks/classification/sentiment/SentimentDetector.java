package it.wsie.twitteranalyzer.model.tasks.classification.sentiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.util.CoreMap;
import net.didion.jwnl.JWNLException;

/**
 * @author Simone Papandrea
 *
 */
public class SentimentDetector {

	private final static int NEGATION_WINDOW = 3;
	private final static int VALENCE_WINDOW = 3;
	private final static double ALPHA_INTENSIFIED = 1; // a>=1
	private final static double BETHA_WEAKENING = 0.5; // 0<=b<=1
	private final  static String LEXICAL_NORMALIZER_PATH = "emnlp_dict.txt";
	private final StanfordCoreNLP mPipeline;
	private final SentiWordNet mSWN;
	private final Map<String, String> mLexicalNormalizer;
	private final TokenizerFactory<CoreLabel> mTokenizerFactory;
	
	public SentimentDetector() throws IOException, JWNLException {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		
		this.mPipeline = new StanfordCoreNLP(props);
		this.mTokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		this.mSWN = new SentiWordNet();
		this.mLexicalNormalizer = new HashMap<String, String>();
		loadLexicalNormalised();
	}

	private void loadLexicalNormalised() throws IOException {

		BufferedReader reader = null;
		String line;
		String[] data;

		reader = new BufferedReader(new FileReader(LEXICAL_NORMALIZER_PATH));

		try {

			while ((line = reader.readLine()) != null) {

				data = line.split("\t");
				mLexicalNormalizer.put(data[0], data[1]);
			}

		} finally {

			if (reader != null)
				reader.close();
		}

	}

	public double detect(String sentence) throws JWNLException {

		Annotation document;
		List<String> lemmas, POSs;
		List<CoreLabel> labels;
		String token;
		Polarity polarity;
		boolean nir = false, cir = false, vsr = false;

		labels = mTokenizerFactory.getTokenizer(new StringReader(sentence)).tokenize();
		
		for(CoreLabel label:labels){
			
			token=label.value();
			
			if (mLexicalNormalizer.containsKey(token))
				sentence=sentence.replace(token,mLexicalNormalizer.get(token));
		}
		
		document = new Annotation(sentence);
		mPipeline.annotate(document);
		lemmas = new ArrayList<String>();
		POSs = new ArrayList<String>();

		for (CoreMap map : document.get(SentencesAnnotation.class)) {

			labels = map.get(TokensAnnotation.class);

			for(CoreLabel label:labels){

				token = label.get(LemmaAnnotation.class);
				lemmas.add(token);
				POSs.add(label.get(PartOfSpeechAnnotation.class));
				cir =cir ||  Arrays.binarySearch(LinguisticInferenceRules.CONTRASTING, token) > -1;
				nir = nir || Arrays.binarySearch(LinguisticInferenceRules.NEGATION, token) > -1;
				vsr =vsr || (Arrays.binarySearch(LinguisticInferenceRules.INTENSIFYING, token) > -1)
						|| (Arrays.binarySearch(LinguisticInferenceRules.WEAKENING, token) > -1);				
			}
		}

		mSWN.setContext(lemmas);

		if (cir)
			polarity = CIRRules(lemmas, POSs);
		else if (nir)
			polarity = NIRRules(lemmas, POSs);
		else if (vsr)
			polarity = VSRRules(lemmas, POSs);
		else
			polarity = getPolarity(lemmas, POSs);

		return polarity.score();
	}

	private Polarity NIRRules(List<String> lemmas, List<String> POSs) throws JWNLException {

		Polarity polarity;
		Pattern pattern;
		String lemma;
		boolean negation;
		int length;
		Double score;

		length = lemmas.size();
		polarity = new Polarity();
		pattern = Pattern.compile("[^a-zA-Z\\d\\s]|[^$]");

		for (int i = 0; i < length; i++) {

			score = mSWN.get(lemmas.get(i), POSs.get(i));

			if (score != null && score != 0) {

				negation = false;
						
				for (int j = Math.max(0, i - NEGATION_WINDOW); j < Math.min(i + NEGATION_WINDOW, length - 1); j++) {

					if (j != i) {

						lemma = lemmas.get(j);

						if (negation || pattern.matcher(lemma).matches())
							break;

						negation = Arrays.binarySearch(LinguisticInferenceRules.NEGATION, lemma) > -1;
					}
				}

				score *= negation ? -1 : 1;
				polarity.add(score);
			}
		}

		return polarity;
	}

	private Polarity VSRRules(List<String> lemmas, List<String> POSs) throws JWNLException {

		Polarity polarity;
		Pattern pattern;
		String lemma;
		boolean intensifyng,weakening ;
		int length;
		Double score;

		length = lemmas.size();
		polarity = new Polarity();
		pattern = Pattern.compile("[^a-zA-Z\\d\\s]|[^$]");

		for (int i = 0; i < length; i++) {

			score = mSWN.get(lemmas.get(i), POSs.get(i));

			if (score != null) {

				intensifyng = false; 
				weakening = false;
						
				for (int j = Math.max(0, i - VALENCE_WINDOW); j <= i; j++) {

					lemma = lemmas.get(j);

					if (intensifyng || weakening || pattern.matcher(lemma).matches())
						break;

					intensifyng = Arrays.binarySearch(LinguisticInferenceRules.INTENSIFYING, lemma) > -1;
					weakening = Arrays.binarySearch(LinguisticInferenceRules.WEAKENING, lemma) > -1;
				}

				if (intensifyng)
					score *= ALPHA_INTENSIFIED;
				else if (weakening)
					score *= BETHA_WEAKENING;

				polarity.add(score);
			}
		}

		return polarity;
	}

	private Polarity CIRRules(List<String> lemmas, List<String> POSs) throws JWNLException {

		Polarity polarity;
		boolean contrast =false;
		int i = 0, length;

		length = lemmas.size();
		polarity = new Polarity();

		while (!contrast  && i < length)
			contrast = Arrays.binarySearch(LinguisticInferenceRules.CONTRASTING, lemmas.get(i++))>-1;
		
		if (contrast) {
			
			polarity = getPolarity(lemmas.subList(i, length), POSs.subList(i, length));

			if (polarity.zero()) {
				polarity = getPolarity(lemmas.subList(0, i-1), POSs.subList(0, i-1));
				polarity.invert();
			}
		}

		return polarity;
	}

	private Polarity getPolarity(List<String> lemmas, List<String> POSs) throws JWNLException {

		Polarity polarity;
		int length;
		Double score;

		polarity = new Polarity();
		length = lemmas.size();

		for (int i = 0; i < length; i++) {

			score = mSWN.get(lemmas.get(i), POSs.get(i));

			if (score != null)
				polarity.add(score);
		}

		return polarity;
	}

	public void close(){
		
		if(mSWN!=null)
			mSWN.closeDictionary();
	}
}
