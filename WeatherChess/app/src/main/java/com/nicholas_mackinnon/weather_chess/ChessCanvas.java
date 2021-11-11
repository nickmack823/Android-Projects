package com.nicholas_mackinnon.weather_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class ChessCanvas extends View {

    Paint paint;
    Rect rect;
    int boardOriginX, boardOriginY, tileDim;
    final int[] pieceIDs = {
            R.drawable.king_white, R.drawable.king_black, R.drawable.queen_white, R.drawable.queen_black,
            R.drawable.rook_white, R.drawable.rook_black, R.drawable.bishop_white, R.drawable.bishop_black,
            R.drawable.knight_white, R.drawable.knight_black, R.drawable.pawn_white, R.drawable.pawn_black
    };
    final Map<Integer, Bitmap> pieceMap = new HashMap<>();

    public ChessCanvas(Context context) {
        super(context);
        paint = new Paint();
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
    }
    private void drawPieces(Canvas canvas) {
        // Finally, the pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                drawPieceAtTile(canvas, i+1, j+1, pieceMap.get(R.drawable.king_white));
            }
        }
    }
    private void drawPieceAtTile(Canvas canvas, int row, int col, Bitmap piece) {
        canvas.drawBitmap(piece, null, new Rect(boardOriginX + (col*tileDim),
                        boardOriginY + (row*tileDim),
                        boardOriginX + ((col+1)*tileDim),
                        boardOriginY + ((row+1)*tileDim)), paint);
    }
    private void drawBoard(Canvas canvas) {
        for (Integer id : pieceIDs) {
            pieceMap.put(id, BitmapFactory.decodeResource(getResources(), id));
        }
        // The board
        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();

        // Now the tiles
        tileDim = cWidth/8;
        int burlyWood = getResources().getColor(R.color.burlyWood);
        int darkGrey = getResources().getColor(R.color.dark_grey);
        int color = burlyWood;
        float leftBound = 0;
        boardOriginX = (int) leftBound;
        // *0.88 to leave 0.12, half of the 1.00 - 0.76 of vSize (for even spacing
        float topBound = (float) (cHeight-(cHeight*0.76));
        boardOriginY = (int) topBound;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                int left, right, top, bottom;
                left = (int) leftBound + ((j* tileDim)- tileDim) ;
                right = left + tileDim;
                top = (int) topBound + ((i-1) * tileDim);
                bottom = top + tileDim;
                paint.setColor(color);
                canvas.drawRect(left, top, right, bottom, paint);
                if (j != 8) {
                    if (color == burlyWood) {
                        color = darkGrey;
                    } else {
                        color = burlyWood;
                    }
                }
            }
        }
    }
}
