package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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


public class DemandeAdhesionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.demande_adhesion_layout);

        // Recuperation des éléments :
        TextView nomReseau = findViewById(R.id.demandeAdhesionSujetReseau);
        TextView descriptionReseau = findViewById(R.id.demandeAdhesionDesciptionReseau);
        TextView pseudoAdmin = findViewById(R.id.demandeAdhesionAdminReseau);
        Button bouton = findViewById(R.id.demandeAdhesionValider);

        final Intent intent = getIntent();

        nomReseau.setText(intent.getStringExtra("sujetReseau"));
        descriptionReseau.setText(intent.getStringExtra("descriptionReseau"));
        pseudoAdmin.setText(intent.getStringExtra("pseudoAdmin"));

        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> parametres = new HashMap<String, String>();
                parametres.put("pseudo", intent.getStringExtra("pseudoUser"));
                parametres.put("sujetReseau",intent.getStringExtra("sujetReseau"));
                //prise en compte de l'adhesion dans la base du serveur
                new AdhesionBase().execute(parametres);
                finish();

            }
        });
    }
}

class AdhesionBase extends AsyncTask<HashMap<String,String>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"adhere/adhesion");

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", hashMap.get("pseudo"));
            jsonObject.accumulate("sujetReseau", hashMap.get("sujetReseau"));

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