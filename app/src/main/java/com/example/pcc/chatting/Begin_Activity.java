package com.example.pcc.chatting;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class Begin_Activity extends AppCompatActivity {
    Boolean result;
    Intent intent;
    public static Socket s=null;
    public static final String IP = "192.168.1.114";
    public static ObjectOutputStream oos=null;
    public static ObjectInputStream ois=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_);

                 connectValidateMac();

    }

    private void connectValidateMac() {
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    s = new Socket(IP,8080);
                    String mac= getMacAddr();
                    if (mac.equals("02:00:00:00:00:00"))
                     mac = "54-8C-A0-0F-7A-DD"; //Phone Device

                    oos = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
                    oos.writeObject(mac);
                    oos.flush();

                    ois = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));


                    result = ois.readBoolean();
                    if (result)
                    {

                        Go_Main_Activity();
                    }
                    else
                    {
                        Go_Start_Activity();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ignored) {
        }
        return "02:00:00:00:00:00";
    }

    void Go_Main_Activity () {
        intent = new Intent(Begin_Activity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    void Go_Start_Activity () {
        intent = new Intent(Begin_Activity.this,Start_Activity.class);
        startActivity(intent);
        finish();
    }

}
