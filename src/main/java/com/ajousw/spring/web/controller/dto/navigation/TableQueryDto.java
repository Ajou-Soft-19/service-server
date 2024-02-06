package com.ajousw.spring.web.controller.dto.navigation;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TableQueryDto {

    private List<String> sources;

    private List<String> destinations;

}
