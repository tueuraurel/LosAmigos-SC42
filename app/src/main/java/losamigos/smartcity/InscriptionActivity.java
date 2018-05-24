package losamigos.smartcity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;

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


        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String date = jour.getText().toString()+"-"+mois.getText().toString()+"-"+annee.getText().toString();
                Intent intent  = new Intent(InscriptionActivity.this, ChoixThemeActivity.class);

                //sexe
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
                //creation de l'utilisateur dans la base du serveur
                new RetrieveUtilisateurTask().execute(parametres);
                //creation de l'utilisateur dans la base du telephone
                UtilisateurBDD maBaseUtilisateur = new UtilisateurBDD(InscriptionActivity.this);
                maBaseUtilisateur.open();
                maBaseUtilisateur.insertUtilisateur(new Utilisateur(pseudo.getText().toString(), MDP.getText().toString(), date, sexe, Float.parseFloat(taille.getText().toString()), Float.parseFloat(poids.getText().toString())));
                maBaseUtilisateur.close();

                //lancement de la prochaine activité -> choix des themes
                intent.putExtra("PSEUDO", pseudo.getText().toString());
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
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"utilisateur");
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", hashMap.get("pseudo"));
            jsonObject.accumulate("MDP", hashMap.get("MDP"));
            jsonObject.accumulate("sexe", Integer.parseInt(hashMap.get("sexe")));
            jsonObject.accumulate("taille", Float.parseFloat(hashMap.get("taille")));
            jsonObject.accumulate("dateNaissance", hashMap.get("dateNaissance"));
            jsonObject.accumulate("poids", Float.parseFloat(hashMap.get("poids")));
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
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