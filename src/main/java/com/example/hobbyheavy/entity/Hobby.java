package com.example.hobbyheavy.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "hobby_name", nullable = false)
    private String hobbyName; // 취미명

    @Column(name = "in_out_door", nullable = false)
    private String inOutDoor; // 실/내외

    @Column
    private String category; // 카테고리

}
