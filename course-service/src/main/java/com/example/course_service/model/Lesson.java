package com.example.course_service.model;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String videoId;

    private int duration;


    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;




}
