package com.example.pcc.chatting;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


class User_Adapter extends RecyclerView.Adapter<User_Adapter.Myviewholder> {
    ArrayList<User> list;
    User_Adapter (ArrayList<User> list)
    {
        this.list=list;
    }


    @Override
    public Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_friends,parent, false);
        return new Myviewholder(v);
    }
             // 2   // TO BIND INFO FROM **MYVIEWHOLDER METHOD** WITH INTERFACE
    @Override
    public void onBindViewHolder(Myviewholder holder, int position) {
        final User user= list.get(position);
        holder.txtview.setText(user.getUserName());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(v.getContext(),Messages_Activity.class);
                //send with intent, username to message_activity ,to send it to server in (string to)
                intent.putExtra("To",user.getUserName());
                v.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

           // 1   // TO GET ELEMENTS FROM LAYOUT

    public class Myviewholder extends RecyclerView.ViewHolder {
    public TextView txtview;
    public LinearLayout linearLayout;


        public Myviewholder(View itemView) {
            super(itemView);
            txtview=(TextView) itemView.findViewById(R.id.display_name);
            linearLayout=(LinearLayout) itemView.findViewById(R.id.linear_layout);

        }
    }
}
