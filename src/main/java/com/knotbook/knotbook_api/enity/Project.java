package com.knotbook.knotbook_api.enity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter
public class Project {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "user_id", nullable = false)
 private User user;

 @Column(nullable = false, length = 150)
 private String title;

 @Column(columnDefinition = "TEXT")
 private String description;

 @Column(length = 50)
 private String category;

 @Enumerated(EnumType.STRING)
 @Column(nullable = false, length = 30)
 private ProjectStatus status = ProjectStatus.PLANNING;

 private LocalDate startDate;

 private LocalDate endDate;

 @Column(nullable = false)
 private Long totalTimeMinutes = 0L;

 @Column(nullable = false)
 private boolean isDeleted = false;

 private LocalDateTime deletedAt;

 @Column(nullable = false, updatable = false)
 private LocalDateTime createdAt;

 @Column(nullable = false)
 private LocalDateTime updatedAt;

 protected Project() {
 }

 public Project(User user, String title, String description,
                String category,      ProjectStatus status,LocalDate startDate,

                LocalDate endDate) {
  this.user = user;
  this.title = title;
  this.description = description;
  this.category = category;
  this.startDate = startDate;
  this.status =status;
  this.endDate = endDate;
 }

 @PrePersist
 protected void onCreate() {
  LocalDateTime now = LocalDateTime.now();
  createdAt = now;
  updatedAt = now;
 }

 @PreUpdate
 protected void onUpdate() {
  updatedAt = LocalDateTime.now();
 }

 public void updateDetails(
         String title,
         String description,
         String category,
         ProjectStatus status,
         LocalDate startDate,
         LocalDate endDate
 ) {
  this.title = title;
  this.description = description;
  this.category = category;
  this.status = status;
  this.startDate = startDate;
  this.endDate = endDate;
 }

 public void softDelete() {
  this.isDeleted = true;
  this.deletedAt = LocalDateTime.now();
 }

 public void addTime(Long additionalMinutes) {
  this.totalTimeMinutes += additionalMinutes;
 }
}