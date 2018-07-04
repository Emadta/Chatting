package com.example.pcc.chatting;


import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.Myviewholder> {
   private ArrayList<Message> list;


    Message_Adapter (ArrayList<Message> list)
    {
        this.list=list;
    }

    @Override
    public Message_Adapter.Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_message,parent, false);
        return new Myviewholder(v);
    }

                       // TO BIND INFO WITH INTERFACE

    @Override
    public void onBindViewHolder(Message_Adapter.Myviewholder holder, int position) {
        Message message=list.get(position);

                                  // for text message

                   // if msg = text and sender is (from)
                   if (MainActivity.userName.equals(message.getFrom()) && Message.TEXT_MSG.equals(message.getType())) {
                       holder.linearLayout_left.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_left.setText(message.getMsg());

                       holder.linearLayout_right.setVisibility(LinearLayout.GONE);

                       holder.img_left.setVisibility(ImageView.GONE);

                   }
                   // if msg= text and is sender is (to)
                   else if (!MainActivity.userName.equals(message.getFrom()) && Message.TEXT_MSG.equals(message.getType())) {
                       holder.linearLayout_right.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_right.setText(message.getMsg());

                       holder.linearLayout_left.setVisibility(LinearLayout.GONE);

                       holder.img_right.setVisibility(ImageView.GONE);

                   }
                   // now if message is Image

                   // if msg = img and sender is (from)
                   else if (MainActivity.userName.equals(message.getFrom()) && Message.IMAGE_MSG.equals(message.getType())) {
                       holder.linearLayout_left.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_left.setVisibility(TextView.GONE);

                       holder.linearLayout_right.setVisibility(LinearLayout.GONE);

                       holder.img_left.setVisibility(ImageView.VISIBLE);

                       // This recieve image bitmap from message_activity to put it in interface
                       holder.img_left.setImageBitmap(Messages_Activity.send_img_to_adapter());

                   }
                   // if msg= img and sender is (to)
                   else if (!MainActivity.userName.equals(message.getTo()) && Message.IMAGE_MSG.equals(message.getType())) {
                       holder.linearLayout_right.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_right.setVisibility(TextView.GONE);

                       holder.linearLayout_left.setVisibility(LinearLayout.GONE);

                       holder.img_right.setVisibility(ImageView.VISIBLE);

                       holder.img_right.setImageBitmap(Messages_Activity.send_img_to_adapter());

                   }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


                    // TO GET INFO FROM XML

    class Myviewholder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout_left;
        LinearLayout linearLayout_right;

        TextView textView_left;
        TextView textView_right;

        ImageView img_left;
        ImageView img_right;

        Myviewholder(View itemView) {
            super(itemView);

            linearLayout_left=(LinearLayout)itemView.findViewById(R.id.chat_left_msg_layout);
            linearLayout_right=(LinearLayout)itemView.findViewById(R.id.chat_right_msg_layout);

            textView_left=(TextView)itemView.findViewById(R.id.chat_left_msg_text_view);
            textView_right=(TextView)itemView.findViewById(R.id.chat_right_msg_text_view);
            
            
                          // // FIXME: 04/07/2018 
            img_left = (ImageView) itemView.findViewById(R.id.chat_left_img_view);
            img_right = (ImageView) itemView.findViewById(R.id.chat_right_img_view);
        }
    }

    void get_bytearray_image ()
    {


    }
}
