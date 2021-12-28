package net.numra.tech.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class conveyor_basic {
    // Defines and registers conveyor_basic blocks and their corresponding items

    // Wood
    public static final Block conveyor_wood = new Block(FabricBlockSettings.of(Material.WOOD).hardness(2.5f).resistance(3.0f));
    // Remember to add corner block! (without item)

    public static void init() {
        // Wood
        Registry.register(Registry.BLOCK, new Identifier("numra", "conveyor_wood"), conveyor_wood);
        Registry.register(Registry.ITEM, new Identifier("numra", "conveyor_wood"), new BlockItem(conveyor_wood, new FabricItemSettings().group(ItemGroup.REDSTONE))); // Put the item in the redstone category until a mod-specific one is made
    }
}