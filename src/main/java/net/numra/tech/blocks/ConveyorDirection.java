package net.numra.tech.blocks;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum ConveyorDirection implements StringIdentifiable {
    NORTH("north", Direction.NORTH,Direction.NORTH),
    NORTH_EAST("north_east",Direction.NORTH,Direction.EAST),
    EAST_NORTH("east_north",Direction.EAST,Direction.NORTH),
    EAST("east",Direction.EAST,Direction.EAST),
    SOUTH_EAST("south_east",Direction.SOUTH,Direction.EAST),
    EAST_SOUTH("east_south",Direction.EAST,Direction.SOUTH),
    SOUTH("south",Direction.SOUTH,Direction.SOUTH),
    SOUTH_WEST("south_west",Direction.SOUTH,Direction.WEST),
    WEST_SOUTH("west_south",Direction.WEST,Direction.SOUTH),
    WEST("west",Direction.WEST,Direction.WEST),
    NORTH_WEST("north_west",Direction.NORTH,Direction.WEST),
    WEST_NORTH("west_north",Direction.WEST,Direction.NORTH);

    ConveyorDirection(String lowName, Direction firstDirection, Direction secondDirection) {
        this.lowName = lowName;
        this.firstDirection = firstDirection;
        this.secondDirection = secondDirection;
    }

    private final Direction firstDirection;
    private final Direction secondDirection;
    private final String lowName;

    public Direction getFirstDirection() {
        return this.firstDirection;
    }
    public Direction getSecondDirection() {
        return this.secondDirection;
    }
    @Override
    public String asString() {
        return this.lowName;
    }
}
