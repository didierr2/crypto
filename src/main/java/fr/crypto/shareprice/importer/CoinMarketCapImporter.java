package fr.crypto.shareprice.importer;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import fr.crypto.shareprice.Constants;

public class CoinMarketCapImporter implements SharePriceImportable {

	@Override
	public boolean isElligible(String url) {
		return url != null && url.toLowerCase().contains(Constants.URL_COINMARKETCAP.toLowerCase());
	}

	@Override
	public SharePriceBean importSharePrice(String url) throws IOException {
		SharePriceBean sp = new SharePriceBean();
		
		// Call distant
		Document doc = Jsoup.connect(url).get();
		// Web scraping
		String cours = doc.getElementsByClass("priceValue___11gHJ").text();
		
		if (cours != null) {
			cours = cours.replace("€", "").replace(",", "").trim();
		}
		sp.setPriceActual(cours);
		
		Elements elems = doc.getElementsByClass("sc-1v2ivon-0 gClTFY");
		boolean positive = elems.first().child(0).text().contains("icon-Caret-up");
		sp.setVar1D(positive ? "" : "-" + elems.first().text().replace("%", ""));
		
		String pos = doc.getElementsByClass("namePill___3p_Ii namePillPrimary___2-GWA").text();
		if (pos.toLowerCase().contains("position")) {
			sp.setPosition(pos.split(" ")[2]);
		}
		
		return sp;
	}

	
	
}
