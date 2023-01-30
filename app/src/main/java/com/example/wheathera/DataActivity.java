package com.example.wheathera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DataActivity extends AppCompatActivity {

    TextView receiver_msg;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_activity);

        receiver_msg = findViewById(R.id.received_value_id);

        Intent intent = getIntent();

        String str = intent.getStringExtra("message_key");

        receiver_msg.setText(str);

    }

    public  boolean isCitySaved(){
        SharedPreferences sharedPreferences = getSharedPreferences("message_key", Context.MODE_PRIVATE);
        return sharedPreferences.contains("CITY");
    }
}
