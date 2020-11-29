package com.example.nfstest.util;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NfcUtil {

    public static boolean supportedTechs(String[] techs){
        boolean isSupport=false;
        List<String> tbList = Arrays.asList(techs);
        if(tbList.contains("android.nfc.tech.MifareClassic")){
            isSupport=true;
        }else if(tbList.contains("android.nfc.tech.MifareUltralight")){
            isSupport=true;
        }else if(tbList.contains("android.nfc.tech.Ndef")){
            isSupport=true;
        }else if(tbList.contains("android.nfc.tech.NfcA")){
            isSupport=true;
        }
        return isSupport;
    }


    public static String readMifareClassicTag(Tag tag, TextView text) {
        MifareClassic mfc = MifareClassic.get(tag);
        boolean auth = false;
        //读取TAG

        try {
            String metaInfo = "";
            //Enable I/O operations to the tag from this TagTechnology object.
            mfc.connect();
            int type = mfc.getType();//获取TAG的类型
            int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaInfo += "卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
                    + mfc.getBlockCount() + "个块\n存储空间,标签容量：" + mfc.getSize()
                    + "B\n";
            for (int j = 0; j < sectorCount; j++) {
                //Authenticate a sector with key A.
                auth = mfc.authenticateSectorWithKeyA(j,MifareClassic.KEY_DEFAULT)||mfc.authenticateSectorWithKeyA(j,MifareClassic.KEY_NFC_FORUM);
                int bCount;
                int bIndex;
                if (auth) {
                    metaInfo += "Sector " + j + ":验证成功\n";
                    // 读取扇区中的块
                    bCount = mfc.getBlockCountInSector(j);
                    bIndex = mfc.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mfc.readBlock(bIndex);
                        metaInfo += "Block " + bIndex + " : "
                                + StringUtil.bytes2HexString(data) + "\n";
                        bIndex++;
                    }
                } else {
                    metaInfo += "Sector " + j + ":验证失败\n";
                }
            }
            return metaInfo;
        } catch (Exception e) {
            StringUtil.setLog(text,"异常："+e.getMessage());
            e.printStackTrace();
        } finally {
            if (mfc != null) {
                try {
                    mfc.close();
                } catch (IOException e) {
                    StringUtil.setLog(text,"异常："+e.getMessage());
                }
            }
        }
        return null;

    }



}
