package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.navigation.dto.TableQueryResultDto;
import com.ajousw.spring.domain.navigation.table.TableService;
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

    private final TableService tableService;

    @PostMapping("/api/navi/table")
    public ApiResponseJson calculateDistanceAndDuration(@Valid @RequestBody TableQueryDto tableQueryDto) {
        List<TableQueryResultDto> tableOfDistancesAndDurations = tableService.getTableOfDistancesAndDurations(
                tableQueryDto.getSource(), tableQueryDto.getDestinations());

        return new ApiResponseJson(HttpStatus.OK, tableOfDistancesAndDurations);
    }
}
