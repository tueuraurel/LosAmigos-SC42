package losamigos.smartcity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
public class ListeMessageReseauActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_message_reseaux);
    }

    protected void onResume() {
        super.onResume();
        final Intent intent = getIntent();
        TextView test = findViewById(R.id.test);
        ListView listeViewMessage = findViewById(R.id.listeViewMessageReseau);

        if (intent != null) {
            Button boutonNouveauMessage = findViewById(R.id.boutonNouveauMessage);

            boutonNouveauMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent2 = new Intent(ListeMessageReseauActivity.this, NouveauMessageActivity.class);
                    intent2.putExtra("sujetReseau", intent.getStringExtra("sujetReseau"));
                    intent2.putExtra("pseudoUser", intent.getStringExtra("pseudoUser"));
                    startActivity(intent2);
                }
            });

            String sujetReseau = intent.getStringExtra("sujetReseau");
            test.setText(intent.getStringExtra("sujetReseau"));

           /* MessageBDD maBaseMessage = new MessageBDD(this);
            maBaseMessage.open();*/

            /*ArrayList<Message> MesMessage;
            MesMessage=renderMessage(RecuperationMessage.getJSON(intent.getStringExtra("sujetReseau")));

            *//*MesMessage = maBaseMessage.getAllMessageWithSujetReseau(sujetReseau);*/

           /* if (MesMessage != null) {
                Log.d("ApresGETALL", "La taille est :" + MesMessage.size());
                String[] NomMessage = new String[MesMessage.size()];

                for (int i = 0; i < MesMessage.size(); i++) {
                    NomMessage[i] = MesMessage.get(i).getContenu();
                }*/
//android.R.layout.simple_list_item_1 est une vue disponible de base dans le SDK android,
//Contenant une TextView avec comme identifiant "@android:id/text1"

               /* final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListeMessageReseauActivity.this,
                        android.R.layout.simple_list_item_1, NomMessage);
                listeViewMessage.setAdapter(adapter);
                //Log.d("Synchro BDD",maBaseMessage.getLatestMessageWithSujetReseau(sujetReseau).getContenu());
                //maBaseMessage.close();
            } else {
                Toast.makeText(this, "Il n'y a pas de message sur ce reseau"
                        , Toast.LENGTH_LONG).show();
            }
        } else {
            test.setText("ERREUR");
        }
    }*/

      /* private void updateThemeData() {
        new Thread() {
            public void run() {
                final Intent intentIn = getIntent();
                final JSONArray json = RecuperationReseauAccess.getJSON(intentIn.getStringExtra("pseudoUser"));
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ListeReseauUtilisateurActivity.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            reseauList = renderTheme(json);
                            thAdapter= new ReseauAdapter(themeList,ChoixThemeActivity.this);
                            ListeDeMesReseaux.setAdapter(thAdapter);
                        }
                    });
                }
            }
        }.start();

    } */


// Recupere un JSONArray de message et le transforme en un ArrayList de message.
   /*     public ArrayList<Message> renderMessage(JSONArray json) {
            try {
                ArrayList<Message> message = new ArrayList<>();

                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonobject = json.getJSONObject(i);
                    message.add(new Message(jsonobject.getString("contenu"),jsonobject.getString("pseudoAuteur"),jsonobject.getString("sujetReseau")));
                }

                return message;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


    }*/
