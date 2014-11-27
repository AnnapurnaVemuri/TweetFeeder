package com.cloud.proj.db.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cloud.proj.commons.Tweets;

public class DataBaseHelper {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    private static final String DB_URL = "jdbc:mysql://clouddb.c64omft7fp1u.us-west-2.rds.amazonaws.com:3306/CloudDB?user=madhuripalle&password=madhuripalle";
	private static Statement stmt=null;
	private Connection connection = null;
	
	public DataBaseHelper() {
		this.connection = createConn();
	}

	  public boolean batchInsert(List<Tweets> twitterStatusList) {
	    PreparedStatement statement = null;
	    try {  
	      String insertStatement = "INSERT INTO TwitData(userid,username,status,latitude,longitude,category) VALUES(?,?,?,?,?,?)";
	      statement = connection.prepareStatement(insertStatement);
	      for (Tweets status : twitterStatusList) {
	        statement.setLong(1, status.getUserId());
	        statement.setString(2, status.getUserName());
	        statement.setString(3, status.getStatus());
	        statement.setDouble(4, status.getLatitude());
	        statement.setDouble(5, status.getLongitude());
	        statement.setString(6, status.getCategory());
	        statement.addBatch();
	      }
	      statement.executeBatch();
	      connection.commit();
	    } catch (SQLException e) {
	      e.printStackTrace();
	      return false;
	    } finally {
	      if (statement != null) {
	        try {
	          statement.close();
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	    return true;
	  }
	  
	public Connection createConn(){
		Connection connection = null;
	    try {
		      Class.forName(JDBC_DRIVER).newInstance();
		      connection = DriverManager.getConnection(DB_URL);
		      connection.setAutoCommit(false);
		      System.out.println("Connected to DB");
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
	    return connection;
	}
	
	public List<Tweets> getAllFeeds(int count) {
		List<Tweets> tc = new ArrayList<Tweets>();
		try {
			if (connection == null) {
				connection = createConn();
			}
			stmt = connection.createStatement();
			String sql = "SELECT * FROM TwitData limit " + count;
			ResultSet rs = stmt.executeQuery(sql);
		
			while(rs.next()){	 
				Tweets t = new Tweets( rs.getLong("userid"),  rs.getString("username"), rs.getString("status"), rs.getDouble("latitude"),rs.getDouble("longitude"),rs.getString("category"));
				tc.add(t);
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tc;
	}
		
	public List<Tweets> getClusteredFeeds(int zoom, int count) {
		List<Tweets> tc = new ArrayList<Tweets>();
		try {
			if (connection == null) {
				connection = createConn();
			}
			stmt = connection.createStatement();
			String sql = "SELECT userid, username, status, round(latitude,"+zoom+") as lat, round(longitude,"+zoom+") as longi, category FROM TwitData limit " + count;
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				Tweets t = new Tweets(rs.getLong("userid"),  rs.getString("username"), rs.getString("status"), rs.getDouble("lat"),rs.getDouble("longi"), rs.getString("category"));
				tc.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tc;
	}
	
	public List<Tweets> getClusteredFeedsByTag(String hashTag, int zoom, int count) {
		List<Tweets> tc = new ArrayList<Tweets>();
		try {
			if (connection == null) {
				connection = createConn();
			}
			stmt = connection.createStatement();
			String sql = "SELECT  userid, username, status, round(latitude,"+zoom+") as lat, round(longitude,"+zoom+") as longi FROM TwitData where category='"+ hashTag +"' limit " + count;
			ResultSet rs = stmt.executeQuery(sql);
	
			while(rs.next()){
				Tweets t=new Tweets(rs.getLong("userid"),  rs.getString("username"), rs.getString("status"), rs.getDouble("lat"),rs.getDouble("longi"), hashTag);
				tc.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tc;
	}

	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void purgeTweets(int size) {
		try {
			connection.prepareStatement("DELETE FROM TwitData LIMIT " + size).execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
