package com.um.asn.i_spy.websockets;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.um.asn.i_spy.ListSlavesActivity;
import com.um.asn.i_spy.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MasterWS extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private Context context;
    private JSONObject userInfo;

    public MasterWS(Context context, JSONObject userInfo) {
        super();
        this.context = context;
        this.userInfo = userInfo;
    }

    @Override
    public void onOpen(okhttp3.WebSocket webSocket, Response response) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("cmd", "CONNEXION");
            msg.put("type", "master");

            msg.put("prop", this.userInfo.toString());

            System.out.println("Connexion websocket : master");
            webSocket.send(msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String text) {
        System.out.println("Receiving : " + text);
        try {
            JSONObject obj = new JSONObject(text);
            String cmd = (String) obj.get("cmd");
            if (cmd.equals("SLAVE_CONNECT")) {
                final String id = (String) obj.get("id");
                final String login = (String) obj.get("login");

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Phone " + id + " (" + login + ") connecté !", Toast.LENGTH_LONG).show();

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_menu)
                                        .setColor(0x00FF00)
                                        .setContentTitle("I-Spy")
                                        .setContentText("Phone " + id + " (" + login + ") connecté !")
                                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                        Intent notificationIntent = new Intent(context, ListSlavesActivity.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(contentIntent);

                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(0, mBuilder.build());
                    }
                });
            } else if (cmd.equals("SLAVE_DISCONNECT")) {
                final String id = (String) obj.get("id");
                final String login = (String) obj.get("login");

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Phone " + id + " (" + login + ") déconnecté !", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
