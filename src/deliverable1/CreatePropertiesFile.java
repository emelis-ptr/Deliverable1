package deliverable1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class CreatePropertiesFile {

	private static final String PATHREPOSITORY = "path_repository.properties";
	private static final String PATH = "PATH";
	
	/**Crea il file properties
	 * @throws IOException
	 */
	public static void createPropertiesFile() throws IOException {
	Properties prop = new Properties();
	prop.setProperty("filePath", "/myPath/");
	FileOutputStream fos = new FileOutputStream("path_repository.properties");
	fos.close();
	}
	
	/**
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Repository repository() throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(PATHREPOSITORY));
		String repoPath = properties.getProperty(PATH);
		
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
	    Repository repository = repositoryBuilder.setGitDir(new File(repoPath)).setMustExist(true).build();
	    
		return repository;
	}
}
