package com.rishad.addtotrello;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class TrelloBoard {
	String id;
	String name;

	public TrelloBoard(String id, String name) {
		Log.i("Info", "Board " + name);
		this.id = id;
		this.name = name;
	}
}

public class Trello {
	private static final String TrelloScheme = "https";
	private static final String TrelloApi = "api.trello.com";
	private static final String TrelloKey = "06537c528f72a5985eb9e0d1ebee4595";
	private static final String Token = "aa06b25297ecbe9887a0920a2fc8ec23b719c86f5a1227ad2771a07ef20a7910";
	private List<String> boards = null;

	public Trello() {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(TrelloScheme)
				.authority(TrelloApi)
				.appendPath("1")
				.appendPath("member")
				.appendPath("me")
				.appendPath("boards")
				.appendQueryParameter("key", TrelloKey)
				.appendQueryParameter("token", Token);
		DownloadBoardsTask task = new DownloadBoardsTask();
		task.execute(builder.build().toString()); //TrelloApi + "?key=" + TrelloKey + "&token=" + Token);
	}

	public List<String> getBoards() {
		return boards;
	}
}

/*
https://www.techrepublic.com/blog/software-engineer/using-androids-asynctask-to-handle-long-running-i-o/

https://code.tutsplus.com/tutorials/android-sdk-making-remote-api-calls--mobile-17568
 */

class DownloadBoardsTask extends AsyncTask<String, Void, String> {

	List<TrelloBoard> boards;

	DownloadBoardsTask() {
		boards = new ArrayList<TrelloBoard>();
	}

	@Override
	protected String doInBackground(String... urls) {

		String result = "";
		URL url;
		HttpURLConnection urlConnection = null;

		try {
			url = new URL(urls[0]);

			urlConnection = (HttpURLConnection) url.openConnection();

			InputStream in = urlConnection.getInputStream();

			InputStreamReader reader = new InputStreamReader(in);

			int data = reader.read();

			while (data != -1) {
				char current = (char) data;
				result += current;
				data = reader.read();
			}

			return result;

		} catch (Exception e) {

			Log.i("Error", "Could not find trello");

		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try {
			JSONArray arr = new JSONArray(result);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject jsonPart = arr.getJSONObject(i);
				boards.add(new TrelloBoard(jsonPart.getString("id"), jsonPart.getString("name")));
			}
		} catch (JSONException e) {
			Log.i("Info", "Could not find weather");
		}

	}
}
