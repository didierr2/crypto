package fr.crypto.shareprice;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import fr.crypto.shareprice.importer.SharePriceBean;
import fr.crypto.shareprice.xls.AbstractWorkbookHandler;
import fr.crypto.shareprice.xls.SharePriceRow;

// TODO Externaliser les configs dans un fichier properties

public class SharePriceImporterStarter extends AbstractWorkbookHandler {


	
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		
		// Check args
		if (args.length == 0 || args[0] == null) {
			System.err.println("Parametre d'execution manquant.");
			System.exit(-1);
		}
		
		// On réalise l'import des cours
		new SharePriceImporterStarter().importStocks(args[0]);
	}
	

	public void importStocks(String filePath) throws FileNotFoundException, IOException {
		readWorkbook(OPEN_MODE.READ_WRITE, filePath, Constants.SHEETS.DATA.value);
	}

	@Override
	protected void processSheet(Workbook workbook, Sheet data) {

		
		// On parcourt toutes les cryptos (tous les rows)
		SharePriceRow sp = SharePriceRow.first(data);
		while(sp.isPresent()) {
			
			if (true || sp.isUpdatable()) {
				// On effectue l'appel distant
				SharePriceBean actualSp = loadSharePrice(sp.getUpdateUrl());
				// On enregistre les infos
				sp.update(actualSp);
				System.out.println("\n" + sp.getIndex() + " : mise a jour reussie, prix actuel = " + sp.getPriceActual() + " euros");
			}
			else {
				System.out.println("\n" + sp.getIndex() + " : mise a jour impossible car l'url de mise a jour n'est pas renseignee");
			}
			
			sp.nextRow();
			
//			sleep();
		}
	}

	
	static int p = 1;
	private SharePriceBean loadSharePrice(String url) {
		SharePriceBean sp = new SharePriceBean();
		sp.setPriceActual("" + p++);
		sp.setVar1D("0.02");
		sp.setVar1W("0.05");
		
		/*LogRecorder recorder = new LogRecorder(); 
		try {
				// Call distant
				recorder.addLog("connexion a l'url... " + url);
				Document doc = Jsoup.connect(url).get();
				// Web scraping
				recorder.addLog("recuperation html ok, parse le dom...");
				Element main = doc.getElementById("main-content");
				Element header = main.getElementsByTag("header").first();
				String cours = header.getElementsByClass("c-instrument--last").first().text();
				if (cours != null) {
					cours = cours.replace(" ", "");
				}
				stock.setCours(cours);
				stock.setIsin(header.getElementsByClass("c-faceplate__isin").first().text());
				stock.setSociete(header.getElementsByClass("c-faceplate__company-link").first().text());
				recorder.addLog("parsing html ok : " + stock);				
			}
		catch (Exception e) {
			recorder.getLogs().forEach(mess -> System.err.println(mess));
			e.printStackTrace();
		}	*/
		return sp;
	}
	
	
	private void sleep() {

		// L'attente est aléatoirement calculée entre 1x et 2x SLEEP_INTERVAL_SECONDS
		try {
			Thread.sleep(((int)(Math.random() * Constants.SLEEP_INTERVAL_SECONDS) + Constants.SLEEP_INTERVAL_SECONDS) * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
