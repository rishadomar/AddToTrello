package com.rishad.addtotrello;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
	List<TrelloBoard> boards;

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
		boards = new ArrayList<TrelloBoard>();
		DownloadBoardsTask task = new DownloadBoardsTask(boards);
		task.execute(builder.build().toString()); //TrelloApi + "?key=" + TrelloKey + "&token=" + Token);
	}

	public List<TrelloBoard> getBoards() {
		return boards;
	}

	public void addCard(String description) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(TrelloScheme)
				.authority(TrelloApi)
				.appendPath("1")
				.appendPath("cards")
				.appendQueryParameter("key", TrelloKey)
				.appendQueryParameter("token", Token);
		PostDetail postDetail = new PostDetail(builder.build().toString());
		PostTask postTask = new PostTask();
		postDetail.addParameter("name", "bingo");
		postDetail.addParameter("desc", description);
		postDetail.addParameter("idList", "58aad0a4e38ef4062a6af521");
		postDetail.addParameter("pos", "bottom");
		postTask.execute(postDetail); //TrelloApi + "?key=" + TrelloKey + "&token=" + Token);
		// See https://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post
		// https://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests
	}
}

/*
https://www.techrepublic.com/blog/software-engineer/using-androids-asynctask-to-handle-long-running-i-o/

https://code.tutsplus.com/tutorials/android-sdk-making-remote-api-calls--mobile-17568
 */

class DownloadBoardsTask extends AsyncTask<String, Void, String> {
	ArrayList<TrelloBoard> boards;

	DownloadBoardsTask(List<TrelloBoard> boards) {
		this.boards = (ArrayList<TrelloBoard>) boards;
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

class TrelloCard {
	String id;
	String name;
	String description;

	void TrelloCard(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}


}

class PostDetail {
	String url;
	JSONObject jsonParam = new JSONObject();

	PostDetail(String url) {
		this.url = url;
		this.jsonParam = new JSONObject();
		//this.params = new List<AbstractMap.SimpleEntry<String, String>>();
	}

	void addParameter(String key, String value) {
		try {
			jsonParam.put(key, value);
		} catch (JSONException e) {
			Log.i("Info", "Error with json setting value. Reason: " + e.getMessage());
		}
	}

	public String getParameters() throws UnsupportedEncodingException
	{
		return jsonParam.toString();
	}
}

class PostTask extends AsyncTask<PostDetail, Void, String> {

	@Override
	protected String doInBackground(PostDetail... postDetails) {

		URL url;
		HttpURLConnection urlConnection = null;

		try {
			PostDetail postDetail = postDetails[0];
			url = new URL(postDetail.url);

			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			urlConnection.setRequestProperty("Accept","application/json");
			//urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);


			DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
			//os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
			os.writeBytes(postDetail.getParameters());

			os.flush();
			os.close();

			Log.i("STATUS", String.valueOf(urlConnection.getResponseCode()));
			Log.i("MSG" , urlConnection.getResponseMessage());

//			OutputStream os = urlConnection.getOutputStream();
//			BufferedWriter writer = new BufferedWriter(
//					new OutputStreamWriter(os, "UTF-8"));
//			String debug = postDetail.getParameters();
//			writer.write(postDetail.getParameters());
//			writer.flush();
//			writer.close();
//			os.close();

			urlConnection.connect();
			Log.i("Info", "PostTask complete");

		} catch (Exception e) {
			Log.i("Error", "Failed in POST. Reason: " + e.getMessage());
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.i("Info", "Successful post: " + result);
	}
}
