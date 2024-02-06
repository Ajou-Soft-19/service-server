package com.ajousw.spring.domain.warn.entity.repository;

import com.ajousw.spring.domain.warn.entity.WarnRecord;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WarnRecordRepository extends JpaRepository<WarnRecord, Long> {
    @Query("select wr from WarnRecord wr where wr.createdDate >= :timeAfter")
    List<WarnRecord> findAllAfterTime(@Param("timeAfter") LocalDateTime timeAfter);

    @Query("select wr from WarnRecord wr where wr.emergencyEvent.emergencyEventId = :emergencyEventId")
    List<WarnRecord> findAllByEmergencyEventId(@Param("emergencyEventId") Long emergencyEventId);

    @Query("select wr from WarnRecord wr where wr.warnRecordId.emergencyEventId = :emergencyEventId and wr.warnRecordId.checkPointIndex = :checkPointIndex")
    List<WarnRecord> findAllByEmergencyEventIdAndCheckPointIndex(@Param("emergencyEventId") Long emergencyEventId,
                                                                 @Param("checkPointIndex") Long checkPointIndex);

    @Query("select wr.warnRecordId.sessionId from WarnRecord wr where wr.warnRecordId.emergencyEventId = :emergencyEventId and wr.warnRecordId.checkPointIndex = :checkPointIndex")
    List<String> findSessionIdByEmergencyEventIdAndCheckPointIndex(@Param("emergencyEventId") Long emergencyEventId,
                                                                   @Param("checkPointIndex") Long checkPointIndex);

}
