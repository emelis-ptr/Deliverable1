package deliverable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
		
        private static Calendar FirstCommit = Calendar.getInstance();
		private static Calendar LastCommit = Calendar.getInstance();
		private static Map<String, Integer> commitYearMonth = new HashMap<>();
        
      	public static void RetrieveCommitTicketsID(List<String> TicketsID) throws IOException, JGitInternalException, NoHeadException {
		
	    //Contiene il path del repository
		Properties properties = new Properties();
		properties.load(new FileInputStream("path_repository.properties"));
		String repo_path = properties.getProperty("PATH");
		
		//Apre il repository
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
	    Repository repository = repositoryBuilder.setGitDir(new File(repo_path)).setMustExist(true).build();
	    
	    //System.out.println("Having repository: " + repository.getDirectory());
	    
		Git git = new Git(repository);
		
		//Trova tutti i commit che hanno al suo interno l'ID del ticket
		 for(String ticketID: TicketsID) {
			Iterable<RevCommit> logs = git.log().call();
			Iterator<RevCommit> iterator =logs.iterator();
			ArrayList<RevCommit> list = new ArrayList<>();
			
			FirstLast(git);
			 
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
        
		String year_month = simpleDateformat.format(lastCommitHash);
		
		CountFixedTickets(year_month);
		
	 }
	 

	/*
	 * Conta quanti ticket vengono chiusi in un determinato anno_mese
	 * 
	 */

	private static void CountFixedTickets(String year_month) {
		
		if(commitYearMonth.containsKey(year_month)) {
			
			commitYearMonth.put(year_month,  commitYearMonth.get(year_month) + 1);
			//System.out.println(totalTicket);
			
		}else {		
			
			commitYearMonth.put(year_month,  Integer.valueOf(1));
			
		}	
	}	
		
		   
    /*
   	 * Viene costruito un arco temporale dalla prima data del primo commit fino all'ultimo
   	 * 
   	 */
    
    private static ArrayList<String> AddMissingDate()  {
    	
    	 ArrayList<String> sortedKeys= new ArrayList<>();
    	 	
    	 int firstCommitYear= FirstCommit.get(Calendar.YEAR);
		 int firstCommitMonth= FirstCommit.get(Calendar.MONTH) +1;
		 int lastCommitYear= LastCommit.get(Calendar.YEAR);
		 int lastCommitMonth= LastCommit.get(Calendar.MONTH) +1 ;
		 
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
      
     // System.out.println("Date " + sortedKeys); 
      return sortedKeys;
      
    }
    
    /*
	 * Scrivere i dati ottenuti in un file Excel
	 * 
	 */
    
     private static void writeFile() throws IOException{
    	 ArrayList<String> keysSorted= AddMissingDate();
    	 
    	 try (PrintWriter pw =new PrintWriter("TicketsAnalysis.csv"))  {
    		 
    		 StringBuilder sb = new StringBuilder();
    		 
    		 sb.append("Anno-Mese");
    		 sb.append(",");
    		 sb.append("Ticket chiusi");
    		 sb.append("\n");
    		 
    		 for (String key: keysSorted) {
                 
 				ArrayList<String> rowData= new ArrayList<>();
 				rowData.add(key);
 				//rowData.add(Integer.toString(commitYearMonth.get(key)));
 				
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
    		    pw.close();
    	 }

    }
     
     /*
 	 * Prende il primo commit e l'ultimo commit del progetto
 	 * 
 	 */
     
     public static void FirstLast(Git git) throws NoHeadException, JGitInternalException {
    	 Iterable<RevCommit> logs = git.log().call();
    	 ArrayList<Date> AllCommitDate = new ArrayList<Date>();
    	 SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-M");
    	 
    	 Boolean firstCommit = true;
		 Boolean lastCommit = true;
    	 
    	 for(RevCommit commit: logs) {
		 Date allCommit = commit.getAuthorIdent().getWhen();
	    
	     AllCommitDate.add(allCommit);
	     
	     //System.out.println(AllCommitDate);
	        }
    	 
    	 //Ottenere il primo e ultimo commit del progetto
    	 for (int i=0; i<AllCommitDate.size(); i++) {
    		 if(firstCommit) {
    			 AllCommitDate.get(AllCommitDate.size()-1);
    			 FirstCommit.setTime(AllCommitDate.get(AllCommitDate.size()-1));
    			//System.out.println(FirstCommit);
	    	 }
    		 if(lastCommit) {
    			 LastCommit.setTime(AllCommitDate.get(0));
    			//System.out.println(LastCommit);
    	    	 }
	     }
    	 
    	   }
}
	

