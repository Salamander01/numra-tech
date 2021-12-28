package net.numra.tech;

import net.fabricmc.api.ModInitializer;
import net.numra.tech.blocks.ConveyorBasic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NumraTech implements ModInitializer {
	public static final Logger logger_main = LogManager.getLogger("NumraTech");

	@Override
	public void onInitialize() {
		logger_main.info("Hello world!");
		ConveyorBasic.init();
	}
}