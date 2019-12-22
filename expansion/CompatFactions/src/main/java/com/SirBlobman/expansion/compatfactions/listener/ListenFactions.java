package com.SirBlobman.expansion.compatfactions.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.SirBlobman.combatlogx.expansion.NoEntryExpansion.NoEntryMode;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.compatfactions.CompatFactions;
import com.SirBlobman.expansion.compatfactions.config.ConfigFactions;
import com.SirBlobman.expansion.compatfactions.utility.FactionsUtil;

public class ListenFactions implements Listener {
    private final FactionsUtil FUTIL;
    private final CompatFactions expansion;
    public ListenFactions(CompatFactions expansion, FactionsUtil futil) {
        this.expansion = expansion;
        this.FUTIL = futil;
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onCancelPVP(EntityDamageByEntityEvent e) {
        if(!e.isCancelled()) return;
        if(ConfigFactions.getNoEntryMode() != NoEntryMode.VULNERABLE) return;
        
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        if(!CombatUtil.isInCombat(player)) return;
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        e.setCancelled(false);
        this.expansion.sendNoEntryMessage(player, enemy);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        if(enemy == null) return;
        
        Location toLoc = e.getTo();
        Location fromLoc = e.getFrom();
        
        if(!FUTIL.isSafeZone(toLoc)) return;
        this.expansion.preventEntry(e, player, toLoc, fromLoc);
    }
}