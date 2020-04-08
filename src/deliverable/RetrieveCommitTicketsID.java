package deliverable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class RetrieveCommitTicketsID {
		
        private static Calendar firstCommit = Calendar.getInstance();
		private static Calendar lastCommit = Calendar.getInstance();
		private static Map<String, Integer> commitYearMonth = new HashMap<>();
        
		private RetrieveCommitTicketsID() {}
      	
		public static void retrieveCommitTicketsID(List<String> TicketsID) throws IOException, JGitInternalException, NoHeadException {
		
	    Properties properties = new Properties();
		properties.load(new FileInputStream("path_repository.properties"));
		String repoPath = properties.getProperty("PATH");
		
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
	    Repository repository = repositoryBuilder.setGitDir(new File(repoPath)).setMustExist(true).build();
	    
	   	Git git = new Git(repository);
		
		//Trova tutti i commit che hanno al suo interno l'ID del ticket
		 for(String ticketID: TicketsID) {
			Iterable<RevCommit> logs = git.log().call();
			Iterator<RevCommit> iterator =logs.iterator();
			ArrayList<RevCommit> list = new ArrayList<>();
			
			firstLastCommit(git);
			 
	        while(iterator.hasNext()) {
	        	
	    		     RevCommit rev = iterator.next(); 
	    		     
	    		     if (rev.getFullMessage().contains(ticketID)) {
	    	        	list.add(rev);
	    	        		 	                 }
	    	           }
	      	
	        if(!list.isEmpty()) {
	        	lastcommitDate(list);
		    	 	
	         }    
	        
	      }
			
		writeFile();
		
	}

	
	/*
	 * Trovare l'anno e mese dell'ultimo commit di un TicketID
	 * 
	 */
	
	private static void lastcommitDate(ArrayList<RevCommit> list) throws NoHeadException, JGitInternalException {
		
		RevCommit lastCommit = list.get(0);      //Prende l'ultimo commit di un TicketID
		 
		Date lastCommitHash = lastCommit.getAuthorIdent().getWhen();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-M");
        
		String yearMonth = simpleDateformat.format(lastCommitHash);
		
		countFixedTickets(yearMonth);
		
	 }
	 

	/*
	 * Conta quanti ticket vengono chiusi in un determinato anno_mese
	 * 
	 */

	private static void countFixedTickets(String yearMonth) {
		
		if(commitYearMonth.containsKey(yearMonth)) {
			
			commitYearMonth.put(yearMonth,  commitYearMonth.get(yearMonth) + 1);
						
		}else {		
			
			commitYearMonth.put(yearMonth,  Integer.valueOf(1));
			
		}	
	}	
		
		   
    /*
   	 * Viene costruito un arco temporale dalla prima data del primo commit fino all'ultimo
   	 * 
   	 */
    
    private static ArrayList<String> addMissingDate()  {
    	
    	 ArrayList<String> sortedKeys= new ArrayList<>();
    	 	
    	 int firstCommitYear= firstCommit.get(Calendar.YEAR);
		 int firstCommitMonth= firstCommit.get(Calendar.MONTH) +1;
		 int lastCommitYear= lastCommit.get(Calendar.YEAR);
		 int lastCommitMonth= lastCommit.get(Calendar.MONTH) +1 ;
		 
		 while(firstCommitYear <= lastCommitYear) {
		
		    if(firstCommitYear == lastCommitYear) {
			
			    while(firstCommitMonth <= lastCommitMonth) {
			 	
				   sortedKeys.add(firstCommitYear+"-"+firstCommitMonth);
				   firstCommitMonth++;
			  }				
		    } else {
		
			    while(firstCommitMonth <= 12) {
			    	
				    sortedKeys.add(firstCommitYear+"-"+firstCommitMonth);
				    firstCommitMonth++;
			}
			firstCommitMonth=1;				
		}
		firstCommitYear++;			
	}		
      
        return sortedKeys;
      
    }
    
    /*
	 * Scrivere i dati ottenuti in un file Excel
	 * 
	 */
    
     private static void writeFile() throws IOException{
    	 ArrayList<String> keysSorted= addMissingDate();
    	 
    	 try (PrintWriter pw =new PrintWriter("TicketsAnalysis.csv"))  {
    		 
    		 StringBuilder sb = new StringBuilder();
    		 
    		 sb.append("Anno-Mese");
    		 sb.append(",");
    		 sb.append("Ticket chiusi");
    		 sb.append("\n");
    		 
    		 for (String key: keysSorted) {
                 
 				ArrayList<String> rowData= new ArrayList<>();
 				rowData.add(key);
 				 				
 				if(commitYearMonth.containsKey(key)) {
					rowData.add(Integer.toString(commitYearMonth.get(key)));
				}
				else {
					rowData.add("0");
				}
 				
 			    sb.append(String.join(",", rowData));
 			    sb.append("\n");
 			    
 			   }
    		 
    		    pw.write(sb.toString());
    	 }

    }
     
     /*
 	 * Prende il primo commit e l'ultimo commit del progetto
 	 * 
 	 */
     
     public static void firstLastCommit(Git git) throws NoHeadException, JGitInternalException {
    	 Iterable<RevCommit> logs = git.log().call();
    	 ArrayList<Date> AllCommitDate = new ArrayList<>();
    	 
    	 Boolean FirstCommit = true;
		 Boolean LastCommit = true;
    	 
    	 for(RevCommit commit: logs) {
		 Date allCommit = commit.getAuthorIdent().getWhen();
	    
	     AllCommitDate.add(allCommit);
	     
	          }
    	 
    	 //Ottenere il primo e ultimo commit del progetto
    	 for (int i=0; i<AllCommitDate.size(); i++) {
    		 if(FirstCommit) {
    			 AllCommitDate.get(AllCommitDate.size()-1);
    			 firstCommit.setTime(AllCommitDate.get(AllCommitDate.size()-1));
    			 FirstCommit = false;
    				    	 }
    		 if(LastCommit) {
    			 lastCommit.setTime(AllCommitDate.get(0));
    			 LastCommit = false;
    			    	    	 }
	     }
    	 
    	   }
}
	

