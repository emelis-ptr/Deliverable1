package deliverable_one;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONObject;
import org.json.JSONArray;
import utils.JsonUtils;
import utils.LogFile;

import static utils.Costants.*;

public class RetrieveTicketsID {

    public static void main(String[] args) {
        LogFile.setupLogger("Deliverable1");

        int j;
        int i = 0;
        int total;

        ArrayList<String> ticketsID= new ArrayList<>();
        HashMap<String, ArrayList<RevCommit>> commitsTicket = new HashMap<>();

        //Get JSON API for closed bugs w/ AV in the project
        do {
            //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
            j = i + 1000;
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + PROJ_NAME + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                    + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                    + i + "&maxResults=" + j;

            JSONObject json = JsonUtils.readJsonFromUrl(url);

            JSONArray issues = json.getJSONArray(ISSUES);
            total = json.getInt(TOTAL);

            for (; i < total && i < j; i++) {
                //Iterate through each bug
                String ticketName = issues.getJSONObject(i%1000).get(KEY).toString();
                ticketsID.add(ticketName);
                commitsTicket.put(ticketName, new ArrayList<>());
            }
        } while (i < total);

        if(!ticketsID.isEmpty()) {
            try {
                CountFixedTickets.retrieveCommitTicketsID(ticketsID, commitsTicket);
            } catch (IOException | NoHeadException e) { e.printStackTrace(); }
        }
    }

}
