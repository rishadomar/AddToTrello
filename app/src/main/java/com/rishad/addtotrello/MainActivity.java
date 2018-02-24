package com.rishad.addtotrello;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	Trello trello;
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;
	TrelloDatabase trelloDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

	    this.trelloDatabase = new TrelloDatabase(getApplicationContext());
	    trello = new Trello(this.trelloDatabase);
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText appEditTextView = findViewById(R.id.appEditText);
                sendToApp(appEditTextView.getText().toString());
                appEditTextView.setText("");
            }
        });

        //
	    // List
	    //
	    ArrayList<String> cards = this.trelloDatabase.getAllCards();
        mainListView = (ListView) findViewById( R.id.mainListView );
        // Create and populate a List of planet names.
        //String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",
        //        "Jupiter", "Saturn", "Uranus", "Neptune"};
        //ArrayList<String> planetList = new ArrayList<String>();
        //planetList.addAll( Arrays.asList(planets) );

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, cards);
//        // Add more planets. If you passed a String[] instead of a List<String>
//        // into the ArrayAdapter constructor, you must not add more items.
//        // Otherwise an exception will occur.
//        listAdapter.add( "Ceres" );
//        listAdapter.add( "Pluto" );
//        listAdapter.add( "Haumea" );
//        listAdapter.add( "Makemake" );
//        listAdapter.add( "Eris" );

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendToApp(String text) {
		//String boardId = "5820c3a79b8e3dbe6f8569d2";
		//String listId = "58aad0a4e38ef4062a6af521";

		TrelloCard trelloCard = new TrelloCard(text);
		trello.addCard(trelloCard);

	    //String action = text.substring(0, text.indexOf(' '));
		//List<TrelloBoard> boards = (ArrayList<TrelloBoard>) trello .getBoards();
    }

}
