package utils;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static utils.Costants.PATHREPOSITORY;

public class InitRepo {

    private InitRepo() {
    }

    /**
     * All'interno del file properties viene salvato il PATH del repository e
     * attraverso questo metodo si inizializza la repo
     *
     * @throws IOException:
     */
    public static Repository repository(String path) throws IOException {

        Properties properties = new Properties();

        try (FileInputStream fileInput = new FileInputStream(PATHREPOSITORY)) {
            properties.load(fileInput);
        } catch (Exception e) {
            LogFile.errorLog("Errore! Impossibile trovare percorso file.");
        }

        String repoPath;
        repoPath = properties.getProperty(path);

        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        return repositoryBuilder.setGitDir(new File(repoPath)).setMustExist(true).build();
    }

}
