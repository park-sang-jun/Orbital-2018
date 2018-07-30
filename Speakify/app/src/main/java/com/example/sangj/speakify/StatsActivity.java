package com.example.sangj.speakify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {
    private TextView totalStatsText;
    private Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        resetBtn = findViewById(R.id.resetBtn);
        totalStatsText = findViewById(R.id.totalStatsText);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.resetTotalCount();
                setTextResults();
            }
        });

        setTextResults();
    }

    private void setTextResults() {
        totalStatsText.setText(MainActivity.getGlobalStatMessage() + "\nWord Count: \n" + MainActivity.getTotalMessage());
    }
}
