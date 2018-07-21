package com.example.pcc.chatting;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import static com.example.pcc.chatting.Begin_Activity.oos;
import static com.example.pcc.chatting.MainActivity.mainActivity;
import static com.example.pcc.chatting.MainActivity.searchActivity;


public class Search_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private  TextInputLayout username_search;
    private Button btn_search;
    private TextView txt_notfound;
    private TextView txt_display_name;
    private CardView cardView;
    boolean user_search;
    private Message user_to_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_);

        initializeData();
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });


    }
    void initializeData() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        username_search = (TextInputLayout) findViewById(R.id.text_input_search);
        txt_notfound = (TextView) findViewById(R.id.textview_Notfound);
        txt_display_name = (TextView) findViewById(R.id.display_name_search);
        cardView = (CardView) findViewById(R.id.card_view);
        btn_search = (Button) findViewById(R.id.button_search);
    }

    void search() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    user_to_send = new Message(username_search.getEditText().getText().toString().trim().toLowerCase(), "search_request");
                    oos.writeObject(user_to_send);
                    oos.flush();

                    searchActivity.acquire();
                    try {
                        Message receivedResult = MainActivity.MSG;
                        user_search = receivedResult.getMsg().equals("true");


                        if (!user_search) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cardView.setVisibility(CardView.INVISIBLE);
                                    txt_notfound.setVisibility(TextView.VISIBLE);
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt_display_name.setText(username_search.getEditText().getText().toString());
                                    cardView.setVisibility(CardView.VISIBLE);
                                    txt_notfound.setVisibility(TextView.INVISIBLE);
                                }
                            });

                            click_card_view();
                        }
                    }finally {
                        mainActivity.release();
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    void click_card_view () {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Intent intent = new Intent(Search_Activity.this,MainActivity.class);
                intent.putExtra("userName",username_search.getEditText().getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }
}