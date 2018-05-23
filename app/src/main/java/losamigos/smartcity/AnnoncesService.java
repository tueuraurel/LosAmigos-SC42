package losamigos.smartcity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnnoncesService extends Service {

    Handler handler;
    final Handler boucleAnnonces= new Handler();

    public AnnoncesService() {
        handler = new Handler();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

        boucleAnnonces.postDelayed(monRunnable,5000);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Runnable monRunnable =new Runnable() {
        @Override
        public void run() {
            verifAnnonces();
            boucleAnnonces.postDelayed(this,5000);
        }
    };

    private void verifAnnonces() {
        new Thread() {
            public void run() {

                final JSONArray json = RemoteFetchIDAnnonces.getJSON();

                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(AnnoncesService.this, "NULL", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(AnnoncesService.this, "SUCCES", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }.start();

    }

}

class RemoteFetchIDAnnonces {

    public static JSONArray getJSON(){
        try {
            URL url = new URL(MainActivity.chemin+"themesprincipaux");
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
