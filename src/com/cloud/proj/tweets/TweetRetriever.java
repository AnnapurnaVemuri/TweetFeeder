package com.cloud.proj.tweets;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import com.cloud.proj.sentiment.SentimentWorker;

public class TweetRetriever {
	private static int batchSize = 1;
	private static int purgeBatchSize = 10;
	private static TweetGet getTweets = null;
	private static TweetPurger purgeTweets = null;
	private static List<SentimentWorker> sentimentWorkers = new ArrayList<SentimentWorker>();

	public static void main(String[] args) throws UnknownHostException {
		if (args.length > 0) {
			batchSize = Integer.parseInt(args[0]);	
		}
		if (args.length > 1) {
			purgeBatchSize = Integer.parseInt(args[1]);	
		}
		launchTweetGetter();
		//launchTweetPurger();
		launchSentimentWorkers(2);
		
		Signal.handle(new Signal("TERM"), new SignalHandler() {
			@Override
		    public void handle(Signal signal) {
				stopAllThread();
		    }
		});
	}

	protected static void stopAllThread() {
		if (getTweets != null) {
			getTweets.stop();
		}
		if (getTweets != null) {
			getTweets.stop();
		}
		if (sentimentWorkers.size() != 0) {
			for (SentimentWorker worker: sentimentWorkers) {
				worker.stop();
			}
		}
	}

	private static void launchSentimentWorkers(int num) {
		for (int i = 1; i < num; i++) {
			SentimentWorker worker = new SentimentWorker();
			worker.start();
			sentimentWorkers.add(worker);
		}
	}

	private static void launchTweetPurger() {
		purgeTweets = new TweetPurger(purgeBatchSize);
		purgeTweets.start();
	}

	private static void launchTweetGetter() {
		getTweets = new TweetGet(batchSize);
		getTweets.start();
	}
}
