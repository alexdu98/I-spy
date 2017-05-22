package com.um.asn.i_spy;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Vincent on 09/04/2017.
 */

public class HttpPostTask extends AsyncTask {


    @Override
    protected Object doInBackground(Object[] params) {

        String replyFromServer = "";

        try {

            /* Recuperation de l'url et de l'objet json a envoyer en POST
             * L'objet json est sous forme de String */
            URL url = new URL((String)params[0]);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            /* Time out pour permettre l'interaction avec le serveur REST */
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            /* Activation de la recuperation d'un InputStream et OutputStream
            * a partir du serveur */
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            /* Objet json sous forme de String */
            String input = (String)params[1];
            OutputStream os = conn.getOutputStream();

            /* Ecriture dans l'entree standard du serveur REST */
            os.write(input.getBytes());
            os.flush();

            /* Code reponse du serveur http */
            switch (conn.getResponseCode()) {

                // En cas de reussite
                case HttpURLConnection.HTTP_CREATED :
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
                replyFromServer += output;
            }


            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return replyFromServer;
    }
}
