package com.cloud.proj.message.queue;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;

public class SimpleQueueService {
	protected String queueName, queueUrl;
	protected AmazonSQS sqs;
	
	public SimpleQueueService(String queueName) {
		this.queueName = queueName;
		initializeQueue();
	}
	
	private void initializeQueue() {
		AWSCredentials credentials = null;
		try {
			credentials = new PropertiesCredentials(SimpleQueueService.class.getResourceAsStream("AwsCredentials.properties"));
	    } catch (Exception e) {
	    	throw new AmazonClientException("Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct location, and is in valid format.", e);
	    }
		sqs = new AmazonSQSClient(credentials);
	    Region usWest2 = Region.getRegion(Regions.US_WEST_2);
	    sqs.setRegion(usWest2);
	        
	    // Create a queue
	    System.out.println("Creating a new SQS queue called "+queueName);
	    CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
	    queueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
	}
	
}
