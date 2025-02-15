package net.numra.tech.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.numra.tech.NumraTech.logger_block;
import static net.numra.tech.items.ItemGroups.NUMRA_CONVEYORS;

public class ConveyorBasic {
    // Defines and registers conveyor_basic blocks and their corresponding items

    public static final ConveyorBasicBlock CONVEYOR_WOOD = new ConveyorBasicBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.5f).resistance(3.0f), 0.0165, 0.0135, 1, 16);

    public static BlockEntityType<ConveyorBasicBlockEntity> CONVEYOR_BASIC_BLOCK_ENTITY;

    public static void initBlockEntities() {
        logger_block.debug("initializing numra:conveyor_basic_block_entity");
        CONVEYOR_BASIC_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "numra:conveyor_basic_block_entity", FabricBlockEntityTypeBuilder.create(ConveyorBasicBlockEntity::new, CONVEYOR_WOOD).build(null));
    }

    public static void initBlocks() {
        // Wood
        logger_block.debug("initializing numra:conveyor_wood");
        Registry.register(Registry.BLOCK, new Identifier("numra:conveyor_wood"), CONVEYOR_WOOD);
        Registry.register(Registry.ITEM, new Identifier("numra:conveyor_wood"), new BlockItem(CONVEYOR_WOOD, new FabricItemSettings().group(NUMRA_CONVEYORS)));
    }
}