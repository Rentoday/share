package com.project.rentoday.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "member_refresh_token")
@Entity
public class MemberRefreshToken {

    @JsonIgnore
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_seq")
    private Long refreshTokenSeq;

    @NotNull
    @Size(max = 64)
    @Column(name = "member_id", length = 64, unique = true)
    private String memberId;

    @NotNull
    @Size(max = 500)
    @Column(name = "refresh_token", length = 256)
    private String refreshToken;

    public MemberRefreshToken(
            @NotNull @Size(max = 64) String memberId,
            @NotNull @Size(max = 500) String refreshToken
    ) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }
}
