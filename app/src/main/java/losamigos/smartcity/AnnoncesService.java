package losamigos.smartcity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

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

        boucleAnnonces.postDelayed(monRunnable,10000);

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
            boucleAnnonces.postDelayed(this,10000);
        }
    };

    private void verifAnnonces() {
        new Thread() {
            public void run() {

                final JSONArray json = RemoteFetchIDAnnonces.getJSON();

                if (json != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(AnnoncesService.this, "SUCCES", Toast.LENGTH_LONG).show();

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AnnoncesService.this, "annoncesChannel")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Test")
                                    .setContentText("Test")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                            mNotificationManager.notify(m, mBuilder.build());

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
