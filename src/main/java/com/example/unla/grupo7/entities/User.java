package com.example.unla.grupo7.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data 
@Entity 
public class User {
    @Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name="username", unique=true, nullable=false, length=45)
	private String username;

	@Column(name="password", nullable=false, length=60)
	private String password;

	private boolean enabled;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="user")
	private Set<UserRole> userRoles = new HashSet<>();
    
}
