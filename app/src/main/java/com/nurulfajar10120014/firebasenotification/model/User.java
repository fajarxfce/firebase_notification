package com.nurulfajar10120014.firebasenotification.model;
/*
Nama    : Nurul Fajar
NIM     : 10120014
Kelas   : IF-1
Matkul  : Aplikasi Komputer Bergerak
*/

public class User {
    private String id,name, email, avatar;

    public User(String name, String email, String avatar) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
