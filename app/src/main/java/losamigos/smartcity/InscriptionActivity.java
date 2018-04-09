package losamigos.smartcity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InscriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        Button btn = (Button) findViewById(R.id.inscrbutton);
        final EditText pseudo = (EditText) findViewById(R.id.pseudoInput);
        final EditText MDP = (EditText) findViewById(R.id.passwordInput);
        final EditText jour = (EditText) findViewById(R.id.day);
        final EditText mois = (EditText) findViewById(R.id.month);
        final EditText annee = (EditText) findViewById(R.id.year);

        final EditText taille = (EditText) findViewById(R.id.tailleInput);
        final EditText poids = (EditText) findViewById(R.id.poidsInput);
//
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String date = jour.getText().toString()+"-"+mois.getText().toString()+"-"+annee.getText().toString();

                //Log.d("date", jour.getText().toString());
                //Log.d("date", date);
                Intent intent  = new Intent(InscriptionActivity.this, ChoixThemeActivity.class);
                //creation de l'utilisateur dans la base du telephone
                final RadioGroup groupe = findViewById(R.id.groupe);
                int sexe=-1;
                RadioButton boutonRadio = findViewById(groupe.getCheckedRadioButtonId());
                if(boutonRadio!=null) {
                    if (boutonRadio.getText().equals("Femme")) {
                        sexe = 1;
                    }
                    if (boutonRadio.getText().equals("Homme")) {
                        sexe = 0;
                    }
                }
                HashMap<String, String> parametres = new HashMap<String, String>();
                parametres.put("pseudo", pseudo.getText().toString());
                parametres.put("MDP", MDP.getText().toString());
                parametres.put("sexe",String.valueOf(sexe));
                parametres.put("taille",taille.getText().toString());
                parametres.put("poids",poids.getText().toString());
                parametres.put("dateNaissance",date);
                new RetrieveUtilisateurTask().execute(parametres);
                UtilisateurBDD maBaseUtilisateur = new UtilisateurBDD(InscriptionActivity.this);
                maBaseUtilisateur.open();

                Log.d("CreationUtilisateur","en cours..");

                maBaseUtilisateur.insertUtilisateur(new Utilisateur(pseudo.getText().toString(), MDP.getText().toString(), date, sexe, Float.parseFloat(taille.getText().toString()), Float.parseFloat(poids.getText().toString())));
                //Log.d("CreationUtilisateur", "pseudo : "+intent.getStringExtra("pseudo"));
                maBaseUtilisateur.close();
                //ajouter l'utilisateur dans la base du serveur
                //new RetrieveUtilisateurTask(pseudo.getText().toString(), MDP.getText().toString(), String.valueOf(sexe), taille.getText().toString(), poids.getText().toString(),date).execute();
                //lancement de la prochaine activité -> choix des themes
                startActivity(intent);
            }
        });
    }

}

class RetrieveUtilisateurTask extends AsyncTask<HashMap<String,String>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost("http://10.0.2.2/~marine/mobile/serveur.php/utilisateur");

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", hashMap.get("pseudo"));
            jsonObject.accumulate("MDP", hashMap.get("MDP"));
            jsonObject.accumulate("sexe", Integer.parseInt(hashMap.get("sexe")));
            jsonObject.accumulate("taille", Float.parseFloat(hashMap.get("taille")));
            jsonObject.accumulate("dateNaissance", hashMap.get("dateNaissance"));
            jsonObject.accumulate("poids", Float.parseFloat(hashMap.get("poids")));

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

///////ERREUR : Ajout dans la BDD serveur d'un objet vide !!