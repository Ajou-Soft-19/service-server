package com.ajousw.spring.domain.vehicle.entity.repository;

import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleStatusRepository extends JpaRepository<VehicleStatus, UUID> {

    /**
     * CREATE INDEX idx_vehicle_status_coordinate ON vehicle_status USING GIST (coordinate); 공간 쿼리에 대해 인덱싱 필요
     *
     * @param longitude 중심 경도
     * @param latitude  중심 위도
     * @param radius    검색 반경(미터 단위)
     * @return 중심 좌표로 부터 radius 안에 있는 VehicleStatus
     */

    @Query("select vs from VehicleStatus vs where "
            + "ST_DWithin(vs.coordinate, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), :radius, false) = true")
    List<VehicleStatus> findAllWithinRadius(@Param("lon") double longitude, @Param("lat") double latitude,
                                            @Param("radius") double radius);
}
