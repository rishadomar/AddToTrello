package com.rishad.addtotrello;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

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

class TrelloCard {
	long id;
	String name;
	String description;
	String status;

	public TrelloCard(Integer id, String status, String name, String description) {
		this.id = id;
		this.status = status;
		this.name = name;
		this.description = description;
	}

	public TrelloCard(String text) {
		if (text.length() > 50) {
			this.name = text.substring(0, 50) + "...";
			this.description = text;
		} else {
			this.name = text;
			this.description = "";
		}
		this.status = TrelloDatabase.STATUS_NEW;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return this.description;
	}

	public String toString() {
		return this.getId() + " " +  this.getStatus() + " " + this.getName();
	}
}

public class Trello {
	private static final String TrelloScheme = "https";
	private static final String TrelloApi = "api.trello.com";
	private static final String TrelloKey = "06537c528f72a5985eb9e0d1ebee4595";
	private static final String Token = "aa06b25297ecbe9887a0920a2fc8ec23b719c86f5a1227ad2771a07ef20a7910";
	private static final String TrelloList = "5a6600d84aa8704105701ce5";
	List<TrelloBoard> boards;
	List<TrelloCard> cards;
	private TrelloDatabase trelloDatabase;

	public Trello(TrelloDatabase trelloDatabase) {
		this.trelloDatabase = trelloDatabase;
		//fetchBoards();
		//readPendingCards();
	}

	private void fetchBoards() {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(TrelloScheme)
				.authority(TrelloApi)
				.appendPath("1")
				.appendPath("member")
				.appendPath("me")
				.appendPath("boards")
				.appendQueryParameter("key", TrelloKey)
				.appendQueryParameter("token", Token);
		this.boards = new ArrayList<TrelloBoard>();
		DownloadBoardsTask task = new DownloadBoardsTask(this.boards);
		task.execute(builder.build().toString()); //TrelloApi + "?key=" + TrelloKey + "&token=" + Token);

	}

	public List<TrelloBoard> getBoards() {
		return boards;
	}

//	private List<TrelloCard> readPendingCards() {
//		Log.i("Cards", "About to read all cards");
//		this.cards = new ArrayList<TrelloCard>();
//		ArrayList<TrelloCard> allCards = this.trelloDatabase.getAllCards();
//		for (int i = 0; i < allCards.size(); i++) {
//			Integer trelloId = allCards.get(i);
//			Log.i("Card", trelloId);
//		}
//
//	}

	public ArrayList<String> getCardsAsStrings() {
		ArrayList<String> array_list = new ArrayList<String>();
		this.cards = this.trelloDatabase.getAllCards();
		for (int i = 0; i < this.cards.size(); i++) {
			TrelloCard t = this.cards.get(i);
			String s = t.toString();
			array_list.add(s);
		}
		return array_list;
	}

	public void addCard(TrelloCard trelloCard, ListAdapter listAdapter) {
		this.cards.add(trelloCard);
		this.trelloDatabase.insertCard(trelloCard);
		sendCard(trelloCard, listAdapter);
	}

	private void sendCard(TrelloCard card, ListAdapter listAdapter) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(TrelloScheme)
				.authority(TrelloApi)
				.appendPath("1")
				.appendPath("cards")
				.appendQueryParameter("key", TrelloKey)
				.appendQueryParameter("token", Token);
		PostDetail postDetail = new PostDetail(card, listAdapter, this.trelloDatabase, builder.build().toString());
		PostTask postTask = new PostTask();
		postDetail.addParameter("name", card.getName());
		postDetail.addParameter("desc", card.getDescription());
		postDetail.addParameter("idList", TrelloList);
		postDetail.addParameter("pos", "bottom");
		postTask.execute(postDetail);
		Log.i("Info", "Execute task launched");
	}
}

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

class PostDetail {
	String url;
	JSONObject jsonParam = new JSONObject();
	TrelloCard card;
	TrelloDatabase trelloDatabase;
	ListAdapter listAdapter;

	PostDetail(TrelloCard card, ListAdapter listAdapter, TrelloDatabase trelloDatabase, String url) {
		this.card = card;
		this.url = url;
		this.jsonParam = new JSONObject();
		this.trelloDatabase = trelloDatabase;
		this.listAdapter = listAdapter;
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

	PostDetail postDetail;


	@Override
	protected String doInBackground(PostDetail... postDetails) {

		URL url;
		HttpURLConnection urlConnection = null;

		try {
			this.postDetail = postDetails[0];
			url = new URL(postDetail.url);

			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			urlConnection.setRequestProperty("Accept","application/json");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);


			DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
			os.writeBytes(postDetail.getParameters());

			os.flush();
			os.close();

			Log.i("STATUS", String.valueOf(urlConnection.getResponseCode()));
			Log.i("MSG" , urlConnection.getResponseMessage());

			urlConnection.connect();
			Log.i("Info", "PostTask complete");
			return Long.toString(postDetail.card.getId());

		} catch (Exception e) {
			Log.i("Error", "Failed in POST. Reason: " + e.getMessage());
			return null;
		}

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.i("Info", "Successful post: " + result);
		postDetail.trelloDatabase.updateStatus(postDetail.card.getId(), TrelloDatabase.STATUS_SENT);
		postDetail.card.setStatus(TrelloDatabase.STATUS_SENT);
		update(postDetail.listAdapter, postDetail.card);
	}

	private void update(ListAdapter listAdapter, TrelloCard trelloCard)
	{
		int c = listAdapter.getCount();
		for (int i = 0; i < c; i++) {
			String item = (String) listAdapter.getItem(i);
			Log.i("Update", item);
		}
	}
}
