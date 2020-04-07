package Deliverable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class CreatePropertiesFile {

	public static void main(String[] args) throws IOException {
		
		//Crea il file properties
	Properties prop = new Properties();

	prop.setProperty("filePath", "/myPath/");

	FileOutputStream fos = new FileOutputStream("path_repository.properties");
	
	fos.close();
	
	}
}
