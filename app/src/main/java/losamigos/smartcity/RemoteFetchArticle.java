package losamigos.smartcity;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetchArticle {



    public static JSONObject getJSON(String ville, String journaux) {
        try {
            URL url;
            if(journaux == null) {
                if (ville == null || ville.equals("FRANCE")) {
                    url = new URL("https://newsapi.org/v2/top-headlines?country=fr&sortBy=popularity&apiKey=75ed10851fb34f4bb26946a2d0a66b7f");
                } else {
                    url = new URL("https://newsapi.org/v2/everything?q=" + ville + "&sortBy=popularity&apiKey=75ed10851fb34f4bb26946a2d0a66b7f");
                }
            }
            else{
                url = new URL("https://newsapi.org/v2/everything?domains="+journaux+"&sortBy=popularity&apiKey=75ed10851fb34f4bb26946a2d0a66b7f");
            }
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