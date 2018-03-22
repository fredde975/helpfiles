
Inspireras av koden från : http://jkoder.com/twitter-search-api-get-tweets-and-tweets-count-hashtag-java-client-twitter4j/


nuläge
======


Frågor
======

Vad är sinceId= 976936034683510785 egentligen?
===============================================
https://stackoverflow.com/questions/6412188/what-exactly-does-since-id-and-max-id-mean-in-the-twitter-api


since_id and max_id are both very simple parameters you can use to limit what you get back from the API. From the docs:

since_id - Returns results with an ID greater than (that is, more recent than) the specified ID. There are limits to the
number of Tweets which can be accessed through the API. If the limit of Tweets has occured since the since_id,
the since_id will be forced to the oldest ID available. max_id - Returns results with an ID less
than (that is, older than) or equal to the specified ID.

So, if you have a given tweet ID, you can search for older or newer tweets by using these two parameters.

count is even simpler -- it specifies a maximum number of tweets you want to get back, up to 200.

Unfortunately the API will not give you back exactly what you want -- you cannot specify a date/time when querying user_timeline -- although you can specify one when using the search API. Anyway, if you need to use user_timeline, then you will need to poll the API, gathering up tweets, figuring out if they match the parameters you desire, and then calculating your stats accordingly.






Hur funkar egentligen Objekten i Twitter: https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/intro-to-tweet-json




Varför får jag tweet count = 851 om jag sätter count = 10?
==========================================================
Total tweets count=======851


 sets the number of tweets to return per page, up to a max of 100
     *
     * @param count the number of tweets to return per page
     */
    public void setCount(int count) {
        this.count = count;
    }


    Query queryMax = new Query(hashTag);
            queryMax.setCount(count);