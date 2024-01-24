package com.ajousw.spring.domain.warn.entity;

import com.ajousw.spring.domain.member.repository.BaseTimeEntity;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.warn.entity.WarnRecord.WarnRecordId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WarnRecord extends BaseTimeEntity implements Persistable<WarnRecordId> {

    @EmbeddedId
    private WarnRecordId warnRecordId;

    @MapsId("emergencyEventId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_event_id")
    private EmergencyEvent emergencyEvent;

    @MapsId("vehicleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_Id")
    private Vehicle vehicle;

    public WarnRecord(EmergencyEvent emergencyEvent, Long checkPointIndex, Vehicle vehicle) {
        this.warnRecordId = new WarnRecordId(emergencyEvent.getEmergencyEventId(), checkPointIndex,
                vehicle.getVehicleId());
        this.emergencyEvent = emergencyEvent;
        this.vehicle = vehicle;
    }

    @Getter
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class WarnRecordId implements Serializable {

        private Long emergencyEventId;

        private Long checkPointIndex;

        private Long vehicleId;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            WarnRecordId that = (WarnRecordId) o;
            return Objects.equals(emergencyEventId, that.emergencyEventId)
                    && Objects.equals(checkPointIndex, that.checkPointIndex)
                    && Objects.equals(vehicleId, that.vehicleId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(emergencyEventId, checkPointIndex, vehicleId);
        }
    }

    @Override
    public WarnRecordId getId() {
        return new WarnRecordId(this.warnRecordId.getEmergencyEventId(), this.warnRecordId.getCheckPointIndex(),
                this.warnRecordId.vehicleId);
    }

    @Override
    public boolean isNew() {
        return this.getCreatedDate() == null;
    }
}