package com.example.ronal.tamagoshi;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Runnable,ServiceConnection
{
    TextView numberOfFood;
    TextView numberOfWater;
    Button giveWater;
    Button giveFood;
    Handler handler;
    ImageView image;
    final ServiceConnection connection = this;
    private CounterService counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(IsServiceRunning(CounterService.class) && counter != null) {
            bindService(new Intent(this, CounterService.class), connection, Context.BIND_AUTO_CREATE);
        }
        else
        {
            startService(new Intent(this, CounterService.class));
            bindService(new Intent(this, CounterService.class), connection, Context.BIND_AUTO_CREATE);
        }
        giveWater = (Button) findViewById(R.id.aguaBtn);
        giveFood = (Button) findViewById(R.id.alimentarBtn);
        numberOfFood = (TextView) findViewById(R.id.amountOfFood);
        numberOfWater = (TextView) findViewById(R.id.amountOfWater);
        image = (ImageView) findViewById(R.id.image);
        handler = new Handler();
        handler.post(this);

        giveWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter != null)
                {
                    if(counter.waterAmount <= 95)
                    {
                        counter.waterAmount += 5;
                    }
                    else
                    {
                        counter.waterAmount = 100;
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Serviço não conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        giveFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter != null)
                {
                    if(counter.foodAmount <= 95)
                    {
                        counter.foodAmount += 5;
                    }
                    else
                    {
                        counter.foodAmount = 100;
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Serviço não conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void onStop()
    {
        super.onStop();
        if(counter != null)
        {
            counter = null;
            unbindService(connection);
        }
    }

    public void run()
    {
        handler.postDelayed(this, 1000);
        if(counter != null) {
            numberOfFood.setText(String.valueOf(counter.foodAmount));
            numberOfWater.setText(String.valueOf(counter.waterAmount));
            if(counter.foodAmount == 25 || counter.waterAmount == 25) {
                image.setImageResource(R.drawable.fome);
            }
            if(counter.foodAmount == 0 || counter.waterAmount == 0)
            {
                image.setImageResource(R.drawable.morto);
            }
        }

    }

    public void onServiceConnected(ComponentName name, IBinder service)
    {
        CounterService.LocalBinder binder = (CounterService.LocalBinder) service;
        counter = binder.getService();

    }

    public void onServiceDisconnected(ComponentName name)
    {
        counter = null;
    }

    private boolean IsServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if(serviceClass.getName().equals(service.service.getClassName())) return true;
        }
        return false;
    }
}
