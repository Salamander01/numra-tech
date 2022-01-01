package net.numra.tech.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ConveyorBasicBlockEntity extends BlockEntity implements SidedInventory {
    private final int inventorySize;
    private final DefaultedList<ItemStack> stacks;

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

    @Override
    public void writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag, stacks);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        Inventories.readNbt(tag, stacks);
    }

    public ConveyorBasicBlockEntity(BlockPos pos, BlockState state) {
        super(ConveyorBasic.CONVEYOR_BASIC_BLOCK_ENTITY, pos, state);
        this.inventorySize = ((ConveyorBasicBlock)state.getBlock()).getInventorySize();
        this.stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
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
            return switch (dir) {
                case UP -> true;
                // Add true cases dependent on state.get(DIRECTION).getFirstDirection()?
                default -> false;
            };
        } else return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return switch (dir) {
            case DOWN -> true; // temporary to get the items out during development lol
            // Add true cases dependent on state.get(DIRECTION).getSecondDirection()?
            default -> false;
        };
    }
}
