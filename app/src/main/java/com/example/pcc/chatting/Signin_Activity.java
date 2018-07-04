package com.example.pcc.chatting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;

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
                    Send_Receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    void Send_Receive() throws IOException {

        // VALIDATE IF EMAIL OR USERNAME IS SENT
        if (user_or_email.contains("@")) {

            // THIS FOR EMAIL
            user_details = new User();
            user_details.setEmail(textInputLayout.getEditText().getText().toString());
            user_details.setPassword(textInputLayout1.getEditText().getText().toString());
            oos.writeObject(user_details);
            oos.flush();
        } else {
            // THIS FOR USERNAME

            user_details = new User();
            user_details.setUserName(textInputLayout.getEditText().getText().toString());
            user_details.setPassword(textInputLayout1.getEditText().getText().toString());
            oos.writeObject(user_details);
            oos.flush();
        }

        // RECEIVE INFO
        result = ois.readBoolean();
        resultUsername=ois.readUTF();


        // VALIDATE IF ACCOUNT IS ALERADY REGISTER , RESULT FROM SERVER
        if (result) {
            // iS FOUND
            Save_Username(resultUsername);
            Go_Main_Activity();

        } else if (!result) {
            // EMAIL IS NOT FOUND (MSG BOX)
            Alert_Dialog();

        }

    }

    void Save_Username (String username) throws IOException
    {
        FileOutputStream fos = openFileOutput("UserName.txt",MODE_PRIVATE);
        fos.write(username.getBytes());
        fos.flush();
        fos.close();
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
                        dialog.cancel();
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