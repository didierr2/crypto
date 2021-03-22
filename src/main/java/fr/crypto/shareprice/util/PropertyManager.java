package fr.crypto.shareprice.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {

	static Properties prop = new Properties();
	
	public static String getProperty(String name) {
		return prop.getProperty(name).trim();
	}

	public static String getPropertyOrElse(String name, String orElse) {
		return prop.containsKey(name) ? prop.getProperty(name).trim() : orElse;
	}
	
	public static int getIntPropertyOrElse(String name, int orElse) {
		int res = orElse;
		try {
			if (prop.containsKey(name)) {
				res = Integer.valueOf(prop.getProperty(name).trim()).intValue();
			}
		}
		catch (Exception e) {
			System.err.println("Erreur a la lecture de la propriete " + name + " : " + e.getMessage());
			res = orElse;
		}
		return res;
	}
	
	public static int loadProperties(String propFileName) throws IOException {
		int nb = 0;
		try (InputStream inputStream = new FileInputStream(propFileName)) {
			prop.load(inputStream);
			nb = prop.size();
			System.out.println("Fichier de propriété chargé : " + nb + " entrées");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return nb;
	}
	
}
