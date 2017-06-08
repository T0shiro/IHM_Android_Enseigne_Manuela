package fr.unice.si3.ihm.ihm_enseigne_manuela;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FirstScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        Button buttonCompare = (Button) findViewById(R.id.compare);
        Button buttonList = (Button) findViewById(R.id.list_shop);
        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMapActivity();
            }
        });
        buttonCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCompareActivity();
            }
        });
        TextView firstSentence = (TextView) findViewById(R.id.textView);
        firstSentence.setTypeface(null, Typeface.BOLD);
        buttonList.setTypeface(null, Typeface.BOLD_ITALIC);
        buttonCompare.setTypeface(null, Typeface.BOLD_ITALIC);

    }

    public void startMapActivity(){
        Intent intent = new Intent(this, MapShopActivity.class);
        startActivity(intent);
    }

    public void startCompareActivity(){
        Intent intent = new Intent(this, ComparaisonActivity.class);
        startActivity(intent);
    }
}
