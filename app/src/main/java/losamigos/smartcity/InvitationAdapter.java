package losamigos.smartcity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class InvitationAdapter extends ArrayAdapter<Reseau> {


    private List<Reseau> reseauList;
    private Context context;
    private String pseudo;

    public InvitationAdapter(List<Reseau> reseauList, Context context,String pseudo) {
        super(context, R.layout.single_reseau_invitation_item, reseauList);
        this.reseauList = reseauList;
        this.context = context;
        this.pseudo =pseudo;
    }

    private static class ReseauHolder {
        public TextView sujet;
        public TextView description;
        public Button accepter;
        public Button refuser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("getView",String.valueOf(position));
        View v = convertView;

        InvitationAdapter.ReseauHolder holder = new InvitationAdapter.ReseauHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_reseau_invitation_item, null);

            holder.sujet = (TextView) v.findViewById(R.id.sujet);
            holder.description = (TextView) v.findViewById(R.id.description);
            holder.accepter = (Button) v.findViewById(R.id.accepter);
            holder.refuser = (Button) v.findViewById(R.id.refuser);
            v.setTag(holder);
        } else {
            holder = (InvitationAdapter.ReseauHolder) v.getTag();
        }


        final Reseau p = reseauList.get(position);
        Log.d("ReseauAdapterList",p.toString());
        Log.d("ReseauAdapterSujet",p.getSujet());
        Log.d("ReseauAdapterDesc",p.getDescription());
        holder.sujet.setText(p.getSujet());
        holder.description.setText(p.getDescription());
        final HashMap<String, String> parametres = new HashMap<String, String>();
        parametres.put("pseudo", pseudo);
        parametres.put("sujetReseau",p.getSujet());
        holder.accepter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //prise en compte de l'adhesion dans la base du serveur
                new AccepterInvitationServeur().execute(parametres);
                new SupprimerInvitationServeur().execute(parametres);
                Intent intent = new Intent(context,ActiviteMesInvitations.class);
                intent.putExtra("pseudoUser",pseudo);
                //context.startActivity(intent);

                //Activity.finish();



            }
        });



        holder.refuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SupprimerInvitationServeur().execute(parametres);
            }
        });

        return v;
    }


    static class AccepterInvitationServeur extends AsyncTask<HashMap<String,String>, Void, Void> {

        @Override
        protected Void doInBackground(HashMap<String, String>[] hashMaps) {
            HashMap<String, String> hashMap = hashMaps[0];
            InputStream inputStream = null;
            String result = "";
            try {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(MainActivity.chemin+"adhere/adhesion");

                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("pseudo", hashMap.get("pseudo"));
                jsonObject.accumulate("sujetReseau", hashMap.get("sujetReseau"));


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
    }



    static class SupprimerInvitationServeur extends AsyncTask<HashMap<String,String>, Void, Void> {

        @Override
        //
        //
        // Classe sans aucun sens logique, on utilise un service get dont on recupere les
        // resultat pour faire passer une requete delete !!!!!!

        protected Void doInBackground(HashMap<String, String>[] hashMaps) {
            HashMap<String, String> hashMap = hashMaps[0];
            try {
            URL url = new URL(MainActivity.chemin+"SuppressionInvitation/"+hashMap.get("pseudo")+"/"+hashMap.get("sujetReseau"));
            Log.d("test",url.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");
                reader.close();

                return null;

            } catch (MalformedURLException e1) {
                Log.d("refus",e1.getMessage());
                e1.printStackTrace();

            } catch (ClientProtocolException e) {
                Log.d("refus",e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("refus",e.getMessage());
                e.printStackTrace();
            }

            return null;
        }
    }
}

