package losamigos.smartcity;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetchArticle {



    public static JSONObject getJSON() {
        try {

            URL url = new URL("https://newsapi.org/v2/top-headlines?country=fr&apiKey=f8a6fdbf9cce430ca6da3c56e89e9518");
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