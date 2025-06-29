package me.jh1236.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = "cuteMod")
public class Config implements ConfigData {
    public boolean fogEnabled = true;

    public boolean nonDirectionalJukebox = false;

    public static Config readConfig() {
        return AutoConfig.getConfigHolder(Config.class).getConfig();
    }

    @ConfigEntry.Gui.CollapsibleObject
    public ZoomConfig zoom = new ZoomConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public MessagesConfig messages = new MessagesConfig();

}
