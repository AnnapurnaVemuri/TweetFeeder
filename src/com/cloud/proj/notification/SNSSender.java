package com.cloud.proj.notification;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;

public class SNSSender {
	private String topic;
	private AmazonSNSClient service;
	private CreateTopicRequest createReq;
	private CreateTopicResult createRes;
	
	public SNSSender(String topic) {
		this.topic = topic;
		initSNS();
	}

	private void initSNS() {
		AWSCredentials credentials = null;
		try {
			credentials = new PropertiesCredentials(SNSSender.class.getResourceAsStream("AwsCredentials.properties"));
	    } catch (Exception e) {
	    	throw new AmazonClientException("Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct location, and is in valid format.", e);
	    }
        service = new AmazonSNSClient(credentials);
        createReq = new CreateTopicRequest().withName(topic);
        createRes = service.createTopic(createReq);
	}
	
	public void sendNotificationMessage(String message) {
        PublishRequest publishReq = new PublishRequest()
            .withTopicArn(createRes.getTopicArn())
            .withMessage(message);
        service.publish(publishReq);
	}
}
