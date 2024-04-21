package com.user.dto;

public class UserDto {
    private String username;
    private String password;
    private Long id;

    // Constructors, getters, and setters

    public UserDto() {
    }

    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
        this.id = id;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String id) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
