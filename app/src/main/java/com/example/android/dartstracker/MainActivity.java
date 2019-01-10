package com.example.android.dartstracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.dartstracker.data.GameDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button twoPlayerGame = (Button) findViewById(R.id.twoPlayers);
        twoPlayerGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent twoPlayerIntent = new Intent(view.getContext(), TwoPlayerGameActivity.class);
                startActivity(twoPlayerIntent);
            }
        });
    }
}
