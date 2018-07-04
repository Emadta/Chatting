package com.example.pcc.chatting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
                   // String mac= Get_mac();
                    String mac = "52-8C-A0-1F-7A-DD";
                   // String mac = "51-8C-A0-0F-7A-DD";
                    //String mac = "s2-23-45-6f-sa-45";

                    oos = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
                    oos.writeObject(mac);
                    oos.flush();

                    // recieve from server , if user is signed in or was logged out
                    ois = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));

                    //// FIXME: must server send username also to use it in messaging , if user was signed in, username will be static

                    result = ois.readBoolean();
                    if (result)
                    {
                          // // TODO: maybe   server send username to store it in file
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

    /*String Get_mac () throws SocketException, UnknownHostException {
        InetAddress ip;
        StringBuilder sb =null;
        try {

            ip = InetAddress.getLocalHost();

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();


            sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

        } catch (UnknownHostException | SocketException e) {

            e.printStackTrace();

        }
        return sb.toString();
    }*/






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



    void close ()
    {
        if (s!=null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (oos!=null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ois!=null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
