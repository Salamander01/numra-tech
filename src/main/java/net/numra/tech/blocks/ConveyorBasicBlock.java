package net.numra.tech.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static net.numra.tech.blocks.ConveyorBasic.CONVEYOR_WOOD;

public class ConveyorBasicBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.of("on");
    public static final EnumProperty<ConveyorDirection> DIRECTION = EnumProperty.of("direction", ConveyorDirection.class );

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
            // Straight
            case NORTH, SOUTH -> VoxelShapes.union(Block.createCuboidShape(3, 1.5, 0, 13, 3.5, 16), Block.createCuboidShape(1, 0, 0, 3, 4, 16), Block.createCuboidShape(13, 0, 0, 15, 4, 16));
            case EAST, WEST -> VoxelShapes.union(Block.createCuboidShape(0, 1.5, 3, 16, 3.5, 13), Block.createCuboidShape(0, 0, 1, 16, 4, 3), Block.createCuboidShape(0, 0, 13, 16, 4, 15));
            // turn
            case NORTH_EAST, WEST_SOUTH -> VoxelShapes.union(Block.createCuboidShape(13, 0, 13, 15, 4, 15), Block.createCuboidShape(15, 0, 13, 16, 4, 15), Block.createCuboidShape(13, 0, 15, 15, 4, 16), Block.createCuboidShape(13, 1.5, 3, 16, 3.5, 13), Block.createCuboidShape(3, 1.5, 13, 13, 3.5, 16), Block.createCuboidShape(3, 1.5, 3, 13, 3.5, 13), Block.createCuboidShape(1, 0, 1, 3, 4, 3), Block.createCuboidShape(3, 0, 1, 16, 4, 3), Block.createCuboidShape(1, 0, 3, 3, 4, 16));
            case NORTH_WEST, EAST_SOUTH -> VoxelShapes.union(Block.createCuboidShape(1, 0, 13, 3, 4, 15), Block.createCuboidShape(1, 0, 15, 3, 4, 16), Block.createCuboidShape(0, 0, 13, 1, 4, 15), Block.createCuboidShape(3, 1.5, 13, 13, 3.5, 16), Block.createCuboidShape(0, 1.5, 3, 3, 3.5, 13), Block.createCuboidShape(3, 1.5, 3, 13, 3.5, 13), Block.createCuboidShape(13, 0, 1, 15, 4, 3), Block.createCuboidShape(13, 0, 3, 15, 4, 16), Block.createCuboidShape(0, 0, 1, 13, 4, 3));
            case SOUTH_EAST, WEST_NORTH -> VoxelShapes.union(Block.createCuboidShape(13, 0, 1, 15, 4, 3), Block.createCuboidShape(13, 0, 0, 15, 4, 1), Block.createCuboidShape(15, 0, 1, 16, 4, 3), Block.createCuboidShape(3, 1.5, 0, 13, 3.5, 3), Block.createCuboidShape(13, 1.5, 3, 16, 3.5, 13), Block.createCuboidShape(3, 1.5, 3, 13, 3.5, 13), Block.createCuboidShape(1, 0, 13, 3, 4, 15), Block.createCuboidShape(1, 0, 0, 3, 4, 13), Block.createCuboidShape(3, 0, 13, 16, 4, 15));
            case SOUTH_WEST, EAST_NORTH -> VoxelShapes.union(Block.createCuboidShape(1, 0, 1, 3, 4, 3),Block.createCuboidShape(0, 0, 1, 1, 4, 3), Block.createCuboidShape(1, 0, 0, 3, 4, 1), Block.createCuboidShape(0, 1.5, 3, 3, 3.5, 13), Block.createCuboidShape(3, 1.5, 0, 13, 3.5, 3), Block.createCuboidShape(3, 1.5, 3, 13, 3.5, 13), Block.createCuboidShape(13, 0, 13, 15, 4, 15), Block.createCuboidShape(0, 0, 13, 13, 4, 15), Block.createCuboidShape(13, 0, 0, 15, 4, 13));
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

    public boolean testConveyorConnect(BlockState outState, Direction direction, BlockState inState) {
        //CREDIT: "! !#6008" in discord helped a lot with this, so thanks :)
        if (direction.getOpposite() == outState.get(DIRECTION).getFirstDirection()) {
            return outState.get(DIRECTION).getFirstDirection() == inState.get(DIRECTION).getSecondDirection();
        } else if (direction == outState.get(DIRECTION).getSecondDirection()) {
            return outState.get(DIRECTION).getSecondDirection() == inState.get(DIRECTION).getFirstDirection();
        }
        return false;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (neighborState.getBlock() == CONVEYOR_WOOD) {
            if (testConveyorConnect(state, direction, neighborState)) {
                return state.with(ACTIVE, neighborState.get(ACTIVE));
            }
        }
        return state;
    }


    public ConveyorBasicBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVE, false).with(DIRECTION, ConveyorDirection.NORTH));
    }
}

