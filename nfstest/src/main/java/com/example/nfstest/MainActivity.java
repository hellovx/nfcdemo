package com.example.nfstest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfstest.util.StringUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    //private Context context;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    IntentFilter[] filters;
    String[][] techLists;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.txtShow);
        text.setMovementMethod(ScrollingMovementMethod.getInstance());
        setLog("hello world");

        //
        nfcCheck();
        nfcInit();

        //扫描按钮动作
        Button bt_scan = findViewById(R.id.bt_scan);
        bt_scan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.v("MainActivity", "Log.v输入日志信息");
                System.out.println("in..");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }




    /**
     * nfc检查
     */
    private void nfcCheck() {
        //setLog("check:in");
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (null == nfcAdapter) {
            Toast.makeText(this, "sorry,手机不支持nfc", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.i("main", "等待ing");
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(this, "请开启nfc", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            }
        }
    }

    private void nfcInit() {
        //setLog("init:in");
        //
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        //
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        intentFilter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
//        intentFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            intentFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        filters = new IntentFilter[]{intentFilter};
        techLists = new String[][] { { IsoDep.class.getName() },{ NfcV.class.getName() }, { NfcF.class.getName() },{ NfcA.class.getName() }, };

    }


    //读取id：
    private String readFromTag(Intent intent){
        try {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String action=intent.getAction();
            String hexStr=StringUtil.toHexString(tag.getId());
            String text=StringUtil.hex2Decimal(hexStr);
            return "["+hexStr+"],["+text+"]";
//            return StringUtil.toHexString(tag.getId());
        }
        catch (Exception e) {
            e.printStackTrace();
            setLog("ERROR:readFromTag错误");
        };
        return null;
    }
    //初次判断是什么类型的NFC卡
    private void resolveIntent(Intent intent){
        if (intent == null)
            return ;
        String action=intent.getAction();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            setLog("ACTION_NDEF_DISCOVERED");
        }else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
            setLog("ACTION_TECH_DISCOVERED");
           Tag tag= intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] temp = tag.getTechList();
            for (String s : temp) {
               setLog("resolveIntent tag: " + s);
            }
            Parcelable[] rawMessage = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(null!=rawMessage){
                setLog("getNdefMsg: ndef格式 ");
            }else{
                setLog("getNdefMsg: unkonwn ");
            }


        }else if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
            setLog("ACTION_TAG_DISCOVERED");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //setLog("TAGTYPES:" + intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        String text=readFromTag(intent);
        resolveIntent(intent);
        if(null==text){
            setLog("ERROR:获取tag内容错误");
        }else{
            setLog("TAGTEXT:"+text);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(null!=nfcAdapter){
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,filters,techLists);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!=nfcAdapter) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private void setLog(String str){
        text.append(str+"\n");
        int offset=text.getLineCount()*text.getLineHeight();
        if(offset>text.getHeight()){
            text.scrollTo(0,offset-text.getHeight());
        }
    }
}
