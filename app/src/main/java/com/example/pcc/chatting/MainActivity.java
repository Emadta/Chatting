package com.example.pcc.chatting;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Semaphore;
import static com.example.pcc.chatting.Begin_Activity.ois;
import static com.example.pcc.chatting.Begin_Activity.oos;
import static com.example.pcc.chatting.Begin_Activity.s;


public class MainActivity extends AppCompatActivity implements ItemClickListener {
    private Toolbar toolbar;
    User_Adapter user_adapter;
    private RecyclerView recyclerView;
    static ArrayList<User> listFriends = new ArrayList<>();
    static String userName, toName;
    Intent intent;
    String FileListFriends= "ListFriends.txt";
    boolean check;
    static Message MSG;
    static Semaphore messageActivity = new Semaphore(0);
    static Semaphore mainActivity = new Semaphore(0);
    static Semaphore searchActivity = new Semaphore(0);
    static boolean ThreadCreated = false;
    static boolean signedIn = true;
    static boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("4Chat");

        userName = loadUserName();
        toName = null;

        if (!ThreadCreated) {
            ReceiveMessages();
            ThreadCreated = true;
        }

        initialize_recyclerview();

        if(!loaded) {
            CheckLoadFriends();
            loaded=true;
        }
        PutSearchResultRecycler();

    }

    String loadUserName() {
        String username = null;
        FileInputStream fis;
        try {
            fis = openFileInput("UserName.txt");
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            username = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return username;
    }

    void initialize_recyclerview() {

        user_adapter = new User_Adapter(listFriends, R.layout.list_friends, this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_friends);

        // to scroll vertically
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        // put layout above to recycle in this activity
        recyclerView.setLayoutManager(linearLayoutManager);

        // for scrolling
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(user_adapter);
        user_adapter.setClickListener(this);
    }

    void CheckLoadFriends() {
        check = fileExists(this, FileListFriends);
        if (check)
            LoadFriendsFromFile();
    }

    void LoadFriendsFromFile() {
        ArrayList<User> List = null;
        FileInputStream fis;
        try {
            fis = openFileInput(FileListFriends);
            ObjectInputStream Ois = new ObjectInputStream(fis);
            List = (ArrayList<User>) Ois.readObject();
            Ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (User user : List) {
            listFriends.add(user);
        }
    }

    void PutSearchResultRecycler() {
        // TO PUT USER IN RECYCLERVIEW THAT YOU CLICKED ON HIS CARD VIEW (GET OBJECT) FROM SEARCH_ACTIVITY
        Intent intent = getIntent();
        User new_user = new User(intent.getStringExtra("userName"));

        if (new_user.getUserName() != null && LoadListFriendsToVerfiy(new_user.getUserName(), listFriends)) {
            listFriends.add(new_user);
            StoreListFriends(listFriends);
        }
    }

    void StoreListFriends(ArrayList<User> arrayList) {

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FileListFriends, MODE_PRIVATE);
            ObjectOutputStream Oos = new ObjectOutputStream(fos);
            Oos.writeObject(arrayList);
            Oos.flush();
            Oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // dont repeat user
    boolean LoadListFriendsToVerfiy(String Name, ArrayList<User> List) {
        Boolean check = true;
        for (User user : List) {
            if (user.getUserName().equals(Name))
                check = false;
        }
        return check;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        signedIn = false;
        ThreadCreated =false;
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            intent = new Intent(this, Search_Activity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.log_out) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        oos.writeObject(new Message("", "", "", "", "sign_out"));
                        oos.flush();
                    } catch (IOException ignored) {
                    }
                }
            }).start();
        }

        if (item.getItemId() == R.id.deleteAccount) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        oos.writeObject(new Message("", "", "", "", "delete_account"));
                        oos.flush();
                    } catch (IOException ignored) {
                    }
                }
            }).start();
        }
        return super.onOptionsItemSelected(item);
    }

    boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view, int position) {
        final User user = listFriends.get(position);
        toName = user.getUserName();
        Intent intent = new Intent(MainActivity.this, Messages_Activity.class);
        startActivity(intent);
    }

    void ReceiveMessages() {
         Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean check, check1, checkFile;
                while (signedIn) {
                    try {
                        MSG = (Message) ois.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (MSG.getKind().equals("search_request")) {
                        searchActivity.release();
                        try {
                            mainActivity.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (MSG.getKind().equals("private_chat")){
                        if (!MSG.getFrom().equals(toName)) {
                            ArrayList<Message> listMessages = new ArrayList<>();
                            String fileMessages = userName + "," + MSG.getFrom() + ".txt";
                            checkFile = fileExists(getApplicationContext(), FileListFriends);
                            Log.d("dev1","the friend file founded     "+checkFile+"");
                            if (checkFile) {
                                check1 = verifySender(MSG);
                                Log.d("dev1","verify sender  "+check1);
                                if (!check1) {
                                    addNewFriend(MSG);
                                }
                            } else {
                                addNewFriend(MSG);
                            }

                            switch (MSG.getType()) {
                                case Message.IMAGE_MSG:
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(MSG.getBytes(), 0, MSG.getBytes().length);
                                    String MsgBitmapString = StoreImageBitmap(bitmap);
                                    MSG = new Message(MsgBitmapString, MSG.getFrom(), MSG.getTo(), MSG.getType(), MSG.getKind());
                                    break;

                                case Message.VIDEO_MSG:
                                    File file = convertBytesToFile(MSG.getBytes());
                                    String MsgVideo = StoreVideo(file.getAbsolutePath());
                                    MSG = new Message(MsgVideo, MSG.getFrom(), MSG.getTo(), MSG.getType(), MSG.getKind());
                                    break;
                            }

                            check = fileExists(getApplicationContext(),fileMessages);
                            if (check)
                                listMessages = LoadMessages(fileMessages);

                            listMessages.add(MSG);
                            StoreMessages(listMessages,fileMessages);

                        } else {
                            messageActivity.release();
                            try {
                                mainActivity.acquire();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if (MSG.getKind().equals("sign_out")) {
                        signedIn = false;
                        ThreadCreated = false;
                        try {
                            oos.close();
                            ois.close();
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(MainActivity.this, Begin_Activity.class));
                        finish();
                    }
                    else if (MSG.getKind().equals("delete_account")) {
                        signedIn = false;
                        ThreadCreated = false;
                        try {
                            oos.close();
                            ois.close();
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(MainActivity.this, Begin_Activity.class));
                        finish();
                    }
                }
            }
        });
        thread.start();
    }

    ArrayList<Message> LoadMessages(String File) {
        Log.d("USU","name of file :" + File);
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
        return List;
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

    boolean verifySender(Message msg) {
        boolean check = false;

        for (User user : listFriends) {
            Log.d("dev1",user.getUserName());
            if (user.getUserName().equals(msg.getFrom())) {
                check = true;
                break;
            }
        }
        return check;
    }

    void addNewFriend(Message msg) {
        User newFriend = new User(msg.getFrom());
        listFriends.add(newFriend);
        for(User user : listFriends){
            Log.d("dev1",user.getUserName());
        }
        int new_position = (listFriends.size() - 1);
        user_adapter.notifyItemInserted(new_position);
        StoreListFriends(listFriends);
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



}