package com.kafka.connect.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parameters")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParametersEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "type")
    private String type;

    @Column(name = "defaultValue")
    private String defaultValue;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

}
