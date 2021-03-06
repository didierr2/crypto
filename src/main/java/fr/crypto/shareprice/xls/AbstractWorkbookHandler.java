package fr.crypto.shareprice.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Classe abstraite de manipulation d'un fichier excel.
 * Gère automatique l'ouverture des flux et leur fermeture.
 * Sauvegarde automatiquement le fichier avant fermeture.
 * @author Didier
 *
 */
public abstract class AbstractWorkbookHandler {

	/** Mode lecture de la fuille uniquement ou enregistrement lorsque le traitement est terminé */
	public enum OPEN_MODE {
		READ_ONLY,
		READ_WRITE;
	};
	
	/**
	 * Méthode principale de lecture d'une feuille d'un fichier excel
	 * Gère la lecture physique du fichier, la fermeture des streams et la sauvegarde du fichier si necesaire
	 * @param openMode
	 * @param workbookPath
	 * @param sheetNames
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readWorkbook(OPEN_MODE openMode, String workbookPath, String... sheetNames) throws FileNotFoundException, IOException {
		ZipSecureFile.setMinInflateRatio(0) ;
		Workbook workbook = null;
		System.out.println("Ouverture du fichier : " + workbookPath);
		
		// Si le fichier est deja ouvert, on ne lance pas le traitement car on ne pourra pas enregistrer les resultats
		if (checkFileIsNotUsed(workbook, workbookPath)) {
		
			try (FileInputStream stream = new FileInputStream(new File(workbookPath))) {
				workbook = new XSSFWorkbook(stream);
				List<Sheet> lstSheet = new ArrayList<>();
				String names = "";
				for (String name : sheetNames) {
					int idx = workbook.getSheetIndex(name);
					if (idx < 0) {
						throw new RuntimeException("La feuille " + name + " n'existe pas dans le classeur excel.");
					}
					Sheet sheet = workbook.getSheetAt(idx);
					lstSheet.add(sheet);
					names += sheet.getSheetName() + " ";
				}
				System.out.println("Recuperation dee la feuille : " + names);
				processSheet(workbook, lstSheet.toArray(new Sheet[lstSheet.size()]));
				
			}
			// on enregistre le fichier
			saveWorkbook(workbook, workbookPath, openMode);
			closeWorkbook(workbook, workbookPath);
		}
	}
	
	protected void processSheet(Workbook workbook, Sheet sheet) {
	}

	protected void processSheet(Workbook workbook, Sheet sheet1, Sheet sheet2) {
	}

	protected void processSheet(Workbook workbook, Sheet sheet1, Sheet sheet2, Sheet sheet3) {
	}

	/**
	 * Méthode de traitement d'une feuille excel
	 * @param sheet
	 */
	protected void processSheet(Workbook workbook, Sheet... sheets) {
		switch (sheets.length) {
		case 1 :
			processSheet(workbook, sheets[0]);
		break;
		case 2 : 
			processSheet(workbook, sheets[0], sheets[1]);
		break;
		case 3 : 
			processSheet(workbook, sheets[0], sheets[1], sheets[2]);
		break;
		default :
			throw new IllegalStateException("Pour traiter plus de 3 feuilles, il faut réécrire cette méthode");
		}
	}
	
	/**
	 * Permet de fermer les streams du fichier excel et de sauvegarder si necessaire
	 * @param workbook
	 * @param filename
	 * @param openMode
	 */
	private void closeWorkbook(Workbook workbook, String filename) {
		if (workbook != null) {
			
			try {
				workbook.close();
			} catch (IOException e) {
				System.err.println("Erreur a la fermeture du fichier excel");
				e.printStackTrace();
			}
		}

	}		
	
	private void saveWorkbook(Workbook workbook, String filename, OPEN_MODE openMode) {
		if (workbook != null) {
			
			if (openMode == OPEN_MODE.READ_WRITE) {
				try (FileOutputStream outputStream = new FileOutputStream(filename)) {
					// Update formula before saving
					XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
					workbook.write(outputStream);
					System.out.println("Fichier sauvegarde : " + filename);
				} catch (IOException e) {
					System.err.println("Erreur a l'enregistrement du fichier excel");
					e.printStackTrace();
				}
			}
		}
	}	
	
	private boolean checkFileIsNotUsed(Workbook workbook, String filename) {
		boolean isNotUsed = true;
		try (FileOutputStream outputStream = new FileOutputStream(filename, true)) {
		} catch (IOException e) {
			System.err.println("Le fichier excel " + filename + " semble déjà utilise, merci de le libérer et de relancer le programme.");
			isNotUsed = false;
		}
		return isNotUsed;
	}	
	
}
