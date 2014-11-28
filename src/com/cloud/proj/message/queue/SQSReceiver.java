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
	List<Message> messages=new ArrayList<Message>();
		try{
		messageObjList = new ArrayList<JsonObject>();
		System.out.println("Receiving messages from " + queueName + "\n");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        Gson gson = new Gson();
        for (Message message : messages) {
        	System.out.println(message.getBody());
        	JsonObject[] objList = gson.fromJson(message.getBody(), JsonObject[].class);
        	for (JsonObject obj : objList) {
        		messageObjList.add(obj);
        		System.out.println(obj.toString());
        	}
        }
		

        // Delete a message
        System.out.println("Deleting the messages\n");
       
		}catch(Exception e){
			e.printStackTrace();
			
		}
		try{
		 for (Message message : messages) {
	            String messageRecieptHandle = message.getReceiptHandle();
	            sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle)); 	
	        }
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Could not delete messages");
		}
        return messageObjList;
	}
}
