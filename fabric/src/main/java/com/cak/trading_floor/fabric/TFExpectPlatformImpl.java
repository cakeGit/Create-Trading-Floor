package com.cak.trading_floor.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class TFExpectPlatformImpl {
	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
}
