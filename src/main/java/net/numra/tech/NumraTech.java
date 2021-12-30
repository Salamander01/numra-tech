package net.numra.tech;

import net.fabricmc.api.ModInitializer;
import net.numra.tech.blocks.ConveyorBasic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NumraTech implements ModInitializer {
	public static final Logger logger_main = LogManager.getLogger("NumraTech");
	public static final Logger logger_block = LogManager.getLogger("NumraTech - Blocks");

	@Override
	public void onInitialize() {
		logger_main.info("Hello from NumraTech!");
		logger_main.debug("Beginning block init");
		ConveyorBasic.init();
		logger_main.debug("init finished");
	}
}