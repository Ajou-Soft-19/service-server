package com.ajousw.spring.domain.navigation.route;

import com.ajousw.spring.domain.navigation.api.info.table.TableApiResponse;
import com.ajousw.spring.domain.navigation.api.provider.NavigationPathProvider;
import com.ajousw.spring.domain.navigation.api.provider.Provider;
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
public class OsrmTableService {

    private final NavigationPathProvider pathProvider;

    public List<TableQueryResultDto> getTableOfMultiDestDistanceAndDuration(String source, List<String> destinations) {
        TableApiResponse tableQueryResult = pathProvider.getTableQueryResult(Provider.OSRM,
                createParams(List.of(source), destinations));

        return createMultiDestTableQueryResultDto(source, destinations, tableQueryResult.getDistances(),
                tableQueryResult.getDurations());
    }

    public List<TableQueryResultDto> getTableOfMultiSourceDistanceAndDuration(List<String> sources,
                                                                              String destination) {
        TableApiResponse tableQueryResult = pathProvider.getTableQueryResult(Provider.OSRM,
                createParams(sources, List.of(destination)));

        return createMultiSourceTableQueryResultDto(sources, destination, tableQueryResult.getDistances(),
                tableQueryResult.getDurations());
    }

    public List<TableQueryResultDto> createMultiDestTableQueryResultDto(String source, List<String> destinations,
                                                                        List<List<Double>> distanceList,
                                                                        List<List<Double>> durationList) {
        List<TableQueryResultDto> tableQueryResultDtos = new ArrayList<>();
        List<Double> distances = distanceList.get(0);
        List<Double> durations = durationList.get(0);
        for (int i = 0; i < destinations.size(); i++) {
            Double distance = distances.get(i);
            Double duration = durations.get(i);
            tableQueryResultDtos.add(new TableQueryResultDto(i, source, destinations.get(i), duration, distance));
        }

        return tableQueryResultDtos;
    }

    public List<TableQueryResultDto> createMultiSourceTableQueryResultDto(List<String> sources, String destination,
                                                                          List<List<Double>> distanceList,
                                                                          List<List<Double>> durationList) {
        List<TableQueryResultDto> tableQueryResultDtos = new ArrayList<>();
        for (int i = 0; i < sources.size(); i++) {
            Double distance = distanceList.get(i).get(0);
            Double duration = durationList.get(i).get(0);
            tableQueryResultDtos.add(new TableQueryResultDto(i, sources.get(i), destination, duration, distance));
        }

        return tableQueryResultDtos;
    }

    public Map<String, Object> createParams(List<String> sources, List<String> destinations) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("sources", sources);
        params.put("destinations", destinations);
        return params;
    }
}
