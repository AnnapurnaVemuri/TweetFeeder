package com.cloud.proj.tweets;

import java.util.ArrayList;
import java.util.List;

import com.cloud.proj.commons.TweetConstants;
import com.cloud.proj.commons.Tweets;
import com.cloud.proj.db.utils.DataBaseHelper;
import com.cloud.proj.message.queue.SQSSender;
import com.google.gson.JsonArray;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.FilterQuery;

public class TweetGet implements Runnable {
	private TwitterStream twitterStream = null;
	private StatusListener listener = null;
	private Thread thread;
	private int batchSize;
	private List<Tweets> tweetsList;
	private DataBaseHelper helper;
	private SQSSender messageSender;
	private final String[] keywords = {"beiber","love","ebola","modi","1989","girl","suarez","apple","lol","god","boy","baby"};
	
	public TweetGet(int batchSize) {
		this.batchSize = batchSize;
		this.messageSender = new SQSSender(TweetConstants.QUEUE_NAME);
		this.helper = new DataBaseHelper();
		this.tweetsList = new ArrayList<Tweets>(batchSize);
	}
	
	@Override
	public void run() {
		insertTweetsToDB();
	}

	public void start() {
	    thread = new Thread(this, "TweetGet");
	    System.out.println("Initialized thread to get tweets");
	    thread.start();
	}
	
	public void stop() {
		stopTweetStream();
		updateDBAndAddToQueue();
		helper.closeConnection();
	}
	  
	public void stopTweetStream(){
		if (listener!=null)
			twitterStream.removeListener(listener);
 		twitterStream.shutdown();
		twitterStream=null;
	}
	
    public void insertTweetsToDB()  {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
        	.setOAuthConsumerKey("KZI4MpW52dWinfzjXlKfTrzKN")
            .setOAuthConsumerSecret("ZzGjkXws3Bp2wzCd7mFqzlE6GK0HXhwi4Xfm8o7Px8auVDFJgD")
            .setOAuthAccessToken("2848198209-fwp23E0HONFs0iQpGJt8ABPk8HM5J3XX8NrhaHS")
            .setOAuthAccessTokenSecret("97BANAvxYHf4j7tFSW9t24i91d2zrVrXVnYrqd6XUxoB9");
        
        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {
          @Override
          public void onStatus(Status status) {
            if (status.getGeoLocation() == null)
            	return;
            for (String filter : keywords) {
            	if( (status.getText().toLowerCase()).contains(filter) ) {
            		Tweets t = new Tweets(status.getUser().getId(),status.getUser().getScreenName(),status.getText(),status.getGeoLocation().getLatitude(),status.getGeoLocation().getLongitude(), filter);
            		tweetsList.add(t);
            	}
            }
            if (tweetsList.size() == batchSize) {
            	updateDBAndAddToQueue();
            	tweetsList.clear();
            }
          }

          @Override
          public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
          }

          @Override
          public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
          }

          @Override
          public void onScrubGeo(long userId, long upToStatusId) {
            System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
          }

          @Override
          public void onStallWarning(StallWarning warning) {
            System.out.println("Got stall warning:" + warning);
          }

          @Override
          public void onException(Exception ex) {
            ex.printStackTrace();
          }
        };
        
        FilterQuery fq = new FilterQuery();
        fq.track(keywords);
        
        twitterStream.addListener(listener);
        twitterStream.filter(fq);
        twitterStream.sample();
    }
    
    private void updateDBAndAddToQueue() {
        while (!helper.batchInsert(tweetsList)) {
          System.out.println("Update failed, Retrying");
        }
        System.out.println("Update successful");
        JsonArray array = new JsonArray();
        for (Tweets tweet: tweetsList) {
        	array.add(tweet.getJsonObject());
        }
        messageSender.sendMessage(array.toString());
    }
}