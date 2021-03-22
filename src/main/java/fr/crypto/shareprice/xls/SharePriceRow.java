package fr.crypto.shareprice.xls;

import static fr.crypto.shareprice.util.CellUtils.writeCell;
import static fr.crypto.shareprice.util.CellUtils.writeNumericCell;
import static fr.crypto.shareprice.util.CellUtils.writePercentCell;

import java.util.Date;

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

	public String getPosition() {
		return getCellAsTextValue(Constants.COLS.POSITION.value);
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

	public boolean isBuyInfoPresent() {
		String quantity = getQuantity();
		String price = getPriceBuy();
		return quantity != null && !quantity.isEmpty() && price != null && !price.isEmpty();
	}

	
	public boolean isUpdatable() {
		String url = getUpdateUrl();
		return url != null && !url.isEmpty();
	}
	
	public double determinePercentPL() {
		double buy = toDouble(getPriceBuy(), 1d);
		double actual = toDouble(getPriceActual(), 1d);
		return (1d / buy * actual) -1; 
	}

	public double determineValuePL() {
		double quantity = toDouble(getQuantity(), 0d);
		double buy = toDouble(getPriceBuy(), 1d);
		double actual = toDouble(getPriceActual(), 1d);
		return (quantity * actual) - (quantity * buy); 
	}

	private double toDouble(String val, Double defaultVal) {
		Double res = defaultVal;
		try {
			res = Double.valueOf(val);
		} catch (Exception exc) {
			System.err.print(exc.getMessage());
			res = defaultVal;
		}
		return res;
	}
	
	public void update (SharePriceBean sp) {
		
		// Enregistre les infos récupérées
		writeNumericCell(sheet, rowIndex, Constants.COLS.PRICE_ACTUAL.value, sp.getPriceActual());
		writeNumericCell(sheet, rowIndex, Constants.COLS.POSITION.value, sp.getPosition());
		writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_1D.value, String.valueOf(Double.valueOf(sp.getVar1D()) / 100));
		//writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_1W.value, sp.getVar1W());

		// Enregistre les calculs de rendement / variation
		writeCell(sheet, rowIndex, Constants.COLS.UPDATE_DATE.value, Constants.SDF_EXCEL.format(new Date()));
		if (isBuyInfoPresent()) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.PL_PERCENT.value, String.valueOf(determinePercentPL()));
			writeNumericCell(sheet, rowIndex, Constants.COLS.PL_VALUE.value, String.valueOf(determineValuePL()));
		}
	}
	
	private String getCellAsTextValue(int colIndex) {
		return CellUtils.getCellAsTextValue(sheet, rowIndex, colIndex);
	}

}
