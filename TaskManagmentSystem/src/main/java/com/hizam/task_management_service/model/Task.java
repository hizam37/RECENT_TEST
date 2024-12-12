package com.hizam.task_management_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(nullable =false,unique = true)
    private Long performerId;


    private String comment;


    @Version
    private Integer version;



}
