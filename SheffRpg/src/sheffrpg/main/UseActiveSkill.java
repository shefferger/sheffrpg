package sheffrpg.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class UseActiveSkill implements CommandExecutor{

	private SheffRpg plugin;
	private int count = 0;
	
	public UseActiveSkill(SheffRpg plugin) {
		this.setPlugin(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		String skName = args[0];
		switch (skName){
			case ("fireballs"):
				skName = "Серия огненных шаров";
			break;
			
			case ("snowball"):
				skName = "Леденящий снаряд";
			break;
			
			case ("nightvision"):
				skName = "Кошачье зрение";
			break;
			
			case ("sprint"):
				skName = "Стремительный рывок";
			break;
			
			case ("saturn"):
				skName = "Пророчество Сатурна";
			break;
		}
		int sCost;
		if (SheffRpg.getRpgPlayer(p).playerActiveSkills.containsKey(skName) && SheffRpg.getRpgPlayer(p).playerActiveSkills.get(skName) > 0){
			switch (skName) {
				case ("Серия огненных шаров"):
					count = SheffRpg.getRpgPlayer(p).playerActiveSkills.get(skName);
					sCost = (count * 8) - ((count - 1 ) * 2);
					if (!checkMana(p, sCost)) {
						sendActionbar(p, ChatColor.YELLOW + "Недостаточно маны! Нужно еще: " + ChatColor.AQUA + (sCost - SheffRpg.getRpgPlayer(p).mana));
						return true;
					} else {
						SheffRpg.getRpgPlayer(p).mana -= sCost;
						SheffRpg.getRpgPlayer(p).updateScoreTable();
					}
					SheffRpg.getRpgPlayer(p).regenMana();
					double metaLvl = count;
					Fireball fire = p.launchProjectile(Fireball.class);
					fire.setVelocity(p.getLocation().getDirection().normalize().multiply(1.9));
					fire.setMetadata("lvl", new FixedMetadataValue(plugin, metaLvl));
					fire.setMetadata("atacker", new FixedMetadataValue(plugin, p.getName()));
					fire.setFallDistance(1);
					fire.setGravity(true);
					p.getWorld().playSound(p.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1f, 1f);
					p.getWorld().spawnParticle(Particle.FLAME, p.getEyeLocation(), 2, 0.1f, 0.3f, 0.2f, 0);
					if (count > 4) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {		
								Fireball fire = p.launchProjectile(Fireball.class);
								fire.setVelocity(p.getLocation().getDirection().normalize().multiply(1.9));
								fire.setMetadata("lvl", new FixedMetadataValue(plugin, metaLvl));
								fire.setMetadata("atacker", new FixedMetadataValue(plugin, p.getName()));
								p.getWorld().playSound(p.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1f, 1f);
								p.getWorld().spawnParticle(Particle.FLAME, p.getEyeLocation(), 2, 0.1f, 0.3f, 0.2f, 0);
							}				
						}, 15);
					}
				return true;
				
				case ("Пророчество Сатурна"):
					count = SheffRpg.getRpgPlayer(p).playerActiveSkills.get(skName);
					sCost = count * 4;
					if (!checkMana(p, sCost)) {
						sendActionbar(p, ChatColor.YELLOW + "Недостаточно маны! Нужно еще: " + ChatColor.AQUA + (sCost - SheffRpg.getRpgPlayer(p).mana));
						return true;
					} else {
						SheffRpg.getRpgPlayer(p).mana -= sCost;
						SheffRpg.getRpgPlayer(p).updateScoreTable();
						SheffRpg.getRpgPlayer(p).regenMana();
					}
					
					Location plLoc = p.getLocation();
					Location loc;
					plLoc.setY(plLoc.getY() + 0.12);
					Block bl = p.getWorld().getBlockAt(plLoc);
					BlockData bld = bl.getBlockData();
					
					if (count == 1) {
						if (bl.getBlockData() instanceof Ageable) {
							int age = ((Ageable) bld).getAge();
							if (bl.getType().equals(Material.BEETROOTS) || bl.getType().equals(Material.NETHER_WART)) {
								if (age < 3) {
									((Ageable) bld).setAge(age + 1);
									bl.setBlockData(bld);
								}
							} else {
								if (age < 7) {
									for (int i = (age + 1); i <= 7; i++) {
										((Ageable) bld).setAge(age);
									}
									bl.setBlockData(bld);
								}
							}
							p.getWorld().spawnParticle(Particle.COMPOSTER, plLoc, 6, 0.5f, 0.4f, 0.5f, 0);
						}
					} else {
						count -= 1;
						loc = plLoc;
						int x = loc.getBlockX();
						int z = loc.getBlockZ();
						for (int i = -count; i <= count; i++) {
							loc.setX(x + i);
							for (int j = -count; j <= count; j++) {
								loc.setZ(z + j);						
								bl = p.getWorld().getBlockAt(loc);
								bld = bl.getBlockData();
								if (bl.getBlockData() instanceof Ageable) {
									int age = ((Ageable) bld).getAge();
									if (bl.getType().equals(Material.BEETROOTS) || bl.getType().equals(Material.NETHER_WART)) {
										if (age < 3) {
											((Ageable) bld).setAge(age + 1);
											bl.setBlockData(bld);
										}
									} else {
										if (age < 7) {
											for (int a = (age + 1); a <= 7; a++) {
												((Ageable) bld).setAge(a);
											}
											bl.setBlockData(bld);
										}
									}
								}
								p.getWorld().spawnParticle(Particle.COMPOSTER, loc, 4, 0.8f, 0.7f, 0.8f, 0);
								//plLoc.setX(plLoc.getX() + i);
								//plLoc.setY(plLoc.getY() + j);
							}
						}
					}
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.4f, 0.4f);
					//p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getEyeLocation(), 6, 0.2f, 0.3f, 0.4f, 0);
				return true;
				
				case ("Кошачье зрение"):
					count = SheffRpg.getRpgPlayer(p).playerActiveSkills.get(skName);
					sCost = (10 + (count * 2));
					if (!checkMana(p, sCost)) {
						sendActionbar(p, ChatColor.YELLOW + "Недостаточно маны! Нужно еще: " + ChatColor.AQUA + (sCost - SheffRpg.getRpgPlayer(p).mana));
						return true;
					} else {
						SheffRpg.getRpgPlayer(p).mana -= sCost;
						SheffRpg.getRpgPlayer(p).updateScoreTable();
						SheffRpg.getRpgPlayer(p).regenMana();
					}
					if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
						p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					}
					p.getWorld().playSound(p.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1f, 1f);
					p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getEyeLocation(), 6, 0.2f, 0.3f, 0.4f, 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, (count * 200), 0, false, false, false));
				return true;
				
				case ("Леденящий снаряд"):
					count = SheffRpg.getRpgPlayer(p).playerActiveSkills.get(skName);
					sCost = ((count * 6) - ((count - 1 ) * 2));
					if (!checkMana(p, sCost)) {
						sendActionbar(p, ChatColor.YELLOW + "Недостаточно маны! Нужно еще: " + ChatColor.AQUA + (sCost - SheffRpg.getRpgPlayer(p).mana));
						return true;
					} else {
						SheffRpg.getRpgPlayer(p).mana -= sCost;
						SheffRpg.getRpgPlayer(p).updateScoreTable();
						SheffRpg.getRpgPlayer(p).regenMana();
					}
					p.launchProjectile(Snowball.class).setMetadata("lvl", new FixedMetadataValue(this.getPlugin(), count));

					//if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
					//	p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					//}
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SNOW_GOLEM_DEATH, 1f, 1f);
					p.getWorld().spawnParticle(Particle.SNOW_SHOVEL, p.getLocation(), 18, 0.7f, 0.5f, 0.8f, 1f);
					//p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, (count * 200), 0, false, false, false));
				return true;
				
				case ("Стремительный рывок"):
					count = SheffRpg.getRpgPlayer(p).playerActiveSkills.get(skName);
					sCost = (3 + (count * 3));
					if (!checkMana(p, sCost)) {
						sendActionbar(p, ChatColor.YELLOW + "Недостаточно маны! Нужно еще: " + ChatColor.AQUA + (sCost - SheffRpg.getRpgPlayer(p).mana));
						return true;
					} else {
						SheffRpg.getRpgPlayer(p).mana -= sCost;
						SheffRpg.getRpgPlayer(p).updateScoreTable();
						SheffRpg.getRpgPlayer(p).regenMana();
					}
					if (p.hasPotionEffect(PotionEffectType.SPEED)) {
						p.removePotionEffect(PotionEffectType.SPEED);
					}
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.5f, 0.5f);
					p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getEyeLocation(), 4, 0.2f, 0.3f, 0.4f, 1f);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 7, (count - 1), false, false, false));
					Vector vec = p.getLocation().getDirection().normalize().multiply(count * 0.3 + 0.9);
					vec.setY(vec.getY() / 9);
					p.setVelocity(vec);
				return true;
			}
		}
		return true;
	}
	
	private boolean checkMana(Player pl, int needMana) {
		if (SheffRpg.getRpgPlayer(pl).mana >= needMana) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void sendActionbar(Player _p, String message) {
		_p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
	
	public SheffRpg getPlugin() {
		return plugin;
	}

	public void setPlugin(SheffRpg plugin) {
		this.plugin = plugin;
	}
}
