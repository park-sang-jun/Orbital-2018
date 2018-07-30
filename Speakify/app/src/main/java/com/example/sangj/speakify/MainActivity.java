
package com.example.sangj.speakify;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;

    private long startTime;
    private long stopTime;
    private int numWords;
    private double wordsPerMinute;
    private double elapsedTime;
    private String statMessage;
    private static int numWordsGlobal = 0;
    private static double totalTimeGlobal = 0;

    private TextView mVoiceInputTv;
    private TextView mFillerCountResult;
    private ImageButton mSpeakBtn;
    private Button statsButton;
    private static HashMap<String, Integer> fillerWordCountLast;
    private static String[] fillerWords;

    public static HashMap<String, Integer> fillerWordCountTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillerWordCountTotal = new HashMap<>();
        fillerWords = getResources().getStringArray(R.array.default_filler_words);
        mVoiceInputTv = findViewById(R.id.voiceInput);
        mFillerCountResult = findViewById(R.id.fillerCount);
        mSpeakBtn = findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
        statsButton = (Button) findViewById(R.id.btnStats);

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StatsActivity.class));
            }
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startTime = System.currentTimeMillis();
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, "Your device does not support speech input.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {
                    stopTime = System.currentTimeMillis();
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String resultString = result.get(0);
                    mVoiceInputTv.setText(resultString);

                    LinkedList<String> messageLL = new LinkedList<>(Arrays.asList(resultString.split(" ")));
                    evaluateTime(messageLL.size());
                    countFillerWords(messageLL, fillerWords);

                    mFillerCountResult.setText(statMessage + "\nWord count: \n" + fillerCountMessage(fillerWords, fillerWordCountLast));
                }
                break;
            }
        }
    }

    private void countFillerWords(LinkedList<String> messageLL, String[] fillerWordArray) {
        // LinkedList<String> messageLL = new LinkedList<>(Arrays.asList(message.split(" ")));
        fillerWordCountLast = new HashMap<>();
        for (String filler : fillerWordArray) {
            int count = 0;
            ListIterator<String> iter = messageLL.listIterator();
            while(iter.hasNext()) {
                if(iter.next().equals(filler)) {
                    count++;
                    iter.remove();
                }
            }
            Integer totalCount = fillerWordCountTotal.get(filler);
            fillerWordCountTotal.put(filler, count + (totalCount == null ? 0 : totalCount));
            if (count > 0) {
                fillerWordCountLast.put(filler, count);
            }
        }
    }

    private static String fillerCountMessage(String[] fillerWordArray, HashMap<String, Integer> wordCounts) {
        StringBuilder message = new StringBuilder();
        for (String filler:fillerWordArray) {
            Integer count = wordCounts.get(filler);
            if (count != null && count > 0) {
                message.append("\'");
                message.append(filler);
                message.append("\' : ");
                message.append(count);
                message.append("\n");
            }
        }
        return message.toString();
    }

    public static String getTotalMessage() {
        return fillerCountMessage(fillerWords, fillerWordCountTotal);
    }

    public static void resetTotalCount() {
        fillerWordCountTotal = new HashMap<>();
    }

    private void evaluateTime(int numberOfWords) {
        elapsedTime = (double) (stopTime - startTime) / (double) 1000;
        wordsPerMinute = (double) numberOfWords * 60 / elapsedTime;
        numWords = numberOfWords;
        numWordsGlobal += numWords;
        totalTimeGlobal += elapsedTime;
        statMessage = getStatMessage(elapsedTime, numWords, wordsPerMinute);
    }

    public static String getStatMessage(double time, int numWords, double wordsPerMinute) {
        return "Time: " + time + "s \n" +
                "Number of words: " + numWords + "\n" +
                "Average WPM: " + String.format("%.2f", wordsPerMinute);
    }

    public static String getGlobalStatMessage() {
        return getStatMessage(totalTimeGlobal, numWordsGlobal, (double) numWordsGlobal * 60 / totalTimeGlobal);
    }
}

