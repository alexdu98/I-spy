package com.um.asn.i_spy;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.um.asn.i_spy.adapters.ContactsAdapter;
import com.um.asn.i_spy.http_methods.HttpGetTask;
import com.um.asn.i_spy.models.Contact;
import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ShowSlaveContactsFragment extends Fragment {

    private Phone targetPhone;
    private User currentUser;
    private ContactsAdapter showSlaveContactsAdapter;
    private ListView showSlaveContactsListView;
    private TextView showSlaveContactsTextView;
    private String url;
    private OnContactSelectedListener contactSelectedCallback;

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            contactSelectedCallback = (OnContactSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContactSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_show_slave_contacts, container, false);

        try {
            showSlaveContactsListView = (ListView) v.findViewById(R.id.show_slave_contacts_list_view);
            showSlaveContactsTextView = (TextView) v.findViewById(R.id.show_slave_contacts_message);

            // Construction de l'url REST
            url = Config.SERVER_DOMAIN + "user/" + currentUser.getId()
                    + "/phone/" + targetPhone.getId() + "/contacts" +
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
                JSONArray contactsArray = (JSONArray) replyFromServer.get("data");

                if (contactsArray.length() == 0) {

                    showSlaveContactsTextView.setText(R.string.message_no_contact);
                    showSlaveContactsTextView.setVisibility(View.VISIBLE);

                } else {

                    // Boucle sur tous les objets JSON renvoye par la requete
                    ArrayList<Contact> contacts = new ArrayList<>();
                    JSONObject contactJSON;

                    for (int i = 0; i < contactsArray.length(); i++) {
                        contactJSON = contactsArray.getJSONObject(i);
                        contacts.add(new Contact((int) contactJSON.get("id"),
                                (String) contactJSON.get("nom"),
                                (String) contactJSON.get("numero")));
                    }

                    showSlaveContactsAdapter = new ContactsAdapter(getActivity(), contacts);

                    showSlaveContactsListView.setAdapter(showSlaveContactsAdapter);

                    showSlaveContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            TextView selectedContactId = (TextView) view.findViewById(R.id.contact_id);
                            TextView selectedContactName = (TextView) view.findViewById(R.id.contact_name);
                            TextView selectedContactPhoneNumber = (TextView) view.findViewById(R.id.contact_phone_number);

                            contactSelectedCallback.onContactSelected(new Contact(
                                    Integer.valueOf(selectedContactId.getText().toString()),
                                    selectedContactName.getText().toString(),
                                    selectedContactPhoneNumber.getText().toString()));
                        }
                    });
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

        getActivity().setTitle(getResources().getString(R.string.show_slave_contacts_fragment_label) + " " + targetPhone.getLogin());

    }

    // Container Activity must implement this interface
    public interface OnContactSelectedListener {
        void onContactSelected(Contact contact);
    }
}
