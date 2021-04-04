package fr.crypto.shareprice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import fr.crypto.shareprice.importer.SharePriceBean;
import fr.crypto.shareprice.importer.SharePriceImportable;
import fr.crypto.shareprice.util.PropertyManager;
import fr.crypto.shareprice.xls.AbstractWorkbookHandler;
import fr.crypto.shareprice.xls.SharePriceRow;


@SpringBootApplication
public class SharePriceImporterStarter extends AbstractWorkbookHandler {

// TODO : réupérer le code de la crypto et le vérifier avec le fichier excel etre sur qu'on a le bon lien de maj
// revoir le mécanisme de tests que le fichiers excel est libre	

	// Map des cryptos deja récupérées afin de ne pas faire plusieurs fois le meme appel
	private HashMap<String, SharePriceBean> alreadyLoaded = new HashMap<>();

	/** Liste des importers de cours */
	static List<SharePriceImportable> SHARE_PRICE_IMPORTERS = null; 

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		ConfigurableApplicationContext ctx = SpringApplication.run(SharePriceImporterStarter.class, args);
		start(args);
		ctx.close();
	}


	public static void start(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		
		// Check args
		if (args.length == 0 || args[0] == null) {
			System.err.println("Parametre d'execution manquant.");
			System.exit(-1);
		}

		// Gere le fichier de conf passé en paramètre
		if (args.length > 1) {
			PropertyManager.loadProperties(args[1]);
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
		final int MAX_EMPTY = 5;
		int retry = MAX_EMPTY;
		while(retry > 0) {
			if (sp.isPresent()) {
				retry = MAX_EMPTY;
			
				if (sp.isUpdatable()) {
					// On effectue l'appel distant
					System.out.print(sp.getIndex() + "   ");
					SharePriceBean actualSp = loadSharePrice(sp.getUpdateUrl(), sp.getIndex());
					
					if (actualSp != null) {
						// On enregistre les infos
						System.out.print("save ");
						sp.update(actualSp);
						System.out.println("");
					} 
					else {
						// TODO On fait quoi, on reinit les cellules ?
						System.out.println("Impossible de charger la valeur " + sp.getIndex());
					}
				}
				else {
					System.out.println(sp.getIndex() + " : l'url de mise a jour n'est pas renseignee");
				}
			}
			else {
				retry--;
			}
			
			sp.nextRow();
		}
	}

	/**
	 * Trouve le bon connecteur de récupération du cours 
	 * (le connecteur dépend du site sur lequel on récupère le cours) 
	 * @param url : url de récupération du cours
	 * @return SharePriceImportable : le connecteur
	 */
	private SharePriceImportable findImporter(String url) {
		SharePriceImportable imp = null;
		
		for (SharePriceImportable tmp: SharePriceImporterStarter.SHARE_PRICE_IMPORTERS) {
			imp = tmp.isElligible(url) ? tmp : imp;
		}
		
		return imp;
	}
	
	/**
	 * Charge le cours de la crypto
	 * @param url : url de récupération du cours
	 * @return SharePriceBean : le databean du cours
	 */
	private SharePriceBean loadSharePrice(String url, String symbol) {
		SharePriceBean sp = null;
		
		// Si la crypto a déjà été importée, on la récupère sans appel distant, sinon appel http
		if (alreadyLoaded.containsKey(url)) {
			sp = alreadyLoaded.get(url);
		} 
		else {
			try {
				SharePriceImportable imp = findImporter(url);
				if (imp != null) {
					sp = imp.importSharePrice(url, symbol);
					alreadyLoaded.put(url, sp);
					sleep();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sp;
	}
	
	/**
	 * Patiente...
	 * Permet de ne pas enchainer trop vite les appels sur le même site afin de ne pas se faire blacklister
	 */
	private void sleep() {

		// L'attente est aléatoirement calculée entre 1x et 2x SLEEP_INTERVAL_MILLISECONDS
		try {
			Thread.sleep((int)(Math.random() * Constants.SLEEP_INTERVAL_MILLISECONDS) + Constants.SLEEP_INTERVAL_MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
