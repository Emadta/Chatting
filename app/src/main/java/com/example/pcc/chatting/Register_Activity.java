package com.example.pcc.chatting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
    ArrayList<User> listInFile = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);
        txin1=(TextInputLayout)findViewById(R.id.txt_inp_Lay1);
        txin2=(TextInputLayout)findViewById(R.id.txt_inp_Lay2);
        txin3=(TextInputLayout)findViewById(R.id.txt_inp_Lay3);

        btn_signup=(Button)findViewById(R.id.btn_sign_up);

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

                    user_details = new User(txin1.getEditText().getText().toString(),txin3.getEditText().getText().toString(), txin2.getEditText().getText().toString());
                    oos.writeObject(user_details);
                    oos.flush();

                    result = ois.readBoolean();
                    if (!result)
                    {
                        Alert_Dialog();
                    }
                    else if (result) {

                        ois.readUTF();

                        boolean check = fileExists(getApplicationContext(), "UserName.txt");
                        if (check)
                        listInFile = loadUsersInFile ();

                        storeUsersInFile(txin1.getEditText().getText().toString(),listInFile);
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

    boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    ArrayList<User> loadUsersInFile (){

        ArrayList<User> List = null;
        FileInputStream fis;
        try {
            fis = openFileInput("UserName.txt");
            ObjectInputStream Ois = new ObjectInputStream(fis);
            List = (ArrayList<User>) Ois.readObject();
            Ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return List;
    }

    void storeUsersInFile(String username , ArrayList<User> list) throws IOException {
        User user = new User(username,true);
        list.add(user);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("UserName.txt", MODE_PRIVATE);
            ObjectOutputStream Oos = new ObjectOutputStream(fos);
            Oos.writeObject(list);
            Oos.flush();
            Oos.close();
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

}
