package deliverable_one;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import utils.InitRepo;
import utils.LogFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


public class CountFixedTickets {

    private CountFixedTickets() {
    }

    protected static final String[] PATH = {"PARQUET-FORMAT", "PARQUET-MR"};

    /**
     * Metodo che recupera tutti i commit che hanno al suo interno il nome del ticket
     *
     * @param ticketsID:     nome di ogni ticket
     * @param commitsTicket: key = nome del ticket; value = commits associato al ticket
     * @throws JGitInternalException:
     * @throws NoHeadException:
     * @throws IOException:
     */
    public static void retrieveCommitTicketsID(List<String> ticketsID, Map<String, ArrayList<RevCommit>> commitsTicket) throws JGitInternalException, NoHeadException, IOException {
        List<Calendar> allFirstLastDate = new ArrayList<>();
        Map<String, Integer> commitYearMonth = new HashMap<>();
        ArrayList<RevCommit> allCommit = new ArrayList<>();

        for (String path : PATH) {
            Repository repository = InitRepo.repository(path);
            Git git = new Git(repository);

            Calendar firstCommit = Calendar.getInstance();
            Calendar lastCommit = Calendar.getInstance();
            ArrayList<RevCommit> ticketsCommit = new ArrayList<>();

            //Trova tutti i commit che hanno al suo interno l'ID del ticket
            for (String ticketID : ticketsID) {
                Iterable<RevCommit> logs = git.log().call();
                Iterator<RevCommit> iterator = logs.iterator();

                firstLastCommit(git, firstCommit, lastCommit);

                while (iterator.hasNext()) {
                    RevCommit rev = iterator.next();
                    allCommit.add(rev);

                    if (rev.getFullMessage().contains(ticketID)) {
                        ticketsCommit.add(rev);
                        commitsTicket.get(ticketID).add(rev);
                    }

                }
            }
            getAllFirstLast(firstCommit, lastCommit, allFirstLastDate);
            lastcommitDate(commitsTicket, commitYearMonth);
        }

        List<String> keysSorted = addMissingDate(allFirstLastDate);
        Map<String, Integer> allProject = addAllFixedTickets(keysSorted, commitYearMonth);
        Map<String, Integer> allCommits = CountAllCommit.retrieveCommits();

        WriteFile.writeFile(allProject, allCommits);

        LogFile.infoLog("Bug-fixed: " + allProject);
        LogFile.infoLog("Total commits: " + allCommits);

    }

    /**
     * Metodo per ottenere il primo e ultimo commit del progetto
     *
     * @param git:         git
     * @param firstCommit: primo commit dell'intero progetto
     * @param lastCommit:  ultimo commit dell'intero progetto
     * @throws NoHeadException:
     * @throws JGitInternalException:
     */
    public static void firstLastCommit(Git git, Calendar firstCommit, Calendar lastCommit) throws NoHeadException, JGitInternalException {
        Iterable<RevCommit> logs = git.log().call();
        ArrayList<Date> allCommitDate = new ArrayList<>();

        boolean first = Boolean.getBoolean(" ");
        boolean last = Boolean.getBoolean(" ");

        //Prende le date di tutti i commit
        for (RevCommit commit : logs) {
            Date allCommit = commit.getAuthorIdent().getWhen();
            allCommitDate.add(allCommit);
        }

        //Ottenere il primo e ultimo commit del progetto
        for (int i = 0; i < allCommitDate.size(); i++) {
            if (Boolean.TRUE.equals(first)) {
                firstCommit.setTime(allCommitDate.get(allCommitDate.size() - 1));
                first = false;
            } else {
                first = true;
            }

            if (Boolean.TRUE.equals(last)) {
                lastCommit.setTime(allCommitDate.get(0));

                last = false;
            } else {
                last = true;
            }
        }
    }

    /**
     * Metodo che inserisce in una lista la prima e la ultima data per ogni repository del progetto
     *
     * @param firstCommit: primo commit dell'intero progetto
     * @param lastCommit:  ultimo commit dell'intero progetto
     * @param allDate:     lista delle prime e le ultime date di ogni repository
     */
    public static void getAllFirstLast(Calendar firstCommit, Calendar lastCommit, List<Calendar> allDate) {
        allDate.add(firstCommit);
        allDate.add(lastCommit);

        Collections.sort(allDate);
    }

