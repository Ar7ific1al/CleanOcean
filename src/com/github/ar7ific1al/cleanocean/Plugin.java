package com.github.ar7ific1al.cleanocean;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin{
	
	public CleanOceanChunkGenerator getDefaultWorldGenerator(String worldName, String genId){
		return new CleanOceanChunkGenerator();
	}

}
