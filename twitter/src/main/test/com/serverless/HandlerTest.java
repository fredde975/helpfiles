package com.serverless;

import com.jcabi.http.Request;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import org.junit.Test;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HandlerTest {
    static long sinceId = 0;
    static long numberOfTweets = 0;
    static final int count = 100;

    @Test
    public void getATweet() {
        makeGetRequest("https://www.arbetsformedlingen.se/rest/pbapi/af/v1/version/");
    }


    @Test
    public void getATweetFromTwitter() {
        final String hashTag = "#Mello";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("SSVbNkmx7ugzaOfaSsG8iRSij")
                .setOAuthConsumerSecret("fBop27mqeaK8tcYp5hmL7vmSWSfbp3ys4qP14JgWyqMsh5UA6X")
                .setOAuthAccessToken("899157129722048512-YHceWk6N45aBm4g4rSrQH3EXxwPjaPG")
                .setOAuthAccessTokenSecret("hNI81vC3XbSp3tpiQ4m1dywL1KbJ0W3O0LqfcDwCsMAqO");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();


        //get latest tweets as of now
        //At this point store sinceId in database
        Query queryMax = new Query(hashTag);
        queryMax.setCount(count);
        getTweets(queryMax, twitter, "maxId");
        queryMax = null;
    }


    private void makeGetRequest(String url) {
        JdkRequest jdkRequest = new JdkRequest(url);
        String ERROR_CONNECTION = "Failed to connect to AWS: %s";

        try {
            JsonResponse response = jdkRequest
                    .uri()
                    //  .queryParam("orginalord", orginalord)
                    .back()
                    .method(Request.GET)
                    .fetch()
                    .as(JsonResponse.class);

            System.out.println(response.body());
        } catch (IOException e) {
            String message = String.format(ERROR_CONNECTION, e.getMessage());
        }
    }

    private static void getTweets(Query query, Twitter twitter, String mode) {
        boolean getTweets = true;
        long maxId = 0;
        long whileCount = 0;

        while (getTweets) {
            try {
                QueryResult result = twitter.search(query);
                if (result.getTweets() == null || result.getTweets().isEmpty()) {
                    getTweets = false;
                } else {
                    System.out.println("***********************************************");
                    System.out.println("Gathered " + result.getTweets().size() + " tweets");
                    int forCount = 0;
                    for (Status status : result.getTweets()) {
                        if (whileCount == 0 && forCount == 0) {
                            sinceId = status.getId();//Store sinceId in database
                            System.out.println("sinceId= " + sinceId);
                        }
                        System.out.println("Id= " + status.getId());
                        System.out.println("@" + status.getUser().getScreenName() + " : " + status.getUser().getName() + "--------" + status.getText());
                        if (forCount == result.getTweets().size() - 1) {
                            maxId = status.getId();
                            System.out.println("maxId= " + maxId);
                        }
                        System.out.println("");
                        forCount++;
                    }
                    numberOfTweets = numberOfTweets + result.getTweets().size();
                    query.setMaxId(maxId - 1);
                }
            } catch (TwitterException te) {
                System.out.println("Couldn't connect: " + te);
                System.exit(-1);
            } catch (Exception e) {
                System.out.println("Something went wrong: " + e);
                System.exit(-1);
            }
            whileCount++;
        }
        System.out.println("Total tweets count=======" + numberOfTweets);
    }

    @Test
    public void getTweets() {
        int numberOfThreads = 4;
        int numberOfRequests = 40;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);


        Date start = new Date();
        for (int i = 0; i < numberOfThreads; i++) {
            Runnable worker = new MyRunnable(numberOfRequests);
            executor.execute(worker);
        }
        executor.shutdown();

        // Wait until all threads are finish
        while (!executor.isTerminated()) {
        }

        System.out.println("\nFinished all threads");
        Date stop = new Date();
        Long duration = stop.getTime() - start.getTime();
        System.out.println("duration = " + duration);

    }


    public class MyRunnable implements Runnable {
        List<Long> durations = new ArrayList<>();
        int numberOfRequests;

        public MyRunnable(int numberOfRequestst) {
            System.out.println("Creating Runnable: " + this.toString());
            this.numberOfRequests = numberOfRequests;
        }

        @Override
        public void run() {

            String result = "";
            int code = 200;
            try {
                for (int i = 0; i < numberOfRequests; i++) {
                    System.out.println("i = " + i);
                    Date start = new Date();
                    JsonResponse response = getResponse();
                    code = response.status();
                    if (code == 200) {
                        System.out.println("got 200 ok");
                    }
                    Date stop = new Date();
                    Long duration = stop.getTime() - start.getTime();
                    System.out.println("duration = " + duration);
                    //durations.add(duration);
                }
            } catch (Exception e) {
                System.out.println("got exception");
            }

            //Long sum = durations.stream().mapToLong(Long::longValue).sum();
            // System.out.println(Thread.currentThread().getName() + "\t:actualAvarege = " + actualAvarege + "ms");
        }
    }


    private static JsonResponse getResponse() throws IOException {

        JdkRequest jdkRequest = new JdkRequest("https://www.arbetsformedlingen.se/rest/pbapi/af/v1/version/");
        String ERROR_CONNECTION = "Failed to connect to AWS: %s";

        try {
            return jdkRequest
                    .uri()
                    //  .queryParam("orginalord", orginalord)
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
