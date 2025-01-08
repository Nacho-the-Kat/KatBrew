package com.katbrew.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class KatBrewHelper<T, R extends Serializable> {

    private final WebClient client = KatBrewWebClient.createWebClient();

    public List<R> fetchPaginated(
            final String url,
            final String lastCursor,
            final String paginationPrefix,
            final ParameterizedTypeReference<T> reference,
            final Function<T, String> getCursor,
            final Function<T, List<R>> getEntries,
            final Integer limit,
            final Function<List<R>, Void> prepareData
    ) {

        List<R> allEntries = new ArrayList<>();
        String nextCursor = lastCursor;
        int errorCounter = 0;
        do {

            String urlIntern = url;

            if (nextCursor != null) {
                urlIntern += paginationPrefix + nextCursor;
            }
            try {

                final T response = fetch(
                        urlIntern,
                        reference
                );

                if (response != null) {
                    final List<R> entries = getEntries.apply(response);
                    if (entries != null) {
                        allEntries.addAll(entries);
                    }
                    nextCursor = getCursor.apply(response);
                } else {
                    nextCursor = null;
                }
                if (limit != null && prepareData != null) {
                    //if the data is too big, we insert the batch
                    if (allEntries.size() >= limit) {
                        log.info("inserting batch that reaches the limit");
                        prepareData.apply(allEntries);
                        allEntries.clear();
                    }
                }
            } catch (Exception e) {
                if (errorCounter < 3) {
                    log.warn("error on fetching " + urlIntern);
                    ++errorCounter;
                } else {
                    break;
                }
            }
        } while (nextCursor != null);

        if (allEntries.isEmpty()) {
            return null;
        }

        return allEntries;
    }

    public T fetch(
            final String url,
            final ParameterizedTypeReference<T> reference
    ) throws InterruptedException {
        int errorCounter = 0;
        T response = null;
        while (true) {
            try {
                response = client
                        .get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(reference)
                        .block();
                break;
            } catch (Exception e) {
                if (errorCounter < 3) {
                    log.warn("error on fetching " + url);
                    ++errorCounter;
                } else {
                    break;
                }
            }
        }

        return response;
    }
}
