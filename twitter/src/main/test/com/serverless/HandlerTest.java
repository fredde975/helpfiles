package com.serverless;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HandlerTest {

    @Test
    public void getTweets() {
        int numberOfThreads = 4;
        int numerOfRequests = 40;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);


        Date start = new Date();
        for (int i = 0; i < numberOfThreads; i++) {
            Runnable worker = new MyRunnable(numberOfRequests, ordet);
            executor.execute(worker);
        }
        executor.shutdown();

        // Wait until all threads are finish
        while (!executor.isTerminated()) {
        }

        System.out.println("\nFinished all threads");
        Date stop = new Date();
        Long duration = stop.getTime() - start.getTime();
        System.out.println("duration = " + duration)

    }


    public class MyRunnable implements Runnable {
        List<Long> durations = new ArrayList<>();
        int numberOfRequests;

        public MyRunnable(int numberOfRequestst) {
            this.numberOfRequests = numberOfRequests;
        }

        @Override
        public void run() {

            String result = "";
            int code = 200;
            try {
                for (int i = 0; i < numberOfRequests; i++) {
                    Date start = new Date();
                    JsonResponse response = getResponse(ordet);
                    code = response.status();
                    if (code == 200) {
                        result = "Green\t";
                    }
                    Date stop = new Date();
                    Long duration = stop.getTime() - start.getTime();
                    durations.add(duration);
                }
            } catch (Exception e) {
                result = "->Red<-\t";
            }

            Long sum = durations.stream().mapToLong(Long::longValue).sum();
            Long actualAvarege = sum / durations.size();
            System.out.println(Thread.currentThread().getName() + "\t:actualAvarege = " + actualAvarege + "ms");
        }
    }


    private static JsonResponse getResponse(String orginalord) throws IOException {

        JdkRequest jdkRequest = new JdkRequest(synonymerURL);
        String ERROR_CONNECTION = "Failed to connect to AWS: %s";

        try {
            return jdkRequest
                    .uri()
                    .queryParam("orginalord", orginalord)
                    .back()
                    .method(Request.GET)
                    .fetch()
                    .as(JsonResponse.class);
        } catch (IOException e) {
            String message = String.format(ERROR_CONNECTION, e.getMessage());
            throw new IOException(message, e);
        }
    }

}
