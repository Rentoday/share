package com.project.rentoday.domain.notification.repository;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByMember(Member member);

    Boolean existsByMember(Member member);

    List<Notification> findByMemberAndReadFalseOrderByCreatedAtAsc(Member member);
}