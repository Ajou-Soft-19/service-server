package com.ajousw.spring.domain.navigation.entity.repository;

import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.PathPoint;
import com.ajousw.spring.domain.warn.entity.WarnRecord;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BatchInsertJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAllPathPointsInBatch(List<PathPoint> pathPoints) {
        String sql = "INSERT INTO path_point (navigation_path_id, point_index, coordinate)"
                + " VALUES (?, ?, ST_Point(?, ?))";

        log.info("Batch Inserting PathPoints Size: {}", pathPoints.size());
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PathPoint pathPoint = pathPoints.get(i);

                ps.setObject(1, pathPoint.getPointId().getNavigationPathId());
                ps.setLong(2, pathPoint.getPointIndex());
                ps.setDouble(3, pathPoint.getCoordinate().getX());
                ps.setDouble(4, pathPoint.getCoordinate().getY());
            }

            @Override
            public int getBatchSize() {
                return pathPoints.size();
            }
        });
    }

    @Transactional
    public void saveAllCheckPointsInBatch(List<CheckPoint> checkPoints) {
        String sql =
                "INSERT INTO check_point (navigation_path_id, coordinate, point_index, distance, duration)"
                        + " VALUES (?, ST_SetSRID(ST_MakePoint(?, ?), 4326), ?, ?, ?)";

        log.info("Batch Inserting CheckPoints Size: {}", checkPoints.size());
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CheckPoint checkPoint = checkPoints.get(i);

                ps.setObject(1, checkPoint.getPointId().getNavigationPathId());
                ps.setDouble(2, checkPoint.getCoordinate().getX());
                ps.setDouble(3, checkPoint.getCoordinate().getY());
                ps.setLong(4, checkPoint.getPointIndex());
                ps.setDouble(5, checkPoint.getDistance());
                ps.setDouble(6, checkPoint.getDuration());
            }

            @Override
            public int getBatchSize() {
                return checkPoints.size();
            }
        });
    }

    @Transactional
    public void saveAllWarnRecordsInBatch(List<WarnRecord> warnRecords) {
        String sql =
                "INSERT INTO warn_record (emergency_event_id, check_point_index, session_id, coordinate, meter_per_sec, direction, using_navi, created_date)"
                        + " VALUES (?, ?, ?, ST_Point(?, ?), ?, ?, ?, ?)";

        log.info("Batch Inserting WarnRecords Size: {}", warnRecords.size());
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                WarnRecord warnRecord = warnRecords.get(i);

                ps.setLong(1, warnRecord.getWarnRecordId().getEmergencyEventId());
                ps.setLong(2, warnRecord.getWarnRecordId().getCheckPointIndex());
                ps.setString(3, warnRecord.getWarnRecordId().getSessionId());
                if (warnRecord.getCoordinate() != null) {
                    ps.setDouble(4, warnRecord.getCoordinate().getX());
                    ps.setDouble(5, warnRecord.getCoordinate().getY());
                } else {
                    ps.setNull(4, Types.DOUBLE);
                    ps.setNull(5, Types.DOUBLE);
                }
                ps.setDouble(6, warnRecord.getMeterPerSec());
                ps.setDouble(7, warnRecord.getDirection());
                ps.setBoolean(8, warnRecord.getUsingNavi());
                ps.setTimestamp(9, Timestamp.valueOf(warnRecord.getCreatedDate()));
            }

            @Override
            public int getBatchSize() {
                return warnRecords.size();
            }
        });
    }
}
