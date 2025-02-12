package com.katbrew.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
    private final int errorAmount = 10;

    public List<R> fetchPaginatedWithoutSave(
            final String url,
            final String lastCursor,
            final Boolean isDesc,
            final Boolean compareCursor,
            final String paginationPrefix,
            final ParameterizedTypeReference<T> reference,
            final Function<T, String> getCursor,
            final Function<T, List<R>> getEntries
    ) {

        List<R> allEntries = new ArrayList<>();

        String nextCursor = isDesc ? null : lastCursor;
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
            } catch (Exception e) {
                if (errorCounter < errorAmount) {
                    long time = (long) (Math.random() * 1000) * (errorCounter + 1);
                    log.warn("error on fetching " + urlIntern);
                    log.warn(e.getMessage());
                    waitFunction(time);
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

    public List<R> fetchPaginated(
            final String url,
            final String lastCursor,
            final Boolean isDesc,
            final Boolean compareCursor,
            final String paginationPrefix,
            final ParameterizedTypeReference<T> reference,
            final Function<T, String> getCursor,
            final Function<T, List<R>> getEntries,
            final Integer limit,
            final Function<List<R>, Void> prepareData
    ) {

        List<R> allEntries = new ArrayList<>();

        /**
         * only if the fetching direction is asc (we get the oldest first and with the cursor we getting newer entries) we insert the cursor
         * if we fetching DESC we need to fetch with null and copare later
         */
        String nextCursor = isDesc ? null : lastCursor;
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
                        log.info("inserting batch that reaches the limit: " + urlIntern);
                        prepareData.apply(allEntries);
                        allEntries.clear();
                    }
                }
            } catch (Exception e) {
                if (errorCounter < errorAmount) {
                    long time = (long) (Math.random() * 1000) * (errorCounter + 1);
                    log.warn("error on fetching " + urlIntern + " waiting seconds: " + time / 1000);
                    log.warn(e.getMessage());
                    waitFunction(time);
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

    public List<R> fetchInitPaginated(
            final String url,
            final String paginationPrefix,
            final ParameterizedTypeReference<T> reference,
            final Function<T, String> getCursor,
            final Function<T, List<R>> getEntries,
            final Integer limit,
            final Function<List<R>, Void> prepareData
    ) {

        List<R> allEntries = new ArrayList<>();

        String nextCursor = null;

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
                        log.info("inserting batch that reaches the limit: " + urlIntern);
                        prepareData.apply(allEntries);
                        allEntries.clear();
                    }
                }
            } catch (Exception e) {
                if (errorCounter < errorAmount) {
                    long time = (long) (Math.random() * 1000) * (errorCounter + 1);
                    log.warn("error on fetching " + urlIntern + " waiting seconds: " + time / 1000);
                    log.warn(e.getMessage());
                    waitFunction(time);
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
                if (e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is4xxClientError()) {
                    break;
                }
                if (errorCounter < errorAmount) {
                    long time = (long) (Math.random() * 1000) * (errorCounter + 1);
                    log.warn("error on fetching " + url + ", waiting seconds: " + time / 1000);
                    waitFunction(time);
                    ++errorCounter;
                } else {
                    break;
                }
            }
        }

        return response;
    }

    private void waitFunction(final long time) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < time) {
            //empty while
        }
    }
}
