package com.pharmacy.entity;

public class User extends Person {
    private String username;
    private String password;
    private String role;

    public User() {
        super();
    }

    public User(Long id, String name, String email, String username, String password, String role) {
        super(id, name, email);
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + getId() + ", name='" + getName() + "', username='" + username + "', role='" + role + "'}";
    }
}
