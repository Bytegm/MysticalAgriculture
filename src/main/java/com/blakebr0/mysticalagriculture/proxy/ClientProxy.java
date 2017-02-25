package com.blakebr0.mysticalagriculture.proxy;

import com.blakebr0.mysticalagriculture.blocks.ModBlocks;
import com.blakebr0.mysticalagriculture.handler.ItemColorHandler;
import com.blakebr0.mysticalagriculture.items.ModItems;
import com.blakebr0.mysticalagriculture.items.armor.upgraded.ItemUpgradedSpeed;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
//	    MinecraftForge.EVENT_BUS.register(new ItemUpgradedSpeed.abilityHandler());
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		ModBlocks.initBlockModels();
		ModItems.initItemModels();
		ModItems.initItemColors();
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}
}
