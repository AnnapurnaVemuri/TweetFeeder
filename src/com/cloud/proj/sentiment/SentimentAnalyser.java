package com.cloud.proj.sentiment;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class SentimentAnalyser {

	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {
		SentimentAnalyser sent = new SentimentAnalyser();
		System.out.println("Testing  - Send Http GET request");
		System.out.println(sent.getSentimentDummy("i am eating."));
	}
	
	public float getSentimentDummy(String tweet) {
		Random r = new Random();
		int v = r.nextInt(2);
		System.out.println(v);
		if (v==0)
			return r.nextFloat();
		else
			return -r.nextFloat();
	}

	public Float sendGet(String tweet) throws Exception {
		String url = "http://access.alchemyapi.com/calls/text/TextGetTextSentiment?apikey=4d6d3f04e0cd5a90fe9f0a140ba5c75cb1e2022a&text="+tweet;
		url = url.replaceAll(" ", "%20");
		URL obj = new URL(url);
		System.out.println(url);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
		String resp = response.toString();
		Document doc = XmlUtil.loadXML(resp);
		Node nl = XmlUtil.getNode(doc, "/results/docSentiment/score");
		Float sentiment = Float.parseFloat(nl.getTextContent());
		return sentiment;
	}
}
