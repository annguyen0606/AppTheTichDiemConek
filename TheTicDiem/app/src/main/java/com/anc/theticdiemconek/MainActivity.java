package com.anc.theticdiemconek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button readNFCActivity,checkInforCustomerActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readNFCActivity = findViewById(R.id.btnReadNFC);
        checkInforCustomerActivity = findViewById(R.id.btnCheckInforCustomer);

        findViewById(R.id.btnReadNFC).setOnClickListener(this);
        findViewById(R.id.btnCheckInforCustomer).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCheckInforCustomer:
                startActivity(new Intent(MainActivity.this,CheckCustomerActivity.class));
                break;
            case R.id.btnReadNFC:
                startActivity(new Intent(MainActivity.this,ReadNFCActivity.class));
                break;
        }
    }
}