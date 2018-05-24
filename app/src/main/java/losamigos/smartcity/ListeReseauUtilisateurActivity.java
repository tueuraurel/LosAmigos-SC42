package losamigos.smartcity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class ListeReseauUtilisateurActivity extends AppCompatActivity {

    ListView ListeDeMesReseaux;
    ArrayList<Reseau> reseauList;
    ReseauAdapter reseauAdapter;
    Handler handler;

    public ListeReseauUtilisateurActivity() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_reseau_utilisateur_layout);
        final Intent intentIn = getIntent();

        // On recupere les elements du layout:
        Button boutonAjouterReseau = findViewById(R.id.boutonVersCreationReseau);
        Button boutonRechercherReseau = findViewById(R.id.boutonVersRechercheReseau);
        Button boutonMesInvitations = findViewById(R.id.boutonVersMesInvitations);
        Button boutonMesReseaux = findViewById(R.id.boutonVersGestionReseaux);

        // Sur le bouton d'ajout on place un click listener permettant d'amener a l activite de creation
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

        // Sur le bouton de recherche on place un click listener permettant d'amener a l activite de recherche
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

        boutonMesInvitations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentVersMesInvitations = new Intent(ListeReseauUtilisateurActivity.this, ActiviteMesInvitations.class);
                intentVersMesInvitations.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                startActivity(intentVersMesInvitations);
            }
        });

        boutonMesReseaux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentVersMesReseaux = new Intent(ListeReseauUtilisateurActivity.this,ListeReseauAdmin.class);
                intentVersMesReseaux.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                startActivity(intentVersMesReseaux);
        }});
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListeDeMesReseaux = (ListView) findViewById(R.id.listeViewReseauUtilisateur);
        //recuperer les données du serveur
        updateReseauData();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reseau, menu);
        return true;
    }

    private Runnable monRunnable =new Runnable() {
        @Override
        public void run() {updateReseauData();       }
    };


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actualiseReseau:
                final Handler boucleMessage= new Handler();
                boucleMessage.post(monRunnable);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void updateReseauData() {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                final JSONArray json = RecuperationReseaux.getJSON(intent.getStringExtra("pseudoUser"));
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ListeReseauUtilisateurActivity.this, R.string.pas_de_reseau, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            reseauList = renderReseau(json);
                            reseauAdapter= new ReseauAdapter(reseauList,ListeReseauUtilisateurActivity.this);
                            ListeDeMesReseaux.setAdapter(reseauAdapter);
                            ListeDeMesReseaux.setOnItemClickListener(new ListClickHandler());
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
            Intent intent = new Intent(ListeReseauUtilisateurActivity.this, ListeMessageReseauActivity.class );
            intent.putExtra("sujetReseau",resultat.getSujet());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
            startActivity(intent);
        }

    }
}


class RecuperationReseaux {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String pseudo){
        try {
            URL url = new URL(MainActivity.chemin+"reseauAccess/"+pseudo);
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