/*

public class ListeMessageReseauActivity extends Activity {

    ListView lv;
    ArrayList<Message> messageList;
    MessageAdapter msgAdapter;
    Handler handler;
    //ArrayList<Theme> themeChoisi = new ArrayList<>();

    public ListeMessageReseauActivity() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lv = (ListView) findViewById(R.id.listview);
        //recuperer les données du serveur
        updateMessageData();
        Button boutonChoix = (Button) findViewById(R.id.choixButton);
        boutonChoix.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*for(Message message : messageList) {
                    if(theme.selected) {
                        Log.d("themechoisi", theme.getNom());
                        HashMap<String, String> parametres = new HashMap<String, String>();
                        //recuperation du pseudo
                        final Intent intent = getIntent();
                        String pseudo = intent.getStringExtra("PSEUDO");
                        //pour la suite, on conserve le pseudo
                        Intent intent2  = new Intent(ChoixThemeActivity.this, ChoixVilleActivity.class);
                        intent2.putExtra("PSEUDO", pseudo);
                        parametres.put("pseudo", pseudo);
                        parametres.put("idTheme", String.valueOf(theme.getId()));
                        //creation de l'utilisateur dans la base du serveur
                        new RetrieveThemeTask().execute(parametres);
                        //lancement de la prochaine activité -> choix de la ville
                        startActivity(intent2);
                    }
                }*/
/*
            }
        });


    }


    private void updateMessageData() {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                final JSONArray json = RecuperationMessage.getJSON(intent.getStringExtra("sujetReseau"));
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ListeMessageReseauActivity.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            messageList = renderMessage(json);
                            msgAdapter= new MessageAdapter(messageList,ListeMessageReseauActivity.this);
                            lv.setAdapter(msgAdapter);
                        }
                    });
                }
            }
        }.start();

    }

    public ArrayList<Message> renderMessage(JSONArray json) {
        try {
            ArrayList<Message> messages = new ArrayList<Message>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                messages.add(new Message(jsonobject.getString("contenu"),jsonobject.getString("pseudoAuteur"),jsonobject.getString("sujetReseau")));
            }

            return messages;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/

/*    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int pos = lv.getPositionForView(buttonView);
        if (pos != ListView.INVALID_POSITION) {
            Theme t = messageList.get(pos);
            t.setSelected(isChecked);
            //Toast.makeText(this, "Clicked on Theme: " + t.getNom() + ". State: is " + isChecked, Toast.LENGTH_SHORT).show();
        }
    }*/
/*}


class RetrieveMessageTask extends AsyncTask<HashMap<String,String>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"utilisateur/apprecie");

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("contenu", hashMap.get("contenu"));
            jsonObject.accumulate("sujetReseau", Integer.parseInt(hashMap.get("sujetReseau")));
            jsonObject.accumulate("pseudoAuteur", hashMap.get("pseudoAuteur"));
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

class RecuperationMessage {

    // Recupere l'ensemble des message d un reseau.
    public static org.json.JSONArray getJSON(String sujetReseau){
        try {
            URL url = new URL(MainActivity.chemin+"message/"+sujetReseau);
            Log.v("test","URI");
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONArray data = new JSONArray(json.toString());
            Log.v("json", json.toString());
            return data;
        }catch(Exception e){
            return null;
        }
    }
}


class MessageAdapter extends ArrayAdapter<Message>{

    private List<Message> messageList;
    private Context context;

    public MessageAdapter(List<Message> messageList, Context context) {
        super(context, R.layout.single_listview_item, messageList);
        this.messageList = messageList;
        this.context = context;
    }

    private static class MessageHolder {
        public TextView message;
        public TextView auteur;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        MessageHolder holder = new MessageHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_message_item, null);

            holder.message = (TextView) v.findViewById(R.id.message);
            holder.auteur = (TextView) v.findViewById(R.id.auteur);

        } else {
            holder = (MessageHolder) v.getTag();
        }

        Message p = messageList.get(position);
        holder.message.setText(p.getContenu());
        holder.auteur.setText(p.getPseudoAuteur());

        return v;
    }
}*/


public class ListeReseauUtilisateurActivity extends Activity {

    ListView ListeDeMesReseaux;
    ArrayList<Reseau> reseauList;
    ReseauAdapter reseauAdapter;
    Handler handler;
    //ArrayList<Theme> themeChoisi = new ArrayList<>();

    public ListeReseauUtilisateurActivity() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_reseau_utilisateur_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
    ListeDeMesReseaux = (ListView) findViewById(R.id.listeViewReseauUtilisateur);
        //recuperer les données du serveur
        updateReseauData();

