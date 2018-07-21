package com.example.pcc.chatting;

import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import static com.example.pcc.chatting.Begin_Activity.ois;
import static com.example.pcc.chatting.Begin_Activity.oos;
import static com.example.pcc.chatting.Begin_Activity.s;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    private Toolbar toolbar;
    User_Adapter user_adapter;
    private RecyclerView recyclerView;
    ArrayList<User> listFriends;
    ArrayList<Message> listMessages;
    static String userName = "", toName;
    Intent intent;
    String FileListFriends;
    boolean check;
    static Message MSG;
    static Semaphore messageActivity = new Semaphore(0);
    static Semaphore mainActivity = new Semaphore(0);
    static Semaphore searchActivity = new Semaphore(0);
    static boolean ThreadCreated = false;
    static boolean signedIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("4Chat");

        userName = loadUserName();
        toName = "";
        FileListFriends ="ListFriends.txt";

        if (!ThreadCreated) {
            ReceiveMessages();
            ThreadCreated = true;
        }
        initialize_recyclerview();
        CheckLoadFriends();
        PutSearchResultRecycler();

    }

    String loadUserName() {
        String username = null;
        FileInputStream fis;
        try {
            fis = openFileInput("UserName.txt");
            int size = fis.available();
            byte [] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            username = new String(buffer);
        } catch (IOException e ) {
            e.printStackTrace();
        }
        return username;
    }

    void initialize_recyclerview() {

        listFriends = new ArrayList<>();
        listMessages = new ArrayList<>();

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
        final Thread thread = new Thread(new Runnable() {
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
                    if (MSG.getKind().equals("private_chat"))
                        if (!MSG.getFrom().equals(toName)) {

                            checkFile = fileExists(getApplicationContext(), FileListFriends);
                            if (checkFile) {
                                check1 = verifySender(MSG);
                                if (!check1) {
                                    addNewFriend();
                                }
                            } else {
                                addNewFriend();
                            }

                            check = fileExists(getApplicationContext(), userName + "," + MSG.getFrom() + ".txt");
                            if (check)
                                listMessages = LoadMessages(userName + "," + MSG.getFrom() + ".txt");
                            listMessages.add(MSG);
                            StoreMessages(listMessages, userName + "," + MSG.getFrom() + ".txt");

                        } else {
                            messageActivity.release();
                            try {
                                mainActivity.acquire();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    if (MSG.getKind().equals("sign_out")) {
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
            if (user.getUserName().equals(msg.getFrom())) {
                check = true;
                break;
            }
        }
        return check;
    }

    void addNewFriend() {

        User newFriend = new User(MSG.getFrom());
        listFriends.add(newFriend);
        int new_position = (listFriends.size() - 1);
        user_adapter.notifyItemInserted(new_position);
        StoreListFriends(listFriends);
    }
}