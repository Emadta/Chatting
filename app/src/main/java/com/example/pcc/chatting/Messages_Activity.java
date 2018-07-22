package com.example.pcc.chatting;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import static com.example.pcc.chatting.Begin_Activity.oos;
import static com.example.pcc.chatting.MainActivity.mainActivity;
import static com.example.pcc.chatting.MainActivity.messageActivity;
import static com.example.pcc.chatting.MainActivity.toName;


public class Messages_Activity extends AppCompatActivity {

    private static final int IMG_PICK = 10;
    private static final int VIDEO_PICK = 20;
    private ArrayList<Message> listMessages;
    private RecyclerView recyclerView;
    private Message_Adapter message_adapter;
    private Button btn_send,btn_pick_img,btn_pick_vid;
    private EditText editText;
    private Message MSG;
    private String MsgVideo,video_selected_path,MsgBitmapString,FileMessages;
    private Bitmap image;
    volatile boolean inMessageActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_);
        Toolbar toolbarMessages = (Toolbar) findViewById(R.id.toolBarMessages);
        setSupportActionBar(toolbarMessages);
        getSupportActionBar().setTitle(MainActivity.toName);
        inMessageActivity=true;

        initializeRecycler();
        FileMessages = MainActivity.userName + "," + MainActivity.toName + ".txt";
        Toast.makeText(this,FileMessages,Toast.LENGTH_LONG).show();
        CheckFileLoadMessages();
        SendButton();
        receive_messages();
        PickImageButton();
        PickVideoButton();
    }

    void initializeRecycler() {

        listMessages = new ArrayList<>();

        btn_send = (Button) findViewById(R.id.btn_send_msg);

        btn_pick_img = (Button) findViewById(R.id.btn_pick_img);

        btn_pick_vid = (Button) findViewById(R.id.btn_pick_vid);

        editText = (EditText) findViewById(R.id.send_msg);

        // user_adapter to bind info with recycleview by put new msgList in listFriends and notify it by recycleview
        message_adapter = new Message_Adapter(this, listMessages);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_message);

        // to scroll vertically
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        // put layout above to recycle in this activity
        recyclerView.setLayoutManager(linearLayoutManager);

        // for scrolling
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(message_adapter);
    }

    private void CheckFileLoadMessages() {
        boolean check = fileExists(FileMessages);
        if (check)
            LoadMessages(FileMessages);
    }

    void LoadMessages(String File) {
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
        for (Message message : List) {
            listMessages.add(message);
        }

    }

    void SendButton() {
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(editText.getText().toString())) {

                        DetermineTypeOfMessage("TEXT");
                }
            }
        });
    }

    private void receive_messages() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (inMessageActivity) {
                    try {
                        messageActivity.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!inMessageActivity)
                        break;
                    try {
                        MSG = MainActivity.MSG;

                        switch (MSG.getType()) {
                            case Message.TEXT_MSG:
                                AddMessageToRecyclerViewReceive();
                                break;

                            case Message.IMAGE_MSG:
                                Bitmap bitmap = BitmapFactory.decodeByteArray(MSG.getBytes(), 0, MSG.getBytes().length);
                                MsgBitmapString = StoreImageBitmap(bitmap);
                                MSG = new Message(MsgBitmapString, MSG.getFrom(), MSG.getTo(), MSG.getType(), MSG.getKind());
                                AddMessageToRecyclerViewReceive();
                                break;

                            case Message.VIDEO_MSG:
                                File file = convertBytesToFile(MSG.getBytes());
                                MsgVideo = StoreVideo(file.getAbsolutePath());
                                MSG = new Message(MsgVideo, MSG.getFrom(), MSG.getTo(), MSG.getType(), MSG.getKind());
                                AddMessageToRecyclerViewReceive();
                                break;
                        }
                    }finally {
                        mainActivity.release();
                    }

                }
            }
        });
        thread.start();
    }

    void DetermineTypeOfMessage(String type) {
        switch (type){
            case "TEXT" :
                MSG = new Message(editText.getText().toString().trim(), MainActivity.userName, MainActivity.toName, Message.TEXT_MSG, "private_chat");
                AddMessageToRecyclerView(MSG);
                editText.setText("");
                break;

            case "IMAGE" :
                MsgBitmapString = StoreImageBitmap(image);
                MSG = new Message(MsgBitmapString, MainActivity.userName, MainActivity.toName, Message.IMAGE_MSG, "private_chat");
                AddMessageToRecyclerView(MSG);
                break;

            case "VIDEO" :
                MsgVideo = StoreVideo(video_selected_path);
                MSG = new Message(MsgVideo, MainActivity.userName, MainActivity.toName, Message.VIDEO_MSG, "private_chat");
                AddMessageToRecyclerView(MSG);
                break;


        }
    }

    void AddMessageToRecyclerView(Message Msg) {

            listMessages.add(Msg);
            int new_position = (listMessages.size() - 1);
            message_adapter.notifyItemInserted(new_position);
            recyclerView.scrollToPosition(new_position);
            StoreMessages(listMessages, FileMessages);
            prepareToSend(Msg);
    }

    void prepareToSend(Message Msg) {

        switch (Msg.getType())
        {
            case Message.TEXT_MSG :
                MSG = new Message(Msg.getMsg(), MainActivity.userName, MainActivity.toName, Msg.getType(), "private_chat");
                break;

            case Message.IMAGE_MSG :
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
                byte[] byteArray = bytearrayoutputstream.toByteArray();
                MSG = new Message(byteArray, MainActivity.userName, MainActivity.toName, Msg.getType(), "private_chat");
                break;

            case Message.VIDEO_MSG :
                byteArray = ConvertVideoToByteArray(MsgVideo);
                MSG = new Message(byteArray,MainActivity.userName, MainActivity.toName, Msg.getType(), "private_chat");
                break;
        }
        SendToServer();

    }

    void AddMessageToRecyclerViewReceive() {
            listMessages.add(MSG);
            int new_position = (listMessages.size() - 1);
            message_adapter.notifyItemInserted(new_position);
            StoreMessages(listMessages, FileMessages);
    }

    void StoreMessages(ArrayList<Message> arrayList, String File) {
        FileOutputStream fos;
        try {
            fos = openFileOutput(File, MODE_PRIVATE);
            ObjectOutputStream Oos = new ObjectOutputStream(fos);
            Oos.writeObject(arrayList);
            Oos.flush();
            Oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void SendToServer() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    oos.writeObject(MSG);
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    boolean fileExists( String File) {
        File file = getFileStreamPath(File);
        return !(file == null || !file.exists());
    }

    void PickImageButton() {
        btn_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_image();
            }
        });
    }

    void pick_image() {    // TO ENTER TO INTENT THAT CAN PICK FROM IT
        Intent intent_pick_image = new Intent(Intent.ACTION_PICK);

        // WHERE WE CAN FIND DATA THAT WE NEED
        File Directory_picture = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); // File : because this environement return file (Environment.java)
        String path_picture = Directory_picture.getPath();

        // TRANSFORM path_picture TO URI
        Uri data = Uri.parse(path_picture);

        // put only images in the intent with URI and images from all types
        intent_pick_image.setDataAndType(data, "image/*");

        startActivityForResult(intent_pick_image, IMG_PICK);

    }

    @TargetApi(Build.VERSION_CODES.M)
    void PickVideoButton (){
        btn_pick_vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean permission = isStoragePermissionGranted();
                if (permission)
                    pick_video();
            }
        });
    }

    void pick_video() {    // TO ENTER TO INTENT THAT CAN PICK FROM IT
        Intent intent_pick_Video = new Intent(Intent.ACTION_PICK);

        // WHERE WE CAN FIND DATA THAT WE NEED
        File Directory_Movie = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES); // File : because this environement return file (Environment.java)
        String path_Video = Directory_Movie.getPath();

        // TRANSFORM path_picture TO URI
        Uri data = Uri.parse(path_Video);

        // put only images in the intent with URI and images from all types
        intent_pick_Video.setDataAndType(data, "video/*");

        startActivityForResult(intent_pick_Video, VIDEO_PICK);

    }

    String StoreImageBitmap(Bitmap bitmap) {

        ContextWrapper cw = new ContextWrapper(getApplicationContext()); // path to /data/data/yourapp/app_data/imageDir

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE); // Create imageDir

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = df.format(c.getTime());

        File mypath = new File(directory, "IMG" + formattedDate + ".jpg");


        FileOutputStream fos ;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mypath.getAbsolutePath();
    }

    String StoreVideo(String path) {

        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir("vidDir", Context.MODE_PRIVATE);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = df.format(c.getTime());

        File mypath = new File(directory, "Video" + formattedDate + ".mp4");


        try {
            FileOutputStream newFile = new FileOutputStream(mypath);
            //path 0 = current path of the video
            FileInputStream oldFile = new FileInputStream(path);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = oldFile.read(buf)) > 0) {
                newFile.write(buf, 0, len);
            }
            newFile.flush();
            newFile.close();
            oldFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mypath.getAbsolutePath();
    }

    byte [] ConvertVideoToByteArray (String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis ;
        try {
            fis = new FileInputStream(new File(path));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    File convertBytesToFile(byte[] bytearray) {
         File outputFile=null;
        try {

            outputFile = File.createTempFile("file", "mp4", getCacheDir());
            outputFile.deleteOnExit();
            FileOutputStream fileoutputstream = new FileOutputStream(outputFile);
            fileoutputstream.write(bytearray);
            fileoutputstream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return outputFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { // if we are here all things is good

            // THIS IMAGE

            if (requestCode == IMG_PICK) { // if we are here we back from gallery

                // we got image as Uri
                Uri image_uri = data.getData();

                // to read image we need inputstream
                try {
                    InputStream inputStream = getContentResolver().openInputStream(image_uri);

                    // we got image from inputstream by bitmap that decode stream
                    image = BitmapFactory.decodeStream(inputStream);
                    //by bitmap we got image now

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    DetermineTypeOfMessage("IMAGE");


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            else if (requestCode == VIDEO_PICK) // this to recognize (startactivityforresult)
            { // if we are here we back from gallery

                // we got video as Uri
                Uri  video_uri = data.getData();

                // got realpath from method getrealpathfromuri
                video_selected_path = getRealPathFromURI(this, video_uri);

                DetermineTypeOfMessage("VIDEO");

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,"Permission is done",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Messages_Activity.this,MainActivity.class);
        inMessageActivity=false;
        messageActivity.release();
        startActivity(intent);
        finish();
    }



}
