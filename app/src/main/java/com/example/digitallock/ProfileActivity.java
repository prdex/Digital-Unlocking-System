package com.example.digitallock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener  {

    private FirebaseAuth firebaseAuth;
    private Button buttonLogout;

    Button onoff;
    TextView t1;
    String address = null;
    String name = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    String phone = MainActivity.getData();
    String tosend = "r" + phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth    = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user    = firebaseAuth.getCurrentUser();

        buttonLogout    = (Button)findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        try{ setw();} catch (Exception e){}
        led_on_off(tosend);

    }

    private void setw() throws IOException{
        t1          =(TextView)findViewById(R.id.textView1);
        bluetooth_connect_device();
        onoff       =(Button)findViewById(R.id.buttonLockUnlock);

        onoff.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        led_on_off("f");
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        led_on_off("b");
                    }
                    return true;
            }

        });


    }

    private void bluetooth_connect_device() throws IOException
    {
        BluetoothDevice bt;
        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            address = myBluetooth.getAddress();
            pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size() > 0) {
                Iterator var2   =pairedDevices.iterator();

                while(var2.hasNext()) {
                    bt = (BluetoothDevice)var2.next();
                    address = bt.getAddress().toString();
                    name = bt.getName().toString();
                    Toast.makeText(this.getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception we){}
        myBluetooth     = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice dispositive = myBluetooth.getRemoteDevice(address);
        btSocket        = dispositive.createInsecureRfcommSocketToServiceRecord(myUUID);
        btSocket.connect();

        try {
            this.t1.setText("BT Name: " + this.name + "\nBT Address: " + this.address);
        } catch (Exception var3) {
        }
    }

    private void led_on_off(String i){
        try{
            if(btSocket != null){
                btSocket.getOutputStream().write(i.toString().getBytes());

            }
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view){
        if(view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
