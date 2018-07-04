package com.example.pcc.chatting;

import java.io.Serializable;


public class Messages implements Serializable {

    //TODO cahnge serialVersion
    private static final long serialVersionUID = 1L;
    public final static String TEXT_MSG="TEXT_MSG";
    public final static String IMAGE_MSG="IMAGE_MSG";
    private String message;
    private byte [] image;
    private String message_type;
    private int message_shape;
    private Messages messages;

    Messages (String message , String message_type , int message_shape)
    {
        this.message=message;
        this.message_type=message_type;
        this.message_shape=message_shape;
    }

    Messages (byte[] image , String message_type , int message_shape)
    {
        this.image=image;
        this.message_type=message_type;
        this.message_shape=message_shape;
    }

    Messages (Messages messages)
    {
        this.messages=messages;

    }





    public String getMessage() {
        return message ;
    }

    public void setMessage(String message1) {
        message = message1;
    }

    public String getMessage_type() {
        return message_type ;
    }

    public void setMessage_type(String message1_Type) {
        message_type = message1_Type;
    }

    public int getMessage_shape() {
        return message_shape ;
    }


}
