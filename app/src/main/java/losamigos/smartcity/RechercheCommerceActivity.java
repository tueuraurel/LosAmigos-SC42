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

public class RechercheCommerceActivity extends AppCompatActivity {

    ListView liste;

    ArrayList<ThemeCommerce> themeList;
    ThemesCommerceAdapter themesCommerceAdapter;

    ArrayList<Commerce> commerceList;
    CommerceAdapter commerceAdapter;

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
        //recuperer les données du serveur
        updateThemesCommerceData();
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
                final JSONArray jsonCommerce = RecuperationCommerce.getJSON(idTheme, ville);

                if (jsonCommerce == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(RechercheCommerceActivity.this, R.string.pas_de_commerce, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            commerceList = renderCommerce(jsonCommerce);
                            commerceAdapter = new CommerceAdapter(commerceList,RechercheCommerceActivity.this);
                            liste.setAdapter(commerceAdapter);
                            liste.setOnItemClickListener(new ListClickHandlerCommerce());
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
                commerce.add(new Commerce(jsonobject.getInt("id"), jsonobject.getString("nom"), jsonobject.getString("pseudoCommercant"), jsonobject.getString("localisation")));
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
        } else {
            holder = (ThemesHolder) v.getTag();
        }

        ThemeCommerce p = themeCommerceList.get(position);
        holder.nom.setText(p.getNom());

        return v;
    }
}

class RecuperationCommerce {

    // Recupere l'ensemble des commerces correspondant au thème
    public static JSONArray getJSON(int idTheme, String ville){

        try {
            URL url;
            Log.v("IDTheme",String.valueOf(idTheme));
            url = new URL(MainActivity.chemin+"commerce/theme/"+idTheme+"/"+ville);

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

class CommerceAdapter extends ArrayAdapter<Commerce> {

    private List<Commerce> commerceList;
    private Context context;

    public CommerceAdapter(List<Commerce> commerceList, Context context) {
        super(context, R.layout.single_textview_item, commerceList);
        this.commerceList = commerceList;
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
        } else {
            holder = (CommerceHolder) v.getTag();
        }

        Commerce p = commerceList.get(position);
        holder.nom.setText(p.getNom());
        Log.v("test","COUCOU JE SUIS DANS COMMERCEADAPTER ET LA VIE EST BELLE PAR ICI");

        return v;
    }
}