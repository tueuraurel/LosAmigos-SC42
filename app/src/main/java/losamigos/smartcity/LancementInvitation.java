package losamigos.smartcity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class LancementInvitation extends AppCompatActivity {

    ListView lv;
    ArrayList<Message> messageList;
    MessageAdapter msgAdapter;
    Handler handler;
    int nombreUtilisateur;
    EditText pseudoReceveur;

    public LancementInvitation() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.envoi_invit_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Intent intent = getIntent();

        final EditText pseudoReceveur = findViewById(R.id.pseudoPourInvit);
        Button valider = findViewById(R.id.envoyerInscription);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (pseudoReceveur.getText().toString().equals("")) {
                    Toast.makeText(LancementInvitation.this, R.string.pas_d_utilisateur, Toast.LENGTH_LONG).show();
                }else {
                    updateNombre();

                    Handler attente = new Handler();
                    attente.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (nombreUtilisateur == 1) {

                                HashMap<String, String> parametres = new HashMap<String, String>();
                                parametres.put("pseudoReceveur", pseudoReceveur.getText().toString());
                                parametres.put("sujetReseau", intent.getStringExtra("sujetReseau"));

                                new EnvoiInvitServeur().execute(parametres);
                                pseudoReceveur.setText("");
                                Toast.makeText(LancementInvitation.this, R.string.invitationEnvoyer, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LancementInvitation.this, R.string.login_incorrecte, Toast.LENGTH_LONG).show();
                            }
                        }
                    },2000);
                }
            }
        });
    }


    private void updateNombre() {
        new Thread() {
            public void run() {
                EditText pseudoReceveur = findViewById(R.id.pseudoPourInvit);
                final JSONArray json = RecuperationNombreUtilisateur.getJSON(pseudoReceveur.getText().toString());
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(LancementInvitation.this, "Erreur", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                JSONObject obj =json.getJSONObject(0);
                                nombreUtilisateur=obj.getInt("nombre");
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }.start();
        Log.d("updateNombre",String.valueOf(nombreUtilisateur));
    }

    class EnvoiInvitServeur extends AsyncTask<HashMap<String, String>, Void, Void> {

        @Override
        protected Void doInBackground(HashMap<String, String>[] hashMaps) {
            HashMap<String, String> hashMap = hashMaps[0];
            InputStream inputStream = null;
            String result = "";
            try {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(MainActivity.chemin + "envoiInvit");

                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("pseudo", hashMap.get("pseudoReceveur"));
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
}


class RecuperationNombreUtilisateur {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String pseudo) {
        try {
            URL url = new URL(MainActivity.chemin + "verifPseudo/" + pseudo);
            Log.v("test", "URI");
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONArray data = new JSONArray(json.toString());
            Log.v("json", json.toString());
            return data;
        } catch (Exception e) {
            return null;
        }
    }}