package com.example.pcc.chatting;


import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;

import java.io.Serializable;
import java.net.ServerSocket;


    public class Message implements Serializable {
        public final static String TEXT_MSG="TEXT_MSG";
        public final static String IMAGE_MSG="IMAGE_MSG";
        public final static String VIDEO_MSG="VIDEO_MSG";
        public final static String AUDIO_MSG="AUDIO_MSG";
        private String msg;
        private String from;
        private String to;
        private long id;
        private String type;
        private String kind;
        private static final long serialVersionUID = 2L;
        private Uri uri;
        private byte [] bytes;
        private File file;


        public Message(String msg, String from, String to, String type, String kind) {
            this.msg = msg;
            this.from = from;
            this.to = to;
            this.type = type;
            this.kind = kind;
        }

        public Message(byte[] bytes, String from, String to, String type, String kind) {
            this.bytes=bytes;
            this.from = from;
            this.to = to;
            this.type = type;
            this.kind = kind;
        }

        public File getFile() {
            return file;
        }

        public Uri getUri() {
            return uri;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public Message(Uri msg, String from, String to, String type, String kind) {
            this.uri = msg;

            this.from = from;
            this.to = to;
            this.type = type;
            this.kind = kind;
        }

        public Message(String msg, String kind) {
            this.msg = msg;
            this.kind = kind;
        }

        public String getMsg() {
            return msg;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public long getId() {
            return id;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public String getKind() {
            return kind;
        }


    }


