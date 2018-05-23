package losamigos.smartcity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OffreCommerceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ListView liste;

    ArrayList<Annonce> annonceList;
    AnnonceAdapter annonceAdapter;
    String choixTri = "toutes";

    Handler handler;

    public OffreCommerceActivity() {
        handler = new Handler();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offre_commerce);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Spinner spinner = (Spinner) findViewById(R.id.selectFiltreAnnonce);
        TextView nomCommerce = (TextView) findViewById(R.id.nomCommerceAnnonces);

        Intent intent = getIntent();
        int idCommerce = intent.getIntExtra("idCommerce", 0);
        if (idCommerce == 0) {
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.choixFiltreAnnonce, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        } else {
            spinner.setVisibility(View.GONE);
            nomCommerce.setVisibility(View.VISIBLE);
            String annoncesNomCommerce = "Liste des annonces du commerce : "+intent.getStringExtra("nomCommerce");
            nomCommerce.setText(annoncesNomCommerce);
        }

        liste = (ListView) findViewById(R.id.listeViewAnnonces);

        updateAnnoncesData();


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
                Intent intent = new Intent(OffreCommerceActivity.this, ActivitePrincipale.class );
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

    private void updateAnnoncesData() {
        new Thread() {
            public void run() {
                Intent intent = getIntent();
                final String pseudoUser = intent.getStringExtra("pseudoUser");
                final String ville = intent.getStringExtra("VILLE");
                final JSONArray jsonAnnonces;
                final int idCommerce = intent.getIntExtra("idCommerce", 0);

                jsonAnnonces = RecuperationAnnoncesCommerce.getJSON(pseudoUser, ville, idCommerce, choixTri);

                if (jsonAnnonces == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(OffreCommerceActivity.this, R.string.noAnnonces, Toast.LENGTH_LONG).show();
                            liste.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            annonceList = renderAnnonce(jsonAnnonces);
                            annonceAdapter = new AnnonceAdapter(annonceList, OffreCommerceActivity.this);
                            liste.setAdapter(annonceAdapter);
                            liste.setOnItemClickListener(new ListClickHandlerAnnonce());
                            liste.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }.start();

    }

    public ArrayList<Annonce> renderAnnonce(JSONArray json) {
        try {
            ArrayList<Annonce> annonce = new ArrayList<>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                annonce.add(new Annonce(jsonobject.getInt("id"), jsonobject.getInt("idCommerce"),
                        jsonobject.getInt("idTheme"), jsonobject.getString("titre"),
                        jsonobject.getString("contenu"), jsonobject.getString("nomCommerce")));

            }
            Log.v("test", annonce.toString());
            return annonce;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ListClickHandlerAnnonce implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Intent intentIn = getIntent();
            Annonce resultat = (Annonce) adapter.getItemAtPosition(position);
            Intent intent = new Intent(OffreCommerceActivity.this, AnnonceActivity.class );
            intent.putExtra("idAnnonce",resultat.getId());
            intent.putExtra("idCommerce", resultat.getIdCommerce());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
            intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
            intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
            startActivity(intent);
        }

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String selected = parent.getItemAtPosition(pos).toString();
        if (selected.equals("Annonces selon préférences")) {
            choixTri = "preferences";
        } else if (selected.equals("Annonces des commerces favoris")) {
            choixTri = "favoris";
        } else if (selected.equals("Toutes les annonces")) {
            choixTri = "toutes";
        }
        updateAnnoncesData();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}

class RecuperationAnnoncesCommerce {

    public static JSONArray getJSON(String pseudoUser, String ville, int idCommerce, String choixTri){

        try {
            URL url = null;

            if (idCommerce == 0) {
                if (choixTri.equals("preferences")) {
                    url = new URL(MainActivity.chemin+"annonces/filtre/"+pseudoUser+"/"+ville);
                } else if (choixTri.equals("favoris")) {
                    url = new URL(MainActivity.chemin+"annonces/favoris/"+pseudoUser);
                } else if (choixTri.equals("toutes")) {
                    url = new URL(MainActivity.chemin+"annonces/toutes/"+pseudoUser);
                }

            } else {
                url = new URL(MainActivity.chemin+"annonces/idCommerce/"+idCommerce+"/"+pseudoUser);
            }

            Log.d("url", url.toString());

            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            Log.d("json", json.toString());

            JSONArray data = new JSONArray(json.toString());
            return data;
        }catch(Exception e){
            Log.d("erreur", e.getMessage());
            return null;
        }
    }
}

class AnnonceAdapter extends ArrayAdapter<Annonce> {

    private List<Annonce> annonceList;
    private Context context;

    public AnnonceAdapter(List<Annonce> annonceList, Context context) {
        super(context, R.layout.single_textview_item, annonceList);
        this.annonceList = annonceList;
        this.context = context;
    }

    private static class AnnonceHolder {
        public TextView nom;
        public TextView nomCommerce;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        AnnonceHolder holder = new AnnonceHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.annonce_item, null);

            holder.nom = (TextView) v.findViewById(R.id.textView);
            holder.nomCommerce = (TextView) v.findViewById(R.id.nomCommerce);
            v.setTag(holder);
        } else {
            holder = (AnnonceHolder) v.getTag();
        }

        Annonce p = annonceList.get(position);
        holder.nom.setText(p.getTitre());
        holder.nomCommerce.setText(p.getNomCommerce());

        return v;
    }
}