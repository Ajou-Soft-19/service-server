package com.ajousw.spring.web.controller.dto.navigation;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TableQueryDto {

    @NotEmpty
    private String source;

    private List<String> destinations;

}
