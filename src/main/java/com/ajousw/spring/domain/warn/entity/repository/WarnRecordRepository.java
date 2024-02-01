package com.ajousw.spring.domain.warn.entity.repository;

import com.ajousw.spring.domain.warn.entity.WarnRecord;
import com.ajousw.spring.domain.warn.entity.WarnRecord.WarnRecordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WarnRecordRepository extends JpaRepository<WarnRecord, Long> {
    @Query("select wr from WarnRecord wr where wr.createdDate >= :timeAfter")
    List<WarnRecord> findAllAfterTime(@Param("timeAfter") LocalDateTime timeAfter);
}
