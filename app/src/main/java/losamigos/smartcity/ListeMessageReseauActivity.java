package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


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

            ArrayList<Message> MesMessage;
            MesMessage=renderMessage(RecuperationMessage.getJSON(intent.getStringExtra("sujetReseau")));
            /*MesMessage = maBaseMessage.getAllMessageWithSujetReseau(sujetReseau);*/

            if (MesMessage != null) {
                Log.d("ApresGETALL", "La taille est :" + MesMessage.size());
                String[] NomMessage = new String[MesMessage.size()];

                for (int i = 0; i < MesMessage.size(); i++) {
                    NomMessage[i] = MesMessage.get(i).getContenu();
                }
                //android.R.layout.simple_list_item_1 est une vue disponible de base dans le SDK android,
                //Contenant une TextView avec comme identifiant "@android:id/text1"

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListeMessageReseauActivity.this,
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
    }

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
        public ArrayList<Message> renderMessage(JSONArray json) {
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


    }

    class RecuperationMessage {

        // Recupere l'ensemble des message d un reseau.
        public static org.json.JSONArray getJSON(String sujetReseau){
            try {
                URL url = new URL("http://192.168.1.114/~aurelien/projetMobile/serveur/serveur.php/message/"+sujetReseau);
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