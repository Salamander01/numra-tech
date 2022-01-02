package net.numra.tech.blocks;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.numra.tech.NumraTech.logger_block;
import static net.numra.tech.blocks.ConveyorBasicBlock.ACTIVE;
import static net.numra.tech.blocks.ConveyorBasicBlock.DIRECTION;

public class ConveyorBasicBlockEntity extends BlockEntity implements SidedInventory {
    private final int inventorySize;
    private final int slotSize;
    private final int transferSpeed;
    private boolean blocked;
    private int[] progress;

    private BlockState selfState;
    private final DefaultedList<ItemStack> stacks;

    public static void tick(World world, BlockPos pos, BlockState state, ConveyorBasicBlockEntity blockEntity) {
        if (blockEntity.selfState.get(ACTIVE)) {
            if (!blockEntity.blocked) {
                for (int i = 0; i < blockEntity.progress.length; ++i) {
                    if (!blockEntity.stacks.get(i).isEmpty()) {
                        blockEntity.progress[i] += blockEntity.transferSpeed;
                        if (blockEntity.progress[i] >= 1000) {
                            if(!blockEntity.progressItem(i, world, pos).equals("Blocked")) {
                                blockEntity.progress[i] = 0;
                            }
                        }
                        blockEntity.markDirty();
                    } else if (blockEntity.progress[i] != 0) blockEntity.progress[i] = 0;
                    blockEntity.markDirty();
                }
            } else {
                blockEntity.checkIfBlocked(world, pos);
            }
        }
    }
    
    private String progressItem(int itemIndex, World world, BlockPos pos) { // as of now the only return that does anything is "Blocked"
        BlockPos outPos = getOutPos(pos);
        PositionImpl dropPos = getDropPos(pos);
        Block outBlock = world.getBlockState(outPos).getBlock();
        if (outBlock instanceof ConveyorBasicBlock) {
            if (((ConveyorBasicBlock)selfState.getBlock()).testConveyorConnect(selfState, selfState.get(DIRECTION).getSecondDirection(), world.getBlockState(outPos))) {
                if (world.getBlockState(outPos) != null) {
                    ConveyorBasicBlockEntity outEntity = (ConveyorBasicBlockEntity) world.getBlockEntity(outPos);
                    ItemStack outStack = stacks.get(itemIndex);
                    Direction outDir = selfState.get(DIRECTION).getSecondDirection().getOpposite(); //Only half understand why this needs to be opposite
                    if (!outEntity.blocked) {
                        if (insertItem(outEntity, outDir, outStack)) {
                            removeStack(itemIndex);
                            return "Transferred";
                        } else {
                            blocked = true;
                            markDirty();
                            return "Blocked";
                        }
                    } else {
                        blocked = true;
                        markDirty();
                        return "Blocked";
                    }
                } else {
                    logger_block.error("Null BlockEntity found during ConveyorBasicBlockEntity.progressItem");
                    return "ERR";
                }
            }
            blocked = true;
            markDirty();
            return "Blocked";
        } else if (outBlock instanceof AirBlock || outBlock instanceof FluidBlock) {
            ItemEntity itemEntity = new ItemEntity(world, dropPos.getX(), dropPos.getY(), dropPos.getZ(), stacks.get(itemIndex), 0, 0, 0);
            itemEntity.setPickupDelay(15);
            world.spawnEntity(itemEntity);
            removeStack(itemIndex);
            return "Dropped";
        } else {
            blocked = true;
            markDirty();
            return "Blocked";
        }
    }
    
    private void checkIfBlocked(World world, BlockPos pos) {
        BlockPos outPos = getOutPos(pos);
        Block outBlock = world.getBlockState(outPos).getBlock();
        if (outBlock instanceof AirBlock || outBlock instanceof FluidBlock || (outBlock instanceof ConveyorBasicBlock && ((ConveyorBasicBlock)selfState.getBlock()).testConveyorConnect(selfState, selfState.get(DIRECTION).getSecondDirection(), world.getBlockState(outPos)))) {
            blocked = false;
            markDirty();
        }
    }
    
    private BlockPos getOutPos(BlockPos pos) {
        return switch (selfState.get(DIRECTION).getSecondDirection()) {
            case NORTH -> pos.north();
            case EAST -> pos.east();
            case SOUTH -> pos.south();
            default -> pos.west(); // Unless someone messes with the enum, up and down will never happen so to appease the IDE we do this.
        };
    }
    
