package fr.crypto.shareprice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Conversions {

	public static Double toDouble(String value) {
		return toDouble(value, null);
	}
	
	public static Double toDouble(String value, Double defaultVal) {
		Double res = null;
		try {
			res = Double.valueOf(value);
		}
		catch (Exception exc) {
//			if (defaultVal == null) {
//				System.err.println(String.format("Error calculating percent of %s : %s", value, exc.getMessage()));
//			}
			res = defaultVal;
		}
		return res;
	}
	
	
}
