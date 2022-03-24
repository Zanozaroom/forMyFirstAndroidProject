package com.example.mytest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import android.widget.TextView;


import android.content.Intent;




public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick_Download(View view) {
        TextView TextButtom = findViewById(R.id.navButton);

        Intent intent = new Intent(this, Pokaz_valut.class);
        startActivity(intent);

    }
}