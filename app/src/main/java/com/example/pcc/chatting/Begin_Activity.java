package com.example.pcc.chatting;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class Begin_Activity extends AppCompatActivity {
    Boolean result;
    Intent intent;
    public static Socket s=null;
    public static final String IP = "192.168.1.107";
    public static ObjectOutputStream oos=null;
    public static ObjectInputStream ois=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_);

             if (isOnline())
               Connect_Validate_Mac ();
             else
              OfflineMode();
    }


    private void Connect_Validate_Mac() {
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    s = new Socket(IP,8080);
                    //String mac= getMacAddr();
                    //String mac = "s25-22-45-6f-fa-41"; // iniesta (my mobile)
                    //String mac = "s25-22-45-6f-sa-45"; // xavi
                    String mac = "54-8C-A0-0F-7A-DD"; //Phone Device

                    oos = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
                    oos.writeObject(mac);
                    oos.flush();

                    // recieve from server , if user is signed in or was logged out
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

    void OfflineMode ()
    {
        //// TODO if mac address is stored , that mean we signed in , and we can use offline mode (load_macAddressFile)
        //// TODO: (save_macAddressFile) will be in signin_Activity ,server send mac address
    }

    // GET MAC ADDRESS

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


    void Go_Main_Activity ()
    {
        intent = new Intent(Begin_Activity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    void Go_Start_Activity ()
    {
        intent = new Intent(Begin_Activity.this,Start_Activity.class);
        startActivity(intent);
        finish();
    }

    protected boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}
