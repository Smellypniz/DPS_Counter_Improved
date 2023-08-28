package com.dpscounterimproved.config;

public enum MeterDisplayMode
{
    DPS("Damage Per Second"),
    DPT("Damage Per Tick"),
    TOTAL_DAMAGE("Total Damage"),
    DAMAGE_TAKEN("Damage Taken");

    private final String name;

    MeterDisplayMode(String name)
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