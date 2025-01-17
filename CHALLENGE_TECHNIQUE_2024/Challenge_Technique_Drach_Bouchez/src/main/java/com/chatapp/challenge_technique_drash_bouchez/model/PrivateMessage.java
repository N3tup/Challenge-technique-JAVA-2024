
package com.chatapp.challenge_technique_drash_bouchez.model;

public class PrivateMessage {
    private String sender;
    private String recipient;
    private String content;
    private long timestamp;

    // Getters and setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}