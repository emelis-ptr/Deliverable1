package deliverable1;

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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;


public class RetrieveCommitTicketsID {
		
	 private Calendar firstCommit;
		private Calendar lastCommit;
		private Map<String, Integer> commitYearMonth;
     
		private RetrieveCommitTicketsID() {
			this.firstCommit = Calendar.getInstance();
			this.lastCommit  = Calendar.getInstance();
			this.commitYearMonth = new HashMap<>();
		}
		
   	
		/** Metodo che verifica se un commit contiene un ticketID
		 * @param ticketsID
		 * @throws IOException
		 * @throws JGitInternalException
		 * @throws NoHeadException
		 */
		public static void retrieveCommitTicketsID(List<String> ticketsID) throws IOException, JGitInternalException, NoHeadException {
		
	    Repository repository = CreatePropertiesFile.repository();
	    
	   	Git git = new Git(repository);
		
	   	RetrieveCommitTicketsID callNoStaticMethod = new RetrieveCommitTicketsID();
	   	
		//Trova tutti i commit che hanno al suo interno l'ID del ticket
		 for(String ticketID: ticketsID) {
			Iterable<RevCommit> logs = git.log().call();
			Iterator<RevCommit> iterator =logs.iterator();
			ArrayList<RevCommit> list = new ArrayList<>();
			
			callNoStaticMethod.firstLastCommit(git);
			 
	        while(iterator.hasNext()) {
	        	
	    		     RevCommit rev = iterator.next(); 
	    		     
	    		     if (rev.getFullMessage().contains(ticketID)) {
	    	        	list.add(rev);
	    	        		 	                 }
	    	           }
	      	
	        if(!list.isEmpty()) {
	        	callNoStaticMethod.lastcommitDate(list);
		    	 	
	         }    
	        
	      }
			
		 callNoStaticMethod.writeFile();
		
	}

	/**Trovare l'anno e mese dell'ultimo commit di un TicketID
	 * @param list
	 * @throws NoHeadException
	 * @throws JGitInternalException
	 */
	private void lastcommitDate(ArrayList<RevCommit> list) throws NoHeadException, JGitInternalException {
		
		RevCommit lastCommitTickets = list.get(0);      //Prende l'ultimo commit di un TicketID
		 
		Date lastCommitHash = lastCommitTickets.getAuthorIdent().getWhen();
     SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-M");
     
		String yearMonth = simpleDateformat.format(lastCommitHash);
		
		countFixedTickets(yearMonth);
		
	 }
	 
	/**Conta quanti ticket vengono chiusi in un determinato anno_mese
	 * @param yearMonth
	 */
	private void countFixedTickets(String yearMonth) {
		
		if(commitYearMonth.containsKey(yearMonth)) {
			
			commitYearMonth.put(yearMonth,  commitYearMonth.get(yearMonth) + 1);
						
		}else {		
			
			commitYearMonth.put(yearMonth,  Integer.valueOf(1));
			
		}	
	}	
	
   /**Viene costruito un arco temporale dalla prima data del primo commit fino all'ultimo
   * @return
   */
   private ArrayList<String> addMissingDate()  {
 	
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
 
  /**Metodo che scrive i dati ottenuti in un file csv
  * @throws IOException
  */
 private void writeFile() throws IOException{
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
  
    /**Metodo che prende il primo commit e l'ultimo del progetto
   * @param git
   * @throws NoHeadException
   * @throws JGitInternalException
   */
  private void firstLastCommit(Git git) throws NoHeadException, JGitInternalException {
 	 Iterable<RevCommit> logs = git.log().call();
 	 ArrayList<Date> allCommitDate = new ArrayList<>();
 	 
 	 Boolean first = Boolean.getBoolean(" ");
		 Boolean last = Boolean.getBoolean(" ");
 	 
 	 for(RevCommit commit: logs) {
		 Date allCommit = commit.getAuthorIdent().getWhen();
	    
	     allCommitDate.add(allCommit);
	     
	          }
 	 
 	 //Ottenere il primo e ultimo commit del progetto
 	 for (int i=0; i<allCommitDate.size(); i++) {
 		 if(Boolean.TRUE.equals(first)) {
 			 allCommitDate.get(allCommitDate.size()-1);
 			 firstCommit.setTime(allCommitDate.get(allCommitDate.size()-1));
 			 first = false;
 				    	 }
 		 else { first = true;}
 		 if(Boolean.TRUE.equals(last)) {
 			 lastCommit.setTime(allCommitDate.get(0));
 			 last = false;
 			    	    }
 		 else { last = true;}
	       }
   }

}