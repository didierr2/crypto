package fr.crypto.shareprice;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import fr.crypto.shareprice.importer.SharePriceBean;
import fr.crypto.shareprice.importer.SharePriceImportable;
import fr.crypto.shareprice.util.PropertyManager;
import fr.crypto.shareprice.xls.AbstractWorkbookHandler;
import fr.crypto.shareprice.xls.SharePriceRow;



public class SharePriceImporterStarter extends AbstractWorkbookHandler {


	
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		
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
		while(sp.isPresent()) {
			
			if (sp.isUpdatable()) {
				// On effectue l'appel distant
				SharePriceBean actualSp = loadSharePrice(sp.getUpdateUrl());
				
				if (actualSp != null) {
					// On enregistre les infos
					sp.update(actualSp);
					System.out.println("\n" + sp.getIndex() + " : mise a jour reussie, prix actuel = " + sp.getPriceActual() + " euros");
					sleep();
				} 
				else {
					// TODO On fait quoi, on reinit les cellules ?
					System.out.println("Impossible de charger la valeur " + sp.getIndex());
				}
			}
			else {
				System.out.println("\n" + sp.getIndex() + " : mise a jour impossible car l'url de mise a jour n'est pas renseignee");
			}
			
			sp.nextRow();
			
			
		}
	}

	
	private SharePriceImportable findImporter(String url) {
		SharePriceImportable imp = null;
		
		for (SharePriceImportable tmp: Constants.SHARE_PRICE_IMPORTERS) {
			imp = tmp.isElligible(url) ? tmp : imp;
		}
		
		return imp;
	}
	
	private SharePriceBean loadSharePrice(String url) {
		SharePriceBean sp = null;
		
		try {
			SharePriceImportable imp = findImporter(url);
			if (imp != null) {
				sp = imp.importSharePrice(url);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return sp;
	}
	
	
	private void sleep() {

		// L'attente est aléatoirement calculée entre 1x et 2x SLEEP_INTERVAL_MILLISECONDS
		try {
			Thread.sleep((int)(Math.random() * Constants.SLEEP_INTERVAL_MILLISECONDS) + Constants.SLEEP_INTERVAL_MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
