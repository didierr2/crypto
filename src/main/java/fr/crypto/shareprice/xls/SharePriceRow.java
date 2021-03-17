package fr.crypto.shareprice.xls;

import static fr.crypto.shareprice.util.CellUtils.writeNumericCell;

import org.apache.poi.ss.usermodel.Sheet;

import fr.crypto.shareprice.Constants;
import fr.crypto.shareprice.importer.SharePriceBean;
import fr.crypto.shareprice.util.CellUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SharePriceRow {

	final Sheet sheet;
	@Getter
	int rowIndex;

	public static SharePriceRow first(Sheet sh) {
		return new SharePriceRow(sh, Constants.FIRST_ROW);
	}	
	
	public void nextRow() {
		rowIndex++;
	}
	
	public String getIndex() {
		return getCellAsTextValue(Constants.COLS.INDEX.value);
	}

	public String getQuantity() {
		return getCellAsTextValue(Constants.COLS.QUANTITY.value);
	}

	public String getPriceBuy() {
		return getCellAsTextValue(Constants.COLS.PRICE_BUY.value);
	}

	public String getPriceActual() {
		return getCellAsTextValue(Constants.COLS.PRICE_ACTUAL.value);
	}

	public String getPlPercent() {
		return getCellAsTextValue(Constants.COLS.PL_PERCENT.value);
	}

	public String getPlValue() {
		return getCellAsTextValue(Constants.COLS.PL_VALUE.value);
	}

	public String getVar1D() {
		return getCellAsTextValue(Constants.COLS.VAR_1D.value);
	}

	public String getVar1W() {
		return getCellAsTextValue(Constants.COLS.VAR_1W.value);
	}
	
	public String getUpdateUrl() {
		return getCellAsTextValue(Constants.COLS.UPDATE_URL.value);
	}

	public boolean isPresent() {
		String ind = getIndex();
		return ind != null && !ind.isEmpty();
	}

	public boolean isUpdatable() {
		String url = getUpdateUrl();
		return url != null && !url.isEmpty();
	}

	
	public void update (SharePriceBean sp) {
		
		// Enregistre les infos récupérées
		writeNumericCell(sheet, rowIndex, Constants.COLS.PRICE_ACTUAL.value, sp.getPriceActual());
		writeNumericCell(sheet, rowIndex, Constants.COLS.VAR_1D.value, sp.getVar1D());
		writeNumericCell(sheet, rowIndex, Constants.COLS.VAR_1W.value, sp.getVar1W());

		// Enregistre les calculs de rendement / variation
		// TODO les calculs de rendement variation
		
		//writeCell(sheet, Constants.COLS.ROW_SOCIETY.value, rowIndex, stock.getSociete());
		//writeNumericCell(sheet, rowIndex, Constants.COLS.PRICE_ACTUAL.value, crypto.getCours());
	}
	
	private String getCellAsTextValue(int colIndex) {
		return CellUtils.getCellAsTextValue(sheet, rowIndex, colIndex);
	}

}
