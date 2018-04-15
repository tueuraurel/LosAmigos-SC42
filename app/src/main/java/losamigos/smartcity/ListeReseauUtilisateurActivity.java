package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ListeReseauUtilisateurActivity extends Activity implements AdapterView.OnItemClickListener{
// Activite permettant de recuperer l ensemble des reseaux accessible par l utilisateur, ainsi que
    //d'amener au page pour en creer un ou en rechercher.
    public static final String EXTRA_SUJETRESEAU="sujetReseau";
    ArrayList<Reseau> reseauList;
    ThemeAdapter thAdapter;
    Handler handler;
    ListView ListeDeMesReseaux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_reseau_utilisateur_layout);
    }

    protected  void onResume(){
        super.onResume();
        final Intent intentIn = getIntent();

        ListeDeMesReseaux = findViewById(R.id.listeViewReseauUtilisateur);
        Button boutonAjouterReseau = findViewById(R.id.boutonVersCreationReseau);
        Button boutonRechercherReseau = findViewById(R.id.boutonVersRechercheReseau);

        boutonAjouterReseau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentVersCreationReseau = new Intent(
                        ListeReseauUtilisateurActivity.this,CreationReseauxActivity.class);
                intentVersCreationReseau.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                intentVersCreationReseau.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
                startActivity(intentVersCreationReseau);

            }
        });


        boutonRechercherReseau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentVersRechercheReseau = new Intent(
                        ListeReseauUtilisateurActivity.this,RechercheReseauActivity.class);
                intentVersRechercheReseau.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                intentVersRechercheReseau.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
                startActivity(intentVersRechercheReseau);

            }
        });


        /*ReseauBDD maBaseReseau = new ReseauBDD(this);
        maBaseReseau.open();*/

        ArrayList<Reseau> MesReseaux;
        Log.d("pseudoUser : ","n"+intentIn.getStringExtra("pseudoUser")+"n");

        MesReseaux=renderReseau(RecuperationReseauAccess.getJSON(intentIn.getStringExtra("pseudoUser").trim()));


/*
        MesReseaux = maBaseReseau.getAllReseauAccessUser(intentIn.getStringExtra("pseudoUser"));
        maBaseReseau.close();*/
        if(MesReseaux!=null) {
            Log.d("ApresGETALL", "La taille est :" + MesReseaux.size());

        /* On cree un tableau de string contenant le nom de tout les reseaux auxquels a acces
        l utilisateur (adherent et/ou admin)
         */
            String[] NomReseaux = new String[MesReseaux.size()];

            for (int i = 0; i < MesReseaux.size(); i++) {
                NomReseaux[i] = MesReseaux.get(i).getSujet();
            }

            //Toast.makeText(this, NomReseaux[0], Toast.LENGTH_LONG).show();


            //android.R.layout.simple_list_item_1 est une vue disponible de base dans le SDK android,
            //Contenant une TextView avec comme identifiant "@android:id/text1"
            // On cree un adapter pour le mettre dans le list view

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListeReseauUtilisateurActivity.this,
                    android.R.layout.simple_list_item_1, NomReseaux);
            ListeDeMesReseaux.setAdapter(adapter);

            ListeDeMesReseaux.setOnItemClickListener(this);

        }
        else{
            Toast.makeText(this, "Vous n etes adherent a aucun reseaux actuellement", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    // pour chaque reseau disponible on peut cliquer dessus pour acceder aux messages du reseau
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d("HelloListView", "You clicked Item: " + id + " at position:" + position);
        Log.d("HelloListView", "Vous avez choisi "+adapterView.getItemAtPosition(position));
        Intent intent = new Intent(ListeReseauUtilisateurActivity.this,ListeMessageReseauActivity.class);
        Intent intentIn = getIntent();
        intent.putExtra(EXTRA_SUJETRESEAU,(String)adapterView.getItemAtPosition(position));
        intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
        intent.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
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


    // Recupere un JSONArray de reseau et le transforme en un ArrayList de reseau.
    public ArrayList<Reseau> renderReseau(JSONArray json) {
        try {
            ArrayList<Reseau> reseaux = new ArrayList<>();
            if(json!=null){
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
            reseaux.add(new Reseau(jsonobject.getString("sujet"),jsonobject.getString("description"),jsonobject.getString("pseudoAdmin"),jsonobject.getString("localisation"), jsonobject.getInt("visibilite")));
            }

            return reseaux;
            }else{
                Log.d("probleme","probleme json");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    }

    class RecuperationReseauAccess {

    // Recupere l'ensemble des reseaux auquel un utilisateur a acces.
    public static org.json.JSONArray getJSON(String pseudoUser){
        try {
            URL url = new URL(MainActivity.chemin+"reseauAccess/"+pseudoUser);
            Log.v("test",url.toString());
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            Log.v("test",url.toString());
            Log.v("test",connection.getResponseMessage());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            Log.v("test",url.toString());
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            Log.v("testFF",url.toString());
            JSONArray data = new JSONArray(json.toString());
            Log.v("json", json.toString());

            return data;
        }catch(Exception e){
            Log.d("catch","probleme");
            return null;
        }
    }
}
