package com.serverless;

import com.jcabi.http.Request;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import org.junit.Test;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class HandlerTest {
    static long sinceId = 0;
    static long numberOfTweets = 0;
    static final int TWEETS_PER_QUERY = 100;

    Map<String, WordItem> resultMap = new HashMap<>();


    @Test
    public void getATweetFromTwitter() {
        final String hashTag = "#bieber";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("SSVbNkmx7ugzaOfaSsG8iRSij")
                .setOAuthConsumerSecret("fBop27mqeaK8tcYp5hmL7vmSWSfbp3ys4qP14JgWyqMsh5UA6X")
                .setOAuthAccessToken("899157129722048512-YHceWk6N45aBm4g4rSrQH3EXxwPjaPG")
                .setOAuthAccessTokenSecret("hNI81vC3XbSp3tpiQ4m1dywL1KbJ0W3O0LqfcDwCsMAqO");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        Query query = new Query(hashTag);
        query.setCount(TWEETS_PER_QUERY);
        query.resultType(Query.ResultType.recent);
        query.setLang("en");

        getTweets2(query, twitter);
        query = null;


        List<WordItem> wordItems = resultMap.values().stream()
                .sorted()
                .limit(10)
                .collect(Collectors.toList());

        wordItems.stream().forEach(System.out::println);


    }


    private void getTweets(Query query, Twitter twitter) {
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

                        String text = status.getText();
                        List<String> ord = Arrays.asList(text.split("\\s+"));
                        ord.stream().forEach(word -> addWordToMap(word));


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
        System.out.println("Total tweets TWEETS_PER_QUERY=======" + numberOfTweets);


    }


    private void addWordToMap(String word) {
        if (resultMap.containsKey(word)) {
            WordItem item = resultMap.get(word);
            resultMap.replace(word, new WordItem(word, item.getCount() + 1));
        } else {
            resultMap.put(word, new WordItem(word, 1));
        }
    }

    private void getTweets2(Query query, Twitter twitter) {
        int MAX_QUERIES = 5;
        int TWEETS_PER_QUERY = 100;
        long maxID = -1;
        int totalTweets = 0;

        try {
            //	This is the loop that retrieve multiple blocks of tweets from Twitter
            for (int queryNumber = 0; queryNumber < MAX_QUERIES; queryNumber++) {

                //does not handle rate limits

                //	If maxID is -1, then this is our first call and we do not want to tell Twitter what the maximum
                //	tweet id is we want to retrieve.  But if it is not -1, then it represents the lowest tweet ID
                //	we've seen, so we want to start at it-1 (if we start at maxID, we would see the lowest tweet
                //	a second time...
                if (maxID != -1) {
                    query.setMaxId(maxID - 1);
                }

                //	This actually does the search on Twitter and makes the call across the network
                QueryResult r = twitter.search(query);

                //	If there are NO tweets in the result set, it is Twitter's way of telling us that there are no
                //	more tweets to be retrieved.  Remember that Twitter's search index only contains about a week's
                //	worth of tweets, and uncommon search terms can run out of week before they run out of tweets
                if (r.getTweets().size() == 0) {
                    break;            // Nothing? We must be done
                }


                //	loop through all the tweets and process them.  In this sample program, we just print them
                //	out, but in a real application you might save them to a database, a CSV file, do some
                //	analysis on them, whatever...
                for (Status s : r.getTweets())                // Loop through all the tweets...
                {
                    //	Increment our TWEETS_PER_QUERY of tweets retrieved
                    totalTweets++;

                    //	Keep track of the lowest tweet ID.  If you do not do this, you cannot retrieve multiple
                    //	blocks of tweets...
                    if (maxID == -1 || s.getId() < maxID) {
                        maxID = s.getId();
                    }

                    String text = s.getText();
                    List<String> ord = Arrays.asList(text.split("\\s+"));
                    ord.stream().forEach(word -> addWordToMap(word));
                }
            }


            System.out.printf("\n\nA total of %d tweets retrieved\n", totalTweets);
            //	That's all, folks!

        } catch (Exception e) {
            //	Catch all -- you're going to read the stack trace and figure out what needs to be done to fix it
            System.out.println("That didn't work well...wonder why?");

            e.printStackTrace();

        }
    }


    private static void getTweets3(Query query, Twitter twitter) {
        boolean getTweets = true;
        long maxId = 0;
        long whileCount = 0;


        try {
            QueryResult result = twitter.search(query);
//            result.getTweets().stream()
//                    .map()


        } catch (TwitterException e) {
            e.printStackTrace();
        }


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
                        System.out.println("Tweet text: " + status.getText());
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
    public void getATweet() {
        makeGetRequest("https://www.arbetsformedlingen.se/rest/pbapi/af/v1/version/");
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
