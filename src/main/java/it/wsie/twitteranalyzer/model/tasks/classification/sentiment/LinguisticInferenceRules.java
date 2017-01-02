package it.wsie.twitteranalyzer.model.tasks.classification.sentiment;

/**
 * @author Simone Papandrea
 *
 */
public interface LinguisticInferenceRules {

	public final static String[] NEGATION = { "ain't", "aint", "aren't", "arent", "can't", "cannot", "cant", "couldn't",
			"couldnt", "daren't", "darent", "despite", "didn't", "didnt", "doesn't", "doesnt", "don't", "dont",
			"hadn't", "hadnt", "hasn't", "hasnt", "haven't", "havent", "isn't", "isnt", "mightn't", "mightnt",
			"mustn't", "mustnt", "needn't", "neednt", "neither", "never", "none", "nope", "nor", "not", "nothing",
			"nowhere", "oughtn't", "oughtnt", "rarely", "seldom", "shan't", "shant", "shouldn't", "shouldnt", "uh-uh",
			"uhuh", "wasn't", "wasnt", "weren't", "werent", "without", "won't", "wont", "wouldn't", "wouldnt" };

	public final static String[] INTENSIFYING = { "absolutely", "amazingly", "awfully", "completely", "considerably",
			"decidedly", "deeply", "effing", "enormously", "entirely", "especially", "exceptionally", "extremely",
			"fabulously", "frickin", "friggin", "frigging", "fucking", "fully", "greatly", "hella", "highly", "hugely",
			"incredibly", "intensely", "ippin", "ipping", "majorly", "more", "most", "particularly", "purely", "quite",
			"really", "remarkably", "so", "substantially", "thoroughly", "totally", "tremendously", "uber",
			"unbelievably", "unusually", "utterly", "very" };

	public final static String[] WEAKENING = { "almost", "barely", "hardly", "just enough", "just-enough", "kind of",
			"kind-of", "kinda", "kindof", "less", "little", "marginally", "occasionally", "partly", "scarcely",
			"slightly", "somewhat", "sort of", "sort-of", "sorta", "sortof" };

	public final static String[] CONTRASTING = { "although", "but", "even if", "even though", "however", "though" };

}
