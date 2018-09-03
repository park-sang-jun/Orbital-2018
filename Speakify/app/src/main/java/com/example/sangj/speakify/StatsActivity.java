package com.example.sangj.speakify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {
    private TextView statsText;
    private Button resetBtn;
    private Button totalStatsBtn;
    private String statsMessage;
    private boolean isTotalStatsDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        resetBtn = findViewById(R.id.resetBtn);
        totalStatsBtn = findViewById(R.id.totalStatBtn);
        statsText = findViewById(R.id.statsText);
        statsMessage = "";
        isTotalStatsDisplayed = false;

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.resetTotalCount();
                if (isTotalStatsDisplayed) {
                    showTotalStats();
                }
            }
        });

        totalStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTotalStatsDisplayed) {
                    showTotalStats();
                } else {
                    setTextResults();
                }
                isTotalStatsDisplayed = !isTotalStatsDisplayed;
            }
        });

        setTextResults();
    }

    private void setTextResults() {
        statsMessage = MainActivity.finalStatMessage;
        statsText.setText(statsMessage);
    }

    private void showTotalStats() {
        statsText.setText(statsMessage + "\nTotal statistics\n" +
                MainActivity.getGlobalStatMessage() + "\nFiller word count\n" + MainActivity.getTotalMessage());
    }
}
