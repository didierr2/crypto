package fr.crypto.shareprice.importer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import fr.crypto.shareprice.Constants;

@Service
public class CoinMarketCapImporter implements SharePriceImportable {

	RestOperations http = new RestTemplate();
	final String DATA_URL = "https://web-api.coinmarketcap.com/v1.1/cryptocurrency/quotes/historical?convert=EUR&format=chart_crypto_details&id={id}&interval=1d&time_end={endTime}&time_start={startTime}";

	@Override
	public boolean isElligible(String url) {
		return url != null && url.toLowerCase().contains(Constants.URL_COINMARKETCAP.toLowerCase());
	}

	@Override
	public SharePriceBean importSharePrice(String url, String symbol) throws IOException {
		SharePriceBean sp = new SharePriceBean();
		
		// Call distant
		Document doc = Jsoup.connect(url).get();

		// Web scraping
		String cryptoSymbol = getCryptoSymbol(doc);
		if (!symbol.equalsIgnoreCase(cryptoSymbol)) {
			System.out.println("Le code de la crypto diffère entre le fichier excel et le site CoinMarketCap : " + symbol + " <> " + cryptoSymbol);
		}		
		System.out.print("price ");
		fillPriceActual(sp, doc);
		System.out.print("position ");
		fillPosition(sp, doc);
		
		// On remplit toutes les variations
		try {
			System.out.print("var:[ ");
			String id = getId(sp, doc, cryptoSymbol);
			double actualPrice = Double.valueOf(sp.getPriceActual());
			
			if (Constants.COLS.VAR_1D.value != null) {
				System.out.print("1 ");
				sp.setVar1D(getPercentVar(getSharePricePreviousDay(id, 1), actualPrice).toString());
			}
			if (Constants.COLS.VAR_7D.value != null) {
				System.out.print("7 ");
				sp.setVar7D(getPercentVar(getSharePricePreviousDay(id, 7), actualPrice).toString());
			}
			if (Constants.COLS.VAR_14D.value != null) {
				System.out.print("14 ");
				sp.setVar14D(getPercentVar(getSharePricePreviousDay(id, 14), actualPrice).toString());
			}
			if (Constants.COLS.VAR_30D.value != null) {
				System.out.print("30 ");
				sp.setVar30D(getPercentVar(getSharePricePreviousDay(id, 30), actualPrice).toString());
			}
			if (Constants.COLS.VAR_60D.value != null) {
				System.out.print("60 ");
				sp.setVar60D(getPercentVar(getSharePricePreviousDay(id, 60), actualPrice).toString());
			}
			if (Constants.COLS.VAR_90D.value != null) {
				System.out.print("90 ");
				sp.setVar90D(getPercentVar(getSharePricePreviousDay(id, 90), actualPrice).toString());
			}
			if (Constants.COLS.VAR_180D.value != null) {
				System.out.print("180 ");
				sp.setVar180D(getPercentVar(getSharePricePreviousDay(id, 180), actualPrice).toString());
			}
			if (Constants.COLS.VAR_360D.value != null) {
				System.out.print("360 ");
				sp.setVar360D(getPercentVar(getSharePricePreviousDay(id, 360), actualPrice).toString());
			}
			if (Constants.COLS.VAR_FIRST_DAY_OF_YEAR.value != null) {
				System.out.print("1erJan ");
				sp.setVarFirstDayOfYear(getPercentVar(getSharePricePreviousDay(id, getNbDaysFromFirstDayOfYear()), actualPrice).toString());
			}
		}
		catch (Exception e) {
			System.err.println("Erreur enregistrement des variations : " + e.getMessage());
		}
		finally {
			System.out.print("] ");
		}
		return sp;
	}
	
	private Double getPercentVar(double from, double to) {
		return from != 0 ? (to / from) : 0;
	}

	private double getSharePricePreviousDay(String id, int nbDays) {
		double price = 0;
		try {
			long startTime = getDatePlusDays(-nbDays);
			long endTime = getDatePlusDays(-(nbDays+1));
			String data = http.getForObject(DATA_URL, String.class, id, startTime, endTime);
			if (data.contains("\"EUR\":[")) {
				String val = data.substring(data.indexOf("\"EUR\":[") + 7);
				val = val.substring(0, val.indexOf(","));
				price = Double.valueOf(val);
			}
		}
		catch (RestClientException rce) {
			price = 0d;
		}
		catch (Exception e) {
			price = 0d;
			System.err.println("Erreur récupération du cours à " + nbDays + " jours : " + e.getMessage());
		}
		
		return price;
	}
	
	private Long getDatePlusDays(int days) {
		LocalDate end = LocalDate.now();
		end = end.plusDays(days);
		Calendar cal = new GregorianCalendar(end.getYear(), end.getMonthValue() - 1, end.getDayOfMonth(), 2, 0);
		return (cal.getTimeInMillis() / 1000);
	}

	private int getNbDaysFromFirstDayOfYear() {
		LocalDate end = LocalDate.now();
		Calendar cal = new GregorianCalendar(end.getYear(), 0, 1, 2, 0);
		Calendar now = GregorianCalendar.getInstance();
		return (int)((now.getTimeInMillis() - cal.getTimeInMillis()) / 1000 / 60 / 60 / 24);
	}

	private String getCryptoSymbol(Document doc) {
		return doc.getElementsByClass("nameSymbol___1arQV").text();
	}

	private void fillPosition(SharePriceBean sp, Document doc) {
		String pos = doc.getElementsByClass("namePill___3p_Ii namePillPrimary___2-GWA").text();
		if (pos.toLowerCase().contains("position")) {
			sp.setPosition(pos.split(" ")[2]);
		}

	}

	private void fillPriceActual(SharePriceBean sp, Document doc) {
		String cours = doc.getElementsByClass("priceValue___11gHJ").text();
		
		if (cours != null) {
			cours = cours.replace("€", "").replace(",", "").trim();
		}
		sp.setPriceActual(cours);
	}
	
	private String getId(SharePriceBean sp, Document doc, String symbol) {
		String html = doc.toString();
		String id = html.substring(0, html.indexOf("\"symbol\":\"" + symbol + "\""));
		id = id.substring(id.lastIndexOf("\"id\":"));
		id = id.substring(5, id.indexOf(","));
		return id;
	}	
	
//	private String fillPercentChange(SharePriceBean sp, Document doc, String symbol) {
//		String html = doc.toString();
//		String jsonData = html.substring(html.indexOf("\"symbol\":\"" + symbol + "\""));
//		sp.setVar1D(getPercentChange(jsonData, "percent_change_24h"));
//		sp.setVar7D(getPercentChange(jsonData, "percent_change_7d"));
//		sp.setVar30D(getPercentChange(jsonData, "percent_change_30d"));
//		sp.setVar60D(getPercentChange(jsonData, "percent_change_60d"));
//		sp.setVar90D(getPercentChange(jsonData, "percent_change_90d"));
//		String id = html.substring(0, html.indexOf("\"symbol\":\"" + symbol + "\""));
//		id = id.substring(id.lastIndexOf("\"id\":"));
//		id = id.substring(5, id.indexOf(","));
//		return id;
//	}
	
	private String getPercentChange(String jsonData, String percentChangeName) {
		String searchString = "\"" + percentChangeName + "\":";
		String pc = null;
		if (jsonData.contains(searchString)) {
			pc = jsonData.substring(jsonData.indexOf(searchString));
			pc = pc.substring(searchString.length(), pc.indexOf(","));
		}
		return pc;
	}
	
	
	
}
