package losamigos.smartcity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ListeReseauAdmin extends AppCompatActivity {
    ListView ListeDeMesReseaux;
    ArrayList<Reseau> reseauList;
    ReseauAdapter reseauAdapter;
    Handler handler;

    public ListeReseauAdmin() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_reseau_admin_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListeDeMesReseaux = (ListView) findViewById(R.id.listeViewReseauAdmin);
        //recuperer les données du serveur
        updateReseauData();
    }


    private void updateReseauData() {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                final JSONArray json = RecuperationReseauxAdmin.getJSON(intent.getStringExtra("pseudoUser"));
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ListeReseauAdmin.this, R.string.pas_de_reseau_admin, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            reseauList = renderReseau(json);
                            reseauAdapter= new ReseauAdapter(reseauList,ListeReseauAdmin.this);
                            ListeDeMesReseaux.setAdapter(reseauAdapter);
                            ListeDeMesReseaux.setOnItemClickListener(new ListeReseauAdmin.ListClickHandler());
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
            Reseau resultat = (Reseau) adapter.getItemAtPosition(position);
            Intent intent = new Intent(ListeReseauAdmin.this, LancementInvitation.class );
            intent.putExtra("sujetReseau",resultat.getSujet());
            startActivity(intent);
        }

    }
}


class RecuperationReseauxAdmin {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String pseudo){
        try {
            URL url = new URL(MainActivity.chemin+"listeReseauAdmin/"+pseudo);
            Log.v("test","URI");
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

