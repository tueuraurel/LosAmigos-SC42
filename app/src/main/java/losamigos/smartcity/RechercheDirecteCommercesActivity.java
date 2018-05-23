package losamigos.smartcity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class RechercheDirecteCommercesActivity extends AppCompatActivity {

    ListView listeCommerces;
    ArrayList<Commerce> commercesList;
    CommerceAdapter commerceAdapter;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_directe_commerces);
    }

    public RechercheDirecteCommercesActivity() {
        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listeCommerces = (ListView) findViewById(R.id.listeViewRechercheCommerce);

        final EditText champRecherche = findViewById(R.id.textRecherche);
        Button boutonOK = findViewById(R.id.rechercheCommerceOK);

        boutonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(champRecherche.getText().toString().equals("")){
                    Toast.makeText(RechercheDirecteCommercesActivity.this, R.string.rechercheVide, Toast.LENGTH_LONG).show();
                }else {
                    updateCommerceData(true);
                }
            }
        });

        // On recupere l'intent precedent pour avoir les donnees qu'il transporte
        final Intent intentIn = getIntent();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.menucommerce, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.retourAccueil:
                Intent intentIn = getIntent();
                Intent intent = new Intent(RechercheDirecteCommercesActivity.this, ActivitePrincipale.class );
                intent.putExtra("PSEUDO",intentIn.getStringExtra("pseudoUser"));
                intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateCommerceData(final boolean avecRecherche) {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                final String ville = intent.getStringExtra("VILLE");
                EditText champRecherche = findViewById(R.id.textRecherche);
                final JSONArray json;

                json = RecuperationCommerceRecherche.getJSON(ville, champRecherche.getText().toString());

                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(RechercheDirecteCommercesActivity.this, R.string.pas_de_commerce, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            listeCommerces = (ListView) findViewById(R.id.listeViewRechercheCommerce);
                            commercesList = renderCommerce(json);
                            commerceAdapter= new CommerceAdapter(commercesList,RechercheDirecteCommercesActivity.this);
                            listeCommerces.setAdapter(commerceAdapter);
                            listeCommerces.setOnItemClickListener(new ListClickHandler());
                        }
                    });
                }

            }
        }.start();

    }

    public ArrayList<Commerce> renderCommerce(JSONArray json) {
        try {
            ArrayList<Commerce> commerces = new ArrayList<>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                commerces.add(new Commerce(jsonobject.getInt("id"), jsonobject.getString("nom"), jsonobject.getString("pseudoCommercant"), jsonobject.getString("localisation"),
                        jsonobject.getString("longitude"), jsonobject.getString("latitude")));
            }

            return commerces;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Intent intentIn = getIntent();
            Commerce resultat = (Commerce) adapter.getItemAtPosition(position);
            Intent intent = new Intent(RechercheDirecteCommercesActivity.this, CommerceActivity.class );
            intent.putExtra("idCommerce",resultat.getId());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
            intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
            intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
            startActivity(intent);
        }

    }
}


class RecuperationCommerceRecherche {


    public static JSONArray getJSON(String lieu,String recherche){
        try {
            URL url = new URL(MainActivity.chemin+"commerceRecherche/"+recherche+"/"+lieu);
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