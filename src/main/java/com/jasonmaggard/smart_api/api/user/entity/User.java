package com.jasonmaggard.smart_api.api.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jasonmaggard.smart_api.api.post.entity.Post;

@Entity
@Table(
  name = "users",
  indexes = @Index(name = "idx_users_email", columnList = "email", unique = true)
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonIgnore
  private List<Post> posts = new ArrayList<>();
}
