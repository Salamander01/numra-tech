package net.numra.tech.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
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

import static net.numra.tech.NumraTech.logger_block;

@SuppressWarnings("deprecation") // As fabric uses deprecation to indicate you should override and not call in the context of "Block" and those warnings get annoying
public class ConveyorBasicBlock extends BlockWithEntity {
    private final double fullVelocity;
    private final double partVelocity;
    private final int inventorySize;
    private final int slotSize;

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

    @Override // TODO
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return switch (ctx.getPlayerFacing().asString()) {
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

    @Override // TODO
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockState out = state;
        if (neighborState.getBlock() instanceof ConveyorBasicBlock) {
            // Turning logic - Will change
            out = turnHandler(state, neighborState, direction, out);
            if (testConveyorConnect(state, direction, neighborState)) {
                out = out.with(ACTIVE, neighborState.get(ACTIVE));
            }
        }
        return out;
    }

    public BlockState turnHandler(BlockState state, BlockState neighborState, Direction direction, BlockState out) {
        if (direction == neighborState.get(DIRECTION).getFirstDirection() && state.get(DIRECTION).getFirstDirection() != neighborState.get(DIRECTION).getFirstDirection().getOpposite()) {
            out = out.with(DIRECTION, ConveyorDirection.newConveyorDirection(state.get(DIRECTION).getFirstDirection(), neighborState.get(DIRECTION).getFirstDirection()));
        } else if (direction == neighborState.get(DIRECTION).getSecondDirection().getOpposite() && state.get(DIRECTION).getSecondDirection() != neighborState.get(DIRECTION).getSecondDirection().getOpposite()){
            out = out.with(DIRECTION, ConveyorDirection.newConveyorDirection(neighborState.get(DIRECTION).getSecondDirection(), state.get(DIRECTION).getSecondDirection()));
        }
        return out;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) { // Used over onEntityCollision as the effect will not overlap with onSteppedOn
        //Note Items won't be transferred like this in the end (fancy block entity stuff), and this might even gain an exception to item entities
        if (state.get(ACTIVE)) {
            if (!entity.isSneaking()) {
                if ((entity.getX() > pos.getX() - 0.05 && entity.getX() < pos.getX() + 1.05) && (entity.getY() > pos.getY() + 0.2 && entity.getY() < pos.getY() + 0.26)) {
                    switch (state.get(DIRECTION)) {
                        case NORTH -> entity.addVelocity(0, 0, -fullVelocity);
                        case EAST -> entity.addVelocity(fullVelocity, 0, 0);
                        case SOUTH -> entity.addVelocity(0, 0, fullVelocity);
                        case WEST -> entity.addVelocity(-fullVelocity, 0, 0);
                        default -> {
                            switch (state.get(DIRECTION).getFirstDirection()) {
                                case NORTH -> entity.addVelocity(0, 0, -partVelocity);
                                case EAST -> entity.addVelocity(partVelocity, 0, 0);
                                case SOUTH -> entity.addVelocity(0, 0, partVelocity);
                                case WEST -> entity.addVelocity(-partVelocity, 0, 0);
                            }
                            switch (state.get(DIRECTION).getSecondDirection()) {
                                case NORTH -> entity.addVelocity(0, 0, -fullVelocity);
                                case EAST -> entity.addVelocity(fullVelocity, 0, 0);
                                case SOUTH -> entity.addVelocity(0, 0, fullVelocity);
                                case WEST -> entity.addVelocity(-fullVelocity, 0, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConveyorBasicBlockEntity(pos, state);
    }

    public int getInventorySize() {
        return this.inventorySize;
    }

    public int getSlotSize() {
        return this.slotSize;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() instanceof ConveyorBasicBlock && newState.getBlock() instanceof ConveyorBasicBlock) {
            if (state.get(DIRECTION) != newState.get(DIRECTION)) {
                if (world.getBlockEntity(pos) != null) {
                    ((ConveyorBasicBlockEntity) world.getBlockEntity(pos)).updateSelfState(newState); // IntelliJ creates a warning here even though I check for nulls...
                } else logger_block.error("Null BlockEntity found during ConveyorBasicBlock.onStateReplaced");
            }
        }
        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) { // Default code
            world.removeBlockEntity(pos);
        }
    }

    public ConveyorBasicBlock(Settings settings, double fullVelocity, double partVelocity, int inventorySize, int slotSize) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVE, false).with(DIRECTION, ConveyorDirection.NORTH));
        this.fullVelocity = fullVelocity;
        this.partVelocity = partVelocity;
        this.inventorySize = inventorySize;
        this.slotSize = slotSize;
    }
}

