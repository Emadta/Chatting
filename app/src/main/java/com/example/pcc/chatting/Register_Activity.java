package com.example.pcc.chatting;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import static com.example.pcc.chatting.Begin_Activity.oos;
import static com.example.pcc.chatting.Begin_Activity.ois;

public class Register_Activity extends AppCompatActivity {
    private TextInputLayout txin1;
    private TextInputLayout txin2;
    private TextInputLayout txin3;
    Intent intent;
    Boolean result;
    User user_details=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        txin1=(TextInputLayout)findViewById(R.id.txt_inp_Lay1);
        txin2=(TextInputLayout)findViewById(R.id.txt_inp_Lay2);
        txin3=(TextInputLayout)findViewById(R.id.txt_inp_Lay3);

        Button btn_signup = (Button) findViewById(R.id.btn_sign_up);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

    }

    void Register () {
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    oos.writeInt(1);
                    oos.flush();

                    user_details = new User(txin1.getEditText().getText().toString().trim().toLowerCase(),txin3.getEditText().getText().toString().trim().toLowerCase(), txin2.getEditText().getText().toString().trim().toLowerCase());
                    oos.writeObject(user_details);
                    oos.flush();

                    result = ois.readBoolean();
                    if (!result)
                    {
                        Alert_Dialog();
                    }
                    else {
                        ois.readUTF();
                        boolean del = deleteFile("UserName.txt");
                        del = deleteFile("ListFriends.txt");
                        storeUsername(txin1.getEditText().getText().toString());
                        Go_Main_Activity();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    void Alert_Dialog() {
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

    void storeUsername (String name){
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("UserName.txt", MODE_PRIVATE);
            fos.write(name.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void Go_Main_Activity () {
        Register_Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(Register_Activity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    public boolean deleteFile(String name){
        File dir = getFilesDir();
        File file = new File(dir,name);
        boolean deleted = file.delete();
        return deleted;
    }

}
