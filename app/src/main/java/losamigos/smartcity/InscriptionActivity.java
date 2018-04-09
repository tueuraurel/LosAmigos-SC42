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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
        final String date = jour.getText().toString()+"-"+mois.getText().toString()+"-"+annee.getText().toString();
        final EditText taille = (EditText) findViewById(R.id.tailleInput);
        final EditText poids = (EditText) findViewById(R.id.poidsInput);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
        Log.d("post","entree");
        HttpPost httppost = new HttpPost("http://10.0.2.2/~marine/mobile/serveur.php/utilisateur");
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("pseudo", hashMap.get("pseudo")));
        postParameters.add(new BasicNameValuePair("MDP", hashMap.get("MDP")));
        postParameters.add(new BasicNameValuePair("sexe", hashMap.get("sexe")));
        postParameters.add(new BasicNameValuePair("taille", hashMap.get("taille")));
        postParameters.add(new BasicNameValuePair("poids", hashMap.get("poids")));
        postParameters.add(new BasicNameValuePair("dateNaissance", hashMap.get("dateNaissance")));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost); //Voila, la requête est envoyée
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity, "utf-8");
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            if (!status.equals("success")) {
                throw new Exception(jsonObject.getString("msg"));
            }
            System.out.println(status);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

///////ERREUR : Ajout dans la BDD serveur d'un objet vide !!