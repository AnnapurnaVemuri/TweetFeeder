package com.cloud.proj.sentiment;

import java.util.List;

import com.cloud.proj.commons.TweetConstants;
import com.cloud.proj.message.queue.SQSReceiver;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SentimentWorker implements Runnable {
	private Thread thread;
	private volatile boolean isStop = false;
	private SQSReceiver reciever;
	private SentimentAnalyser analyser = new SentimentAnalyser();
	
	public SentimentWorker() {
		reciever = new SQSReceiver(TweetConstants.QUEUE_NAME);
	}
	
	@Override
	public void run() {
		runSentimentOnMessages();
	}
	
	private void runSentimentOnMessages() {
		while(!isStop) {
			List<JsonObject> messages = reciever.receiveMessages();
			for (JsonObject message: messages) {
				String status = message.get("status").getAsString();
				if (status != null && status.length() > 0) {
					try {
						Float sentiment = analyser.sendGet(status);
						message.addProperty("weight", sentiment);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			JsonArray arr = new JsonArray();
			for (JsonObject m : messages) {
				arr.add(m);
			}
			String notificationMsg = arr.toString();
		}	
	}

	public void start() {
	    thread = new Thread(this, "SentimentWorker");
	    System.out.println("Initialized thread to get sentiment of tweets");
	    thread.start();
	}

	public void stop() {
		isStop = true;
	}
	
}
