package com.example.digitallock;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editUserName;
    private EditText editPhoneNumber;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
//
    private static  String globalphone;

//    TextView t1;
//    String address = null;
//    String name = null;
//    BluetoothAdapter myBluetooth = null;
//    BluetoothSocket btSocket = null;
//    Set<BluetoothDevice> pairedDevices;
//    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static String getData(){
        return globalphone;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            //profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editUserName = (EditText) findViewById(R.id.edittext);
        editPhoneNumber = (EditText) findViewById(R.id.editPhone);

        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);



    }

        private void registerUser() {
        final String name = editUserName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editPhoneNumber.getText().toString().trim();

        globalphone         = phone;


        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "You Forgot One of the field", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "You Forgot Email", Toast.LENGTH_SHORT).show();
            //stopping the further execution
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "You Forgot to write Password", Toast.LENGTH_SHORT).show();
            //stopping the further execution
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "You Forgot One of the field", Toast.LENGTH_SHORT).show();
        }
        if (phone.length() != 10) {
            editPhoneNumber.setError("enter valid phone number");
            return;
        }
        //if validate is ok
        //we will show progressbar

        progressDialog.setMessage("Registering User.....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            User user = new User(
                                    name,
                                    email,
                                    phone
                            );

                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                        //Toast.makeText(MainActivity.this,"Successs...", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(MainActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {

        if (view == buttonRegister) {

            registerUser();

            final String phone = editPhoneNumber.getText().toString().trim();
            String phoneNumber = "+91" + phone;
            Intent intent = new Intent(MainActivity.this, VerifyPhone.class);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);

        }
        if (view == textViewSignin) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}

