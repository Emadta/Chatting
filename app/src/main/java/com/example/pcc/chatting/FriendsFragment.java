package com.example.pcc.chatting;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    User_Adapter user_adapter;
    RecyclerView recyclerView;
    ArrayList<User> list=new ArrayList();

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*User user=new User("Server","emad","12345");
        listFriends.add(user);*/

        // TO GET USER THAT YOU CLICKED ON HIS CARD VIEW (GET OBJECT) FROM SEARCH_ACTIVITY
        //User new_user = Search_Activity.send_to_friends_list();

        // TO PUT NEW USER IN LIST THAT IS IN RECYCLEVIEW
        //listFriends.add(new_user);

        // user_adapter that contents recycleview methods and get the (listFriends) above
        user_adapter=new User_Adapter(list);
        recyclerView=new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(user_adapter);

        return recyclerView;
    }

}
