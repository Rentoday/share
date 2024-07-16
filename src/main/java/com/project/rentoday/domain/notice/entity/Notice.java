package com.project.rentoday.domain.notice.entity;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @NotNull
    @Size(max = 500)
    private String title;

    @NotNull
    @Size(max = 10000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder(builderMethodName = "createNotice")
    public Notice(
            @NotNull @Size(max = 500) String title,
            @NotNull @Size(max = 10000) String content,
            @NotNull Member member
    ) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public void updateNotice(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
