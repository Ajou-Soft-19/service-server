package com.ajousw.spring.domain.navigation.entity.repository;

import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.PathPoint;
import com.ajousw.spring.domain.warn.entity.WarnRecord;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class BatchInsertJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAllInBatch(List<PathPoint> pathPoints) {
        String sql = "INSERT INTO path_point (path_point_id, coordinate, index, navigation_path_id)"
                + " VALUES (?, ST_Point(?, ?), ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PathPoint pathPoint = pathPoints.get(i);

                UUID pathPointId = pathPoint.getPathPointId() != null ? pathPoint.getPathPointId() : UUID.randomUUID();
                ps.setObject(1, pathPointId); // 첫 번째 파라미터로 UUID를 설정합니다.
                ps.setDouble(2, pathPoint.getCoordinate().getX()); // Point의 X 좌표
                ps.setDouble(3, pathPoint.getCoordinate().getY()); // Point의 Y 좌표
                ps.setLong(4, pathPoint.getIndex()); // index 값
                ps.setObject(5, pathPoint.getNavigationPath().getNaviPathId()); // navigation_path_id 값
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
                "INSERT INTO check_point (check_point_id, navigation_path_id, coordinate, point_index, distance, duration)"
                        + " VALUES (?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326), ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CheckPoint checkPoint = checkPoints.get(i);

                UUID checkPointId =
                        checkPoint.getCheckPointId() != null ? checkPoint.getCheckPointId() : UUID.randomUUID();
                ps.setObject(1, checkPointId);
                ps.setObject(2, checkPoint.getNavigationPath().getNaviPathId());
                ps.setDouble(3, checkPoint.getCoordinate().getX());
                ps.setDouble(4, checkPoint.getCoordinate().getY());
                ps.setLong(5, checkPoint.getPointIndex());
                ps.setDouble(6, checkPoint.getDistance());
                ps.setDouble(7, checkPoint.getDuration());
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