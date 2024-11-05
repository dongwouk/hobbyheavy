package com.example.hobbyheavy.entity;

import com.example.hobbyheavy.type.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(name = "user_id", unique = true, length = 20, nullable = false)
    private String userId; // 로그인 시 사용하는 ID

    @Column(name = "user_name", length = 20, nullable = false)
    private String username; // 사용자의 이름

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String email;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> role;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
}
