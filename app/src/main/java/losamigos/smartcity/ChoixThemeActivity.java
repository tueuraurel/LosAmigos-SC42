package losamigos.smartcity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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

public class ChoixThemeActivity extends Activity implements android.widget.CompoundButton.OnCheckedChangeListener {

    ListView lv;
    ArrayList<Theme> themeList;
    ThemeAdapter thAdapter;
    Handler handler;

    public ChoixThemeActivity() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lv = (ListView) findViewById(R.id.listview);
        //recuperer les données du serveur
        updateThemeData();
        Button boutonChoix = (Button) findViewById(R.id.choixButton);
        boutonChoix.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (Theme theme : themeList) {
                    if(theme.selected) {
                        HashMap<String, String> parametres = new HashMap<String, String>();
                        //recuperation du pseudo
                        final Intent intent = getIntent();
                        String pseudo = intent.getStringExtra("PSEUDO");
                        //pour la suite, on conserve le pseudo
                        Intent intent2  = new Intent(ChoixThemeActivity.this, ChoixVilleActivity.class);
                        intent2.putExtra("PSEUDO", pseudo);
                        parametres.put("pseudo", pseudo);
                        parametres.put("idTheme", String.valueOf(theme.getId()));
                        //creation de l'utilisateur dans la base du serveur
                        new RetrieveThemeTask().execute(parametres);
                        //lancement de la prochaine activité -> choix de la ville
                        startActivity(intent2);
                    }
                }
            }
        });
    }

    private void updateThemeData() {
        new Thread() {
            public void run() {
                final JSONArray json = RemoteFetchTheme.getJSON();
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ChoixThemeActivity.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            themeList = renderTheme(json);
                            thAdapter= new ThemeAdapter(themeList,ChoixThemeActivity.this);
                            lv.setAdapter(thAdapter);
                        }
                    });
                }
            }
        }.start();

    }

    public ArrayList<Theme> renderTheme(JSONArray json) {
        try {
            ArrayList<Theme> themes = new ArrayList<Theme>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                themes.add(new Theme(jsonobject.getString("nom"), jsonobject.getInt("id")));
            }
            return themes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int pos = lv.getPositionForView(buttonView);
        if (pos != ListView.INVALID_POSITION) {
            Theme t = themeList.get(pos);
            t.setSelected(isChecked);
        }
    }
}


class RetrieveThemeTask extends AsyncTask<HashMap<String,String>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"utilisateur/apprecie");

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", hashMap.get("pseudo"));
            jsonObject.accumulate("idTheme", Integer.parseInt(hashMap.get("idTheme")));

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