    private PositionImpl getDropPos(BlockPos pos) {
        BlockPos tempPos;
        switch (selfState.get(DIRECTION).getSecondDirection()) {
            case NORTH -> { tempPos = pos.north(); return new PositionImpl((tempPos.getX() + 0.5), tempPos.getY(), (tempPos.getZ() + 0.8)); }
            case EAST -> { tempPos = pos.east(); return new PositionImpl((tempPos.getX() + 0.2), tempPos.getY(), (tempPos.getZ() + 0.5)); }
            case SOUTH -> { tempPos = pos.south(); return new PositionImpl((tempPos.getX() + 0.5), tempPos.getY(), (tempPos.getZ() + 0.2)); }
            default -> { tempPos = pos.west(); return new PositionImpl((tempPos.getX() + 0.8), tempPos.getY(), (tempPos.getZ() + 0.5)); } // see above comment
        }
    }

    public void updateSelfState(BlockState state) {
        selfState = state;
    }
    
    @Override
    public int size() {
        return inventorySize;
    }

    @Override
    public boolean isEmpty() { //CREDIT: Stolen from https://fabricmc.net/wiki/tutorial:inventory
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (!(slot < 0 || slot >= size())) {
            return this.stacks.get(slot);
        } else return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int count) { //CREDIT: Adapted from https://fabricmc.net/wiki/tutorial:inventory
        ItemStack result = Inventories.splitStack(stacks, slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) { //CREDIT: Adapted from https://fabricmc.net/wiki/tutorial:inventory
        return Inventories.removeStack(stacks, slot);
    }

    @Override
    public int getMaxCountPerStack() { // Doesn't seem to do anything for some reason
        return slotSize;
    }

    @Override
    public void setStack(int slot, ItemStack stack) { //CREDIT: Adapted from https://fabricmc.net/wiki/tutorial:inventory
        stacks.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    @Override
    public void clear() { //CREDIT: Adapted from https://fabricmc.net/wiki/tutorial:inventory
        stacks.clear();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }
    
    public boolean insertItem(ConveyorBasicBlockEntity destination, Direction direction, ItemStack inStack) {
        for (int slot : destination.getAvailableSlots(direction)) {
            if (destination.canInsert(slot, inStack, direction)) {
                if (!destination.getStack(slot).isEmpty()) {
                    if (destination.getStack(slot).isOf(inStack.getItem())) {
                        destination.setStack(slot, new ItemStack(inStack.getItem(), this.getStack(slot).getCount() + inStack.getCount()));
                        return true;
                    }
                } else {
                    destination.setStack(slot, inStack);
                    return true;
                }
            }
        }
        return false;
    }

    public void insertDroppedItem(ItemEntity droppedItem) {
        ItemStack newStack = droppedItem.getStack();
        if (insertItem(this, Direction.UP, newStack)) droppedItem.discard();
    }
    @Override
    public void writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag, stacks);
        tag.putIntArray("progress", progress);
        tag.putBoolean("blocked", blocked);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        Inventories.readNbt(tag, stacks);
        progress = tag.getIntArray("progress");
        blocked = tag.getBoolean("blocked");
    }

    @Override
    public int[] getAvailableSlots(Direction side) { //CREDIT: Adapted from https://fabricmc.net/wiki/tutorial:inventory
        int[] result = new int[stacks.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir != null) {
            return (dir == Direction.UP || dir == selfState.get(DIRECTION).getFirstDirection().getOpposite()) && !(stacks.get(slot).getCount() + stack.getCount() > slotSize /* Hacky replacement for getMaxCountPerStack() */);
        } else return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN /* Temporary */ || dir == selfState.get(DIRECTION).getSecondDirection();
    }

    public ConveyorBasicBlockEntity(BlockPos pos, BlockState state) {
        super(ConveyorBasic.CONVEYOR_BASIC_BLOCK_ENTITY, pos, state);
        this.inventorySize = ((ConveyorBasicBlock)state.getBlock()).getInventorySize();
        this.slotSize = ((ConveyorBasicBlock)state.getBlock()).getSlotSize();
        this.stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        this.selfState = state;
        this.transferSpeed = ((ConveyorBasicBlock)state.getBlock()).getTransferSpeed();
        this.progress = new int[inventorySize];
        this.blocked = false;
    }
}
