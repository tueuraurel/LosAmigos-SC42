package losamigos.smartcity;

import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecuperationCommerce {

    // Recupere l'ensemble des commerces correspondant au thème
    public static JSONArray getJSON(int idTheme, String ville, String typeRecherche,
                                    double longitude, double latitude){

        try {
            URL url;
            Log.v("IDTheme",String.valueOf(idTheme));

            if (typeRecherche.equals("proximite")) {
                url = new URL(MainActivity.chemin+"commerce/proximite/"+idTheme+"/"+ville+"/"+latitude+"/"+longitude);
            } else {
                url = new URL(MainActivity.chemin+"commerce/theme/"+idTheme+"/"+ville);
            }

            Log.v("test","URI");
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