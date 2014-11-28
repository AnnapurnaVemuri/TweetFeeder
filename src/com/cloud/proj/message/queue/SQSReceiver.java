package com.cloud.proj.message.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SQSReceiver extends SimpleQueueService {
	
	public SQSReceiver(String queueName) {
		super(queueName);
	}
	
	public List<JsonObject> receiveMessages() {
		List<JsonObject> messageObjList =null;
		try{
		messageObjList = new ArrayList<JsonObject>();
		System.out.println("Receiving messages from " + queueName + "\n");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        Gson gson = new Gson();
        for (Message message : messages) {
        	JsonObject[] objList = gson.fromJson(message.getBody(), JsonObject[].class);
        	for (JsonObject obj : objList) {
        		messageObjList.add(obj);
        	}
        }

        // Delete a message
        System.out.println("Deleting the messages\n");
        for (Message message : messages) {
            String messageRecieptHandle = message.getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle)); 	
        }
		}catch(Exception e){
			e.printStackTrace();
		}
        return messageObjList;
	}
}
