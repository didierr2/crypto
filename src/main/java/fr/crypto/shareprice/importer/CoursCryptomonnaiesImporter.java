package fr.crypto.shareprice.importer;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import fr.crypto.shareprice.Constants;

@Service
public class CoursCryptomonnaiesImporter implements SharePriceImportable {

	@Override
	public boolean isElligible(String url) {
		return url != null && url.toLowerCase().contains(Constants.URL_COURSCRYPTOMONNAIES.toLowerCase());
	}

	@Override
	public SharePriceBean importSharePrice(String url, String symbol) throws IOException {
		SharePriceBean sp = new SharePriceBean();
		
		// Call distant
		Document doc = Jsoup.connect(url).get();
		// Web scraping
		String cours = doc.getElementsByClass("price-value").text();
		
		if (cours != null) {
			cours = cours.replace("€", "").replace(",", "").trim();
		}
		sp.setPriceActual(cours);
		
		String var1D = doc.getElementsByClass("variation-text").text();
		sp.setVar1D(var1D.replace("%", ""));
		
		return sp;
	}

	
	
}
