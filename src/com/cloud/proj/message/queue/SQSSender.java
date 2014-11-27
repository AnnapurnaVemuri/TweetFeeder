package com.cloud.proj.message.queue;

import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSSender extends SimpleQueueService {
	
	public SQSSender(String queueName) {
		super(queueName);
	}

	public void sendMessage(String message) {	
	    System.out.println("Sending a message to "+queueName);
	    sqs.sendMessage(new SendMessageRequest(queueUrl, message));       
	}
}

