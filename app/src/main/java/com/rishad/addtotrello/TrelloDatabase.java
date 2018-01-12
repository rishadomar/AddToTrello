package com.rishad.addtotrello;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class TrelloDatabase extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "trello.db";
	public static final String CARDS_TABLE_NAME = "cards";
	public static final String CARDS_COLUMN_ID = "id";
	public static final String CARDS_COLUMN_NAME = "name";
	public static final String CARDS_COLUMN_DESCRIPTION = "description";

	public TrelloDatabase(Context context) {
		super(context, DATABASE_NAME , null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"create table " +
						CARDS_TABLE_NAME +
						"(" +
						CARDS_COLUMN_ID +
						" integer primary key, " +
						CARDS_COLUMN_NAME +
						" text, " +
						CARDS_COLUMN_DESCRIPTION +
						" text)"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CARDS_TABLE_NAME);
		onCreate(db);
	}

	public boolean insertCard(String name, String description) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("description", description);
		db.insert("cards", null, contentValues);
		return true;
	}

	public Cursor getData(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from cards where id="+id+"", null );
		return res;
	}

	public int numberOfRows(){
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, CARDS_TABLE_NAME);
		return numRows;
	}

	public boolean updateCard(Integer id, String name, String description) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("description", description);
		db.update("cards", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
		return true;
	}

	public Integer deleteCard(Integer id) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete("cards",
				"id = ? ",
				new String[] { Integer.toString(id) });
	}

	public ArrayList<String> getAllCards() {
		ArrayList<String> array_list = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from cards", null );
		res.moveToFirst();

		while(res.isAfterLast() == false){
			array_list.add(res.getString(res.getColumnIndex(CARDS_COLUMN_ID)));
			res.moveToNext();
		}
		return array_list;
	}
}

