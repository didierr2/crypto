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

	// On fait -1 car la premiere ligne a le numéro 0
	public static final int FIRST_ROW = PropertyManager.getIntPropertyOrElse("FIRST_ROW", 2) - 1;
	
	public static final int SLEEP_INTERVAL_MILLISECONDS = PropertyManager.getIntPropertyOrElse("SLEEP_INTERVAL_MILLISECONDS", 1000);
	

	/** Liste des importers de cours */
	public static final List<SharePriceImportable> SHARE_PRICE_IMPORTERS = Arrays.asList(new SharePriceImportable[] {
			new CoursCryptomonnaiesImporter(),
			new CoinMarketCapImporter()
	}); 
	
	
	public static final String URL_CRYPTOAST = "/cryptoast.fr/"; // Importer pas encore fonctionnel
	public static final String URL_COINMARKETCAP = "/coinmarketcap.com/";
	public static final String URL_COURSCRYPTOMONNAIES = "/courscryptomonnaies.com/";
	
	public enum SHEETS {
		DATA ("Feuil1");
		
		public String value = null;
		SHEETS(String n) {
			value = PropertyManager.getPropertyOrElse(this.getClass().getSimpleName() + "." + this.name(), n);
		}
	}
	
	public enum COLS {
		NAME ("A"), 
		INDEX ("B"), // INDICE (XBT, ETH)
		QUANTITY (null),
		PRICE_BUY (null),
		PRICE_ACTUAL ("E"),
		PL_PERCENT (null),
		PL_VALUE (null),
		VAR_1D (null),
		VAR_7D (null),
		VAR_14D (null),
		VAR_30D (null),
		VAR_60D (null),
		VAR_90D (null),
		VAR_180D (null),
		VAR_360D (null),
		VAR_FIRST_DAY_OF_YEAR(null),
		POSITION (null),
		UPDATE_DATE (null),
		UPDATE_URL ("L");
		
		public String value = null;
		COLS(String n) {
			value = PropertyManager.getPropertyOrElse(this.getClass().getSimpleName() + "." + this.name(), n);
			if (value != null && value.trim().length() == 0) {
				value = null;
			}
		}
	}
			
}
