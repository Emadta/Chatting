package com.example.pcc.chatting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings_Activity extends AppCompatActivity {
private Button btn_ch_stat;
    private TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_);
        btn_ch_stat=(Button)findViewById(R.id.btn_ch_status);
        Bundle b=getIntent().getExtras();
        String user= b.getString("user");
        textView1=(TextView) findViewById(R.id.txv1);
        textView1.setText(user);



        btn_ch_stat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Settings_Activity.this,Status_Activity.class);
                startActivity(intent);
            }
        });
    }
}
