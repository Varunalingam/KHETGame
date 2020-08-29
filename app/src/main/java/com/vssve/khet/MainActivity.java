package com.vssve.khet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    KHETBoard Board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Board = findViewById(R.id.board);
    }

    @Override
    public void onBackPressed() {
        Board.Undo();
    }
}