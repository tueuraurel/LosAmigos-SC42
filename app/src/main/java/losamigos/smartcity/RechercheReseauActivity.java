package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RechercheReseauActivity extends Activity {

    ListView ListeDesReseaux;
    ArrayList<Reseau> reseauList;
    ReseauAdapter reseauAdapter;
    Handler handler;

    public RechercheReseauActivity() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recherche_nouveau_reseau_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListeDesReseaux = (ListView) findViewById(R.id.listeViewRechercheNouveauReseau);
        //recuperer les données du serveur
        updateReseauData(false);
        final EditText champRecherche = findViewById(R.id.textRecherche);
        Button boutonOK = findViewById(R.id.rechercheNouveauReseauOK);
        boutonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(champRecherche.getText().toString().equals("")){
                    Toast.makeText(RechercheReseauActivity.this, R.string.rechercheVide, Toast.LENGTH_LONG).show();
                }else {
                    updateReseauData(true);
                }
            }
        });
    }


    private void updateReseauData(final boolean avecRecherche) {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                EditText champRecherche = findViewById(R.id.textRecherche);
                final JSONArray json;
                if (avecRecherche){
                     json = RecuperationNouveauReseauxRecherche.getJSON(intent.getStringExtra("pseudoUser"),intent.getStringExtra("lieuUser"),champRecherche.getText().toString());
                }
                else {
                     json = RecuperationNouveauReseaux.getJSON(intent.getStringExtra("pseudoUser"), intent.getStringExtra("lieuUser"));
                }
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(RechercheReseauActivity.this, R.string.aucunReseauCorrespondant, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            ListeDesReseaux = (ListView) findViewById(R.id.listeViewRechercheNouveauReseau);
                            reseauList = renderReseau(json);
                            reseauAdapter= new ReseauAdapter(reseauList,RechercheReseauActivity.this);
                            ListeDesReseaux.setAdapter(reseauAdapter);
                            ListeDesReseaux.setOnItemClickListener(new ListClickHandler());
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
            Intent intent = new Intent(RechercheReseauActivity.this, DemandeAdhesionActivity.class );
            intent.putExtra("sujetReseau",resultat.getSujet());
            intent.putExtra("descriptionReseau",resultat.getDescription());
            intent.putExtra("pseudoAdmin",resultat.getPseudoAdmin());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
            startActivity(intent);
        }

    }
}


class RecuperationNouveauReseaux {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String pseudo, String lieu){
        try {
            URL url = new URL(MainActivity.chemin+"reseau/"+lieu+"/"+pseudo);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            JSONArray data = new JSONArray(json.toString());
            return data;
        }catch(Exception e){
            return null;
        }
    }
}

class RecuperationNouveauReseauxRecherche {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String pseudo, String lieu,String recherche){
        try {
            URL url = new URL(MainActivity.chemin+"rechercheReseau/"+lieu+"/"+pseudo+"/"+recherche);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            JSONArray data = new JSONArray(json.toString());
            return data;
        }catch(Exception e){
            return null;
        }
    }
}

