package com.nicholas_mackinnon.weather_chess;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;

public class ChessGame extends AppCompatActivity {

    ChessCanvas chessCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chess_game);
        chessCanvas = new ChessCanvas(this);
        chessCanvas.setBackgroundColor(Color.GRAY);
        setContentView(chessCanvas);
    }

}