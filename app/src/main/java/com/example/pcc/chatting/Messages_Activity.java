package com.example.pcc.chatting;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
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
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static com.example.pcc.chatting.Begin_Activity.ois;
import static com.example.pcc.chatting.Begin_Activity.oos;

public class Messages_Activity extends AppCompatActivity {

    Toolbar toolbarMessages;
    static final int IMG_PICK = 10;
    static final int VIDEO_PICK = 20;
    static final int AUDIO_PICK = 30;
    ArrayList<Message> listMessages;
    private RecyclerView recyclerView;
    Message_Adapter message_adapter;
    private Button btn_send;
    private Button btn_pick_img;
    private EditText editText;
    Message msgServer;
    String To;
    String MsgVideo;
    String video_selected_path,audio_selected_path,MsgBitmapString;
    Intent intent;
    String FileMessages;
    boolean check;
    byte[] byteArray;
    long ImageId,VideoId,AudioId;
    TextView textView;
    private final Object lock1 = new Object();
    Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_);
        toolbarMessages = (Toolbar) findViewById(R.id.toolBarMessages);
        setSupportActionBar(toolbarMessages);
        textView = (TextView) findViewById(R.id.textaa);


        init_recycleview();
        Get_TO_fromAdapter();
        FileMessages = MainActivity.userName + "," + To + ".txt";
        CheckFileLoadMessages();
        SendButton();
        receive_messages();
        PickImageButton();
    }

    void init_recycleview() {
        // this listFriends to add new msgList to recycleview
        listMessages = new ArrayList<>();

        btn_send = (Button) findViewById(R.id.btn_send_msg);

        // this button to go to gallery and got image
        btn_pick_img = (Button) findViewById(R.id.btn_pick_img);

        // this to write msgList
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

        // finally, put message_adapter to recycleview , message_adapter contents listFriends
        recyclerView.setAdapter(message_adapter);
    }

    void Get_TO_fromAdapter() {
        intent = getIntent();
        To = intent.getStringExtra("To");
        getSupportActionBar().setTitle(To);
    }

    void CheckFileLoadMessages() {
        check = fileExists(getApplicationContext(), FileMessages);
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

                    //  to determine this msgList for who ,receive String username (TO)

                    if (intent != null) {
                        DetermineTypeOfMessage("TEXT");
                    }
                }
            }
        });
    }

    void receive_messages() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        msgServer = (Message) ois.readObject();
                    } catch (EOFException e) {
                        break;
                    } catch (IOException | ClassNotFoundException ignored) {
                    }

                    if (msgServer.getType().equals("TEXT_MSG")) {
                        AddMessageToRecyclerViewReceive();
                    }

                    else if (msgServer.getType().equals("IMAGE_MSG")) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(msgServer.getBytes(), 0, msgServer.getBytes().length);
                        MsgBitmapString =StoreImageBitmap (bitmap);
                        msgServer = new Message(MsgBitmapString,msgServer.getFrom(),msgServer.getTo(),msgServer.getType(),msgServer.getKind());
                        AddMessageToRecyclerViewReceive();
                    }

                    else if (msgServer.getType().equals("VIDEO_MSG")) {

                        MsgVideo = StoreVideo(msgServer.getFile().getAbsolutePath());
                        msgServer = new Message(MsgVideo,msgServer.getFrom(),msgServer.getTo(),msgServer.getType(),msgServer.getKind());
                        AddMessageToRecyclerViewReceive();
                    }
                }

            }
        });
        thread.start();
    }

    void DetermineTypeOfMessage(String type) {
        if (type.equals("TEXT")) {

            AddMessageToRecyclerView(editText.getText().toString().trim(), Message.TEXT_MSG);
            editText.setText("");

        } else if (type.equals("IMAGE")) {

            MsgBitmapString = StoreImageBitmap(image);
            AddMessageToRecyclerView(MsgBitmapString, Message.IMAGE_MSG);

        } else if (type.equals("VIDEO")) {

            MsgVideo = StoreVideo(video_selected_path);
            AddMessageToRecyclerView(MsgVideo, Message.VIDEO_MSG);
            textView.setText(MsgVideo);

        } else if (type.equals("AUDIO")) {

            //String MsgAudio = StoreAudio(audio_selected_path);
            //AddMessageToRecyclerView(MsgAudio, Message.AUDIO_MSG);
            textView.setText(audio_selected_path);
        }

        SendToServer();
    }

    void AddMessageToRecyclerView(String msg, String type) {
        synchronized (lock1) {

            msgServer = new Message(msg, MainActivity.userName, To, type, "private_chat");
            listMessages.add(msgServer);
            int new_position = (listMessages.size() - 1);
            message_adapter.notifyItemInserted(new_position);
            recyclerView.scrollToPosition(new_position);
            StoreMessages(listMessages, FileMessages);

            if(type.equals(Message.IMAGE_MSG)) {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
                byteArray = bytearrayoutputstream.toByteArray();
                image.recycle();
                msgServer = new Message(byteArray, MainActivity.userName, To, type, "private_chat");
            }
            else if (type.equals(Message.VIDEO_MSG))
            {
                File file = new File(MsgVideo);
                msgServer = new Message(file,MainActivity.userName, To, type, "private_chat");
            }
        }
    }

    void AddMessageToRecyclerViewReceive() {
        synchronized (lock1) {
            listMessages.add(msgServer);
            int new_position = (listMessages.size() - 1);
            message_adapter.notifyItemInserted(new_position);
            //recyclerView.scrollToPosition(new_position);
            StoreMessages(listMessages, FileMessages);
        }
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
                    oos.writeObject(msgServer);
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    boolean fileExists(Context context, String File) {
        File file = context.getFileStreamPath(File);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

   /* void PickImageButton() {
        btn_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_image();
            }
        });
    }*/

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

    String StoreImageBitmap(Bitmap bitmap) {

        ContextWrapper cw = new ContextWrapper(getApplicationContext()); // path to /data/data/yourapp/app_data/imageDir

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE); // Create imageDir

        CheckMediaId("ImageId.txt");

        File mypath = new File(directory, "IMG" + Long.toString(++ImageId) + ".jpg");
        StoreMediaId(ImageId,"ImageId.txt");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mypath.getAbsolutePath();
    }

    void StoreMediaId(long id , String fildId) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fildId, MODE_PRIVATE);
            fos.write(Long.toString(id).getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void CheckMediaId(String fileId) {
        boolean x = fileExists(this, fileId);
        if (x)
            ImageId = LoadMediaId(fileId);
    }

    long LoadMediaId(String fileId) {
        long l = 0;
        FileInputStream fis = null;
        try {
            fis = openFileInput(fileId);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            String idString = new String(buffer);
            l = Long.parseLong(idString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }

    void PickImageButton() {
        btn_pick_img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                Boolean permission = isStoragePermissionGranted();
                if (permission)
                pick_video();
            }
        });
    }  // // TODO

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

    void pick_audio() {    // TO ENTER TO INTENT THAT CAN PICK FROM IT
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Audio "), AUDIO_PICK);
    }

    void PickAudioButton() {
        btn_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_audio();
            }
        });
    }  // // TODO

    String StoreVideo(String path) {

        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir("vidDir", Context.MODE_PRIVATE);

        CheckMediaId("VideoId.txt");
        File mypath = new File(directory, "Video" + Long.toString(++VideoId) + ".mp4");
        StoreMediaId(VideoId,"VideoId.txt");

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mypath.getAbsolutePath();
    }

    String StoreAudio(String path) {

        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir("AudioDir", Context.MODE_PRIVATE);

        CheckMediaId("AudioId.txt");
        File mypath = new File(directory, "Audio" + Long.toString(++AudioId) + ".mp3");
        StoreMediaId(AudioId,"AudioId.txt");

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mypath.getAbsolutePath();
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

            if (requestCode == VIDEO_PICK) // this to recognize (startactivityforresult)
            { // if we are here we back from gallery

                // we got video as Uri
                Uri  video_uri = data.getData();

                // got realpath from method getrealpathfromuri
                video_selected_path = getRealPathFromURI(this, video_uri);

                DetermineTypeOfMessage("VIDEO");

            }
            if (requestCode == AUDIO_PICK) // this to recognize (startactivityforresult)
            { // if we are here we back from gallery

                Uri audio_uri = data.getData();

                audio_selected_path = _getRealPathFromURI(this,audio_uri);

                DetermineTypeOfMessage("AUDIO");

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

    private String _getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
        else { //permission is automatically granted on sdk<23 upon installation
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

}
