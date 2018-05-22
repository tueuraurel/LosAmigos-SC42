package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


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
import android.os.AsyncTask;

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
                                //ReseauBDD maBaseReseau = new ReseauBDD(CreationReseauxActivity.this);
                                //maBaseReseau.open();

                                Log.d("CreationReseau","La visibilite est : " + visibilite);

                              //  maBaseReseau.insertReseau(new Reseau (editTextSujet.getText().toString(),editTextDescription.getText().toString(),
                               //         intent.getStringExtra("pseudoUser"),intent.getStringExtra("lieuUser"),visibilite));
                                Log.d("CreationReseau", "pseudoUser : "+intent.getStringExtra("pseudoUser"));
                                Log.d("CreationReseau", "lieuUser : " + intent.getStringExtra("lieuUser"));
                                //maBaseReseau.close();
                    /* A modifier ensuite pour ne pas pouvoir faire un retour dessus,
                     utiliser finish();
                     */
                    finish();
                                /*
                                Intent intentRetour = new Intent(CreationReseauxActivity.this,ListeReseauUtilisateurActivity.class);
                                intentRetour.putExtra("pseudoUser",intent.getStringExtra("pseudoUser"));
                                intentRetour.putExtra("lieuUser",intent.getStringExtra("lieuUser"));
                                startActivity(intentRetour);*/
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
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"reseau/ajoutReseau");

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("sujet", hashMap.get("sujet"));
            jsonObject.accumulate("description", hashMap.get("description"));
            jsonObject.accumulate("pseudoAdmin", hashMap.get("pseudoAdmin"));
            jsonObject.accumulate("localisation", hashMap.get("localisation"));
            jsonObject.accumulate("visibilite", Integer.parseInt(hashMap.get("visibilite")));
            Log.d("insertionReseau",MainActivity.chemin+"reseau/ajoutReseau");
            Log.d("insertionReseau",jsonObject.toString());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);
            Log.d("insertionReseau",se.getContent().toString());
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
    }}