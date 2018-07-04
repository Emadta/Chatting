package com.example.pcc.chatting;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.pcc.chatting.Begin_Activity.oos;
import static com.example.pcc.chatting.Begin_Activity.ois;

public class Register_Activity extends AppCompatActivity {
    private TextInputLayout txin1;
    private TextInputLayout txin2;
    private TextInputLayout txin3;
    private Button btn_signup;
    Intent intent;
    Boolean result;
    User user_details=null;
    String userName; // this to use it in message_activity,class Message(String from)
    String Password,Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);
        txin1=(TextInputLayout)findViewById(R.id.txt_inp_Lay1);
        txin2=(TextInputLayout)findViewById(R.id.txt_inp_Lay2);
        txin3=(TextInputLayout)findViewById(R.id.txt_inp_Lay3);
        
        userName = txin1.getEditText().getText().toString();
        Password = txin2.getEditText().getText().toString();
        Email= txin3.getEditText().getText().toString();
        
        btn_signup=(Button)findViewById(R.id.btn_sign_up);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

    }

    void Register ()
    {
        /*Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    oos.writeInt(1);
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();*/


        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    oos.writeInt(1);
                    oos.flush();
                    Send_Receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    void Send_Receive() throws IOException
    {
            //SEND INFORMATION
        long x=1;
            user_details = new User(txin1.getEditText().getText().toString(),txin3.getEditText().getText().toString(), txin2.getEditText().getText().toString());
            oos.writeObject(user_details);
            oos.flush();

            result = ois.readBoolean();
            // VALIDATE IF ACCOUNT IS ALREADY USED OR NOT
            if (!result)
            { // IS USED (MSG BOX)
                Alert_Dialog();
            }
            else if (result) {
                // IS NOT USED , THAT MEAN IS ACCEPTABLE

                // save username to use it in messaging
                Save_Username (txin1.getEditText().getText().toString());

                Go_Main_Activity();
            }

    }

    void Alert_Dialog()
    {
        Register_Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(Register_Activity.this);
                alertdialog.setTitle("Wrong");
                alertdialog.setMessage("The Email is used already");
                alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertdialog.show();
            }
        });
    }

    void Save_Username (String username) throws IOException
    {
        FileOutputStream fos = openFileOutput("UserName.txt",MODE_PRIVATE);
        fos.write(username.getBytes());
        fos.flush();
        fos.close();
    }

    void Go_Main_Activity ()
    {
        Register_Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(Register_Activity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

}
