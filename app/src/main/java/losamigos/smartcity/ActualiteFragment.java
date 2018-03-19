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
import android.widget.ListView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actualite, container, false);
        listeArticles = (ListView) rootView.findViewById(R.id.listView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateActualiteData();
    }

    private void updateActualiteData() {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetchArticle.getJSON();
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
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
}