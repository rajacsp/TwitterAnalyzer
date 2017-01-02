package it.wsie.twitteranalyzer.model.tasks.identification.parser;

import org.json.JSONObject;

import it.wsie.twitteranalyzer.model.tasks.identification.Message;
import it.wsie.twitteranalyzer.model.tasks.identification.User;

/**
 * @author Simone Papandrea
 *
 */
public class TweetJSONParser {

	public static Message parseTweet(String json) {

		JSONObject jsonTweet;
		long  timestamp;
		String id,text;

		jsonTweet = new JSONObject(json);
		id = jsonTweet.getString("id_str");
		text = jsonTweet.getString("text");
		timestamp = jsonTweet.getLong("timestamp_ms");

		return new Message(id, text, timestamp);

	}

	public static User parseUser(String json) {

		JSONObject jsonUser;
		int friends;
		String id,name, screenName, icon;

		jsonUser = new JSONObject(json).getJSONObject("user");
		id = jsonUser.getString("id_str");
		name = jsonUser.getString("name");
		screenName = jsonUser.getString("screen_name");
		icon = jsonUser.getString("profile_image_url");
		friends=jsonUser.getInt("friends_count");
		
		return new User(id, name, screenName, icon,friends);

	}
}