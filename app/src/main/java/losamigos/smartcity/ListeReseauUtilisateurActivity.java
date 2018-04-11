package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    public static final String EXTRA_SUJETRESEAU="sujetReseau";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_reseau_utilisateur_layout);
    }

    protected  void onResume(){
        super.onResume();
        final Intent intentIn = getIntent();

        ListView ListeDeMesReseaux = findViewById(R.id.listeViewReseauUtilisateur);
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


        ReseauBDD maBaseReseau = new ReseauBDD(this);
        maBaseReseau.open();
        ArrayList<Reseau> MesReseaux;



        MesReseaux = maBaseReseau.getAllReseauAccessUser(intentIn.getStringExtra("pseudoUser"));
        maBaseReseau.close();
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


    }

    class RecuperationReseauAccess {

    public static org.json.JSONArray getJSON(String pseudoUser){
        try {
            URL url = new URL("http://192.168.1.64/~aurelien/projetMobile/serveur/serveur.php/reseauAccess/"+pseudoUser);
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
