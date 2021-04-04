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

	public String getPosition() {
		return getCellAsTextValue(Constants.COLS.POSITION.value);
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
		if (Constants.COLS.POSITION.value != null) {
			writeNumericCell(sheet, rowIndex, Constants.COLS.POSITION.value, sp.getPosition());
		}
		
		// Enregistre les variations
		if (Constants.COLS.VAR_1D.value != null) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_1D.value, sp.getVar1D() == null ? null : String.valueOf(Double.valueOf(sp.getVar1D()) / 100));
		}
		if (Constants.COLS.VAR_7D.value != null) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_7D.value, sp.getVar7D() == null ? null : String.valueOf(Double.valueOf(sp.getVar7D()) / 100));
		}
		if (Constants.COLS.VAR_14D.value != null) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_14D.value, sp.getVar14D() == null ? null : String.valueOf(Double.valueOf(sp.getVar14D()) / 100));
		}
		if (Constants.COLS.VAR_30D.value != null) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_30D.value, sp.getVar30D() == null ? null : String.valueOf(Double.valueOf(sp.getVar30D()) / 100));
		}
		if (Constants.COLS.VAR_60D.value != null) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_60D.value, sp.getVar60D() == null ? null : String.valueOf(Double.valueOf(sp.getVar60D()) / 100));
		}
		if (Constants.COLS.VAR_90D.value != null) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_90D.value, sp.getVar90D() == null ? null : String.valueOf(Double.valueOf(sp.getVar90D()) / 100));
		}
		if (Constants.COLS.VAR_180D.value != null) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_180D.value, sp.getVar180D() == null ? null : String.valueOf(Double.valueOf(sp.getVar180D()) / 100));
		}
		if (Constants.COLS.VAR_360D.value != null) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_360D.value, sp.getVar360D() == null ? null : String.valueOf(Double.valueOf(sp.getVar360D()) / 100));
		}
		if (Constants.COLS.VAR_FIRST_DAY_OF_YEAR.value != null) {
			writePercentCell(sheet.getWorkbook(), sheet, rowIndex, Constants.COLS.VAR_FIRST_DAY_OF_YEAR.value, sp.getVarFirstDayOfYear() == null ? null : String.valueOf(Double.valueOf(sp.getVarFirstDayOfYear()) / 100));
		}

		// Enregistre la date de mise a jour
		if (Constants.COLS.UPDATE_DATE.value != null) {
			writeCell(sheet, rowIndex, Constants.COLS.UPDATE_DATE.value, Constants.SDF_EXCEL.format(new Date()));
		}
	}
	
	private String getCellAsTextValue(String colName) {
		return colName == null ? null : CellUtils.getCellAsTextValue(sheet, rowIndex, colName);
	}
	

}
