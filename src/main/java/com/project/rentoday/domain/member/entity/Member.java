package com.project.rentoday.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.rentoday.domain.comment.entity.Comment;
import com.project.rentoday.domain.notice.entity.Notice;
import com.project.rentoday.domain.notification.entity.Notification;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.global.entity.BaseEntity;
import com.project.rentoday.global.jwt.entity.RefreshToken;
import com.project.rentoday.global.type.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    /**
     * Member Primary key for Local login
     */
    @NotNull
    @Size(max = 512)
    @Column(name = "email", length = 512, unique = true)
    private String email;

    /**
     * Member Primary key for OAuth login
     */
    @Column(name = "oauth_id", length = 64, unique = true)
    private String oauthId;

    @JsonIgnore //민감한 정보는 직렬화하지 않음으로 데이터 노출을 방지
    @Column(name = "password", length = 256)
    private String password;

    @Column(name = "phone")
    private String phone;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @NotNull
    @JsonIgnore
    @Column(name = "profile_image_url", length = 512)
    private String profileImage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 20)
    private RoleType roleType;

    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Park> parks = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> Notices;

    @Builder(builderMethodName = "createMember")
    public Member(
            @NotNull @Size(max = 512) String email,
            @Size(max = 64) String oauthId,
            @Size(max = 256) String password,
            @NotNull String phone,
            @Size(max = 100) String name,
            @NotNull String profileImage

    ) {
        this.email = email;
        this.oauthId = oauthId;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.profileImage = profileImage;
        this.roleType = RoleType.USER;
        this.isDeleted = false;
    }

    @Builder(builderMethodName = "createKakaoMember")
    public Member(
            @NotNull @Size(max = 512) String email,
            @Size(max = 64) String oauthId,
            @Size(max = 100) String name,
            @NotNull String profileImage

    ) {
        this.email = email;
        this.oauthId = oauthId;
        this.name = name;
        this.profileImage = profileImage;
        this.roleType = RoleType.USER;
        this.isDeleted = false;
    }

    public void updateKakaoProfile(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;
    }

    public void updateProfile(String profileImage, String password) {
        this.profileImage = profileImage;
        this.password = password;
    }

}
