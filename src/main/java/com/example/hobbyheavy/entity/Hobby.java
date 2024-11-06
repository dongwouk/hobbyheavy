package com.example.hobbyheavy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hobby extends Base {

    @Id
    @Column(name = "hobby_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hobbyId;

    @Column(name = "hobby_name", unique = true, nullable = false, length = 50)
    private String hobbyName; // 취미명

    @Column(name = "in_out_door", nullable = false)
    private String inOutDoor; // 실/내외

    @Column(length = 50)
    private String category; // 카테고리

}
