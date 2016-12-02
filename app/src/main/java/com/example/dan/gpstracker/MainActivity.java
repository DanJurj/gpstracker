package com.example.dan.gpstracker;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private BroadcastReceiver broadcastReceiver;
    private TCPClient mTcpClient;

    @Override
    protected void onResume()
    {
        super.onResume();                   //apelare constructor clasa de baza
        if (broadcastReceiver == null )
        {
            broadcastReceiver = new BroadcastReceiver()
            {
                public void onReceive(Context context, Intent intent)
                {
                    String s="\n" + intent.getExtras().get("coordinates");
                    textView.append(s);
                    if (mTcpClient != null)
                    {
                        mTcpClient.sendMessage(s);  //trimitem coordonatele la server
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (broadcastReceiver != null)
        {
            unregisterReceiver(broadcastReceiver);
        }
        Intent i=new Intent(getApplicationContext(),GPS_Service.class);
        stopService(i);                     //oprim cautarea gps
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   //se incarca interfata grafica
        textView = (TextView) findViewById(R.id.textView);    //assign textView
        if (!runtime_permissions())
            enable_buttons();
        new connectTask().execute("");  //conectarea la server
    }

    private void enable_buttons()
    {
        Intent i=new Intent(getApplicationContext(),GPS_Service.class);
        startService(i);
    }

    private boolean runtime_permissions()
    {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},100);
            return true;
        }
        return false;
    }

    //clasa care asigura conectarea TCP la server
    public class connectTask extends AsyncTask<String, String, TCPClient>
    {
        @Override
        protected TCPClient doInBackground(String... message)
        {
            //se creeaza obiectul TcpClient...
            mTcpClient = new TCPClient();
            mTcpClient.run();
            return null;
        }
    }
}
