package losamigos.smartcity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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



public class ListeMessageReseauActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<Message> messageList;
    MessageAdapter msgAdapter;
    Handler handler;

    public ListeMessageReseauActivity() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_message_reseaux);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lv = (ListView) findViewById(R.id.listeViewMessageReseau);
        //recuperer les données du serveur

        final Handler boucleMessage= new Handler();


        boucleMessage.postDelayed(monRunnable,2000);
        // updateMessageData();
       // Button boutonNouveauMessage = findViewById(R.id.boutonNouveauMessage);

        final Intent intent = getIntent();

        TextView titre = findViewById(R.id.listeMessageSujetReseau);
        titre.setText(intent.getStringExtra("sujetReseau"));
        Log.d("testInt",intent.getStringExtra("sujetReseau"));


        final EditText nouveauMessage = findViewById(R.id.EditTextNouveauMessage);
        Button valider = findViewById(R.id.BoutonValider);


        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> parametres = new HashMap<String, String>();
                parametres.put("contenu", nouveauMessage.getText().toString());
                parametres.put("pseudoAuteur", intent.getStringExtra("pseudoUser"));
                parametres.put("sujetReseau",intent.getStringExtra("sujetReseau"));
                Log.d("NouveauMessBase",nouveauMessage.getText().toString());
                Log.d("NouveauMessBase",intent.getStringExtra("pseudoUser"));
                Log.d("NouveauMessBase",intent.getStringExtra("sujetReseau"));

                new exportMessageServeur().execute(parametres);

                boucleMessage.postDelayed(monRunnable,2000);
                nouveauMessage.setText("");

            }
        });
    }

    // met a jour les messages toutes les 2 secondes
    private Runnable monRunnable =new Runnable() {
        @Override
        public void run() {
            updateMessageData();
        }
    };


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actualiseMessage:
                final Handler boucleMessage= new Handler();
                boucleMessage.post(monRunnable);
                return true;
            case R.id.seDesinscrire:
                final HashMap<String, String> parametres = new HashMap<String, String>();
                parametres.put("pseudo", getIntent().getStringExtra("pseudoUser"));
                parametres.put("sujetReseau",getIntent().getStringExtra("sujetReseau"));
                new SupprimerAdhesion().execute(parametres);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMessageData() {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                Log.d("updateMessage int",intent.getStringExtra("sujetReseau"));
                final JSONArray json = RecuperationMessage.getJSON(intent.getStringExtra("sujetReseau"));
                Log.d("updateMessage","json: "+json);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ListeMessageReseauActivity.this, R.string.pas_de_message, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            messageList = renderMessage(json);
                            msgAdapter = new MessageAdapter(messageList, ListeMessageReseauActivity.this, intent);
                            lv.setAdapter(msgAdapter);
                            lv.setSelection(lv.getAdapter().getCount()-1);
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
                messages.add(new Message(jsonobject.getString("contenu"), jsonobject.getString("sujetReseau"), jsonobject.getString("pseudoAuteur")));
            }

            return messages;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
class RecuperationMessage {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String sujetReseau){
        try {
            String sujetReseaubis = sujetReseau.replaceAll(" ","%20"); // on remplace les " " par de %20 pour le serveur
            URL url = new URL(MainActivity.chemin+"message/"+sujetReseaubis);

            Log.d("recupMessage","URL  : "+url.toString());
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
            return data;
        }catch(Exception e){
            return null;
        }
    }
}


class MessageAdapter extends ArrayAdapter<Message>{

    private List<Message> messageList;
    private Context context;
    private Intent intent;

    public MessageAdapter(List<Message> messageList, Context context, Intent intent) {
        super(context, R.layout.single_message_item, messageList);
        this.messageList = messageList;
        this.context = context;
        this.intent = intent;
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
            v.setTag(holder);

        } else {
            holder = (MessageHolder) v.getTag();
        }

        Message p = messageList.get(position);
        holder.message.setText(p.getContenu());
        holder.auteur.setText(p.getPseudoAuteur());
        Log.d("MessageAdapter","pseudoUser = "+intent.getStringExtra("pseudoUser"));

        if(p.getPseudoAuteur().equals(intent.getStringExtra("pseudoUser"))){
            holder.auteur.setGravity(Gravity.END);
            holder.message.setGravity(Gravity.END);
        }else {
            holder.auteur.setGravity(Gravity.LEFT);
            holder.message.setGravity(Gravity.LEFT);
        }

        return v;
    }
}

class exportMessageServeur extends AsyncTask<java.util.HashMap<String,String>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"reseau/message");

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("contenu", hashMap.get("contenu"));
            jsonObject.accumulate("pseudoAuteur", hashMap.get("pseudoAuteur"));
            jsonObject.accumulate("sujetReseau", hashMap.get("sujetReseau"));
            //Log.d("insertionBase",jsonObject.toString());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

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

class SupprimerAdhesion extends AsyncTask<HashMap<String,String>, Void, Void> {

    @Override
    //
    //
    // Classe sans aucun sens logique, on utilise un service get dont on recupere les
    // resultat pour faire passer une requete delete !!!!!!

    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        try {
            URL url = new URL(MainActivity.chemin+"SuppressionAdhere/"+hashMap.get("pseudo")+"/"+hashMap.get("sujetReseau"));
            Log.d("test",url.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            return null;

        } catch (MalformedURLException e1) {
            Log.d("refus",e1.getMessage());
            e1.printStackTrace();

        } catch (ClientProtocolException e) {
            Log.d("refus",e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("refus",e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}


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