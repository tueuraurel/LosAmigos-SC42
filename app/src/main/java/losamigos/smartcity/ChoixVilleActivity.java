package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChoixVilleActivity extends Activity {
    ListView lv;
    ArrayList<Villes> villesList;
    VilleAdapter vAdapter;
    Handler handler;
    GPSTracker gps;
    double latitude;
    double longitude;
    String villeSaisie;
    Villes ville;

    private static final int REQUEST_CODE_ONE = 1;

    public ChoixVilleActivity() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ville);
        if(ContextCompat.checkSelfPermission(ChoixVilleActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        }
        else {
            ActivityCompat.requestPermissions(ChoixVilleActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ONE);
        }
        //on recupere la position géographique
        gps = new GPSTracker(ChoixVilleActivity.this);
        // si le GPS est disponible
        if(gps.canGetLocation()){
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            Button boutonChoix2 = (Button) findViewById(R.id.buttonSaisie);
            boutonChoix2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //ville saisie recuperation nom
                    EditText editVille = findViewById(R.id.villeEdit);
                    villeSaisie = editVille.getText().toString();
                    ResearchVilleData();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Erreur GPS", Toast.LENGTH_LONG).show();
            //on a pas pu localiser, on lance une alarme sur les parametres du telephone
            gps.showSettingsAlert();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        lv = (ListView) findViewById(R.id.listview);
        //recuperer les données du serveur
        updateVilleData();

    }

    @Override
    protected void onStop() {
        if (gps != null) {
            gps.stopUsingGPS();
        }
        super.onStop();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ONE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission GPS Acceptée", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission GPS refusée", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    private void ResearchVilleData() {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetchVille.getJSONVille(villeSaisie);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            ville = null;
                            Toast.makeText(ChoixVilleActivity.this, R.string.ville_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                ville = new Villes(json.getString("nom"), json.getString("latitude"), json.getString(("longitude")));
                                HashMap<String, String> parametres = new HashMap<String, String>();
                                //recuperation du pseudo
                                final Intent intent = getIntent();
                                String pseudo = intent.getStringExtra("PSEUDO");
                                //pour la suite, on conserve le pseudo
                                Intent intent2 = new Intent(ChoixVilleActivity.this, ActivitePrincipale.class);
                                intent2.putExtra("PSEUDO", pseudo);
                                intent2.putExtra("LATITUDE", ville.latitude);
                                intent2.putExtra("LONGITUDE", ville.longitude);
                                intent2.putExtra("VILLE", ville.nom);
                                parametres.put("pseudo", pseudo);
                                parametres.put("nomVille", ville.nom);
                                //creation de l'utilisateur dans la base du serveur
                                new RetrieveVilleTask().execute(parametres);
                                //lancement de la prochaine activité -> choix des themes
                                startActivity(intent2);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }.start();

    }

    private void updateVilleData() {
        new Thread() {
            public void run() {
                final JSONArray json = RemoteFetchVille.getJSON(latitude, longitude);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ChoixVilleActivity.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            villesList = renderVille(json);
                            vAdapter= new VilleAdapter(villesList,ChoixVilleActivity.this);
                            lv.setAdapter(vAdapter);
                            lv.setOnItemClickListener(new ListClickHandler());
                        }
                    });
                }
            }
        }.start();

    }

    public ArrayList<Villes> renderVille(JSONArray json) {
        try {
            ArrayList<Villes> villes = new ArrayList<Villes>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                villes.add(new Villes(jsonobject.getString("nom"), jsonobject.getString("latitude"), jsonobject.getString(("longitude"))));
            }

            return villes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Villes ville = (Villes) adapter.getItemAtPosition(position);
            HashMap<String, String> parametres = new HashMap<String, String>();
            //recuperation du pseudo
            final Intent intent = getIntent();
            String pseudo = intent.getStringExtra("PSEUDO");
            //pour la suite, on conserve le pseudo
            Intent intent2 = new Intent(ChoixVilleActivity.this, ActivitePrincipale.class);
            intent2.putExtra("PSEUDO", pseudo);
            intent2.putExtra("LATITUDE", ville.latitude);
            intent2.putExtra("LONGITUDE", ville.longitude);
            intent2.putExtra("VILLE", ville.nom);
            parametres.put("pseudo", pseudo);
            parametres.put("nomVille",ville.nom);
            //creation de l'utilisateur dans la base du serveur
            new RetrieveVilleTask().execute(parametres);
            //lancement de la prochaine activité -> Activité principale
            startActivity(intent2);
        }

    }


}


class RetrieveVilleTask extends AsyncTask<HashMap<String,String>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"utilisateur/choisiVille");
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", hashMap.get("pseudo"));
            jsonObject.accumulate("nomVille", hashMap.get("nomVille"));
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