package losamigos.smartcity;

import android.os.AsyncTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class NouveauMessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouveau_message_layout);
        final EditText nouveauMessage = findViewById(R.id.EditTextNouveauMessage);
        Button valider = findViewById(R.id.BoutonValider);

        final Intent intent = getIntent();

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> parametres = new HashMap<String, String>();
                parametres.put("contenu", nouveauMessage.getText().toString());
                parametres.put("pseudoAuteur", intent.getStringExtra("pseudoUser"));
                parametres.put("sujetReseau",intent.getStringExtra("sujetReseau"));
                Log.d("insertionBase",nouveauMessage.getText().toString());
                Log.d("insertionBase",intent.getStringExtra("pseudoUser"));
                Log.d("insertionBase",intent.getStringExtra("sujetReseau"));

                new exportMessageServeur().execute(parametres);

                MessageBDD maBaseMessage = new MessageBDD(NouveauMessageActivity.this);
                maBaseMessage.open();
                /*maBaseMessage.insertMessage(new Message(nouveauMessage.getText()),
                        intent.getStringExtra(pseudoUser),intent.getStringExtra(sujetReseau));*/
                //Log.d("NouveauMessageActivity",String.valueOf(nouveauMessage.getText()));
                //Log.d("NouveauMessageActivity",intent.getStringExtra("sujetReseau"));
                maBaseMessage.insertMessage(new Message(nouveauMessage.getText().toString(),intent.getStringExtra("sujetReseau"),"Aurelien"));
                maBaseMessage.close();
                finish();
                /*Intent intent2 = new Intent(NouveauMessageActivity.this,ListeMessageReseau.class);
                intent2.putExtra("sujetReseau",intent.getStringExtra("sujetReseau"));
                startActivity(intent2);*/
            }
        });

    }
}
/*
class exportMessageServeur extends AsyncTask<java.util.HashMap<String,String>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"reseau/message");

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("contenu", hashMap.get("contenu"));
            jsonObject.accumulate("pseudoAuteur", hashMap.get("pseudoAuteur"));
            jsonObject.accumulate("sujetReseau", hashMap.get("sujetReseau"));
            //Log.d("insertionBase",jsonObject.toString());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

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
} */