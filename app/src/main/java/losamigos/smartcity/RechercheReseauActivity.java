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


public class RechercheReseauActivity extends Activity implements AdapterView.OnItemClickListener{


    ListView ListeDeNouveauxReseaux;
    ArrayList<Reseau> reseauList;
    ReseauAdapter reseauAdapter;
    Handler handler;

    public RechercheReseauActivity() {
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.recherche_nouveau_reseau_layout);
        ListeDeNouveauxReseaux = findViewById(R.id.listeViewRechercheNouveauReseau);
        updateReseauData();
        final Intent intent = getIntent();





    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d("HelloListView", "You clicked Item: " + id + " at position:" + position);
        Log.d("HelloListView", "Vous avez choisi "+adapterView.getItemAtPosition(position));
        Intent intent = new Intent(RechercheReseauActivity.this,DemandeAdhesionActivity.class);
        intent.putExtra("reseauClique",(String)adapterView.getItemAtPosition(position));
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    // charge les infos du serveur
    private void updateReseauData() {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                Log.d("pseudo",intent.getStringExtra("pseudoUser"));
                Log.d("ville",intent.getStringExtra("lieuUser"));

                final JSONArray json = RechercheReseaux.getJSON(intent.getStringExtra("lieuUser"),intent.getStringExtra("pseudoUser"));
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(RechercheReseauActivity.this, R.string.pas_de_message, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            reseauList = renderReseau(json);
                            reseauAdapter= new ReseauAdapter(reseauList,RechercheReseauActivity.this);
                            ListeDeNouveauxReseaux.setAdapter(reseauAdapter);
                            ListeDeNouveauxReseaux.setOnItemClickListener(new RechercheReseauActivity.ListClickHandler());
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
            Intent intent = new Intent(RechercheReseauActivity.this, ListeMessageReseauActivity.class );
            intent.putExtra("sujetReseau",resultat.getSujet());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
            startActivity(intent);
        }

    }
}


class RechercheReseaux {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String lieu,String pseudo){
        try {
            URL url = new URL(MainActivity.chemin+"reseau/"+lieu+"/"+pseudo);
            Log.v("getJsonRecherche",url.toString());
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

