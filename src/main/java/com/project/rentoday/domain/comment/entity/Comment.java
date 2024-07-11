package com.project.rentoday.domain.comment.entity;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column
    @NotNull
    @Size(max = 2500)
    private String content;

    @Column
    @NotNull
    private int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(targetEntity = Park.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pa_id")
    private Park park;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children;

    @Builder(builderMethodName = "createComment")
    public Comment(
            @NotNull @Size(max = 2500) String content,
            @NotNull Member member,
            @NotNull Park park
    ) {
        this.content = content;
        this.depth = 0;
        this.member = member;
        this.park = park;
    }

    @Builder(builderMethodName = "createReply")
    public Comment(
            @NotNull @Size(max = 2500) String content,
            @NotNull Member member,
            @NotNull Park park,
            @NotNull Comment parent
    ) {
        this.content = content;
        this.depth = 1;
        this.member = member;
        this.park = park;
        this.parent = parent;
    }

    public void updateComment(String comment) {
        this.content = comment;
    }
}
