package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.navigation.dto.TableQueryResultDto;
import com.ajousw.spring.domain.navigation.route.OsrmTableService;
import com.ajousw.spring.web.controller.dto.navigation.TableQueryDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TableController {

    private final OsrmTableService osrmTableService;

    @PostMapping("/api/navi/table/multi-dest")
    public ApiResponseJson calculateDistanceAndDurationMultiDest(@Valid @RequestBody TableQueryDto tableQueryDto) {
        List<TableQueryResultDto> tableOfDistancesAndDurations = osrmTableService.getTableOfMultiDestDistanceAndDuration(
                tableQueryDto.getSources().get(0), tableQueryDto.getDestinations());

        return new ApiResponseJson(HttpStatus.OK, tableOfDistancesAndDurations);
    }

    @PostMapping("/api/navi/table/multi-source")
    public ApiResponseJson calculateDistanceAndDurationMultiSource(@Valid @RequestBody TableQueryDto tableQueryDto) {
        List<TableQueryResultDto> tableOfDistancesAndDurations = osrmTableService.getTableOfMultiSourceDistanceAndDuration(
                tableQueryDto.getSources(), tableQueryDto.getDestinations().get(0));

        return new ApiResponseJson(HttpStatus.OK, tableOfDistancesAndDurations);
    }
}
