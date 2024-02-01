package com.ajousw.spring.domain.warn.entity.repository;

import com.ajousw.spring.domain.warn.entity.WarnRecord;
import com.ajousw.spring.domain.warn.entity.WarnRecord.WarnRecordId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarnRecordRepository extends JpaRepository<WarnRecord, WarnRecordId> {
}
