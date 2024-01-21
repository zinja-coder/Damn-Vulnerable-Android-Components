// src/main/java/com.zin.dvac/Password.java
package com.zin.dvac;

public class Password {
    private long id;
    private String username;
    private String password;
    private String description;

    // Default constructor
    public Password() {
        // Empty constructor
    }

    // Constructor with parameters
    public Password(long id, String username, String password, String description) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.description = description;
    }
    public Password(String username, String password, String description) {
        this.username = username;
        this.password = password;
        this.description = description;
    }

    // Getters and setters (if not already present)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
