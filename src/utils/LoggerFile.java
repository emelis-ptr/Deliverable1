package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class LoggerFile {
	
	private static final String PATHLOG = "logFilePath.properties";
	private static final String PATH = "PATH";
	
	private static LoggerFile fileLogger = null;	//Istanza del singleton
	private Logger logger;								
	
	//Costruttore per inizializzare gli attribuiti
	private LoggerFile() {
		Properties propertiesLog = new Properties();
		try (FileInputStream fileInput = new FileInputStream(PATHLOG)){
			propertiesLog.load(fileInput);
		} catch (FileNotFoundException e) {
			LoggerFile.getLogger().error("Error FileNotFounfException");
			e.printStackTrace();
		} catch (IOException e) {
			LoggerFile.getLogger().error("IOException");
			e.printStackTrace();
		}
		
		String logFilePath = propertiesLog.getProperty(PATH);
		System.setProperty("java.util.logging.config.file", logFilePath);
		
	}
	
	
	public void info (String message) {
		logger.info(message);
	}
	
	public void warning (String message) {
		logger.warn(message);
	}
	
	public void error (String message) {
		logger.error(message);
	}
	
	//Metodo "getIstance()"
	public static LoggerFile getLogger() { 
		if(fileLogger == null) fileLogger = new LoggerFile();
		return fileLogger;
	}
}
