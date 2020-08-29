package com.vssve.khet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    KHETBoard Board;
    public static MediaPlayer M;
    TextView Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Board = findViewById(R.id.board);
        Message = findViewById(R.id.message);
        Board.Message = Message;
        Board.GL = new GameOverListener() {
            @Override
            public void OnGameOver(int Winner) {
                Log.d("Hello","Hi");
                AlertDialog alertDialog1 = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog1.setTitle("Game Over");
                alertDialog1.setMessage("Player " + (Winner== 1? "Silver":"Red") + " Won");
                alertDialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                        MainActivity.this.finish();
                    }
                });
                alertDialog1.show();
            }
        };
    }

    @Override
    public void onBackPressed() {
        Board.Undo();
    }
}

interface GameOverListener
{
    void OnGameOver(int Winner);
}