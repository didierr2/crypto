package fr.crypto.shareprice.importer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SharePriceBean {

	String priceActual; 
	String var1D;
	String var7D;
	String var14D;
	String var30D;
	String var60D;
	String var90D;
	String var180D;
	String var360D;
	String varFirstDayOfYear;
	String position;
}
