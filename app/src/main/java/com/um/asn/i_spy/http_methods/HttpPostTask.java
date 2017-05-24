package com.um.asn.i_spy.http_methods;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HttpPostTask extends AsyncTask {

    @Override
    @SuppressWarnings("deprecation")
    protected Object doInBackground(Object[] params) {

        String replyFromServer = "";
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost((String) params[0]);
        StringEntity httpBody = null;

        try {
            httpBody = new StringEntity("json=" + URLEncoder.encode((String) params[1]));
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(httpBody);
            HttpResponse response = null;
            response = httpClient.execute(request);

            replyFromServer = EntityUtils.toString(response.getEntity());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(replyFromServer);

        return replyFromServer;
    }
}
