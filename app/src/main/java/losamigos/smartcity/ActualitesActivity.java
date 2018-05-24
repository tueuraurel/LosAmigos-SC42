package losamigos.smartcity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ActualitesActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualites);
        context = getApplicationContext();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ActualiteFragment())
                    .commit();
        }

    }
}
