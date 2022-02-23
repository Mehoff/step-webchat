package com.step.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private final MessagesAPI api = new MessagesAPI();
    private Button btnSend;
    private EditText editMessage;
    private LinearLayout layoutMessages;
    private ScrollView scrollView;

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
            scrollView = findViewById(R.id.messagesScrollView);

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
        Editable editable = editMessage.getText();
        String messageText = editable.toString().trim();

        String author = "Mehoff";

        if(messageText.length() <= 0){
            Toast.makeText(this, "Message is too short", Toast.LENGTH_SHORT);
            return;
        }

        // not good placement, but good for now

        runOnUiThread(() -> {
                    layoutMessages.removeAllViews();
                }
        );

        new Thread (() -> {
            ArrayList<Message> messages = this.api.sendMessage(author, messageText);

            for(int i = 0; i < messages.size(); ++i){
                TextView tv = createMessageView(messages.get(i));
                runOnUiThread(() -> {
                    layoutMessages.addView(tv);
                });
            }
        }).start();

        // Does not work for some reason
        // Even in "runOnUiThread"
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    };

    private void loadMessages(){
        runOnUiThread(() -> {
                    layoutMessages.removeAllViews();
                }
        );

        new Thread(() -> {
            ArrayList<Message> messages = api.getMessages();

            messages.sort(Comparator.comparingLong(m -> m.getMoment().getTime()));

            for(int i = 0; i < messages.size(); ++i){
                TextView tv = createMessageView(messages.get(i));

                runOnUiThread(() -> {
                    layoutMessages.addView(tv);
                });
            }
        }).start();
    }

    private int pxToDp(int px){
        int dpValue = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                px,
                MainActivity.this.getBaseContext().getResources().getDisplayMetrics());

        return dpValue;
    }

    // Todo: for now it creates only incoming message view, make a separation later
    private TextView createMessageView(Message message){

        // Todo: change hardcoded nickname

        TextView view = message.getAuthor().equals("Mehoff")
                ? createOutgoingMessageView(message)
                : createIncomingMessageView(message);

        return view;

    }


    private TextView createOutgoingMessageView(Message message){

        StringBuilder sb = new StringBuilder();

        sb.append(message.getAuthor());
        sb.append(": ");
        sb.append(message.getText());

        TextView textView = new TextView(this);
        textView.setId(message.getId());
        textView.setBackground(getDrawable(R.drawable.shape_message_outgoing));
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setPadding(pxToDp(10), pxToDp(4), pxToDp(14), pxToDp(10));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        textView.setTextColor(getColor(R.color.white));
        textView.setTextSize((float)13.5);

        runOnUiThread(() -> {

            ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(textView.getId(), ConstraintSet.START, R.id.messagesLayout, ConstraintSet.START);
            constraintSet.connect(textView.getId(), ConstraintSet.TOP, R.id.messagesLayout, ConstraintSet.TOP);

            constraintSet.applyTo(constraintLayout);

        });

        textView.setText(Html.fromHtml("<b>" + message.getAuthor() + "</b>" +  "<br />" +
                "<small>" + message.getText() + "</small>" + "<br />" +
                "<small>" + message.getDateString() + "</small>"));

        return textView;

    }

    private TextView createIncomingMessageView(Message message){
        StringBuilder sb = new StringBuilder();

        sb.append(message.getAuthor());
        sb.append(": ");
        sb.append(message.getText());

        TextView textView = new TextView(this);
        textView.setId(message.getId());
        textView.setBackground(getDrawable(R.drawable.shape_message_incoming));
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setPadding(pxToDp(14), pxToDp(4), pxToDp(10), pxToDp(10));
        textView.setTextColor(getColor(R.color.white));
        textView.setTextSize((float)13.5);

        runOnUiThread(() -> {

            ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(textView.getId(), ConstraintSet.START, R.id.messagesLayout, ConstraintSet.START);
            constraintSet.connect(textView.getId(), ConstraintSet.TOP, R.id.messagesLayout, ConstraintSet.TOP);

            constraintSet.applyTo(constraintLayout);

        });

        textView.setText(Html.fromHtml("<b>" + message.getAuthor() + "</b>" +  "<br />" +
                "<small>" + message.getText() + "</small>" + "<br />" +
                "<small>" + message.getDateString() + "</small>"));

        return textView;

    }
}