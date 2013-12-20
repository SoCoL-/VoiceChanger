package ru.mobstudio.voicechanger.Utils;

/**
 * Created by Evgenij on 25.11.13.
 *
 */
public class CountryInfo
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private String mNameCountry;
    private String mPrefix;

    //-----------------------------
    //Ctors
    //-----------------------------

    public CountryInfo()
    {
        this.mPrefix = "";
        this.mNameCountry = "";
    }

    public CountryInfo(String name, String prefix)
    {
        this.mNameCountry = name;
        this.mPrefix = prefix;
    }

    //-----------------------------
    //Methods
    //-----------------------------

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    public String getNameCountry() { return mNameCountry; }

    public void setNameCountry(String mName) { this.mNameCountry = mName; }

    public String getPrefix() { return mPrefix; }

    public void setPrefix(String mPrefix) { this.mPrefix = mPrefix; }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
