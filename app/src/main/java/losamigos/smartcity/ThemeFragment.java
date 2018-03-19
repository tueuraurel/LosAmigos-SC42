package losamigos.smartcity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThemeFragment extends Fragment {
    ListView listeTheme;
    Handler handler;

    public ThemeFragment() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_themes, container, false);
        listeTheme = (ListView) rootView.findViewById(R.id.listView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateThemeData();
    }

    private void updateThemeData() {
        new Thread() {
            public void run() {
                final JSONArray json = RemoteFetchTheme.getJSON();
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
                            List<Theme> themes = renderTheme(json);
                            ThemeAdapter adapter = new ThemeAdapter(getActivity(), themes);
                            listeTheme.setAdapter(adapter);
                        }
                    });
                }
            }
        }.start();

    }

    private List<Theme> renderTheme(JSONArray json) {
        try {
            List<Theme> themes = new ArrayList<Theme>();

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
}
