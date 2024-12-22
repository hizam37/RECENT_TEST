package com.hizam.task_management_service.model;


import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "reference_token")
public class ReferenceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long performerId;

    private String referenceToken;

    @Version
    private Integer version;

}
