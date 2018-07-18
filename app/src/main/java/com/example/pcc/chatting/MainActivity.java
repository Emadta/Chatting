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
import static com.example.pcc.chatting.Begin_Activity.ois;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    private Toolbar toolbar;
    User_Adapter user_adapter;
    private RecyclerView recyclerView;
    ArrayList<User> listFriends =new ArrayList();
    ArrayList<Message> list;
    static String userName,toName;
    Intent intent;
    String FileListFriends = "ListUsers.txt";
    boolean check;
    volatile boolean inMainActivity;
    Message MSG;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("4Chat");
        list =new ArrayList<>();
        inMainActivity =true;

        RecieveMessages();
        // username (From)
        init_username();
        init_recycleview();
        CheckLoadFriends();
        PutSearchResultRecycler();

    }

    void init_recycleview ()
    {
        // user_adapter to bind info with recycleview by put new msgList in listFriends and notify it by recycleview
        user_adapter=new User_Adapter(listFriends,R.layout.list_friends,this);

        recyclerView=(RecyclerView) findViewById(R.id.recycler_view_friends);

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

    void init_username ()
    {
        try {
            userName = Load_UserName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String Load_UserName () throws IOException
    {
        FileInputStream fis = openFileInput("UserName.txt");
         int size = fis.available();
         byte [] buffer = new byte[size];
         fis.read(buffer);
         fis.close();
         String user = new String(buffer);

        return user;
    }

     void CheckLoadFriends()
    {
        check=fileExists(this,FileListFriends);
        if (check)
            LoadFriendsFromFile();
    }

    void LoadFriendsFromFile()
    {
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
        for (User user : List)
        {
            listFriends.add(user);
        }
    }

    void PutSearchResultRecycler ()
    {
        // TO PUT USER IN RECYCLERVIEW THAT YOU CLICKED ON HIS CARD VIEW (GET OBJECT) FROM SEARCH_ACTIVITY
        Intent intent = getIntent();
        User new_user = new User(intent.getStringExtra("userName"));

        if (new_user.getUserName() !=null && LoadListFriendsToVerfiy(new_user.getUserName(),listFriends)) {
            listFriends.add(new_user);
            StoreListFriends(listFriends);
        }
    }

     void StoreListFriends (ArrayList<User> arrayList)
    {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FileListFriends,MODE_PRIVATE);
            ObjectOutputStream Oos = new ObjectOutputStream(fos);
            Oos.writeObject(arrayList);
            Oos.flush();
            Oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
                    // dont repeat user
    boolean LoadListFriendsToVerfiy (String Name,ArrayList<User> List)
    {  Boolean check=true;
        for (User user : List)
        {
         if (user.getUserName().equals(Name))
            check=false;
        }
        return check;
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getItemId()==R.id.action_search)
        {
            intent =new Intent(this,Search_Activity.class);
            startActivity(intent);
        }

        if (item.getItemId()==R.id.log_out)
        {
            intent =new Intent(this,Start_Activity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.set)
        {
            Bundle b=getIntent().getExtras();
            b.getString("user");
            intent = new Intent(this, Settings_Activity.class);
            intent.putExtras(b);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    boolean fileExists(Context context, String filename)
    {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view, int position) {
        final User user = listFriends.get(position);
        toName = user.getUserName();
        SwitchThread();
        Intent intent=new Intent(MainActivity.this,Messages_Activity.class);
        startActivity(intent);
    }

    private void SwitchThread() {
        if (inMainActivity)
            inMainActivity =false;

        Log.d("1MAIN","step3");
    }

    void RecieveMessages (){
         Log.d("1MAIN","step1");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (inMainActivity)
                    {
                        try {
                            MSG = (Message) ois.readObject();
                            Log.d("MAIN1ACTIV","step2");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        boolean check = fileExists(getApplicationContext(),userName + "," + MSG.getFrom() + ".txt");
                        if (check)
                            list = LoadMessages(userName + "," + MSG.getFrom() + ".txt");

                        list.add(MSG);
                        StoreMessages(list,userName + "," + MSG.getFrom() + ".txt");
                    }
                }
            });
            thread.start();
    }

    ArrayList<Message> LoadMessages (String File){
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


}
