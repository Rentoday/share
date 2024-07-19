package com.project.rentoday.domain.comment.repository;

import com.project.rentoday.domain.comment.entity.Comment;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndMember(Long CommentEntityId, Member memberId);

    List<Comment> findByPark(Park park);

}
