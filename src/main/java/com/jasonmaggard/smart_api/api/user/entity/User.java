package com.jasonmaggard.smart_api.api.user.entity;

import jakarta.persistence.*;
import java.util.*;

import com.jasonmaggard.smart_api.api.post.entity.Post;

@Entity
@Table(
  name = "users",
  indexes = @Index(name = "idx_users_email", columnList = "email", unique = true)
)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<Post> posts = new ArrayList<>();
}
