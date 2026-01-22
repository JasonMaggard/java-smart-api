package com.jasonmaggard.smart_api.api.post.entity;
import jakarta.persistence.*;
import java.util.*;

import com.jasonmaggard.smart_api.api.user.entity.User;

@Entity
@Table(
  name = "posts",
  indexes = @Index(name = "idx_users_email", columnList = "email", unique = true)
)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;   

    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
