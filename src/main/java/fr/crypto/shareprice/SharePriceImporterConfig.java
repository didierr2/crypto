package fr.crypto.shareprice;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.crypto.shareprice.importer.CoinMarketCapImporter;
import fr.crypto.shareprice.importer.CoursCryptomonnaiesImporter;
import fr.crypto.shareprice.importer.SharePriceImportable;

@Configuration
public class SharePriceImporterConfig {

	/** Liste des importers de cours */
	@Bean
	public List<SharePriceImportable> sharePriceImporters(
			@Autowired CoinMarketCapImporter coinMarketCapImporter,
			@Autowired CoursCryptomonnaiesImporter coursCryptomonnaiesImporter) {
		return Arrays.asList(new SharePriceImportable[] {
			coursCryptomonnaiesImporter,
			coinMarketCapImporter
		});
	}
	
	
	@Bean
	@SuppressWarnings("unchecked")
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			SharePriceImporterStarter.SHARE_PRICE_IMPORTERS = (List<SharePriceImportable>)ctx.getBean("sharePriceImporters");
		};
	}
	
}
