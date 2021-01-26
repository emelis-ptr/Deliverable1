package deliverable;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import utils.JsonUtils;

import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.json.JSONArray;

/**
 *  Recuperare tickets tramite URL del progetto Parquet di tipo bug
 */

public class RetrieveIDTickets {
	
	private static final String ISSUE = "issues";
    private static final String TOTAL = "total";
	private static final String KEY = "key";
	 
	 
	     public static void main(String[] args) throws IOException, JSONException, NoHeadException, JGitInternalException {
	    	  
        	   String projName ="Parquet";
    		   ArrayList<String> ticketsID= new ArrayList<>();
    		   
    		   Integer j = 0;
    		   Integer i = 0;
    		   Integer total = 1;
    	      //Get JSON API for closed bugs w/ AV in the project
    	      do {
    	         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
    	         j = i + 1000;
    	         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
    	                + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
    	                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
    	                + i.toString() + "&maxResults=" + j.toString();
    	         JSONObject json = JsonUtils.readJsonFromUrl(url);
    	         JSONArray issues = json.getJSONArray(ISSUE);
    	         total = json.getInt(TOTAL);
    	         for (; i < total && i < j; i++) {
    	            //Iterate through each bug
    	            String ticket = issues.getJSONObject(i%1000).get(KEY).toString();
    	            ticketsID.add(ticket);
    	            
    	         }  
    	      } while (i < total);
    	      if(!ticketsID.isEmpty()) {
    	    	 
    	    	  RetrieveCommitTicketsID.retrieveCommitTicketsID(ticketsID);
    		  }    
    	   }
}
