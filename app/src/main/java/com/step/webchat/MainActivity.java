package com.step.webchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final MessagesAPI api = new MessagesAPI();
    private Button btnSend;
    private EditText editMessage;
    private LinearLayout layoutMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        initViews();
        loadMessages();
    }

    private void initViews(){
        try {
            btnSend = findViewById(R.id.btnSend);
            editMessage = findViewById(R.id.editMessage);
            layoutMessages = findViewById(R.id.messagesLayout);


            btnSend.setOnClickListener((v) -> {
                new Thread(sendMessage).start();
            });
        }
        catch (Exception ex){
            ex.getMessage();
        }
    }
    private final Runnable displayOK = () -> {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        tv.setText("Ok");

        layoutMessages.addView(tv);
    };

    private final Runnable displayError = () -> {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        tv.setText("Error");

        layoutMessages.addView(tv);
    };

    private final Runnable sendMessage = () -> {
        boolean isOk = this.api.sendMessage("Mehoff", "Hello world");

        if(isOk){
            runOnUiThread(displayOK);

        } else {
            runOnUiThread(displayError);
        }
    };

    private void loadMessages(){
        // Todo: sort messages by date;
        
        new Thread(() -> {
            ArrayList<Message> messages = api.getMessages();

            for(int i = 0; i < messages.size(); ++i){
                TextView tv = createMessageView(messages.get(i));

                runOnUiThread(() -> {
                    layoutMessages.addView(tv);
                });
            }
        }).start();
    }

    private TextView createMessageView(Message message){
        StringBuilder sb = new StringBuilder();

        sb.append(message.getAuthor());
        sb.append(": ");
        sb.append(message.getText());

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(sb.toString());

        return textView;
    }
}