package fr.crypto.shareprice.importer;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import fr.crypto.shareprice.Constants;

public class CryptoastImporter implements SharePriceImportable {

	@Override
	public boolean isElligible(String url) {
		return url != null && url.toLowerCase().contains(Constants.URL_CRYPTOAST.toLowerCase());
	}

	@Override
	public SharePriceBean importSharePrice(String url) throws IOException {
		SharePriceBean sp = new SharePriceBean();
		
		// Call distant
		Document doc = Jsoup.connect(url).get();
		// Web scraping
		Elements chiffresCles = doc.getElementsByClass("top_cours");
		String cours = chiffresCles.get(0).child(1).getElementsByTag("span").get(1).text();
		
		if (cours != null) {
			cours = cours.replace(" ", "").replace(".", ",").replace("€", "");
		}
		sp.setPriceActual(cours);
//		sp.setIsin(header.getElementsByClass("c-faceplate__isin").first().text());
//		sp.setSociete(header.getElementsByClass("c-faceplate__company-link").first().text());
		
		return sp;
	}

	
	
}
