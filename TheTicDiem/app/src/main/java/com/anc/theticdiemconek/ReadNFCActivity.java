package com.anc.theticdiemconek;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anc.theticdiemconek.Class.ConvertDecToHec;
import com.anc.theticdiemconek.DataBase.DataProvider;
import com.anc.theticdiemconek.Model.ThongTinKhachHang;

public class ReadNFCActivity extends AppCompatActivity implements View.OnClickListener {
    EditText UIDTAG, nameCustomer, addressCustomer, phoneCustomer, pricePay, accumaulatePoint,conversionRate;
    Button registration, confirmPoints;
    TextView currentPointsKH;
    NfcAdapter nfcAdapter;
    DataProvider dataProvider;
    int diemTichHienTai = 0;
    private static SwipeRefreshLayout loadDataCustomer;//Load Data from Server
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_n_f_c);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        AnhXa();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this,"Your phone didn't support NFC",Toast.LENGTH_SHORT).show();
        }else if(!nfcAdapter.isEnabled()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog);
            builder.setTitle("Inform");
            builder.setMessage("You haven't to enable NFC. Please, Enable NFC");
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }else {
        }

        loadDataCustomer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadKhachHang();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadDataCustomer.setRefreshing(false);
                    }
                },1000);
            }
        });
    }

    private void AnhXa() {
        UIDTAG = findViewById(R.id.edtUIDTAG);
        loadDataCustomer = findViewById(R.id.pullToLoadData);
        nameCustomer = findViewById(R.id.edtNameCustomer);
        addressCustomer = findViewById(R.id.edtAddressCustomer);
        phoneCustomer = findViewById(R.id.edtPhoneCustomer);
        pricePay = findViewById(R.id.edtPricePay);
        accumaulatePoint = findViewById(R.id.edtAccumulatepoints);
        conversionRate = findViewById(R.id.edtConversionRate);
        registration = findViewById(R.id.btnRegistration);
        confirmPoints = findViewById(R.id.btnConfirmPoints);
        currentPointsKH = findViewById(R.id.tvCurrentPoints);

        findViewById(R.id.btnRegistration).setOnClickListener(this);
        findViewById(R.id.btnConfirmPoints).setOnClickListener(this);
        conversionRate.setText("1000");
        pricePay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(pricePay.getText() == null || pricePay.getText().toString().equals("")){

                }else {
                    int price = Integer.parseInt(pricePay.getText().toString());
                    accumaulatePoint.setText(String.valueOf((price/Integer.parseInt(conversionRate.getText().toString().trim()))));
                    currentPointsKH.setText(String.valueOf(Integer.parseInt(accumaulatePoint.getText().toString().trim()) + diemTichHienTai));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFilter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        intentFilter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        nfcAdapter.enableForegroundDispatch(ReadNFCActivity.this,pendingIntent,new IntentFilter[]{intentFilter},this.techList);
    }

    private final String[][] techList = new String[][]{
        new String[]{
                NfcF.class.getName(),
                Ndef.class.getName()
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String decUID = ConvertDecToHec.IDHex2Dec(id);
        UIDTAG.setText("0"+decUID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegistration:
                if(UIDTAG.getText() == null || UIDTAG.getText().toString().equals("")){
                    Toast.makeText(this,"You haven't touch Card, Please check",Toast.LENGTH_SHORT).show();
                }else {
                    ThongTinKhachHang thongTinKhachHang = dataProvider.getInstance().LayThongTinKhachHang(UIDTAG.getText().toString().trim());
                    if(thongTinKhachHang.getUIDKH().equals(UIDTAG.getText().toString().trim())){
                        Toast.makeText(this,"You haven't get infor customer or Customer existed into System\nCan't Add new, Please Check",Toast.LENGTH_SHORT).show();
                    }else {
                        ThemKhachHangMoi themKhachHangMoi = new ThemKhachHangMoi(this);
                        themKhachHangMoi.execute();
                    }
                }
                break;
            case R.id.btnConfirmPoints:
                int diemTichCong = Integer.parseInt(accumaulatePoint.getText().toString().trim());
                if(UIDTAG.getText() == null || UIDTAG.getText().toString().equals("")){
                    Toast.makeText(this,"You haven't touch Card, Please check",Toast.LENGTH_SHORT).show();
                }else {
                    ThongTinKhachHang thongTinKhachHang = dataProvider.getInstance().LayThongTinKhachHang(UIDTAG.getText().toString().trim());
                    if(thongTinKhachHang.getUIDKH().equals(UIDTAG.getText().toString().trim())){
                        EditThongTinKhachHang editThongTinKhachHang = new EditThongTinKhachHang(this);
                        editThongTinKhachHang.execute();
                    }else {
                        Toast.makeText(this,"You haven't get infor customer or Customer existed into System\nCan't Add new, Please Check",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void LoadKhachHang(){
        LoadThongTinKhachHang loadThongTinKhachHang = new LoadThongTinKhachHang(ReadNFCActivity.this);
        loadThongTinKhachHang.execute(UIDTAG.getText().toString().trim());
    }

    private class LoadThongTinKhachHang extends AsyncTask<String,Void, ThongTinKhachHang>{
        ProgressDialog progressDialog;

        public LoadThongTinKhachHang(Context mContext) {
            progressDialog = new ProgressDialog(mContext, android.app.AlertDialog.THEME_HOLO_DARK);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected ThongTinKhachHang doInBackground(String... strings) {
            return dataProvider.getInstance().LayThongTinKhachHang(strings[0]);
        }

        @Override
        protected void onPostExecute(ThongTinKhachHang thongTinKhachHang) {
            if(thongTinKhachHang.getUIDKH().equals(UIDTAG.getText().toString().trim())){
                nameCustomer.setText(thongTinKhachHang.getNameKH());
                addressCustomer.setText(thongTinKhachHang.getAddressKH());
                phoneCustomer.setText(thongTinKhachHang.getPhoneKH());
                //accumaulatePoint.setText(String.valueOf(thongTinKhachHang.getTichDiem()));
                diemTichHienTai = thongTinKhachHang.getTichDiem();
                currentPointsKH.setText(String.valueOf(thongTinKhachHang.getTichDiem()));
            }else {
                Toast.makeText(ReadNFCActivity.this,"Customer didn't exist, Please check",Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    private class ThemKhachHangMoi extends AsyncTask<Void,Void,Integer>{
        ProgressDialog progressDialog;

        public ThemKhachHangMoi(Context mContext) {
            progressDialog = new ProgressDialog(mContext, android.app.AlertDialog.THEME_HOLO_DARK);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Processing...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return dataProvider.getInstance().InsertNewCustomer(UIDTAG.getText().toString().trim(),nameCustomer.getText().toString().trim(),addressCustomer.getText().toString().trim(),
                                                phoneCustomer.getText().toString().trim(),Integer.parseInt(accumaulatePoint.getText().toString().trim()));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer >= 1){
                Toast.makeText(ReadNFCActivity.this,"Registration Success",Toast.LENGTH_SHORT).show();
                ClearActivity();
            }else {
                Toast.makeText(ReadNFCActivity.this,"Registration Failed, Please Check",Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    private class EditThongTinKhachHang extends AsyncTask<Void,Void,Integer>{
        ProgressDialog progressDialog;
        public EditThongTinKhachHang(Context mContext) {
            progressDialog = new ProgressDialog(mContext, android.app.AlertDialog.THEME_HOLO_DARK);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Processing...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return dataProvider.getInstance().UpdateThongTinKhachHang(UIDTAG.getText().toString().trim(),nameCustomer.getText().toString().trim(),addressCustomer.getText().toString().trim(),
                    phoneCustomer.getText().toString().trim(),Integer.parseInt(currentPointsKH.getText().toString().trim()));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer >= 1){
                Toast.makeText(ReadNFCActivity.this,"Add Points Success",Toast.LENGTH_SHORT).show();
                ClearActivity();
            }else {
                Toast.makeText(ReadNFCActivity.this,"Add Points Failed, Please Check",Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    void ClearActivity(){
        UIDTAG.setText("");
        nameCustomer.setText("");
        addressCustomer.setText("");
        phoneCustomer.setText("");
        pricePay.setText("0");
        accumaulatePoint.setText("0");
        conversionRate.setText("0");
        currentPointsKH.setText("0");
    }
}