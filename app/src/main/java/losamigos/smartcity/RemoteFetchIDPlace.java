package losamigos.smartcity;


import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetchIDPlace {

    public static JSONObject getJSON(String latitude, String longitude) {
        try {
            Log.d("tset","test:"+latitude+" "+longitude);
            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latitude+","+longitude+"&radius=1000&key=AIzaSyBXLfKfWodcGvq-HM0wsoHsOviym0RV33k");
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            InputStream responseBody = connection.getInputStream();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(responseBody, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder(); String inputStr;
            while ((inputStr = streamReader.readLine()) != null) responseStrBuilder.append(inputStr);

            JSONObject data = new JSONObject(responseStrBuilder.toString());

            return data;

        } catch (Exception e) {
            return null;
        }


    }

}