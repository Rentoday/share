package com.project.rentoday.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.payment.entity.Pay;
import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.global.oauth.entity.ProviderType;
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
public class Member {

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

    @NotNull
    @Column(name = "phone")
    private String phone;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @JsonIgnore
    @Column
    private String profileImageFileKey;

    @JsonIgnore
    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 20)
    private RoleType roleType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", length = 20)
    private ProviderType providerType;

    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Park> parks = new ArrayList<>();


    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    @Builder(builderMethodName = "createMember")
    public Member(
            @NotNull @Size(max = 512) String email,
            @Size(max = 64) String oauthId,
            @Size(max = 256) String password,
            @NotNull String phone,
            @Size(max = 100) String name,
            @NotNull RoleType roleType,
            @NotNull ProviderType providerType,
            @NotNull @Size(max = 512) String profileImageUrl
    ) {

        this.email = email;
        this.oauthId = oauthId;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.roleType = roleType != null ? roleType : RoleType.USER;
        this.providerType = providerType != null ? providerType : ProviderType.KAKAO;
        this.profileImageUrl = profileImageUrl;
        this.isDeleted = false;
    }

    public void updateName(String name) {
        if (name != null) this.name = name;
    }

    public void updateProfileImage(String profileImageFileKey, String profileImageUrl) {
        if (profileImageFileKey != null) this.profileImageFileKey = profileImageFileKey;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
    }

}
