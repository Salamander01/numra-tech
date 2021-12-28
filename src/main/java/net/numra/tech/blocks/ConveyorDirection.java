package net.numra.tech.blocks;

import net.minecraft.util.StringIdentifiable;

public enum ConveyorDirection implements StringIdentifiable {
    NORTH("n"),
    EAST("e"),
    SOUTH("s"),
    WEST("w"),
    NORTH_EAST("ne"),
    EAST_NORTH("en"),
    SOUTH_EAST("se"),
    EAST_SOUTH("es"),
    SOUTH_WEST("sw"),
    WEST_SOUTH("ws"),
    NORTH_WEST("nw"),
    WEST_NORTH("wn");

    ConveyorDirection(String name) {
    }

    @Override
    public String asString() {
        return this.name();
    }
}
