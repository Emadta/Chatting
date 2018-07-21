package com.example.pcc.chatting;




import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.Myviewholder> {
   private ArrayList<Message> list;
    private Context mcontext;


    Message_Adapter (Context context, ArrayList<Message> list)
    {
        this.list=list;
        mcontext=context;
    }

    @Override
    public Message_Adapter.Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_message,parent, false);
        return new Myviewholder(v);
    }

                       // TO BIND INFO WITH INTERFACE

    @Override
    public void onBindViewHolder(final Message_Adapter.Myviewholder holder, int position) {
        final Message message=list.get(position);

                   // if MSG = text and sender is (from)
                   if (MainActivity.userName.equals(message.getFrom()) && Message.TEXT_MSG.equals(message.getType())) {
                       holder.linearLayout_left.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_left.setText(message.getMsg());

                       holder.linearLayout_right.setVisibility(LinearLayout.GONE);

                       holder.img_left.setVisibility(ImageView.GONE);

                       holder.vid_left.setVisibility(VideoView.GONE);


                   }
                   // if MSG= text and is sender is (to)
                   else if (!MainActivity.userName.equals(message.getFrom()) && Message.TEXT_MSG.equals(message.getType())) {

                       holder.linearLayout_right.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_right.setText(message.getMsg());

                       holder.linearLayout_left.setVisibility(LinearLayout.GONE);

                       holder.img_right.setVisibility(ImageView.GONE);

                       holder.vid_right.setVisibility(VideoView.GONE);

                   }


                   else if (MainActivity.userName.equals(message.getFrom()) && Message.IMAGE_MSG.equals(message.getType())) {

                       Bitmap bitmap = BitmapConvert (message.getMsg());

                       holder.linearLayout_left.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_left.setVisibility(TextView.GONE);

                       holder.linearLayout_right.setVisibility(LinearLayout.GONE);

                       holder.img_left.setVisibility(ImageView.VISIBLE);

                       holder.vid_left.setVisibility(VideoView.GONE);

                       holder.img_left.setImageBitmap(bitmap);

                   }

                   else if (!MainActivity.userName.equals(message.getFrom()) && Message.IMAGE_MSG.equals(message.getType())) {

                       Bitmap bitmap = BitmapConvert (message.getMsg());

                       holder.linearLayout_right.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_right.setVisibility(TextView.GONE);

                       holder.linearLayout_left.setVisibility(LinearLayout.GONE);

                       holder.img_right.setVisibility(ImageView.VISIBLE);

                       holder.vid_right.setVisibility(VideoView.GONE);

                       holder.img_right.setImageBitmap(bitmap);

                   }

                   else if (MainActivity.userName.equals(message.getFrom()) && Message.VIDEO_MSG.equals(message.getType())) {

                       holder.linearLayout_left.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_left.setVisibility(TextView.GONE);

                       holder.linearLayout_right.setVisibility(LinearLayout.GONE);

                       holder.img_left.setVisibility(ImageView.GONE);

                       holder.vid_left.setVisibility(VideoView.VISIBLE);

                       MediaController mediaController = new MediaController(this.mcontext);
                       holder.vid_left.setVideoPath(message.getMsg());
                       holder.vid_left.setMediaController(mediaController);
                               holder.vid_left.start();



                   }

                   else if (!MainActivity.userName.equals(message.getFrom()) && Message.VIDEO_MSG.equals(message.getType())) {

                       holder.linearLayout_right.setVisibility(LinearLayout.VISIBLE);

                       holder.textView_right.setVisibility(TextView.GONE);

                       holder.linearLayout_left.setVisibility(LinearLayout.GONE);

                       holder.img_right.setVisibility(ImageView.GONE);

                       holder.vid_right.setVisibility(VideoView.VISIBLE);

                       MediaController mediaController = new MediaController(this.mcontext);
                       holder.vid_right.setVideoPath(message.getMsg());
                       holder.vid_right.setMediaController(mediaController);
                               holder.vid_right.start();



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

        VideoView vid_left;
        VideoView vid_right;




        Myviewholder(View itemView) {
            super(itemView);

            linearLayout_left=(LinearLayout)itemView.findViewById(R.id.chat_left_msg_layout);
            linearLayout_right=(LinearLayout)itemView.findViewById(R.id.chat_right_msg_layout);

            textView_left=(TextView)itemView.findViewById(R.id.chat_left_msg_text_view);
            textView_right=(TextView)itemView.findViewById(R.id.chat_right_msg_text_view);

            img_left = (ImageView) itemView.findViewById(R.id.chat_left_img_view);
            img_right = (ImageView) itemView.findViewById(R.id.chat_right_img_view);

            vid_left = (VideoView) itemView.findViewById(R.id.left_video_view);
            vid_right = (VideoView) itemView.findViewById(R.id.right_video_view);

        }
    }

    Bitmap BitmapConvert (String path)
    {
        File fullpath = new File(path);
        String ParentPath = fullpath.getParent();
        String Imagename = fullpath.getName();
        Bitmap b = null;
        try {
            File f=new File(ParentPath, Imagename);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }


}
