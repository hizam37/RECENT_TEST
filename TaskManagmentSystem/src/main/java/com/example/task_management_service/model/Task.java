package com.example.task_management_service.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable =false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable =false)
    private TaskStatus taskStatus;


    @Enumerated(EnumType.STRING)
    @Column(nullable =false)
    private TaskPriority taskPriority;

    @Column(nullable =false)
    private String author;

    @Column(nullable =false)
    private Long performerId;


    private String comment;


}
