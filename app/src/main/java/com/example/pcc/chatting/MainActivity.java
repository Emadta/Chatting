package com.example.pcc.chatting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private User_Adapter user_adapter;
    private RecyclerView recyclerView;
    ArrayList<User> listFriends =new ArrayList();
    static String userName;
    Intent intent;
    String FileListFriends = "ListUsers.txt";
    boolean check;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("4Chat");

        /*File dir = getFilesDir();
        File file = new File(dir, "ListUsers.txt");
        boolean deleted = file.delete();
        File f = new File(dir, "UserName.txt");
        boolean deleted1 = f.delete();*/

        // username (From)
        init_username();
        init_recycleview();
        CheckLoadFriends();
        PutSearchResultRecycler();

    }

    void init_recycleview ()
    {
        // user_adapter to bind info with recycleview by put new msgList in listFriends and notify it by recycleview
        user_adapter=new User_Adapter(listFriends);

        recyclerView=(RecyclerView) findViewById(R.id.recycler_view_friends);

        // to scroll vertically
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        // put layout above to recycle in this activity
        recyclerView.setLayoutManager(linearLayoutManager);

        // for scrolling
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(user_adapter);
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

        if (new_user.getUserName() !=null && LoadListFriendsToVerfiy(listFriends,new_user.getUserName())) {
            listFriends.add(new_user);
            StoreListFriends(listFriends);
        }
        //// TODO: here to make the sender is the top of recyclerview //ArraylistObj.remove(object); && ArrayListObj.add(position, Object);//
        //// TODO: and notifyitemremoved then notifyiteminserted
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
    boolean LoadListFriendsToVerfiy (ArrayList<User> arrayList , String Name)
    {
        for (User user : arrayList)
        {
         if (user.getUserName().equals(Name))
             return false;
        }
        return true;
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

}
