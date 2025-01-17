package com.chatapp.challenge_technique_drash_bouchez.model;

import lombok.Getter;


import jakarta.persistence.*;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "utilisateur")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @javax.validation.constraints.Size(max = 50)
    @javax.validation.constraints.NotNull
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @javax.validation.constraints.Size(max = 100)
    @javax.validation.constraints.NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @javax.validation.constraints.Size(max = 255)
    @javax.validation.constraints.NotNull
    @Column(name = "password", nullable = false)
    private String password;



    //getter setter
    public User (){

    }

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }


    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
}