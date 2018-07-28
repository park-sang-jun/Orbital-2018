
package com.example.sangj.speakify;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    private TextView mVoiceInputTv;
    private TextView mFillerCountResult;
    private ImageButton mSpeakBtn;
    private HashMap<String, Integer> fillerWordCount;
    private String[] fillerWords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillerWordCount = new HashMap<>();
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
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
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
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String resultString = result.get(0);
                    mVoiceInputTv.setText(resultString);

                    countFillerWords(resultString, fillerWords);

                    mFillerCountResult.setText(fillerCountMessage(fillerWords));
                }
                break;
            }
        }
    }

    private void countFillerWords(String message, String[] fillerWordArray) {
        LinkedList<String> messageLL = new LinkedList<>(Arrays.asList(message.split(" ")));
        for (String filler : fillerWordArray) {
            int count = 0;
            ListIterator<String> iter = messageLL.listIterator();
            while(iter.hasNext()) {
                if(iter.next().equals(filler)) {
                    count++;
                    iter.remove();
                }
            }
            fillerWordCount.put(filler, count);
        }
    }

    private String fillerCountMessage(String[] fillerWordArray) {
        StringBuilder message = new StringBuilder();
        for (String filler:fillerWordArray) {
            int count = fillerWordCount.get(filler);
            if (count > 0) {
                message.append("\'");
                message.append(filler);
                message.append("\' : ");
                message.append(count);
                message.append("\n");
            }
        }
        return message.toString();
    }
}

