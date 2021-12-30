package net.numra.tech.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.numra.tech.NumraTech.logger_block;
import static net.numra.tech.items.ItemGroups.NUMRA_CONVEYORS;

public class ConveyorBasic {
    // Defines and registers conveyor_basic blocks and their corresponding items

    public static final ConveyorBasicBlock CONVEYOR_WOOD = new ConveyorBasicBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.5f).resistance(3.0f), 0.0165, 0.0135);

    public static void init() {

        // Wood
        logger_block.debug("initializing CONVEYOR_WOOD");
        Registry.register(Registry.BLOCK, new Identifier("numra", "conveyor_wood"), CONVEYOR_WOOD);
        Registry.register(Registry.ITEM, new Identifier("numra", "conveyor_wood"), new BlockItem(CONVEYOR_WOOD, new FabricItemSettings().group(NUMRA_CONVEYORS)));
    }
}