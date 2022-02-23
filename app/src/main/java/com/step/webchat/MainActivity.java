package com.step.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    };

    private void loadMessages(){
        // Todo: sort messages by date;

        // not good placement, but good for now

        runOnUiThread(() -> {
                    layoutMessages.removeAllViews();
                }
        );

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

    private int pxToDp(int px){
        int dpValue = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                px,
                MainActivity.this.getBaseContext().getResources().getDisplayMetrics());

        return dpValue;
    }

    // Todo: for now it creates only incoming message view, make a separation later
    private TextView createMessageView(Message message){
        StringBuilder sb = new StringBuilder();

        sb.append(message.getAuthor());
        sb.append(": ");
        sb.append(message.getText());

        TextView textView = new TextView(this);
        textView.setBackground(getDrawable(R.drawable.shape_message_incoming));
        textView.setLayoutParams(new LinearLayout.LayoutParams(pxToDp(200), LinearLayout.LayoutParams.WRAP_CONTENT));
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

//
//    android:layout_width="200dp" ok
//    android:layout_height="wrap_content" ok
//    android:background="@drawable/shape_message_incoming" ok
//    android:lineSpacingExtra="2dp" ?
//    android:paddingLeft="10dp" ok
//    android:paddingTop="4dp" ok
//    android:paddingRight="10dp" ok
//    android:paddingBottom="10dp" ok
//    android:text="Hi, How are you?" ok
//    android:textColor="@color/black" ok
//    android:textSize="13.5dp" ok
//    app:layout_constraintStart_toStartOf="parent"
//    app:layout_constraintTop_toTopOf="parent"
//    app:layout_constraintWidth_max="wrap"
//    app:layout_constraintWidth_percent="0.8"
}