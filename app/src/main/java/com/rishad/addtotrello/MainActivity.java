package com.rishad.addtotrello;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText appEditTextView = findViewById(R.id.appEditText);
                sendToApp(appEditTextView.getText().toString());
            }
        });
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
	    String action = text.substring(0, text.indexOf(' '));
	    if (action.equalsIgnoreCase("remind")) {
		    Toast.makeText(getApplicationContext(), "Send to slack: " + text, Toast.LENGTH_SHORT).show();
	    } else if (action.matches("^.*?(monday|tuesday|wednesday|thursday|friday|weekend|drawer).*$")) {
		    Toast.makeText(getApplicationContext(), "Send to column: " + action, Toast.LENGTH_SHORT).show();
	    } else {
		    Toast.makeText(getApplicationContext(), "Send to drawer: " + text, Toast.LENGTH_SHORT).show();
		    Trello trello = new Trello();

	    }
    }

}
