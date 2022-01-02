package net.numra.tech.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.numra.tech.NumraTech.logger_block;
import static net.numra.tech.blocks.ConveyorBasicBlock.DIRECTION;

public class ConveyorBasicBlockEntity extends BlockEntity implements SidedInventory {
    private final int inventorySize;
    private final int slotSize;
    private final int transferSpeed;
    private int[] progress;

    private BlockState selfState;
    private final DefaultedList<ItemStack> stacks;

    public static void tick(World world, BlockPos pos, BlockState state, ConveyorBasicBlockEntity blockEntity) {
        for (int i = 0; i < blockEntity.progress.length; ++i) {
            if (!blockEntity.stacks.get(i).isEmpty()) {
                blockEntity.progress[i] += blockEntity.transferSpeed;
                if (blockEntity.progress[i] >= 1000) {
                    blockEntity.progressItem(i);
                    blockEntity.progress[i] = 0;
                }
                blockEntity.markDirty();
            } else if (blockEntity.progress[i] != 0) blockEntity.progress[i] = 0; blockEntity.markDirty();
        }
    }

    private void progressItem(int itemIndex) {
        this.removeStack(itemIndex); //temp
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

    public void insertDroppedItem(ItemEntity droppedItem) {
        ItemStack newStack = droppedItem.getStack();
        for (int slot : this.getAvailableSlots(Direction.UP)) {
            if (this.canInsert(slot, newStack, Direction.UP)) {
                if (!this.getStack(slot).isEmpty()) {
                    if (this.getStack(slot).isOf(newStack.getItem())) {
                        this.setStack(slot, new ItemStack(newStack.getItem(), this.getStack(slot).getCount() + newStack.getCount()));
                        droppedItem.discard();
                        break;
                    }
                } else {
                    this.setStack(slot, newStack);
                    droppedItem.discard();
                    break;
                }
            }
        }
    }
    @Override
    public void writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag, stacks);
        tag.putIntArray("progress", progress);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        Inventories.readNbt(tag, stacks);
        progress = tag.getIntArray("progress");
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
    }
}
