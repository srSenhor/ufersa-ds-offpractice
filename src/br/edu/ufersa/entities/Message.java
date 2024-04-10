package br.edu.ufersa.entities;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private String content;
    private String hash;

    public Message(String content, String hash) {
        this.setContent(content);
        this.setHash(hash);
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        if (content != null && !content.isBlank()) {
            this.content = content;
        } else {
            this.content = "null";
        }
    }

    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        if (hash != null && !hash.isBlank()) {
            this.hash = hash;
        } else {
            this.hash = "null";
        }
    }

    
}
