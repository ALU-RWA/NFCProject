package com.example.nfcproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    DataBaseHelper myDatabase;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;

    private Button addBtn, saveBtn, viewBtn, btnDelete, btnSearch;
    private Spinner spinner2;
    private EditText mEdt_txt_std_id_tag_no, mEdt_txt_student_name, mEdt_txt_reg_no, mEdt_txt_password,mEdt_txt_email;
    private TextView mTxt_Dept,mTxt_Session,mTxt_gender;
    private RadioButton mRdo_male,mRdo_female;

    //varibale to store input element value
    String tag;
    String regno;
    String name;
    String pass;
    String email;
    String gender;
    String dept;
    String session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner2 = findViewById(R.id.spin_session);
        myDatabase = new DataBaseHelper(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

//        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,);
        mEdt_txt_std_id_tag_no = findViewById(R.id.edt_txt_std_id_tag_no);
        mEdt_txt_student_name = findViewById(R.id.edt_txt_student_name);
        mEdt_txt_reg_no = findViewById(R.id.edt_txt_reg_no);
        mEdt_txt_password = findViewById(R.id.edt_txt_password);
        mEdt_txt_email = findViewById(R.id.edt_txt_email);

        btnDelete = findViewById(R.id.btn_delete_data);
        viewBtn = findViewById(R.id.btn_view_data);
        btnSearch=findViewById(R.id.btn_search_data);

        addBtn = findViewById(R.id.btn_update_student);
        saveBtn = findViewById(R.id.btn_save_data);
        SaveData();
        ReadData();
        deleteData();
        searchData();

        mEdt_txt_std_id_tag_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Cursor Result=myDatabase.readIndividualData(s);
                        if(Result!=null && Result.getCount()>0){
                            while (Result.moveToNext()){
                                mEdt_txt_reg_no.setText(Result.getString(2));
                                mEdt_txt_student_name.setText(Result.getString(3));
                                mEdt_txt_password.setText(Result.getString(4));
                                mEdt_txt_email.setText(Result.getString(5));
                            }

                        }
                    }
                },500);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void searchData() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Bye",Toast.LENGTH_LONG);
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchIntent);
//                finish();
            }
        });
    }
    public void SaveData(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check= myDatabase.addStudent(mEdt_txt_std_id_tag_no.getText().toString(),
                        mEdt_txt_reg_no.getText().toString(),
                        mEdt_txt_student_name.getText().toString(),
                        mEdt_txt_password.getText().toString(),
                        mEdt_txt_email.getText().toString());
                mEdt_txt_std_id_tag_no.setText("");
                mEdt_txt_reg_no.setText("");
                mEdt_txt_student_name.setText("");
                mEdt_txt_password.setText("");
                mEdt_txt_email.setText("");
                if (check==true)
                    Toast.makeText(MainActivity.this, "data has been inserted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "data has not been inserted", Toast.LENGTH_LONG).show();


            }
        });
        if (nfcAdapter == null){
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }
    public void ReadData(){
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor results = myDatabase.ReadData();
                if (results.getCount()==0){
                    showMessage("Error", "Nothing in the database");
                    return;
                }
                StringBuffer sb = new StringBuffer();
                while ( results.moveToNext()){
//                    sb.append("ID :"+ results.getString(0)+ "\n");
                    sb.append("TagNo :"+ results.getString(1)+ "\n");
                    sb.append("RegNo :"+ results.getString(2)+ "\n");
                    sb.append("Name :"+ results.getString(3)+ "\n");
                    sb.append("Password :"+ results.getString(4)+ "\n");
                    sb.append("Email :"+ results.getString(5)+ "\n\n");
                }
                showMessage("Students Record", sb.toString());
            }
        });
    }
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
    public void deleteData(){
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer deletedRows= myDatabase.deleteData(mEdt_txt_reg_no.getText().toString());
                if(deletedRows>0) {
                    mEdt_txt_reg_no.setText("");
                    mEdt_txt_student_name.setText("");
                    mEdt_txt_password.setText("");
                    mEdt_txt_email.setText("");
                    Toast.makeText(MainActivity.this, "data has been Deleted", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(MainActivity.this, "data has not been Deleted", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
//            Toast.makeText(this, "not null nfc adapter", Toast.LENGTH_SHORT).show();
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
        protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }
    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        Toast.makeText(this, "NFC Tag Read", Toast.LENGTH_SHORT).show();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }

            displayMsgs(msgs);
        }
    }
    private void displayMsgs(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0)
            return;

        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();

        for (int i = 0; i < size; i++) {
            ParsedNdefRecord record = records.get(i);
            String str = record.str();
            builder.append(str).append("\n");
        }
        String str=builder.toString();

        mEdt_txt_std_id_tag_no.setText(str.trim());
        //Toast.makeText(this, "Inside display Msgs"+str, Toast.LENGTH_SHORT).show();
        mEdt_txt_reg_no.setText("");
        mEdt_txt_student_name.setText("");
        mEdt_txt_password.setText("");
        mEdt_txt_email.setText("");
    }
    private String dumpTagData(Tag tag) {
        byte[] id = tag.getId();
        String tagid="";
        tagid=(String.valueOf(toDec(id)));

        return tagid;
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }
    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }
    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

}
