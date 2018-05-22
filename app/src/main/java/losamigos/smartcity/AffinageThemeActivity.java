package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class AffinageThemeActivity extends Activity implements android.widget.CompoundButton.OnCheckedChangeListener{

    ListView lv;
    ArrayList<Theme> themeList;
    ThemeAdapter2 thAdapter;
    Handler handler;
    String pseudo = "";

    public AffinageThemeActivity(){
        handler = new Handler();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affinage_theme);

        final Intent intent2 = getIntent();
        pseudo = intent2.getStringExtra("PSEUDO");

        lv = (ListView) findViewById(R.id.listViewAffinage);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAffinageThemeData(pseudo);

        Button boutonChoix = (Button) findViewById(R.id.buttonThemeAffinage);
        boutonChoix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = getIntent();
                Intent intent  = new Intent(AffinageThemeActivity.this, ActivitePrincipale.class);
                intent.putExtra("LATITUDE", intent2.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intent2.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                intent.putExtra("pseudoUser",pseudo);
                intent.putExtra("PSEUDO",pseudo);
                for (Theme theme : themeList) {
                    if(theme.selected) {
                        Log.d("themechoisi", theme.getNom());
                        HashMap<String, String> parametres = new HashMap<String, String>();
                        parametres.put("pseudo", pseudo);
                        parametres.put("idTheme", String.valueOf(theme.getId()));
                        //creation de l'utilisateur dans la base du serveur
                        new RetrieveAffinageThemeTask().execute(parametres);
                    }
                }
                startActivity(intent);
            }
        });
    }

    private void updateAffinageThemeData(final String pseudo) {
        new Thread() {
            public void run() {
                final JSONArray json = RemoteFetchAffinageTheme.getJSON(pseudo);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(AffinageThemeActivity.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            themeList = renderAffinageTheme(json);
                            thAdapter= new ThemeAdapter2(themeList,AffinageThemeActivity.this);
                            lv.setAdapter(thAdapter);
                        }
                    });
                }
            }
        }.start();
    }

    private ArrayList<Theme> renderAffinageTheme(JSONArray json) {
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

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int pos = lv.getPositionForView(buttonView);
        if (pos != ListView.INVALID_POSITION) {
            Theme t = themeList.get(pos);
            t.setSelected(isChecked);
            //Toast.makeText(this, "Clicked on Theme: " + t.getNom() + ". State: is " + isChecked, Toast.LENGTH_SHORT).show();
        }
    }

}

class RetrieveAffinageThemeTask extends AsyncTask<HashMap<String,String>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"utilisateur/apprecie");

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", hashMap.get("pseudo"));
            jsonObject.accumulate("idTheme", Integer.parseInt(hashMap.get("idTheme")));

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
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