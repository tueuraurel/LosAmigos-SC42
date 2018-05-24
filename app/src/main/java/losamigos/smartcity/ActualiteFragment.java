package losamigos.smartcity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ActualiteFragment extends Fragment  {
    ListView listeArticles;
    Handler handler;

    public ActualiteFragment() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actualite, container, false);
        listeArticles = (ListView) rootView.findViewById(R.id.listView);
        Intent intent = getActivity().getIntent();
        //filtre de lieu des actus
        final Spinner spinnerRegion = (Spinner) rootView.findViewById(R.id.spinnerAuteur);
        String[] lRegion={"FRANCE",intent.getStringExtra("VILLE")};
        ArrayAdapter<String> dataAdapterR = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,lRegion);
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(dataAdapterR);

        //filtre de journal
        final Spinner spinnerAuteur = (Spinner) rootView.findViewById(R.id.spinnerJournal);
        Journaux[] journaux={new Journaux("Le Monde", "lemonde.fr"),new Journaux("L'Obs", "nouvelobs.com"), new Journaux("L'Express", "lexpress.fr"), new Journaux("20 minutes", "20minutes.fr"), new Journaux("BFM", "bfmtv.com"),new Journaux("Libération", "liberation.fr"),new Journaux("Le Point", "lepoint.fr")};
        ArrayAdapter<Journaux> dataAdapterJournaux = new ArrayAdapter<Journaux>(getActivity(), android.R.layout.simple_spinner_item,journaux);
        dataAdapterJournaux.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAuteur.setAdapter(dataAdapterJournaux);

        //-- gestion du Click sur la liste Région
        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String myRegion = String.valueOf(spinnerRegion.getSelectedItem());
                updateActualiteData(myRegion, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //rien
            }
        });

        //-- gestion du Click sur la liste des journaux
        spinnerAuteur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Journaux auteur = (Journaux) spinnerAuteur.getSelectedItem();
                updateActualiteData(null,auteur.getDomaine()); //recuperation du nom de domaine du journal
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //rien
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler attendre = new Handler();
        attendre.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateActualiteData(null, null);
            }
        },2000);

    }

    private void updateActualiteData(final String ville, final String journaux) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetchArticle.getJSON(ville, journaux);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.data_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            List<Article> articles = renderActualite(json);
                            ArticleAdapter adapter = new ArticleAdapter(getActivity(), articles);
                            listeArticles.setAdapter(adapter);
                            listeArticles.setOnItemClickListener(new ListClickHandler());
                        }
                    });
                }
            }
        }.start();

    }

    private List<Article> renderActualite(JSONObject json) {
        try {
            List<Article> articles = new ArrayList<Article>();

            JSONArray jsonArray = json.getJSONArray("articles");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                JSONObject source = jsonobject.getJSONObject("source");
                String auteur = source.getString("name");
                articles.add(new Article(jsonobject.getString("title"), auteur, jsonobject.getString("description"), jsonobject.getString("url")));
            }

            return articles;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Article resultat = (Article) adapter.getItemAtPosition(position);
            Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(resultat.getURL()));
            startActivity(i);
        }

    }

    public class Journaux{
        public String nom;
        public String domaine;

        public Journaux(String nom, String domaine){
            this.nom = nom; this.domaine = domaine;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getDomaine() {
            return domaine;
        }

        public void setDomaine(String domaine) {
            this.domaine = domaine;
        }

        public String toString()
        {
            return nom;
        }
    }
}
