package com.example.pcc.chatting;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Image_Activity extends AppCompatActivity {

    private ImageView display_img;
    private Button btnsend_img;
    Bitmap image;
    Bitmap video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_);

        display_img = (ImageView) findViewById(R.id.imageview_picked);
        btnsend_img = (Button) findViewById(R.id.btn_send_img);

        btnsend_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_image_video_to_message_activity();
            }
        });


    }


    void send_image_video_to_message_activity() {

        // if picked an image send it to msg_activity to put it in list_message ,this image is bitmap
        if (image != null) {
            // transform bitmap to bytearray to send it to msg_activity
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byte_array = stream.toByteArray();


            Intent intent = new Intent(Image_Activity.this, Messages_Activity.class);
            Bundle bundle = new Bundle();
            bundle.putByteArray("image", byte_array);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        // if picked a video send it to msg_activity to put it in list_message ,this video is bitmap
        if (video != null) {


        }


    }


    // this is a result of pick (image or video) from message_activity intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { // if we are here all things is good

            // THIS IMAGE

            if (requestCode == Messages_Activity.IMG_PICK) // this to recognize (startactivityforresult)
            { // if we are here we back from gallery

                // we got image as Uri
                Uri image_uri = data.getData();

                // to read image we need inputstream
                try {
                    InputStream inputStream = getContentResolver().openInputStream(image_uri);

                    // we got image from inputstream by bitmap that decode stream
                    image = BitmapFactory.decodeStream(inputStream);
                    //by bitmap we got image now

                    //to show image we need imageview (BY setimagebitmap)
                    display_img.setImageBitmap(image);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


            // THIS VIDEO

            if (requestCode == Messages_Activity.VIDEO_PICK) // this to recognize (startactivityforresult)
            { // if we are here we back from gallery

                // we got video as Uri
                Uri video_uri = data.getData();

                // got realpath from method getrealpathfromuri
                String video_selected_path = getRealPathFromURI(this,video_uri);

                // send this string above (vidio_selected_path) to Message_activity then to server



                                 // to display video , put this video_selected_path on videoview here


            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

                       // to get real path for video
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
}
