package com.example.hobbyheavy.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class Base {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedDate; // 수정일자

    @Column(name = "deleted_at")
    private LocalDateTime deletedDate;

    @Setter(AccessLevel.PRIVATE)
    private Boolean deleted = false;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    // 논리적 삭제를 처리하는 메서드
    public void markAsDeleted() {
        this.deletedDate = LocalDateTime.now();
        this.deleted = true;
    }

    // 엔티티가 논리적으로 삭제되었는지 확인하는 메서드
    public boolean isDeleted() {
        return deletedDate != null;
    }
}
