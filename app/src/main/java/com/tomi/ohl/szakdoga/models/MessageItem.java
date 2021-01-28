package com.tomi.ohl.szakdoga.models;

public class MessageItem {
    private String sender;
    private String content;
    private long date;

    public MessageItem() {
    }

    public MessageItem(String sender, String content, long date) {
        this.sender = sender;
        this.content = content;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public long getDate() {
        return date;
    }
}
