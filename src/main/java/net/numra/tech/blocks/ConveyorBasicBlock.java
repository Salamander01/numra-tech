package net.numra.tech.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ConveyorBasicBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.of("on");
    EnumProperty<ConveyorDirection> DIRECTION = EnumProperty.of("direction", ConveyorDirection.class );

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(ACTIVE);
        stateManager.add(DIRECTION);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockState(pos).get(ACTIVE)) { // This is temporary
            world.setBlockState(pos, state.with(ACTIVE, false));
        } else {
            world.setBlockState(pos, state.with(ACTIVE, true));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return switch (state.get(DIRECTION)) {
            case NORTH, SOUTH -> VoxelShapes.union(Block.createCuboidShape(3, 1.5, 0, 13, 3.5, 16), Block.createCuboidShape(1, 0, 0, 3, 4, 16), Block.createCuboidShape(13, 0, 0, 15, 4, 16));
            case EAST, WEST -> VoxelShapes.union(Block.createCuboidShape(0, 1.5, 3, 16, 3.5, 13), Block.createCuboidShape(0, 0, 1, 16, 4, 3), Block.createCuboidShape(0, 0, 13, 16, 4, 15));
            default -> VoxelShapes.fullCube(); // Will have to add cases for corners
        };
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return switch (ctx.getPlayerFacing().asString()) {
            case "north" -> this.getDefaultState().with(DIRECTION, ConveyorDirection.NORTH);
            case "east" -> this.getDefaultState().with(DIRECTION, ConveyorDirection.EAST);
            case "south" -> this.getDefaultState().with(DIRECTION, ConveyorDirection.SOUTH);
            case "west" -> this.getDefaultState().with(DIRECTION, ConveyorDirection.WEST);
            default -> this.getDefaultState();
        };
    }


    public ConveyorBasicBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVE, false).with(DIRECTION, ConveyorDirection.NORTH));
    }
}

