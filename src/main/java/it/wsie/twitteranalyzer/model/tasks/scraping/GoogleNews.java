package it.wsie.twitteranalyzer.model.tasks.scraping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Simone Papandrea
 *
 */
class GoogleNews {

	public final int mPageSize;
	private static final String GOOGLE_SEED = "http://www.google.com/search?hl=en&gl=us&cr=countryUS&hl=en&tbm=nws";
	private static final int TIMEOUT = 10 * 000;
	private static final int RATE = 300;
	private static final int MIN_WAIT = 2;
	private static final int MAX_WAIT = 6;
	private static final int LONG_WAIT = 150;
	private static final String[] USER_AGENTS = {
			"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0",
			"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0",
			"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:47.0) Gecko/20100101 Firefox/47.0",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
			"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:47.0) Gecko/20100101 Firefox/47.0",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586" };

	private final GoogleNewsListener mListener;

	public GoogleNews(GoogleNewsListener listener, int pageSize) {

		this.mPageSize = pageSize;
		this.mListener = listener;

	}

	void get(String key, int page) throws IOException, InterruptedException {

		Document doc;
		List<String> news;
		String agent, seed;
		boolean hasNews = true, completed = false;

		seed = GOOGLE_SEED + "&q=" + key.replaceAll("\\s", "+");

		do {

			agent = USER_AGENTS[(int) (Math.random() * USER_AGENTS.length)];

			try {

				doc = Jsoup.connect(seed + "&start=" + page).userAgent(agent).timeout(TIMEOUT).get();
				news = scrape(doc, key);
				hasNews = news.size() > 0;

				if (hasNews) {

					completed = mListener.GoogleNewsReady(news, page);

					if (!completed) {
						
						page += mPageSize;
						Thread.sleep((page % RATE == 0 ? LONG_WAIT
								: ((MIN_WAIT + (int) (Math.random() * ((MAX_WAIT - MIN_WAIT) + 1))) * 10)) * 1000);
					}
				}

			} catch (HttpStatusException ex) {

				hasNews = false;

				if (ex.getStatusCode() != 503)
					throw ex;
			}

		} while (hasNews && !completed);
	}

	private List<String> scrape(Document box, String key) {

		List<String> news;
		Elements elements, paragraphs;
		HashSet<Integer> indexes;
		String url, text, sentences[];
		Document doc;
		int size;
		final int window = 1;

		news = new ArrayList<String>();
		elements = box.select(".g");
		key = (key.split(" ")[1]);
		indexes = new HashSet<Integer>();

		for (Element element : elements) {

			url = element.select("h3 > a").first().attr("href");

			try {

				doc = Jsoup.connect(url).get();
				paragraphs = doc.select("p");
				text = "";

				for (Element paragraph : paragraphs) {

					sentences = paragraph.text().split("(?<=[.?!;])\\s+(?=\\p{Lu})");
					size = sentences.length;

					for (int i = 0; i < size; i++) {

						if (sentences[i].contains(key)) {

							for (int j = i - window; j <= i + window; j++) {

								if (j > -1 && j < size && !indexes.contains(j)) {

									text += sentences[j] + " ";
									indexes.add(j);
								}
							}
						}
					}
				}

				news.add(text.trim());
				indexes.clear();

			} catch (Exception ex) {

			}
		}

		return news;
	}

	public interface GoogleNewsListener {

		public boolean GoogleNewsReady(List<String> news, int page);

	}
}
