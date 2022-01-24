package com.jayr.mqttclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jayr.mqttclient.utility.MQTTHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private MQTTHelper mqttHelper;
    private TextView bpm,spo2;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("max30100");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bpm = findViewById(R.id.BPM);
        spo2 = findViewById(R.id.SPO2);
        startMqtt(bpm, spo2);
    }

    private void startMqtt(TextView bpm, TextView spo2){
        mqttHelper = new MQTTHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
            }

            @Override
            public void connectionLost(Throwable throwable) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                System.out.println(" ====== > Received @ " +topic + ":"+ mqttMessage.toString());

                if(topic.equals("BPM")){
                    bpm.setText(mqttMessage.toString());
                    if(!mqttMessage.toString().equals("0.00"))
                    {
                        myRef.child("BPM").push().setValue(mqttMessage.toString());

                    }

                       }
                if(topic.equals("SPO2")){

                    spo2.setText(mqttMessage.toString()+"%");
                    if(!mqttMessage.toString().equals("0.00"))
                    {
                        myRef.child("SPO2").push().setValue(mqttMessage.toString());

                    }
                          }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        });
    }
}