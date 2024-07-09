package com.project.rentoday.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.management.relation.Role;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RoleType {

    ADMIN("ROLE_ADMIN", "관리자 권한"),
    USER("ROLE_USER", "일반 사용자 권한"),
    SELLER("ROLE_SELLER", "판매자 권한");

    private final String code;
    private final String name;

    public static RoleType of(String code) {
        if (code == null) {
            throw new IllegalArgumentException("권한 설정이 되어 있지 않습니다.");
        }

        return Arrays.stream(RoleType.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(SELLER);
    }
}
