package com.example.pcc.chatting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    Message msgServer;
    String To;
    Message msgList;
    Intent intent;
    long lastId=0;
    String FileMessages;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_);
        toolbarMessages = (Toolbar) findViewById(R.id.toolBarMessages);
        setSupportActionBar(toolbarMessages);

        /*File dir = getFilesDir();
        File file = new File(dir,"mat,omar.txt");
        boolean deleted = file.delete();
        File file1 = new File(dir,"mat,ramy.txt");
        boolean deleted1 = file1.delete();
        File file2 = new File(dir,"mat,emad.txt");
        boolean deleted2 = file2.delete();*/

          init_recycleview ();
          Get_TO_fromAdapter();
          FileMessages = MainActivity.userName+","+To+".txt";
          CheckFileLoadMessages();
          SendButton();
          receive_messages ();


        btn_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_image();
            }
        });
        //// FIXME: 28/06/2018 //// FIXME: 28/06/2018 
       receive_image_picked ();

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

        // add this msgList object to listFriends and give it new position in listFriends and put it in interface
        //// FIXME:
       // msgList = new Message(byte_array_image,Messages.MSG_SENT,Messages.IMAGE_MSG);
        listFriends.add(msgList);
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








    void init_recycleview ()
    {
        // this listFriends to add new msgList to recycleview
        listMessages =new ArrayList<>();

        btn_send=(Button)findViewById(R.id.btn_send_msg);

        // this button to go to gallery and got image
        btn_pick_img = (Button) findViewById(R.id.btn_pick_img);

        // this to write msgList
        editText=(EditText)findViewById(R.id.send_msg);

        // user_adapter to bind info with recycleview by put new msgList in listFriends and notify it by recycleview
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

    void Get_TO_fromAdapter()
    {
        intent = getIntent();
        To = intent.getStringExtra("To");
        getSupportActionBar().setTitle(To);
    }

    void CheckFileLoadMessages()
    {
        check = fileExists(getApplicationContext(),FileMessages);
        if (check)
            LoadMessages(FileMessages);
    }

    void LoadMessages (String File)
    {
       ArrayList<Message> List = null;
       FileInputStream fis;
       try {
           fis = openFileInput(File);
           ObjectInputStream Ois = new ObjectInputStream(fis);
           List = (ArrayList<Message>) Ois.readObject();
           Ois.close();
       } catch (IOException | ClassNotFoundException e) {
           e.printStackTrace();
       }
       for (Message message : List)
       {
           listMessages.add(message);
       }

    }

    void SendButton ()
    {
         btn_send.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 if (!TextUtils.isEmpty(editText.getText().toString())) {

                     //  to determine this msgList for who ,receive String username (TO)

                     if (intent!=null)
                     {
                         AddMessageToRecyclerView(To);
                     }
                 }
             }
         });
    }

    void receive_messages ()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    try {
                        msgServer = (Message) ois.readObject();

                    } catch(EOFException e){
                        break;
                    }
                    catch (IOException | ClassNotFoundException ignored) {
                    }
                    if (check)
                        lastId = GetLastId(FileMessages);

                    msgList = new Message(msgServer.getMsg(), msgServer.getFrom(), msgServer.getTo(), msgServer.getType(), msgServer.getKind(),++lastId);
                    listMessages.add(msgList);
                    int new_position=( listMessages.size() - 1 );
                    message_adapter.notifyItemInserted(new_position);
                    //recyclerView.scrollToPosition(new_position);

                    StoreMessages (listMessages, FileMessages);
                }


            }
        });
        thread.start();
    }

    void AddMessageToRecyclerView(String To)
    {
        msgServer = new Message(editText.getText().toString().trim(),MainActivity.userName,To,Message.TEXT_MSG,"private_chat");
        if (check)
            lastId = GetLastId(FileMessages);
        msgList = new Message(editText.getText().toString().trim(),MainActivity.userName,To,Message.TEXT_MSG,"private_chat",++lastId);
        listMessages.add(msgList);
        int new_position=( listMessages.size() - 1 );
        message_adapter.notifyItemInserted(new_position);
        recyclerView.scrollToPosition(new_position);
        editText.setText("");
        StoreMessages (listMessages, FileMessages);

        SendToServer();
    }

    void StoreMessages (ArrayList<Message> arrayList , String File)
    {
        FileOutputStream fos;
        try {
            fos = openFileOutput(File,MODE_PRIVATE);
            ObjectOutputStream Oos = new ObjectOutputStream(fos);
            Oos.writeObject(arrayList);
            Oos.flush();
            Oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendToServer()
    {   Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {

            try {
                oos.writeObject(msgServer);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
        thread.start();
    }

    boolean fileExists(Context context, String File)
    {
        File file = context.getFileStreamPath(File);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    long GetLastId (String File)
    {
        ArrayList<Message> List = null;
        FileInputStream fis;
        try {
            fis = openFileInput(File);
            ObjectInputStream Ois = new ObjectInputStream(fis);
            List = (ArrayList<Message>) Ois.readObject();
            Ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
            Message message1= List.get(List.size()-1);
            return message1.getId();

    }


}
