package losamigos.smartcity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class ActiviteMesInvitations extends AppCompatActivity {


    ListView ListeDesReseaux;
    ArrayList<Reseau> reseauList;
    InvitationAdapter invitationAdapter;
    Handler handler;


    public ActiviteMesInvitations() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestioninvitation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ListeDesReseaux = (ListView) findViewById(R.id.listeViewMesInvitations);
        //recuperer les données du serveur
        updateInvitationData();


        // On recupere l'intent precedent pour avoir les donnees qu'il transporte
        final Intent intentIn = getIntent();
        Log.d("pseudoListeRes",intentIn.getStringExtra("pseudoUser"));


    }


    private void updateInvitationData() {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                final JSONArray json = RecuperationInvitation.getJSON(intent.getStringExtra("pseudoUser"));

                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ActiviteMesInvitations.this, R.string.aucuneInvitation, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            ListeDesReseaux = (ListView) findViewById(R.id.listeViewMesInvitations);
                            reseauList = renderReseau(json);
                            Log.d("handlerBug",reseauList.toString());
                            invitationAdapter= new InvitationAdapter(reseauList,ActiviteMesInvitations.this,intent.getStringExtra("pseudoUser"));
                            ListeDesReseaux.setAdapter(invitationAdapter);
                           // ListeDesReseaux.setOnItemClickListener(new ActiviteMesInvitations.ListClickHandler());
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
                reseaux.add(new Reseau(jsonobject.getString("sujetReseau"), jsonobject.getString("description"), jsonobject.getString("pseudoAdmin"), jsonobject.getString("localisation"), jsonobject.getInt("visibilite")));
            }

            return reseaux;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
/*
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

    }*/
}


class RecuperationInvitation {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String pseudo){
        try {
            URL url = new URL(MainActivity.chemin+"invitation/"+pseudo);
            Log.v("getJSON URI",url.toString());
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


