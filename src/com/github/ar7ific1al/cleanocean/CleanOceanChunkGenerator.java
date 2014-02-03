package com.github.ar7ific1al.cleanocean;

/**
 * <Credits>
 * Various snippets and general instruction provided by jtjj222
 * http://forums.bukkit.org/threads/the-always-up-to-date-definitive-guide-to-terrain-generation-part-one-prerequisites-and-setup.93982/
 * </Credits>
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class CleanOceanChunkGenerator extends ChunkGenerator {

	@SuppressWarnings("deprecation")
	void setBlock(int x, int y, int z, byte[][] chunk, Material material) {
		if (chunk[y >> 4] == null)
			chunk[y >> 4] = new byte[16 * 16 * 16];
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0)) {
			return;
		}
		try {
			chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
		} catch (Exception e) {
			// do nothing
		}
	}
	
	byte getBlock(int x, int y, int z, byte[][] chunk) {
		// if the Block section the block is in hasn't been used yet, allocate
		// it
		if (chunk[y >> 4] == null)
			return 0; // block is air as it hasnt been allocated
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0))
			return 0;
		try {
			return chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x];
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public byte[][] generateBlockSections(World world, Random rand, int ChunkX,
			int ChunkZ, BiomeGrid biome) {
		rand = new Random(world.getSeed());
		
		SimplexOctaveGenerator gen1 = new SimplexOctaveGenerator(world, 8);
		SimplexOctaveGenerator gen2 = new SimplexOctaveGenerator(world, 8);
		gen1.setScale(1/96.0);
		gen2.setScale(1/64.0);
		
		byte[][] chunk = new byte[world.getMaxHeight() / 16][];
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = x + ChunkX * 16;
				int realZ = z + ChunkZ * 16;
				double frequency = 0.5;
				double amplitude = 0.5;
				int multitude = 16;
				int sea_level = 32;
				
				double maxHeight = Math.max(gen1.noise(realX, realZ, frequency, amplitude) * multitude + sea_level, gen2.noise(realX, realZ, frequency, amplitude) * (multitude / 2) + sea_level) - 16;
				//	Create standard terrain layers
				for (int y = 0; y < maxHeight; y++) {
					if (y == 0){
						setBlock(x, y, z, chunk, Material.BEDROCK);
					}
					else if (y < 8 && y > 0){
						setBlock(x, y, z, chunk, Material.STONE);
					}
					else if (y > 7 && y < 10){
						setBlock(x, y, z, chunk, Material.DIRT);
					}
					else if (y > 9 && y < 30){
						setBlock(x, y, z, chunk, Material.SAND);
					}
				}
				//	Fill with water! WHEEEE!!
				for (int y = 32; y > 0; y--){
					int currentBlock = getBlock(x, y, z, chunk);
					if (currentBlock == Material.AIR.getId()){
						setBlock(x, y, z, chunk, Material.WATER);
					}
				}
				//	Find where the water is shallow, set dirt and grass accordingly! :D
				int thirdBlockUp = getBlock(x, (int) (maxHeight + 3), z, chunk);
				if (thirdBlockUp == Material.AIR.getId()){
					setBlock(x, (int) maxHeight - 1, z, chunk, Material.DIRT);
					setBlock(x, (int) maxHeight - 2, z, chunk, Material.DIRT);
					if (getBlock(x, (int) maxHeight + 1, z, chunk) == Material.AIR.getId()){
						setBlock(x, (int) maxHeight, z, chunk, Material.GRASS);
					}
				}
				biome.setBiome(x, z, Biome.DEEP_OCEAN);
			}
		}
		return chunk;
	}
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world){
		ArrayList<BlockPopulator> pops = new ArrayList<BlockPopulator>();
		//	TODO: all the things! :P
		return pops;
	}

}
