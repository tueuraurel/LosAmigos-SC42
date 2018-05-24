package losamigos.smartcity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
                            invitationAdapter= new InvitationAdapter(reseauList,ActiviteMesInvitations.this,intent.getStringExtra("pseudoUser"));
                            ListeDesReseaux.setAdapter(invitationAdapter);
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

                    HttpClient httpclient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(MainActivity.chemin+"adhere/adhesion");

                    String json = "";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("pseudo", hashMap.get("pseudo"));
                    jsonObject.accumulate("sujetReseau", hashMap.get("sujetReseau"));

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



        class SupprimerInvitationServeur extends AsyncTask<HashMap<String,String>, Void, Void> {

            @Override
            protected Void doInBackground(HashMap<String, String>[] hashMaps) {
                HashMap<String, String> hashMap = hashMaps[0];
                try {
                    URL url = new URL(MainActivity.chemin+"SuppressionInvitation/"+hashMap.get("pseudo")+"/"+hashMap.get("sujetReseau"));
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
                    e1.printStackTrace();

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
            return data;
        }catch(Exception e){
            return null;
        }
    }
}


