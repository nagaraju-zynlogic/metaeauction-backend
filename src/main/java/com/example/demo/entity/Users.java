package com.example.demo.entity;

import java.util.List;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Users12", schema = "public")

@Data
@Where(clause = "active = 1")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String email;
    private String password;
   
    private String status;
    
    
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Auction> auctions;
    
    // active user	
    private Integer active;

    // Default constructor setting the status to 'pending'
    public Users() {
        this.status = "Not Uploaded";  // Default value
    }
}
   
