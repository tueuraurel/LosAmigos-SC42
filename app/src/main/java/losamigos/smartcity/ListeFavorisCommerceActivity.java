package losamigos.smartcity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class ListeFavorisCommerceActivity extends AppCompatActivity {

    ListView liste;

    ArrayList<Commerce> favorisList;
    FavorisCommerceAdapter favorisCommerceAdapter;

    Handler handler;

    public ListeFavorisCommerceActivity() {
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_favoris_commerce);
    }

    @Override
    protected void onResume() {
        super.onResume();

        liste = (ListView) findViewById(R.id.listeViewFavorisCommerce);

        //recuperer les données du serveur
        updateFavorisCommerceData();
    }

    private void updateFavorisCommerceData() {
        new Thread() {
            public void run() {
                Intent intent = getIntent();
                final String pseudoUser = intent.getStringExtra("pseudoUser");
                final JSONArray json = RecuperationFavorisCommerce.getJSON(pseudoUser);

                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {

                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            favorisList = renderFavorisCommerce(json);
                            favorisCommerceAdapter = new FavorisCommerceAdapter(favorisList,ListeFavorisCommerceActivity.this);
                            liste.setAdapter(favorisCommerceAdapter);
                            liste.setOnItemClickListener(new ListClickHandlerCommerce());
                        }
                    });
                }
            }
        }.start();

    }

    public ArrayList<Commerce> renderFavorisCommerce(JSONArray json) {
        try {
            ArrayList<Commerce> commerce = new ArrayList<>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                commerce.add(new Commerce(jsonobject.getInt("id"), jsonobject.getString("nom"), jsonobject.getString("pseudoCommercant"), jsonobject.getString("localisation"),
                        jsonobject.getString("longitude"), jsonobject.getString("latitude")));
            }
            Log.v("test",commerce.toString());
            return commerce;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ListClickHandlerCommerce implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Intent intentIn = getIntent();
            Commerce resultat = (Commerce) adapter.getItemAtPosition(position);
            Intent intent = new Intent(ListeFavorisCommerceActivity.this, CommerceActivity.class );
            intent.putExtra("idCommerce",resultat.getId());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
            intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
            intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
            startActivity(intent);
        }

    }
}

class RecuperationFavorisCommerce {

    // Recupere l'ensemble des commerces favoris
    public static JSONArray getJSON(String user){

        try {
            URL url;
            url = new URL(MainActivity.chemin+"utilisateur2/favorisCommerce/"+user);

            Log.v("test",url.toString());
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

class FavorisCommerceAdapter extends ArrayAdapter<Commerce> {

    private List<Commerce> favorisList;
    private Context context;

    public FavorisCommerceAdapter(List<Commerce> favorisList, Context context) {
        super(context, R.layout.single_textview_item, favorisList);
        this.favorisList = favorisList;
        this.context = context;
    }

    private static class CommerceHolder {
        public TextView nom;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        CommerceHolder holder = new CommerceHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_textview_item, null);

            holder.nom = (TextView) v.findViewById(R.id.textView);
            v.setTag(holder);
        } else {
            holder = (CommerceHolder) v.getTag();
        }

        Commerce p = favorisList.get(position);
        holder.nom.setText(p.getNom());

        return v;
    }
}
