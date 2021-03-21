package fr.crypto.shareprice.importer;

import java.io.IOException;

public interface SharePriceImportable {

	boolean isElligible(String url);
	
	SharePriceBean importSharePrice (String url) throws IOException ;
	
}
