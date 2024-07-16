package com.project.rentoday.domain.notification.repository;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByMember(Member member);

    Boolean existsByMember(Member member);

    @Query("SELECT n FROM Notification n " +
            "WHERE n.member = :member AND n.isRead = false " +
            "ORDER BY n.createdDate DESC")
    List<Notification> findUnreadMessages(@Param("member") Member member);
}