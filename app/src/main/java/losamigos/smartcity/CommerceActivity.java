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

public class CommerceActivity extends AppCompatActivity {

    TextView textViewNomCommerce;
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
                final JSONObject jsonCommerce = RecuperationInfosCommerce.getJSON(idCommerce);

                if (jsonCommerce == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CommerceActivity.this, R.string.pas_de_commerce, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                textViewNomCommerce = (TextView) findViewById(R.id.nomCommerce);
                                textViewNomCommerce.setText(jsonCommerce.getString("nom"));
                            }catch (Exception e){

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
    public static JSONObject getJSON(int idCommerce){

        try {
            URL url;
            Log.v("IDCommerce",String.valueOf(idCommerce));
            url = new URL(MainActivity.chemin+"commerce/id/"+idCommerce);

            Log.v("test","URI");
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            String ligne=reader.readLine();
            reader.close();

            JSONObject jso=new JSONObject(ligne);
            //response=jso.getString("pseudo");

            Log.v("json", jso.toString());

            return jso;
        }catch(Exception e){
            return null;
        }
    }
}
