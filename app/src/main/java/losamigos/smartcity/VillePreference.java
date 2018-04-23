package losamigos.smartcity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class VillePreference {

    SharedPreferences prefs;

    public VillePreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    //ville preferée
    String getCity(){
        return prefs.getString("city", "Montpellier, FR");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }

}