package com.ajousw.spring.domain.navigation.route.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PathPointJdbcRepository {

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
}
