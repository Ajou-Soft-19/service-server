package com.ajousw.spring.domain.navigation.warn;

import com.ajousw.spring.domain.navigation.api.NavigationPathProvider;
import com.ajousw.spring.domain.navigation.api.Provider;
import com.ajousw.spring.domain.navigation.api.info.table.TableApiResponse;
import com.ajousw.spring.domain.navigation.dto.TableQueryResultDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TableService {

    private final NavigationPathProvider pathProvider;

    public List<TableQueryResultDto> getTableOfDistancesAndDurations(String source, List<String> destinations) {
        TableApiResponse tableQueryResult = pathProvider.getTableQueryResult(Provider.OSRM,
                createParams(source, destinations));

        return createTableQueryResultDto(source, destinations, tableQueryResult.getDistances(),
                tableQueryResult.getDurations());
    }

    public List<TableQueryResultDto> createTableQueryResultDto(String source, List<String> destinations,
                                                               List<List<Double>> distanceList,
                                                               List<List<Double>> durationList) {
        List<TableQueryResultDto> tableQueryResultDtos = new ArrayList<>();
        List<Double> distances = distanceList.get(0);
        List<Double> durations = durationList.get(0);
        for (int i = 0; i < destinations.size(); i++) {
            Double distance = distances.get(i);
            Double duration = durations.get(i);
            tableQueryResultDtos.add(new TableQueryResultDto(i, source, destinations.get(i), distance, duration));
        }

        return tableQueryResultDtos;
    }

    public Map<String, Object> createParams(String source, List<String> destinations) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("source", source);
        params.put("destinations", destinations);
        return params;
    }
}
