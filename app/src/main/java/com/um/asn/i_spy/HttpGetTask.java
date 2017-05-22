package com.um.asn.i_spy;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Vincent on 22/05/2017.
 */

public class HttpGetTask extends AsyncTask {


    @Override
    protected Object doInBackground(Object[] params) {

        try {

            /* Recuperation de l'url contenant le chemin de la ressource a retourner */
            URL url = new URL((String)params[0]);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            /* Time out pour permettre l'interaction avec le serveur REST */
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            /* Activation de la recuperation d'un InputStream et OutputStream
            * a partir du serveur */
            conn.setDoInput(true);
            conn.setRequestMethod("GET");

            /* Code reponse du serveur http */
            switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_CREATED :
                    System.out.println("Http request completed !");
                    break;

                case HttpURLConnection.HTTP_OK :
                    System.out.println("Http request completed !");
                    break;

                case HttpURLConnection.HTTP_NOT_FOUND :
                    System.out.println("File not found !");
                    break;

                default :
                    System.out.println("Failed : HTTP error code : "
                            + conn.getResponseCode());
            }

            /* Lecture de la sortie standard du serveur http */
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;
    }

}
