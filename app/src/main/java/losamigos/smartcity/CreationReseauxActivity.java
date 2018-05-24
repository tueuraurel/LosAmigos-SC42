package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;

public class CreationReseauxActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_reseaux_layout);
        final EditText editTextSujet = findViewById(R.id.newSujetReseau);
        final EditText editTextDescription = findViewById(R.id.newDescriptionReseau);
        Button confirmer = findViewById(R.id.newReseauConfirmer);
        final Intent intent = getIntent();
        if (intent!=null){
        final RadioGroup groupe = findViewById(R.id.radioGroupeVisibilite);
        confirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibilite=-1;
                RadioButton boutonRadio = findViewById(groupe.getCheckedRadioButtonId());
                if(boutonRadio!=null){
                    if (boutonRadio.getText().equals("Reseau public")){
                        visibilite=1;
                    }
                    if (boutonRadio.getText().equals("Reseau prive")){
                        visibilite=0;
                    }
                }else {
                    Toast.makeText(CreationReseauxActivity.this, "Veuillez choisir public ou prive", Toast.LENGTH_LONG).show();
                }
                if (editTextSujet.getText().toString().isEmpty()){ /* Il faudra aussi verifier que le sujet n est pas deja dans la base */
                    Toast.makeText(CreationReseauxActivity.this, "Veuillez remplir le sujet", Toast.LENGTH_LONG).show();
                }else {
                    if (editTextDescription.getText().toString().isEmpty()) {
                        Toast.makeText(CreationReseauxActivity.this, "Veuillez remplir la description", Toast.LENGTH_LONG).show();
                    }
                    else {
                        HashMap<String, String> parametres = new HashMap<String, String>();
                        parametres.put("sujet", editTextSujet.getText().toString());
                        parametres.put("description", editTextDescription.getText().toString());
                        parametres.put("pseudoAdmin",intent.getStringExtra("pseudoUser"));
                        parametres.put("localisation",intent.getStringExtra("lieuUser"));
                        parametres.put("visibilite",String.valueOf(visibilite));

                        //creation du reseau dans la base du serveur
                        new insertionReseauBase().execute(parametres);

                        //exemple utilisation de SQLLite
                        //ReseauBDD maBaseReseau = new ReseauBDD(CreationReseauxActivity.this);
                        // maBaseReseau.open();
                        //  maBaseReseau.insertReseau(new Reseau (editTextSujet.getText().toString(),editTextDescription.getText().toString(),
                        // intent.getStringExtra("pseudoUser"),intent.getStringExtra("lieuUser"),visibilite));
                        //maBaseReseau.close();

                        finish();
                        }
                    }

                }
            });

        }
    }

}

class insertionReseauBase extends AsyncTask<java.util.HashMap<String,String>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"reseau/ajoutReseau");
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("sujet", hashMap.get("sujet"));
            jsonObject.accumulate("description", hashMap.get("description"));
            jsonObject.accumulate("pseudoAdmin", hashMap.get("pseudoAdmin"));
            jsonObject.accumulate("localisation", hashMap.get("localisation"));
            jsonObject.accumulate("visibilite", Integer.parseInt(hashMap.get("visibilite")));

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