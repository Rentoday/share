package com.project.rentoday.global.jwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.rentoday.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
@Entity
public class RefreshToken {

    @JsonIgnore
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id")
    private Long id;

    @NotNull
    @Size(max = 500)
    @Column(name = "token", length = 256)
    private String refreshToken;

    @NotNull
    @Column(length = 256)
    private String expiration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public RefreshToken(
            @NotNull @Size(max =500) String refreshToken,
            @NotNull Member member,
            @NotNull String expiration
    ) {
        this.refreshToken = refreshToken;
        this.member = member;
        this.expiration = expiration;
    }
}
