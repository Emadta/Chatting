package com.example.pcc.chatting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static com.example.pcc.chatting.Begin_Activity.ois;
import static com.example.pcc.chatting.Begin_Activity.oos;

public class Signin_Activity extends AppCompatActivity {
    private Button btn_signin;
    private TextInputLayout textInputLayout;
    private TextInputLayout textInputLayout1;
    Intent intent;
    Boolean result;
    User user_details = null;
    String user_or_email;
    String password;
    String resultUsername;
    ArrayList<User> listInFile = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_);
        textInputLayout = (TextInputLayout) findViewById(R.id.txxtinput1);
        textInputLayout1 = (TextInputLayout) findViewById(R.id.txxtinput2);

        user_or_email = textInputLayout.getEditText().getText().toString();
        password = textInputLayout1.getEditText().getText().toString();

        btn_signin = (Button) findViewById(R.id.btn_sign_in);
                btn_signin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Sign_In();
                    }
                });
            }

    void Sign_In() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    oos.writeInt(2);
                    oos.flush();
                    if (user_or_email.contains("@")) {

                        user_details = new User();
                        user_details.setEmail(textInputLayout.getEditText().getText().toString());
                        user_details.setPassword(textInputLayout1.getEditText().getText().toString());
                        oos.writeObject(user_details);
                        oos.flush();

                    } else {

                        user_details = new User();
                        user_details.setUserName(textInputLayout.getEditText().getText().toString());
                        user_details.setPassword(textInputLayout1.getEditText().getText().toString());
                        oos.writeObject(user_details);
                        oos.flush();
                    }

                    result = ois.readBoolean();
                    resultUsername=ois.readUTF();


                    if (result) {

                        boolean check = fileExists(getApplicationContext(), "UserName.txt");
                        if (check)
                            listInFile = loadUsersInFile (resultUsername);

                        storeUsersInFile(listInFile,resultUsername);
                        Go_Main_Activity();

                    } else if (!result) {
                        Alert_Dialog();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    ArrayList<User> loadUsersInFile (String username){
        ArrayList<User> List = null;
        ArrayList<User> List1 = null;
        FileInputStream fis;
        try {
            fis = openFileInput("UserName.txt");
            ObjectInputStream Ois = new ObjectInputStream(fis);
            List = (ArrayList<User>) Ois.readObject();
            Ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (User user : List)
        {
            if (user.getUserName().equals(username)){
                user.setSignedIn(true);
                List1.add(user);
            }
            else {
                List1.add(user);
            }
        }
        return List1;
    }

    void storeUsersInFile(ArrayList<User> list, String name) throws IOException {

        if (list != null && list.isEmpty()){
        User user = new User(name,true);
        list.add(user);
        }
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

    void Alert_Dialog() {
        Signin_Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(Signin_Activity.this);
                alertdialog.setTitle("Wrong");
                alertdialog.setMessage("The Username(Email) or Password is not correct");
                alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertdialog.show();
            }
        });
    }

    void Go_Main_Activity() {
        Signin_Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(Signin_Activity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}