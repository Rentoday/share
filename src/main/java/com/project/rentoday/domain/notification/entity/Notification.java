package com.project.rentoday.domain.notification.entity;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.global.entity.BaseEntity;
import com.project.rentoday.global.type.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column
    @NotNull
    @Size(max = 100)
    private String message;

    @NotNull
    @Column(name = "is_read")
    private Boolean isRead;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder(builderMethodName = "createMessage")
    public Notification(
            @NotNull @Size(max = 100) String message,
            @NotNull NotificationType type,
            @NotNull Member member,
            @NotNull Boolean isRead
    ) {
        this.message = message;
        this.type = type;
        this.member = member;
        this.isRead = isRead;
    }

    public void updateRead() {
        this.isRead = true;
    }
}
