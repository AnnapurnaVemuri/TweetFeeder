package com.cloud.proj.tweets;

import com.cloud.proj.db.utils.DataBaseHelper;

public class TweetPurger implements Runnable {
	int size;
	private Thread thread;
	private volatile boolean isStop = false;
	
	public TweetPurger(int size) {
		this.size = size;
	}
	
	@Override
	public void run() {
		while (!isStop) {
			purgeDB();
			try {
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

	public void start() {
	    thread = new Thread(this, "TweetPurger");
	    System.out.println("Initialized thread to purge tweets from DB");
	    thread.start();
	}

	private void purgeDB() {
		DataBaseHelper dbHelper = new DataBaseHelper();
		dbHelper.purgeTweets(size);
		dbHelper.closeConnection();
	}
	
	public void stop() {
		isStop = true;
	}
}
