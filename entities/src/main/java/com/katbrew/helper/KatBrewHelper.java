package com.katbrew.helper;

import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.services.tables.LastUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class KatBrewHelper<T, R extends Serializable> {

    private final WebClient client = KatBrewWebClient.createWebClient();

    private final LastUpdateService lastUpdateService;

    public List<R> fetchPaginated(
            final String url,
            final String lastCursor,
            final Boolean compareCursor,
            final String paginationPrefix,
            final String safetySafeIdentifier,
            final ParameterizedTypeReference<T> reference,
            final Function<T, String> getCursor,
            final Function<T, List<R>> getEntries,
            final Integer limit,
            final Function<List<R>, Void> prepareData
    ) {

        List<R> allEntries = new ArrayList<>();
        final LastUpdate safetySafe = safetySafeIdentifier != null
                ? lastUpdateService.findByIdentifier(safetySafeIdentifier)
                : null;
        String nextCursor = safetySafe != null ? safetySafe.getData() : null;
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
                    final String cursor = getCursor.apply(response);
                    if (compareCursor && cursor != null && lastCursor != null) {
                        if (new BigInteger(cursor).compareTo(new BigInteger(lastCursor)) < 0) {
                            nextCursor = null;
                        } else {
                            nextCursor = cursor;
                        }
                    } else {
                        nextCursor = cursor;
                    }
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
