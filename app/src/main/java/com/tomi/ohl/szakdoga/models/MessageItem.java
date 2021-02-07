package com.tomi.ohl.szakdoga.models;

public class MessageItem {
    private String senderUid;
    private String sender;
    private String content;
    private long date;

    public MessageItem() {
    }

    public MessageItem(String senderUid, String sender, String content, long date) {
        this.senderUid = senderUid;
        this.sender = sender;
        this.content = content;
        this.date = date;
    }

    public String getSenderUid() {
        return senderUid;
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
