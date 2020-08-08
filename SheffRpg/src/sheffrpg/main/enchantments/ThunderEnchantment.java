package sheffrpg.main.enchantments;

import java.util.logging.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftSpectralArrow;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ThunderEnchantment extends Enchantment implements Listener {
  int id;
  
  Logger logger = Logger.getLogger("Minecraft");
  
  public ThunderEnchantment(NamespacedKey i, int _id) {
    super(i);
    this.id = _id;
  }
  
  public int getId() {
    return this.id;
  }
  
  @EventHandler
  public void onPlayerHit(EntityDamageByEntityEvent e) {
    int chance = 15;
    if (e.getDamager() instanceof Player) {
      Player p = (Player)e.getDamager();
      ItemStack item = p.getInventory().getItemInMainHand();
      if (item.containsEnchantment(this) && TryChance.tryChance(chance))
        e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation()); 
    } else if (e.getDamager() instanceof Arrow || e.getDamager() instanceof org.bukkit.craftbukkit.v1_15_R1.entity.CraftTippedArrow || e.getDamager() instanceof CraftSpectralArrow) {
      if (e.getDamager() instanceof CraftSpectralArrow) {
        CraftSpectralArrow a = (CraftSpectralArrow)e.getDamager();
        if (a.getShooter() instanceof Player) {
          Player p = (Player)a.getShooter();
          ItemStack item = p.getInventory().getItemInMainHand();
          if (item.containsEnchantment(this)) {
            if (item.getEnchantmentLevel(this) == 1) {
              chance = 15;
            } else {
              chance = 60;
            } 
            if (TryChance.tryChance(chance))
              e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation()); 
          } 
        } 
      } else {
        Arrow a = (Arrow)e.getDamager();
        if (a.getShooter() instanceof Player) {
          Player p = (Player)a.getShooter();
          ItemStack item = p.getInventory().getItemInMainHand();
          if (item.containsEnchantment(this)) {
            if (item.getEnchantmentLevel(this) == 1) {
              chance = 15;
            } else {
              chance = 60;
            } 
            if (TryChance.tryChance(chance))
              e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation()); 
          } 
        } 
      } 
    } 
  }
  
  public boolean canEnchantItem(ItemStack arg0) {
    return true;
  }
  
  public boolean conflictsWith(Enchantment arg0) {
    if (arg0.equals(Enchantment.ARROW_FIRE))
      return true; 
    return false;
  }
  
  public EnchantmentTarget getItemTarget() {
    return EnchantmentTarget.BOW;
  }
  
  public int getMaxLevel() {
    return 2;
  }
  
  public String getName() {
    return "ZEUS";
  }
  
  public int getStartLevel() {
    return 1;
  }
  
  public boolean isCursed() {
    return false;
  }
  
  public boolean isTreasure() {
    return false;
  }
}
