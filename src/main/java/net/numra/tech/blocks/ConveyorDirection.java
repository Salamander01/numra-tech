package net.numra.tech.blocks;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum ConveyorDirection implements StringIdentifiable {
    NORTH("north", Direction.NORTH,Direction.NORTH),
    NORTH_EAST("north_east",Direction.NORTH,Direction.EAST),
    WEST_SOUTH("west_south",Direction.WEST,Direction.SOUTH),
    EAST("east",Direction.EAST,Direction.EAST),
    SOUTH_EAST("south_east",Direction.SOUTH,Direction.EAST),
    WEST_NORTH("west_north",Direction.WEST,Direction.NORTH),
    SOUTH("south",Direction.SOUTH,Direction.SOUTH),
    SOUTH_WEST("south_west",Direction.SOUTH,Direction.WEST),
    EAST_NORTH("east_north",Direction.EAST,Direction.NORTH),
    WEST("west",Direction.WEST,Direction.WEST),
    NORTH_WEST("north_west",Direction.NORTH,Direction.WEST),
    EAST_SOUTH("east_south",Direction.EAST,Direction.SOUTH);

    ConveyorDirection(String lowName, Direction firstDirection, Direction secondDirection) {
        this.lowName = lowName;
        this.firstDirection = firstDirection;
        this.secondDirection = secondDirection;
    }

    private final Direction firstDirection;
    private final Direction secondDirection;
    private final String lowName;

    public static ConveyorDirection newConveyorDirection(Direction inFirstDirection, Direction inSecondDirection) {
        switch (inFirstDirection) {
            case NORTH -> {
                switch (inSecondDirection) {
                    case NORTH -> { return NORTH; }
                    case EAST -> { return NORTH_EAST; }
                    case WEST -> { return NORTH_WEST; }
                    default -> throw new IllegalArgumentException();
                }
            }
            case EAST -> {
                switch (inSecondDirection) {
                    case EAST -> { return EAST; }
                    case NORTH -> { return EAST_NORTH; }
                    case SOUTH -> { return EAST_SOUTH; }
                    default -> throw new IllegalArgumentException();
                }
            }
            case SOUTH -> {
                switch (inSecondDirection) {
                    case SOUTH -> { return SOUTH; }
                    case EAST -> { return SOUTH_EAST; }
                    case WEST -> { return SOUTH_WEST; }
                    default -> throw new IllegalArgumentException();
                }
            }
            case WEST -> {
                switch (inSecondDirection) {
                    case WEST -> { return WEST; }
                    case NORTH -> { return WEST_NORTH; }
                    case SOUTH -> { return WEST_SOUTH; }
                    default -> throw new IllegalArgumentException();
                }
            }
            default -> throw new IllegalArgumentException();
        }
    }

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
