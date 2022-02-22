package com.step.webchat;
import android.util.Log;

import com.step.webchat.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MessagesAPI {

    private final String BASE_URL = "http://chat.momentfor.fun/";
    ArrayList<Message> messages = new ArrayList<>();

    public ArrayList<Message> getMessages() {
        try{
            URL url = new URL(BASE_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("accept", "application/json");

            InputStream stream = con.getInputStream();

            String contentBuffer = null;
            StringBuilder sb = new StringBuilder();
            int sym;
            while((sym = stream.read()) != -1)
                sb.append((char) sym);

            con.disconnect();
            contentBuffer = new String(sb.toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            JSONObject jsonResponse = new JSONObject(contentBuffer);
            JSONArray messagesJsonArray = jsonResponse.getJSONArray("data");

            messages.clear();
            for(int i = 0; i < messagesJsonArray.length(); ++i){
                JSONObject obj = messagesJsonArray.getJSONObject(i);

                Message msg = new Message(
                        obj.getInt("id"),
                        obj.getString("author"),
                        obj.getString("text"),
                        obj.getString("moment")
                );
                messages.add(msg);
            }
            return messages;
        }
        catch (Exception ex){
            Log.e("getMessages", ex.getMessage());
            return null;
        }
    }

    public boolean sendMessage(String author, String message){
        if(author.length() <= 1 || message.length() <= 0){
            return false;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("http://chat.momentfor.fun/?author=");
        sb.append(author);
        sb.append("&msg=");
        sb.append(message);

        String contentBuffer = null;
        URL url = null;
        try {
            url = new URL(sb.toString());

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("accept", "application/json");

            InputStream stream = con.getInputStream();

            sb = new StringBuilder();
            int sym;
            while((sym = stream.read()) != -1)
                sb.append((char) sym);

            con.disconnect();
            contentBuffer = new String(sb.toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            Log.i("sendMessage", contentBuffer);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("sendMessage", e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e("sendMessage", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

//    private final Runnable makeRequest = (String url) -> {
//        String contentBuffer;
//        try{
//            InputStream stream = new URL(url).openStream();
//            StringBuilder sb = new StringBuilder();
//            int sym;
//            while((sym = stream.read()) != -1)
//                sb.append((char) sym);
//
//            contentBuffer = new String(sb.toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
//
//            runOnUiThread(parseContent);
//        }
//        catch (Exception ex){
////            Log.e("loadCurrency()", ex.getMessage());
////            runOnUiThread(() -> {
////                Toast.makeText(MainActivity.this, "Failed to load currency, try again later", Toast.LENGTH_SHORT).show();
////            });
//        }
//    };
}