package com.example.pcc.chatting;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import static com.example.pcc.chatting.Begin_Activity.ois;
import static com.example.pcc.chatting.Begin_Activity.oos;


public class Search_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private  TextInputLayout username_search;
    private Button btn_search;
    private TextView txt_notfound;
    private TextView txt_display_name;
    private ImageView image_user;
    private CardView cardView;
    //// FIXME: 29/06/2018 // FIXME: 29/06/2018
    static  User user_search_result;
    //// FIXME: 29/06/2018 // FIXME: 29/06/2018
    boolean user_search;

    private Message user_to_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_);

         init_data();

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

    }
    void init_data () {
        toolbar = (Toolbar) findViewById(R.id.tool_bar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // TAKE NAME FROM TEXTINPUTLAYOUT AND PUT IT IN username
        username_search = (TextInputLayout) findViewById(R.id.text_input_search);

        // THIS TO DISPLAY NOT FOUND
        txt_notfound = (TextView) findViewById(R.id.textview_Notfound);

        // THIS TO PUT USERNAME IF FOUND AND IMAGEVIEW
        txt_display_name = (TextView) findViewById(R.id.display_name_search);
        image_user = (ImageView) findViewById(R.id.imageView_search);

        // TO MAKE CARDVIEW VISIBLE
        cardView = (CardView) findViewById(R.id.card_view);

        btn_search = (Button) findViewById(R.id.button_search);
    }

    void search() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Send_Receive();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    void Send_Receive() throws IOException, ClassNotFoundException {

        // SEND USERNAME TO SERVER
         user_to_send = new Message(username_search.getEditText().getText().toString(),"search_request");
         oos.writeObject(user_to_send);
         oos.flush();

         //// FIXME: 29/06/2018 we will try boolean but future we need object contain username and imageview

             user_search = ois.readBoolean();


        if (!user_search) {
            // DISPLAY TEXTVIEW THAT CONTENTS "Not found"
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cardView.setVisibility(CardView.INVISIBLE);
                    txt_notfound.setVisibility(TextView.VISIBLE);
                }
            });

        }
        else {
            // PUT USERNAME TO DISPLAY NAME SEARCH TEXTVIEW IN LINEAR LAYOUT AND IMAGEVIEW
            //// FIXME: 29/06/2018  // FIXME: 29/06/2018
            //String name = user_search.getUserName();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_display_name.setText(username_search.getEditText().getText().toString());
                    cardView.setVisibility(CardView.VISIBLE);
                    txt_notfound.setVisibility(TextView.INVISIBLE);
                }
            });

            //// FIXME: 29/06/2018 // FIXME: 29/06/2018
           // user_search_result = new User(username_search.getEditText().getText().toString());

            //todo here to put imageview (this image byte array from server)

            // when click on CardView
            click_card_view();
        }

    }

    void click_card_view () {
        // if we clicked on card that mean this friend will be in main_activity
        //GetUserToFriendsList();

        
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // GO INTENT MSG ACTIVITY (intent)
                //// FIXME: MainActivity.listFriends.add(newuser) will try it after store


                Intent intent = new Intent(Search_Activity.this,MainActivity.class);
                intent.putExtra("userName",username_search.getEditText().getText().toString());
                startActivity(intent);
                finish();


            }
        });
    }

    static User GetUserToFriendsList () {
         User new_friend = user_search_result;
        return new_friend;
    }

//// TODO: send username when click on his cardview to message_activity by method above  , (To)


}