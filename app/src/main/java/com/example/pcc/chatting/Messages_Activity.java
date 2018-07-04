package com.example.pcc.chatting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static com.example.pcc.chatting.Begin_Activity.ois;
import static com.example.pcc.chatting.Begin_Activity.oos;

public class Messages_Activity extends AppCompatActivity {

    Toolbar toolbarMessages;
    static final int IMG_PICK = 10;
    static final int VIDEO_PICK = 20;
    ArrayList<Message> listMessages;
    private RecyclerView recyclerView;
    Message_Adapter message_adapter;
    private Button btn_send;
    private Button btn_pick_img;
    private EditText editText;
    static byte[] byte_array_image;
    static Bitmap bitmap;
    String msg;
    String To;
    Message message;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_);

        toolbarMessages = (Toolbar) findViewById(R.id.toolBarMessages);
        setSupportActionBar(toolbarMessages);
        intent = getIntent();
        To = intent.getStringExtra("To");
        getSupportActionBar().setTitle(To);

          init_recycleview ();
           // // FIXME: 29/06/2018 problem when recive msg
        // this contains while loop to receive messages
        receive_messages ();


        // when click on send btn ,send msg to server
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                msg=editText.getText().toString();

                if (!TextUtils.isEmpty(editText.getText().toString())) {

                    //  to determine this message for who ,receive String username (TO)

                          // 1-way , this from click on Linear from recyclerview from User_Adapter

                    if (intent!=null)
                    {
                          AddMessageToRecyclerView(To);
                    }

                         // 2-way , this from click on Linear from Search_Activity
                    /*User user_msg = Search_Activity.GetUserToFriendsList();
                    if (user_msg != null)
                    {
                        AddMessageToRecyclerView(user_msg.getUserName());
                    }*/

                }
            }
        });



        btn_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_image();
            }
        });
        //// FIXME: 28/06/2018 //// FIXME: 28/06/2018 
       receive_image_picked ();

    }

    // initilaize recyclerview and all data
    void init_recycleview ()
    {
        // this listFriends to add new message to recycleview
        listMessages =new ArrayList<>();

        btn_send=(Button)findViewById(R.id.btn_send_msg);

        // this button to go to gallery and got image
        btn_pick_img = (Button) findViewById(R.id.btn_pick_img);

        // this to write message
        editText=(EditText)findViewById(R.id.send_msg);

        // user_adapter to bind info with recycleview by put new message in listFriends and notify it by recycleview
        message_adapter=new Message_Adapter(listMessages);


        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_message);

        // to scroll vertically
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        // put layout above to recycle in this activity
        recyclerView.setLayoutManager(linearLayoutManager);

        // for scrolling
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        recyclerView.setHasFixedSize(true);

        // finally, put message_adapter to recycleview , message_adapter contents listFriends
        recyclerView.setAdapter(message_adapter);
    }

    void AddMessageToRecyclerView(String To)
    {
        // add this message object to listFriends and give it new position in listFriends and put it in interface

        message = new Message(editText.getText().toString().trim(),MainActivity.userName,To,Messages.TEXT_MSG,"private_chat");
        listMessages.add(message);
        int new_position=( listMessages.size() - 1 );
        message_adapter.notifyItemInserted(new_position);

        // make recycleview scroll to new position auto
        recyclerView.scrollToPosition(new_position);
        editText.setText("");

        SendToServer();
    }

    public void SendToServer()
    {   Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {

            try {
               oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
        thread.start();
    }

    void receive_messages ()
    {
        Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true)
            {


                try {
                    message = (Message) ois.readObject();

                } catch(EOFException e){
                    break;
                }
                catch (IOException | ClassNotFoundException ignored) {
                }
                listMessages.add(message);
                int new_position=( listMessages.size() - 1 );
                message_adapter.notifyItemInserted(new_position);
                //recyclerView.scrollToPosition(new_position);
            }


        }
    });
        thread.start();
    }





                   // // TODO: 29/06/2018 Image and other media

    void pick_image()
    {    // TO ENTER TO INTENT THAT CAN PICK FROM IT
        Intent intent_pick_image = new Intent(Intent.ACTION_PICK);

        // WHERE WE CAN FIND DATA THAT WE NEED
        File Directory_picture = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); // File : because this environement return file (Environment.java)
        String path_picture = Directory_picture.getPath();

        // TRANSFORM path_picture TO URI
        Uri data = Uri.parse(path_picture);

        // put only images in the intent with URI and images from all types
        intent_pick_image.setDataAndType(data,"image/*");

        startActivityForResult(intent_pick_image,IMG_PICK);

    }

    void pick_video ()
    {    // TO ENTER TO INTENT THAT CAN PICK FROM IT
        Intent intent_pick_Video = new Intent(Intent.ACTION_PICK);

        // WHERE WE CAN FIND DATA THAT WE NEED
        File Directory_Movie = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES); // File : because this environement return file (Environment.java)
        String path_Video = Directory_Movie.getPath();

        // TRANSFORM path_picture TO URI
        Uri data = Uri.parse(path_Video);

        // put only images in the intent with URI and images from all types
        intent_pick_Video.setDataAndType(data,"video/*");

        startActivityForResult(intent_pick_Video,VIDEO_PICK);

    }

    //// FIXME
    // receive image from image_activity
    // add image to recycleview

    //// FIXME: 28/06/2018 // // FIXME: 28/06/2018
    void receive_image_picked ()
    {
        /*Bundle bundle = getIntent().getExtras();
        byte_array_image = bundle.getByteArray("image");

        // add this message object to listFriends and give it new position in listFriends and put it in interface
        //// FIXME:
       // message = new Message(byte_array_image,Messages.MSG_SENT,Messages.IMAGE_MSG);
        listFriends.add(message);
        int new_position=( listFriends.size() - 1 );
        message_adapter.notifyItemInserted(new_position);
        recyclerView.scrollToPosition(new_position);

        SendToServer();

*/
    }


   // THIS CONVERT IMAGE FROM BYTE ARRAY TO BITMAP , TO SEND IT TO MESSAGE ADAPTER
   static Bitmap send_img_to_adapter ()
   {
              if (byte_array_image != null)
               bitmap = BitmapFactory.decodeByteArray(byte_array_image,0,byte_array_image.length);

       return bitmap;
   }

}
