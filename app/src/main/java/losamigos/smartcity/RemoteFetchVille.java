package losamigos.smartcity;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetchVille {

    public static JSONArray getJSON(double latitude, double longitude){
        try {
            Log.d("test3",longitude+" "+latitude);
            URL url = new URL(MainActivity.chemin+"villes/proximite/"+latitude+"/"+longitude);
            Log.d("fetchVille",url.toString());
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
    //permet de trouver la ville saisie
    public static JSONObject getJSONVille(String ville){
        try {
            URL url = new URL(MainActivity.chemin+"villes/"+ville);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());
            Log.v("json", json.toString());
            return data;
        }catch(Exception e){
            return null;
        }
    }
}
