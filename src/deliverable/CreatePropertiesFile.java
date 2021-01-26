package deliverable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class CreatePropertiesFile {

	private CreatePropertiesFile() {
	    throw new IllegalStateException("Utility class");
	  }
	
	private static final String PATHREPOSITORY = "path_repository.properties";
	private static final String PATH = "PATH";
	
	/**Crea il file properties
	 * @throws IOException
	 */
	public static void createPropertiesFile() throws IOException {
	Properties prop = new Properties();
	prop.setProperty("filePath", "/myPath/");
	FileOutputStream fos = new FileOutputStream(PATHREPOSITORY);
	fos.close();
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	public static Repository repository() throws IOException {
		Properties properties = new Properties();
		try (FileInputStream fileInput = new FileInputStream(PATHREPOSITORY)){
		properties.load(fileInput);
		} catch (Exception e) {System.out.println("Errore nel path");}
		
		String repoPath = properties.getProperty(PATH);
		
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
	    return repositoryBuilder.setGitDir(new File(repoPath)).setMustExist(true).build();
	    
	}
}
