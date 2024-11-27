package com.example.hobbyheavy.entity;

import com.example.hobbyheavy.type.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(name = "user_id", unique = true, length = 50, nullable = false)
    private String userId; // 로그인 시 사용하는 ID

    @Column(name = "user_name", length = 20, nullable = false)
    private String username; // 사용자의 이름

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String email;

    @Column(nullable = false)
    private Boolean gender; // 성별

    @Column(nullable = false)
    private Integer age; // 나이

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_hobby",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "hobby_id")
    )
    private Set<Hobby> hobbies = new HashSet<>(); // 취미 ID

    @Column(nullable = false)
    private Boolean alarm = true; // 알림구독 여부

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    private Set<UserRole> userRole;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
}
