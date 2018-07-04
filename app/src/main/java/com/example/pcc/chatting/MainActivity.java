package com.example.pcc.chatting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private User_Adapter user_adapter;
    private RecyclerView recyclerView;
    ArrayList<User> listFriends =new ArrayList();
    static String userName;
    Intent intent;
    private  JSONArray jsonArray = new JSONArray();
    User ListFriends;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // to load username from File , to use it in other Activities
        init_username();

        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("4Chat");

        init_recycleview();

    }

    void init_recycleview ()
    {
        LoadListFriends();

        Intent intent = getIntent();
        User new_user = new User(intent.getStringExtra("userName"));
        if (new_user.getUserName() !=null && LoadListFriendsToVerfiy(new_user.getUserName())) {
            listFriends.add(new_user);
            StoreListFriends(new_user);
        }


        //// FIXME edit
        // TO PUT USER IN RECYCLERVIEW THAT YOU CLICKED ON HIS CARD VIEW (GET OBJECT) FROM SEARCH_ACTIVITY
        /*User new_user = Search_Activity.user_search_result;

        // TO PUT NEW USER IN LIST THAT IS IN RECYCLEVIEW
        if (new_user !=null) {
            listFriends.add(new_user);
            StoreListFriends(new_user);
        }*/

        //// TODO: here to make the sender is the top of recyclerview //ArraylistObj.remove(object); && ArrayListObj.add(position, Object);//
        //// TODO: and notifyitemremoved then notifyiteminserted

        // user_adapter to bind info with recycleview by put new message in listFriends and notify it by recycleview
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

           // username (From)
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

           // store listFriends friends
    void StoreListFriends (User user)
    {
            try {
                Map map = new LinkedHashMap(1);
                map.put("userName", user.getUserName());
                jsonArray.add(map);
                String json = jsonArray.toJSONString();
                StoreToFile(json);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    void StoreToFile (String json) throws IOException
    {
            FileOutputStream fos = openFileOutput("ListUsers.json",MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.flush();
            fos.close();
    }

           // load listFriends friends
    String LoadFriendsFromFile()
    { String user = "";
        FileInputStream fis = null;
        try {
            fis = openFileInput("ListUsers.json");
            int size = fis.available();
            byte [] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            user = new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
       return user;
    }

    void ConvertToJsonArray ()
    {
        JSONParser jsonParser = new JSONParser();
        try {
             jsonArray = (JSONArray) jsonParser.parse(LoadFriendsFromFile());
            }catch (ParseException e1) {
            e1.printStackTrace();
            }
    }

    void LoadListFriends ()
    {
        ConvertToJsonArray();
        Iterator<JSONObject> itr2 = jsonArray.iterator();
        JSONObject user1;
        while (itr2.hasNext()) {
            user1 = itr2.next();
            ListFriends = new User(user1.get("userName").toString());
            listFriends.add(ListFriends);
        }
    }

           // this to verfiy if user is already added to listFriends
    boolean LoadListFriendsToVerfiy (String user)
    {
        ConvertToJsonArray();
        Iterator<JSONObject> itr2 = jsonArray.iterator();
        JSONObject user1;
        while (itr2.hasNext()) {
            user1 = itr2.next();
            if (user1.get("userName").toString().equals(user))
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

}
