package deliverable_one;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import utils.InitRepo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static deliverable_one.CountFixedTickets.PATH;

public class CountAllCommit {

    private CountAllCommit() {
    }

    /**
     * Metodo che restituisce tutti i commit
     *
     * @throws IOException:
     * @throws NoHeadException:
     * @return : map<String, Integer>. key: anno-mese; value: conteggio dei commit totali per anno-mese
     */
    public static Map<String, Integer> retrieveCommits() throws IOException, GitAPIException {
        ArrayList<RevCommit> allCommit = new ArrayList<>();
        List<Calendar> allFirstLastDate = new ArrayList<>();
        Map<String, Integer> commitYearMonth = new HashMap<>();

        for (String path : PATH) {
            Repository repository = InitRepo.repository(path);
            Git git = new Git(repository);

            Calendar firstCommit = Calendar.getInstance();
            Calendar lastCommit = Calendar.getInstance();

            CountFixedTickets.firstLastCommit(git, firstCommit, lastCommit);

            Iterable<RevCommit> logs = git.log().call();

            for (RevCommit rev : logs) {
                allCommit.add(rev);
            }

            CountFixedTickets.getAllFirstLast(firstCommit, lastCommit, allFirstLastDate);
            countAllCommits(allCommit, commitYearMonth);
        }

        List<String> keysSorted = CountFixedTickets.addMissingDate(allFirstLastDate);
        return CountFixedTickets.addAllFixedTickets(keysSorted, commitYearMonth);
    }

    /**
     * Metodo per ottenere le date di ogni commit
     *
     * @param allCommit:       lista di tutti i commit
     * @param commitYearMonth: chiave: anno-mese; value: conteggio dei commit totali per anno-mese
     */
    public static void countAllCommits(List<RevCommit> allCommit, Map<String, Integer> commitYearMonth) {

        for (RevCommit commit : allCommit) {

            Date lastCommitHash = commit.getAuthorIdent().getWhen();
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-M");

            String yearMonth = simpleDateformat.format(lastCommitHash);

            CountFixedTickets.countFixedTickets(yearMonth, commitYearMonth);
        }
    }
}
