package fr.crypto.shareprice;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import fr.crypto.shareprice.importer.CoinMarketCapImporter;
import fr.crypto.shareprice.importer.CoursCryptomonnaiesImporter;
import fr.crypto.shareprice.importer.SharePriceImportable;
import fr.crypto.shareprice.util.PropertyManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

	public static final SimpleDateFormat SDF_EXCEL = new SimpleDateFormat(PropertyManager.getPropertyOrElse("UPDATE_DATE_FORMAT", "dd/MM/yyyy"));

	public static final int FIRST_ROW = PropertyManager.getIntPropertyOrElse("FIRST_ROW", 1);
	
	public static final int SLEEP_INTERVAL_MILLISECONDS = PropertyManager.getIntPropertyOrElse("SLEEP_INTERVAL_MILLISECONDS", 1000);
	

	/** Liste des importers de cours */
	public static final List<SharePriceImportable> SHARE_PRICE_IMPORTERS = Arrays.asList(new SharePriceImportable[] {
			new CoursCryptomonnaiesImporter(),
			new CoinMarketCapImporter()
	}); 
	
	
	public static final String URL_CRYPTOAST = "/cryptoast.fr/";
	public static final String URL_COINMARKETCAP = "/coinmarketcap.com/";
	public static final String URL_COURSCRYPTOMONNAIES = "/courscryptomonnaies.com/";
	
	public enum SHEETS {
		DATA(0);
		
		public int value = 0;
		SHEETS(int n) {
			value = PropertyManager.getIntPropertyOrElse(this.getClass().getSimpleName() + "." + this.name(), n);
		}
	}
	
	public enum COLS {
		NAME (0), 
		INDEX (1), // INDICE (XBT, ETH)
		QUANTITY (2),
		PRICE_BUY (3),
		PRICE_ACTUAL (4),
		PL_PERCENT (5),
		PL_VALUE (6),
		VAR_1D (7),
		VAR_1W (8),
		POSITION (9),
		UPDATE_DATE (10),
		UPDATE_URL (11);
		
		public int value = 0;
		COLS(int n) {
			value = PropertyManager.getIntPropertyOrElse(this.getClass().getSimpleName() + "." + this.name(), n);
		}
	}
			
}
