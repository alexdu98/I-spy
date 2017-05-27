package com.um.asn.i_spy.websockets;

import android.content.Context;

import com.um.asn.i_spy.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MasterWS extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private Context context;

    public MasterWS(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onOpen(okhttp3.WebSocket webSocket, Response response) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("cmd", "CONNEXION");
            msg.put("type", "master");

            User user = new User();
            user.loadWithFile(this.context);

            msg.put("prop", user.getJSON().toString());

            webSocket.send(msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String text) {
        System.out.println("Receiving : " + text);
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, ByteString bytes) {
        return;
    }

    @Override
    public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        System.out.println("Closing : " + code + " / " + reason);
    }

    @Override
    public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
        System.out.println("Closed : " + code + " / " + reason);
    }

    @Override
    public void onFailure(okhttp3.WebSocket webSocket, Throwable t, Response response) {
        System.out.println("Error : " + t.getMessage());
    }
}
