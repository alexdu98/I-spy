package com.um.asn.i_spy.http_methods;


import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;


public class HttpGetTask extends AsyncTask {

    @Override
    @SuppressWarnings("deprecation")
    protected Object doInBackground(Object[] params) {

        String replyFromServer = "";

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet((String) params[0]);

        try {

            HttpResponse response = null;
            response = httpClient.execute(request);

            replyFromServer = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(replyFromServer);

        return replyFromServer;
    }

}
