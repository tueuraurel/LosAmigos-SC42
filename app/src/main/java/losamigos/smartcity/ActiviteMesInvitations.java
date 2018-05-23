package losamigos.smartcity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiviteMesInvitations extends AppCompatActivity {


    ListView ListeDesReseaux;
    ArrayList<Reseau> reseauList;
    InvitationAdapter invitationAdapter;
    Handler handler;


    public ActiviteMesInvitations() {
        handler = new Handler();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestioninvitation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ListeDesReseaux = (ListView) findViewById(R.id.listeViewMesInvitations);
        //recuperer les données du serveur
        updateInvitationData();


        // On recupere l'intent precedent pour avoir les donnees qu'il transporte
        final Intent intentIn = getIntent();
        Log.d("pseudoListeRes",intentIn.getStringExtra("pseudoUser"));


    }


    public void updateInvitationData() {
        new Thread() {
            public void run() {
                final Intent intent = getIntent();
                final JSONArray json = RecuperationInvitation.getJSON(intent.getStringExtra("pseudoUser"));

                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ActiviteMesInvitations.this, R.string.aucuneInvitation, Toast.LENGTH_LONG).show();
                            ListeDesReseaux.setAdapter(null);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            ListeDesReseaux = (ListView) findViewById(R.id.listeViewMesInvitations);
                            reseauList = renderReseau(json);
                            Log.d("handlerBug",reseauList.toString());
                            invitationAdapter= new InvitationAdapter(reseauList,ActiviteMesInvitations.this,intent.getStringExtra("pseudoUser"));
                            ListeDesReseaux.setAdapter(invitationAdapter);
                           // ListeDesReseaux.setOnItemClickListener(new ListClickHandlerInvitation());
                        }
                    });
                }
            }
        }.start();

    }

    public ArrayList<Reseau> renderReseau(JSONArray json) {
        try {
            ArrayList<Reseau> reseaux = new ArrayList<Reseau>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                reseaux.add(new Reseau(jsonobject.getString("sujetReseau"), jsonobject.getString("description"), jsonobject.getString("pseudoAdmin"), jsonobject.getString("localisation"), jsonobject.getInt("visibilite")));
            }

            return reseaux;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

   /* public class ListClickHandlerInvitation implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            final Handler actualisation= new Handler();
            Log.d("clickEvent","vous avez cliqué");
            actualisation.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("clickEvent","vous avez cliqué");
                    updateInvitationData();
                }},2000);

            /*Intent intentIn = getIntent();
            Reseau resultat = (Reseau) adapter.getItemAtPosition(position);
            Intent intent = new Intent(RechercheReseauActivity.this, DemandeAdhesionActivity.class );
            intent.putExtra("sujetReseau",resultat.getSujet());
            intent.putExtra("descriptionReseau",resultat.getDescription());
            intent.putExtra("pseudoAdmin",resultat.getPseudoAdmin());
            intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
            intent.putExtra("lieuUser",intentIn.getStringExtra("lieuUser"));
            startActivity(intent);*/
       /* }

    }*/


    class InvitationAdapter extends ArrayAdapter<Reseau> {


        private List<Reseau> reseauList;
        private Context context;
        private String pseudo;

        public InvitationAdapter(List<Reseau> reseauList, Context context,String pseudo) {
            super(context, R.layout.single_reseau_invitation_item, reseauList);
            this.reseauList = reseauList;
            this.context = context;
            this.pseudo =pseudo;
        }

        private class ReseauHolder {
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
                    Toast.makeText(ActiviteMesInvitations.this, R.string.invitationAccepter, Toast.LENGTH_LONG).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateInvitationData();
                        }
                    },2000);


                }
            });


            holder.refuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SupprimerInvitationServeur().execute(parametres);
                    Toast.makeText(ActiviteMesInvitations.this, R.string.invitationRefuser, Toast.LENGTH_LONG).show();
                    updateInvitationData();
                }
            });

            return v;
        }


        class AccepterInvitationServeur extends AsyncTask<HashMap<String,String>, Void, Void> {

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



        class SupprimerInvitationServeur extends AsyncTask<HashMap<String,String>, Void, Void> {

            @Override
            //
            //
            // Classe sans aucun sens logique, on utilise un service get dont on recupere les
            // resultat pour faire passer une requete delete !!!!!!

            protected Void doInBackground(HashMap<String, String>[] hashMaps) {
                HashMap<String, String> hashMap = hashMaps[0];
                try {
                    Log.d("HashMapPseudo",hashMap.get("pseudo"));
                    Log.d("HashMapReseau",hashMap.get("sujetReseau"));
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
}





class RecuperationInvitation {

    // Recupere l'ensemble des message d un reseau.
    public static JSONArray getJSON(String pseudo){
        try {
            URL url = new URL(MainActivity.chemin+"invitation/"+pseudo);
            Log.v("getJSON URI",url.toString());
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


