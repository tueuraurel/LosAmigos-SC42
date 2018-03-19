package losamigos.smartcity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

//import android.support.v7.app.AppCompatActivity;

public class HorlogeActivity extends Activity {
        TimePicker alarmTimePicker;
        PendingIntent pendingIntent;
        AlarmManager alarmManager;

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_horloge);
            alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
        public void OnToggleClicked(View view)
        {
            long time;
            if (((ToggleButton) view).isChecked())
            {
                Toast.makeText(HorlogeActivity.this, "Alarme allumée", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                Intent intent = new Intent(this, HorlogeReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

                time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
                if(System.currentTimeMillis()>time)
                {
                    if (calendar.AM_PM == 0)
                        time = time + (1000*60*60*12);
                    else
                        time = time + (1000*60*60*24);
                }
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
            }
            else
            {
                alarmManager.cancel(pendingIntent);
                Toast.makeText(HorlogeActivity.this, "Alarme éteinte", Toast.LENGTH_SHORT).show();
            }
        }
    }