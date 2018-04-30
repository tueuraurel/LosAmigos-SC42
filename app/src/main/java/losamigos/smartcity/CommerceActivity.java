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

public class CommerceActivity extends AppCompatActivity {

    TextView textViewNomCommerce;
    Button boutonFavoriCommerce;
    Handler handler;

    public CommerceActivity() {
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commerce);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCommerce();

        Intent intent = getIntent();
        int idCommerce = intent.getIntExtra("idTheme",0);
        Log.v("test",String.valueOf(idCommerce));
    }

    private void updateCommerce() {
        new Thread() {
            public void run() {
                Intent intent = getIntent();
                final int idCommerce = intent.getIntExtra("idCommerce",0);
                final String pseudoUser = intent.getStringExtra("pseudoUser");
                final JSONObject jsonCommerce = RecuperationInfosCommerce.getJSON(idCommerce, pseudoUser);

                if (jsonCommerce == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CommerceActivity.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                textViewNomCommerce = (TextView) findViewById(R.id.nomCommerce);
                                boutonFavoriCommerce = (Button) findViewById(R.id.favoriCommerce);

                                textViewNomCommerce.setText(jsonCommerce.getString("nom"));

                                if (jsonCommerce.getBoolean("favori")) {
                                    boutonFavoriCommerce.setText(R.string.supprimerFavori);
                                    boutonFavoriCommerce.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            // Code here executes on main thread after user presses button
                                        }
                                    });
                                } else {
                                    boutonFavoriCommerce.setText(R.string.ajoutFavori);
                                    boutonFavoriCommerce.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            // Code here executes on main thread after user presses button
                                        }
                                    });
                                }
                            } catch (Exception e){

                            }
                        }
                    });
                }
            }
        }.start();

    }
}

class RecuperationInfosCommerce {

    // Recupere l'ensemble des commerces correspondant au thème
    public static JSONObject getJSON(int idCommerce, String pseudoUser){

        try {
            URL url;
            Log.v("IDCommerce",String.valueOf(idCommerce));
            url = new URL(MainActivity.chemin+"commerce/id/"+idCommerce);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            String ligne=reader.readLine();
            reader.close();

            JSONObject jso=new JSONObject(ligne);
            //response=jso.getString("pseudo");

            Log.v("json", jso.toString());

            try {
                URL url2;
                url2 = new URL(MainActivity.chemin+"utilisateur/favoriCommerce/"+pseudoUser+"/"+idCommerce);

                Log.v("test",url2.toString());
                HttpURLConnection connection2 =
                        (HttpURLConnection)url2.openConnection();

                BufferedReader reader2 = new BufferedReader(
                        new InputStreamReader(connection2.getInputStream()));

                String ligne2=reader2.readLine();
                reader2.close();

                if (ligne2.equals("false")){
                    jso.put("favori", false);
                }else {
                    jso.put("favori", true);
                }

                Log.v("test",jso.toString());
                return jso;

            }catch(Exception e){
                return null;
            }

        }catch(Exception e){
            return null;
        }

    }
}
