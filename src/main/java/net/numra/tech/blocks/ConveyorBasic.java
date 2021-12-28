package net.numra.tech.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.numra.tech.items.ItemGroups.NUMRA_CONVEYORS;

public class ConveyorBasic {
    // Defines and registers conveyor_basic blocks and their corresponding items

    // Wood
    public static final ConveyorBasicBlock CONVEYOR_WOOD = new ConveyorBasicBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.5f).resistance(3.0f));


    public static void init() {
        // Wood
        Registry.register(Registry.BLOCK, new Identifier("numra", "conveyor_wood"), CONVEYOR_WOOD);
        Registry.register(Registry.ITEM, new Identifier("numra", "conveyor_wood"), new BlockItem(CONVEYOR_WOOD, new FabricItemSettings().group(NUMRA_CONVEYORS))); // Put the item in the redstone category until a mod-specific one is made
    }
}