package com.dpscounterimproved.config;

public enum MeterUpdateMode
{
    CONTINUOUS("Continuous"),
    HITSPLAT("Hitplat"),
    GAME_TICK("Game Tick");

    private final String name;

    MeterUpdateMode(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}