    /**
     * Metodo che ottiene la data dell'ultimo commit di ogni ticket
     *
     * @param commitsTicket:   key = nome del ticket; value = commits associato al ticket
     * @param commitYearMonth: key = anno-mese; value = numero di ticket fixed associato ad ogni anno-mese
     * @throws JGitInternalException:
     */
    private static void lastcommitDate(Map<String, ArrayList<RevCommit>> commitsTicket, Map<String, Integer> commitYearMonth) throws JGitInternalException {

        for (Map.Entry<String, ArrayList<RevCommit>> entry : commitsTicket.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                RevCommit lastCommitTickets = entry.getValue().get(0);      //Prende l'ultimo commit di un TicketID

                Date lastCommitHash = lastCommitTickets.getAuthorIdent().getWhen();
                SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-M");

                String yearMonth = simpleDateformat.format(lastCommitHash);

                countFixedTickets(yearMonth, commitYearMonth);

            }
        }
    }

    /**
     * Metodo che inserisce le date mancanti nell'arco temporale del progetto
     *
     * @param allDate: lista delle prime e le ultime date di ogni repository
     * @return : lista delle date ordinate
     */
    public static List<String> addMissingDate(List<Calendar> allDate) {

        ArrayList<String> sortedKeys = new ArrayList<>();
        Calendar firstCommit = allDate.get(0);
        Calendar lastCommit = allDate.get(allDate.size() - 1);

        int firstCommitYear = firstCommit.get(Calendar.YEAR);
        int firstCommitMonth = firstCommit.get(Calendar.MONTH) + 1;
        int lastCommitYear = lastCommit.get(Calendar.YEAR);
        int lastCommitMonth = lastCommit.get(Calendar.MONTH) + 1;

        while (firstCommitYear <= lastCommitYear) {

            if (firstCommitYear == lastCommitYear) {

                while (firstCommitMonth <= lastCommitMonth) {
                    sortedKeys.add(firstCommitYear + "-" + firstCommitMonth);
                    firstCommitMonth++;
                }
            } else {

                while (firstCommitMonth <= 12) {

                    sortedKeys.add(firstCommitYear + "-" + firstCommitMonth);
                    firstCommitMonth++;
                }
                firstCommitMonth = 1;
            }
            firstCommitYear++;
        }

        return sortedKeys;
    }

    /**
     * Metodo che conta quanti ticket fixed ci sono per un dato anno-mese
     *
     * @param yearMonth:       anno-mese di ogni commit
     * @param commitYearMonth: key = anno-mese; value = numero di ticket fixed associato ad ogni anno-mese
     */
    public static void countFixedTickets(String yearMonth, Map<String, Integer> commitYearMonth) {

        if (commitYearMonth.containsKey(yearMonth)) {
            commitYearMonth.put(yearMonth, commitYearMonth.get(yearMonth) + 1);
        } else {
            commitYearMonth.put(yearMonth, 1);
        }

    }

    /**
     * Metodo che inserisce in un'unica map le chiavi e i valori dei progetti considerati
     *
     * @param keysSorted:      chiave anno-mese
     * @param commitYearMonth: key: anno-mese; value: conteggio dei ticket fixed. Per ogni progetto.
     */
    public static Map<String, Integer> addAllFixedTickets(List<String> keysSorted, Map<String, Integer> commitYearMonth) {
        Map<String, Integer> allProjectSorted = new LinkedHashMap<>();

        for (String key : keysSorted) {

            if (commitYearMonth.containsKey(key) && allProjectSorted.get(key) == null) {
                allProjectSorted.put(key, commitYearMonth.get(key));
            } else if (commitYearMonth.containsKey(key) && allProjectSorted.get(key) != null) {
                allProjectSorted.put(key, allProjectSorted.get(key) + commitYearMonth.get(key));
            } else {
                allProjectSorted.put(key, 0);
            }
        }
        return allProjectSorted;
    }

}



