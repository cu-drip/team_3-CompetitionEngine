package com.drip.competitionengine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Column(name = "id")
  @Id
  UUID id;
  @Column(name = "name")
  String name;
  @Column(name = "surname")
  String surname;
  @Column(name = "patronymic")
  String patronymic;
  @Column(name = "phone_number")
  String phoneNumber;
  @Column(unique=true, name = "email") String email;
  @Column(name = "hashed_password")
  String hashedPassword;
  @Column(name = "is_admin")
  boolean isAdmin;
  @Column(name = "date_of_birth")
  Instant dateOfBirth;
  @Column(name = "age")
  Integer age;
  @Column(name = "sex")
  String sex;
  @Column(name = "weight")
  Float weight;
  @Column(name = "height")
  Float height;
  @Column(name = "created_at")
  Instant createdAt;
  @Column(name = "bio")
  String bio;
  @Column(name = "avatar_url")
  String avatarUrl;
}