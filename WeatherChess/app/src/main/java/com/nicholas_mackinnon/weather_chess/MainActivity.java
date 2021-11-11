package com.nicholas_mackinnon.weather_chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goPlay(View view) {
        Intent goPlay = new Intent(this, ChessGame.class);
        startActivity(goPlay);
    }
}