        // On recupere l'intent precedent pour avoir les donnees qu'il transporte
        final Intent intentIn = getIntent();
        Log.d("pseudoListeRes",intentIn.getStringExtra("pseudoUser"));
        Log.d("villeListeRes",intentIn.getStringExtra("lieuUser"));

        // On recupere les elements du layout:
        ListeDeMesReseaux = findViewById(R.id.listeViewReseauUtilisateur);
        Button boutonAjouterReseau = findViewById(R.id.boutonVersCreationReseau);
        Button boutonRechercherReseau = findViewById(R.id.boutonVersRechercheReseau);

        // Sur le bouton d'ajout on place un click listener permettant d'amener a l activite de creation
        boutonAjouterReseau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentVersCreationReseau = new Intent(
                        ListeReseauUtilisateurActivity.this,CreationReseauxActivity.class);
                intentVersCreationReseau.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                intentVersCreationReseau.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
                startActivity(intentVersCreationReseau);

            }
        });


        // Sur le bouton de recherche on place un click listener permettant d'amener a l activite de recherche

        boutonRechercherReseau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentVersRechercheReseau = new Intent(
                        ListeReseauUtilisateurActivity.this,RechercheReseauActivity.class);
                intentVersRechercheReseau.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                intentVersRechercheReseau.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
                startActivity(intentVersRechercheReseau);

            }
        });
    }


    private void updateReseauData() {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                final JSONArray json = RecuperationReseaux.getJSON(intent.getStringExtra("pseudoUser"));
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ListeReseauUtilisateurActivity.this, R.string.pas_de_reseau, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            reseauList = renderReseau(json);
                            reseauAdapter= new ReseauAdapter(reseauList,ListeReseauUtilisateurActivity.this);
                            ListeDeMesReseaux.setAdapter(reseauAdapter);
                            ListeDeMesReseaux.setOnItemClickListener(new ListClickHandler());
                        }
                    });
                }
            }
        }.start();

    }

    public ArrayList<Reseau> renderReseau(JSONArray json) {
        try {
            ArrayList<Reseau> reseaux = new ArrayList<Reseau>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                reseaux.add(new Reseau(jsonobject.getString("sujet"), jsonobject.getString("description"), jsonobject.getString("pseudoAdmin"), jsonobject.getString("localisation"), jsonobject.getInt("visibilite")));
            }

            return reseaux;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Intent intentIn = getIntent();
            Reseau resultat = (Reseau) adapter.getItemAtPosition(position);
            Intent intent = new Intent(ListeReseauUtilisateurActivity.this, ListeMessageReseauActivity.class );
            intent.putExtra("sujetReseau",resultat.getSujet());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
            startActivity(intent);
        }

    }
}


class RecuperationReseaux {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String pseudo){
        try {
            URL url = new URL(MainActivity.chemin+"reseauAccess/"+pseudo);
            Log.v("test","URI");
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONArray data = new JSONArray(json.toString());
            Log.v("json", json.toString());
            return data;
        }catch(Exception e){
            return null;
        }
    }
}


class ReseauAdapter extends ArrayAdapter<Reseau>{

    private List<Reseau> reseauList;
    private Context context;

    public ReseauAdapter(List<Reseau> reseauList, Context context) {
        super(context, R.layout.single_listview_item, reseauList);
        this.reseauList = reseauList;
        this.context = context;
    }

    private static class ReseauHolder {
        public TextView sujet;
        public TextView description;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        ReseauHolder holder = new ReseauHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_reseau_item, null);

            holder.sujet = (TextView) v.findViewById(R.id.sujet);
            holder.description = (TextView) v.findViewById(R.id.description);
        } else {
            holder = (ReseauHolder) v.getTag();
        }

        Reseau p = reseauList.get(position);
        holder.sujet.setText(p.getSujet());
        holder.description.setText(p.getDescription());

        return v;
    }
}

