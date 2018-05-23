package losamigos.smartcity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RechercheCommerceActivity extends AppCompatActivity {

    ListView liste;

    ArrayList<ThemeCommerce> themeList;
    ThemesCommerceAdapter themesCommerceAdapter;

    ArrayList<Commerce> commerceList;
    CommerceAdapter commerceAdapter;
    CommerceAdapterProximite commerceAdapterProximite;

    String typeRecherche = "alphabetique";
    String echecGPS = "";

    GPSTracker gps;
    double latitude;
    double longitude;

    private static final int REQUEST_CODE_ONE = 1;

    Handler handler;

    public RechercheCommerceActivity() {
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_commerce);
    }

    @Override
    protected void onResume() {
        super.onResume();

        liste = (ListView) findViewById(R.id.listeViewCommerce);

        TextView affichageTypeRecherche = findViewById(R.id.typeRecherche);
        affichageTypeRecherche.setText(R.string.rechercheAlphabetique);

        Button boutonProximite = (Button) findViewById(R.id.rechercheProximite);
        boutonProximite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                typeRecherche = "proximite";
                if (ContextCompat.checkSelfPermission(RechercheCommerceActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    ActivityCompat.requestPermissions(RechercheCommerceActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ONE);
                }
                //on recupere la position géographique
                gps = new GPSTracker(RechercheCommerceActivity.this);
                updateCommerceData();
            }
        });

        Button boutonAlphabetique = (Button) findViewById(R.id.rechercheAlphabetique);
        boutonAlphabetique.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                typeRecherche = "alphabetique";
                TextView affichageTypeRecherche = findViewById(R.id.typeRecherche);
                affichageTypeRecherche.setText(R.string.rechercheAlphabetique);
                updateCommerceData();

            }
        });


        //recuperer les données du serveur
        updateThemesCommerceData();

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
                Intent intent = new Intent(RechercheCommerceActivity.this, ActivitePrincipale.class );
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

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ONE: {
                if (typeRecherche.equals("proximite")) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        Toast.makeText(this, "Permission GPS refusée", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            }
        }
    }


    private void updateThemesCommerceData() {
        new Thread() {
            public void run() {
                Intent intent = getIntent();
                final int idTheme = intent.getIntExtra("idTheme",0);
                final JSONArray json = RecuperationThemesCommerce.getJSON(idTheme);

                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            updateCommerceData();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            themeList = renderThemesCommerce(json);
                            themesCommerceAdapter = new ThemesCommerceAdapter(themeList,RechercheCommerceActivity.this);
                            liste.setAdapter(themesCommerceAdapter);
                            liste.setOnItemClickListener(new ListClickHandler());
                        }
                    });
                }
            }
        }.start();

    }

    private void updateCommerceData() {
        new Thread() {
            public void run() {
                Intent intent = getIntent();
                final int idTheme = intent.getIntExtra("idTheme",0);
                final String ville = intent.getStringExtra("VILLE");
                final JSONArray jsonCommerce;

                if (typeRecherche.equals("proximite")) {

                    // si le GPS est disponible
                    if (gps.canGetLocation()) {
                        //Toast.makeText(getApplicationContext(), "Votre localisation est - \nLat: " + gps.getLatitude() + "\nLong: " + gps.getLongitude(), Toast.LENGTH_LONG).show();
                        longitude = gps.getLongitude();
                        latitude = gps.getLatitude();

                        Log.d("longitude", String.valueOf(longitude));
                        Log.d("latitude", String.valueOf(latitude));

                        if (longitude == 0 || latitude == 0) {
                            typeRecherche = "alphabetique";
                            echecGPS = "noDATA";

                        } else {
                            echecGPS = "";
                        }

                        jsonCommerce = RecuperationCommerce.getJSON(idTheme, ville, typeRecherche, longitude, latitude);

                    } else {
                        Toast.makeText(getApplicationContext(), "Erreur GPS, affichage par ordre alphabétique", Toast.LENGTH_LONG).show();
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();

                        typeRecherche = "alphabetique";
                        echecGPS = "erreurGPS";

                        jsonCommerce = RecuperationCommerce.getJSON(idTheme, ville, typeRecherche, longitude, latitude);
                    }
                } else {
                    echecGPS = "";
                    jsonCommerce = RecuperationCommerce.getJSON(idTheme, ville, typeRecherche, longitude, latitude);
                }

                if (jsonCommerce == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(RechercheCommerceActivity.this, R.string.pas_de_commerce, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {

                            LinearLayout typeRechercheLayout = findViewById(R.id.layoutTypeRecherche);
                            TextView typeRechercheText = findViewById(R.id.typeRecherche);
                            typeRechercheLayout.setVisibility(View.VISIBLE);
                            typeRechercheText.setVisibility(View.VISIBLE);

                            commerceList = renderCommerce(jsonCommerce);
                            if (echecGPS.equals("noDATA")) {
                                Toast.makeText(getApplicationContext(), "Aucunes données de localisation récuperées, affichage par ordre alphabétique", Toast.LENGTH_LONG).show();
                                TextView affichageTypeRecherche = findViewById(R.id.typeRecherche);
                                affichageTypeRecherche.setText(R.string.rechercheAlphabetique);
                            } else if (echecGPS.equals("erreurGPS")) {
                                Toast.makeText(getApplicationContext(), "Erreur GPS, affichage par ordre alphabétique", Toast.LENGTH_LONG).show();
                                TextView affichageTypeRecherche = findViewById(R.id.typeRecherche);
                                affichageTypeRecherche.setText(R.string.rechercheAlphabetique);
                            }

                            if (typeRecherche.equals("proximite")) {
                                TextView affichageTypeRecherche = findViewById(R.id.typeRecherche);
                                affichageTypeRecherche.setText(R.string.rechercheProximite);
                                commerceAdapterProximite = new CommerceAdapterProximite(commerceList, RechercheCommerceActivity.this);
                                liste.setAdapter(commerceAdapterProximite);
                                liste.setOnItemClickListener(new ListClickHandlerCommerce());
                            } else {
                                commerceAdapter = new CommerceAdapter(commerceList,RechercheCommerceActivity.this);
                                liste.setAdapter(commerceAdapter);
                                liste.setOnItemClickListener(new ListClickHandlerCommerce());
                            }
                        }
                    });
                }
            }
        }.start();

    }

    public ArrayList<ThemeCommerce> renderThemesCommerce(JSONArray json) {
        try {
            ArrayList<ThemeCommerce> themesCommerce = new ArrayList<>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                themesCommerce.add(new ThemeCommerce(jsonobject.getString("nom"), jsonobject.getInt("id"), jsonobject.getInt("idNomPere")));
            }

            return themesCommerce;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Commerce> renderCommerce(JSONArray json) {
        try {
            ArrayList<Commerce> commerce = new ArrayList<>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                if (typeRecherche.equals("proximite")) {
                    commerce.add(new Commerce(jsonobject.getInt("id"), jsonobject.getString("nom"), jsonobject.getString("pseudoCommercant"), jsonobject.getString("localisation"),
                            jsonobject.getString("longitude"), jsonobject.getString("latitude"), jsonobject.getDouble("dist")));
                } else {
                    commerce.add(new Commerce(jsonobject.getInt("id"), jsonobject.getString("nom"), jsonobject.getString("pseudoCommercant"), jsonobject.getString("localisation"),
                            jsonobject.getString("longitude"), jsonobject.getString("latitude")));
                }
            }
            Log.v("test",commerce.toString());
            return commerce;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Intent intentIn = getIntent();
            ThemeCommerce resultat = (ThemeCommerce) adapter.getItemAtPosition(position);
            Intent intent = new Intent(RechercheCommerceActivity.this, RechercheCommerceActivity.class );
            intent.putExtra("idTheme",resultat.getId());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
            intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
            intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
            startActivity(intent);
        }

    }

    public class ListClickHandlerCommerce implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Intent intentIn = getIntent();
            Commerce resultat = (Commerce) adapter.getItemAtPosition(position);
            Intent intent = new Intent(RechercheCommerceActivity.this, CommerceActivity.class );
            intent.putExtra("idCommerce",resultat.getId());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
            intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
            intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
            startActivity(intent);
        }

    }

}

