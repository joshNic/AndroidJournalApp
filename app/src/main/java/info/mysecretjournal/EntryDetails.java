package info.mysecretjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class EntryDetails extends AppCompatActivity {
    private TextView entryTitle;
    private TextView entryBody;
    private TextView entryTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        entryBody = (TextView)findViewById(R.id.entryBody);
        entryTitle = (TextView)findViewById(R.id.entryTitle);
        entryTime = (TextView)findViewById(R.id.timestamp);

        Intent i = this.getIntent();
        final String title = i.getStringExtra("title");
        final String body = i.getStringExtra("body");

        final String time = i.getStringExtra("time");

        entryTitle.setText(title);
        entryBody.setText(body);
        entryTime.setText("Published on: "+""+time);
    }
}
