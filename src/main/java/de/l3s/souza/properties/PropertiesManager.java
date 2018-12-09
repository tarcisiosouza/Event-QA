package de.l3s.souza.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {
	
	private int maxQueries;
	private String dataFolder;
	private String format;
	private int maxRelations;
	
	public PropertiesManager () {
		
		readPropFile();
		
	}
	
	public int getMaxRelations() {
		return maxRelations;
	}

	public int getMaxQueries() {
		return maxQueries;
	}

	public String getDataFolder() {
		return dataFolder;
	}

	public String isUrlFormat() {
		return format;
	}

	private void readPropFile ()
	{
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			
			String filename = "config.properties";
    		input = PropertiesManager.class.getClassLoader().getResourceAsStream(filename);
    		if(input==null){
    	            System.out.println("Sorry, unable to find " + filename);
    	            return;
    		}
		//	input = new FileInputStream("src/main/resources/config.properties");

			// load a properties file
			prop.load(input);

			format = prop.getProperty("format");
			dataFolder = prop.getProperty("data_folder");
			maxQueries = Integer.parseInt(prop.getProperty("max_queries"));
			maxRelations = Integer.parseInt(prop.getProperty("max_relations"));


		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}

  }	
}
