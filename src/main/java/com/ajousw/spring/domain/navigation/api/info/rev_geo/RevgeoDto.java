package com.ajousw.spring.domain.navigation.api.info.rev_geo;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class RevgeoDto {
    private final String area1Name;
    private final String area2Name;
    private final String area3Name;
    private final String area4Name;

    public String getFullAddress() {
        return area1Name + " " + area2Name + " " + area3Name + " " + area4Name;
    }

    @SuppressWarnings("unchecked")
    public RevgeoDto(Map<String, Object> attributes) {
        List<Map<String, Object>> results = (List<Map<String, Object>>) attributes.get("results");
        if (results != null && !results.isEmpty()) {
            // 첫 번째 결과만 사용
            Map<String, Object> firstResult = results.get(0);
            Map<String, Object> region = (Map<String, Object>) firstResult.get("region");
            Map<String, Object> area1 = (Map<String, Object>) region.get("area1");
            Map<String, Object> area2 = (Map<String, Object>) region.get("area2");
            Map<String, Object> area3 = (Map<String, Object>) region.get("area3");
            Map<String, Object> area4 = (Map<String, Object>) region.get("area4");

            this.area1Name = area1 != null ? (String) area1.get("name") : "";
            this.area2Name = area2 != null ? (String) area2.get("name") : "";
            this.area3Name = area3 != null ? (String) area3.get("name") : "";
            this.area4Name = area4 != null ? (String) area4.get("name") : "";
        } else {
            // 결과가 없는 경우 빈 문자열로 초기화
            this.area1Name = "";
            this.area2Name = "";
            this.area3Name = "";
            this.area4Name = "";
        }
    }
}
