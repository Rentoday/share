package com.project.rentoday.domain.notice.repository;

import com.project.rentoday.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