class RecuperationThemesCommerce {

    // Recupere l'ensemble des themes de commerce.
    public static JSONArray getJSON(int idTheme){

        try {
            URL url;

            if (idTheme == 0) {
                String typeTheme = "Commerce";
                url = new URL(MainActivity.chemin+"themes/listeThemesEnfant/nom/"+typeTheme);
            } else {
                int typeTheme = idTheme;
                url = new URL(MainActivity.chemin+"themes/listeThemesEnfant/id/"+typeTheme);
            }

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

class ThemesCommerceAdapter extends ArrayAdapter<ThemeCommerce> {

    private List<ThemeCommerce> themeCommerceList;
    private Context context;

    public ThemesCommerceAdapter(List<ThemeCommerce> themeCommerceList, Context context) {
        super(context, R.layout.single_textview_item, themeCommerceList);
        this.themeCommerceList = themeCommerceList;
        this.context = context;
    }

    private static class ThemesHolder {
        public TextView nom;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        ThemesHolder holder = new ThemesHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_textview_item, null);

            holder.nom = (TextView) v.findViewById(R.id.textView);
            v.setTag(holder);
        } else {
            holder = (ThemesHolder) v.getTag();
        }

        ThemeCommerce p = themeCommerceList.get(position);
        holder.nom.setText(p.getNom());

        return v;
    }
}

class CommerceAdapterProximite extends ArrayAdapter<Commerce> {

    private List<Commerce> commerceList;
    private Context context;

    public CommerceAdapterProximite(List<Commerce> commerceList, Context context) {
        super(context, R.layout.single_textview_item, commerceList);
        this.commerceList = commerceList;
        this.context = context;
    }

    private static class CommerceHolder {
        public TextView nom;
        public TextView distance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        CommerceHolder holder = new CommerceHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.proximite_textview_item, null);

            holder.nom = (TextView) v.findViewById(R.id.textViewNom);
            holder.distance = (TextView) v.findViewById(R.id.textViewDistance);
            v.setTag(holder);
        } else {
            holder = (CommerceHolder) v.getTag();
        }

        Commerce p = commerceList.get(position);
        holder.nom.setText(p.getNom());

        double distance = p.getDistance();
        String textDistance = "";
        if (distance > 1) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            textDistance = df.format(distance);
            holder.distance.setText(textDistance + "km");
        } else {
            distance = distance * 1000;
            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            textDistance = df.format(distance);
            holder.distance.setText(textDistance + "m");
        }

        return v;
    }
}