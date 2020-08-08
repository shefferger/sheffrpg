package sheffrpg.main.enchantments;

import java.util.logging.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CrusherEnchantment extends Enchantment implements Listener {
  int id;
  
  Logger logger = Logger.getLogger("Minecraft");
  
  public CrusherEnchantment(NamespacedKey i, int _id) {
    super(i);
    this.id = _id;
  }
  
  public int getId() {
    return this.id;
  }
  
  @EventHandler
  public void onPlayerHit(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
      Player p = (Player)e.getDamager();
      ItemStack item = p.getInventory().getItemInMainHand();
      if (item.containsEnchantment(this)) {
        int chance = 10;
        int slowlvl = 1;
        int enLvl = item.getEnchantmentLevel(this);
        if (enLvl == 1) {
          chance = 14;
        } else if (enLvl == 2) {
          chance = 18;
        } else if (enLvl == 3) {
          chance = 22;
          slowlvl = 2;
        } 
        if (TryChance.tryChance(chance)) {
          PotionEffect pot = new PotionEffect(PotionEffectType.SLOW, enLvl * 20, slowlvl, false, false);
          PotionEffect pot2 = new PotionEffect(PotionEffectType.POISON, enLvl * 10, 1, false, false);
          Player victim = (Player)e.getEntity();
          victim.addPotionEffect(pot);
          victim.addPotionEffect(pot2);
        } 
      } 
    } 
  }
  
  public boolean canEnchantItem(ItemStack arg0) {
    return true;
  }
  
  public boolean conflictsWith(Enchantment arg0) {
    return false;
  }
  
  public EnchantmentTarget getItemTarget() {
    return null;
  }
  
  public int getMaxLevel() {
    return 3;
  }
  
  public String getName() {
    return "CRUSHER";
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