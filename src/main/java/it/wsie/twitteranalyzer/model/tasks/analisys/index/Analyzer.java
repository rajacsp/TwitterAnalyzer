package it.wsie.twitteranalyzer.model.tasks.analisys.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * @author Simone Papandrea
 *
 */
public class Analyzer implements Indexer {

	private IndexWriter mWriter;
	private Pattern mPattern;

	@Override
	public void open(String name) throws IOException {

		mWriter = new IndexWriter(new SimpleFSDirectory(Paths.get(INDEX_FOLDER + File.separator + name)),
				new IndexWriterConfig(new StandardAnalyzer()));
		mWriter.deleteAll();
		mPattern = Pattern.compile("\\b(" + name.replaceAll("\\s", "\\\\s*") + ")\\b", Pattern.CASE_INSENSITIVE);
	}

	public void close() throws IOException {

		if (mWriter != null) {			
			mWriter.commit();
			mWriter.close();
		}
	}

	public final void write(List<String> messages) throws IOException {

		Document document;
		Field field;

		document = new Document();
		field = new Field(TWEET_FIELD, "", getFieldType());
		document.add(field);

		for (String message : messages) {

			message = mPattern.matcher(message).replaceAll("");
			field.setStringValue(message);
			mWriter.addDocument(document);
		}
	}

	private static FieldType getFieldType() {

		FieldType field;

		field = new FieldType();
		field.setIndexOptions(IndexOptions.DOCS);
		field.setTokenized(true);
		field.setStored(true);
		field.freeze();

		return field;
	}

}
