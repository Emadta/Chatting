package com.example.pcc.chatting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class Start_Activity extends AppCompatActivity {
    private Button btn_signup;
    private Button btn_signin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_);

        btn_signup=(Button)findViewById(R.id.btn_get_acc);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //set_status(1);
                Go_Register_Activity();

            }
        });


        btn_signin=(Button)findViewById(R.id.btn_log);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //set_status(2);
                Go_Signin_Activity();

            }
        });
    }



      // 1 for regiester 2 for sign in
    /*void set_status(final int value)
    {
        Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                //FIXME :
                //Begin_Activity.s= new Socket(Begin_Activity.IP,8080);
                Begin_Activity.dout.writeInt(value);
                //dout.close();
                //s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
        t.start();
    }*/



    void Go_Register_Activity ()
    {
        Intent intent=new Intent(Start_Activity.this,Register_Activity.class);
        startActivity(intent);

    }
    void Go_Signin_Activity ()
    {
        Intent intent=new Intent(Start_Activity.this,Signin_Activity.class);
        startActivity(intent);
    }


}

