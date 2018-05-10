package com.gmail.nossr50.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class EnchantmentListener implements Listener {

    private final mcMMO plugin;

    public EnchantmentListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPrepareEnchant(PrepareItemEnchantEvent event) {

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(event.getEnchanter());        
        int playerSkillLevel = mcMMOPlayer.getSkillLevel(SkillType.ALCHEMY);
        double discount = Math.min(0.5, 0*Math.pow(playerSkillLevel, 2)+(0.0005*playerSkillLevel)+0);
        
    	for (EnchantmentOffer offer : event.getOffers()) {
        	int newCost = Math.max(1,(int) (offer.getCost() - discount*offer.getCost()));
        	offer.setCost(newCost);
    	}
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDoEnchant(EnchantItemEvent event) {
    	
    	int lapisSpent = event.whichButton()+1; //Future mechanics may change this, be wary!
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(event.getEnchanter());        
        int playerSkillLevel = mcMMOPlayer.getSkillLevel(SkillType.ALCHEMY);
        
    	int amountSalvaged = 0;
    	for (int i = 1; i <= lapisSpent; i++) {
    		System.out.println("Lapis piece" + i);
    		//Formula: Lapis has a %1/2(0.008*skilllevel) (capped at 0.8) of incrementing
    		amountSalvaged += (int) Math.round(Math.min(0.8,Math.random()*playerSkillLevel*0.008));
    		System.out.println("Salvaged: " + amountSalvaged);
    	}
    	if (amountSalvaged > 0) {
    		Misc.dropItems(event.getEnchanter().getLocation(), new ItemStack(Material.INK_SACK, amountSalvaged, (short)4), 1);
    		event.getEnchanter().sendMessage(LocaleLoader.getString("mcMMO.LapisRecovered"));
    	}
        
    	mcMMOPlayer.applyXpGain(SkillType.ENCHANTING, 100*event.getExpLevelCost(), com.gmail.nossr50.datatypes.skills.XPGainReason.getXPGainReason("ENCHANT"));
    	
    }
}
