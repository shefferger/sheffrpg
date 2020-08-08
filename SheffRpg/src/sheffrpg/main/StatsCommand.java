package sheffrpg.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.google.common.collect.Lists;


public class StatsCommand implements CommandExecutor {
	
	private SheffRpg plugin;
	
	public StatsCommand(SheffRpg plugin) {
		this.setPlugin(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		int page = 1;
		if (!(sender instanceof Player))
			return false;
		RpgPlayer pl = SheffRpg.getRpgPlayer(sender);

		if (args.length == 1 || args.length == 2) {
			if (args[0].equalsIgnoreCase("top")) {
				Map<String, Integer> topMap = sortByValue(RpgPlayer.topLvl);
				Integer i = 1;
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "\nТоп 10 по уровню:");
				for (String s : topMap.keySet()) {
					if (i > 10) {
						return true;
					}
					sender.sendMessage(ChatColor.GOLD  + i.toString() + ". " + ChatColor.YELLOW + s + ChatColor.GOLD + ": " + ChatColor.BOLD + ChatColor.AQUA + topMap.get(s) + ChatColor.RESET + ChatColor.GOLD + " уровень");
					i += 1;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("hermes_boots") && sender.hasPermission("SheffRpg.admin")) {
				ItemStack hermes_boots = new ItemStack(Material.LEATHER_BOOTS, 1);
				LeatherArmorMeta meta = (LeatherArmorMeta) hermes_boots.getItemMeta();
				meta.setDisplayName(ChatColor.AQUA + "Сапоги Гермеса");
				meta.setLore(Lists.newArrayList("", "Сапоги всегда ", "помогали Гермесу уйти от", "ответсвенности в нужную минуту"));
				AttributeModifier movspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", 0.06, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
				meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, movspeed);
				meta.setColor(Color.fromRGB(255, 255, 200));
				meta.setCustomModelData(1);
				hermes_boots.setItemMeta(meta);
				hermes_boots.addEnchantment(Enchantment.VANISHING_CURSE, 1);
				pl.getPlayer().getInventory().addItem(hermes_boots);
				return true;
			}
			if (args[0].equalsIgnoreCase("plate_armor") && sender.hasPermission("SheffRpg.admin")) {
				ItemStack plate_armor = new ItemStack(Material.IRON_CHESTPLATE, 1);
				ItemMeta meta = plate_armor.getItemMeta();
				meta.setDisplayName(ChatColor.AQUA + "Латный доспех");
				meta.setLore(Lists.newArrayList("", "Доспехи, отлитые", "первым воином края"));
				meta.setCustomModelData(1);
				AttributeModifier movspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", -0.02, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
				AttributeModifier knockbackRes = new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
				AttributeModifier protection = new AttributeModifier(UUID.randomUUID(), "generic.armor", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
				meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, movspeed);
				meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockbackRes);
				meta.addAttributeModifier(Attribute.GENERIC_ARMOR, protection);
				
				plate_armor.setItemMeta(meta);
				plate_armor.addEnchantment(Enchantment.VANISHING_CURSE, 1);
				pl.getPlayer().getInventory().addItem(plate_armor);
				return true;
			}
			if (args[0].equalsIgnoreCase("fb") && (sender.hasPermission("SheffRpg.admin") || sender.hasPermission("essentials.fireball"))) {
				ItemStack fb = new ItemStack(Material.STICK, 1);
				ItemMeta meta = fb.getItemMeta();
				meta.setDisplayName(ChatColor.WHITE + "Посох " + ChatColor.GOLD + "Огненного шара");
				meta.setLore(Lists.newArrayList("", ChatColor.RED + "Да познают неверные", ChatColor.RED + "огня свет,", ChatColor.RED + "да облачатся они,", ChatColor.RED + "в объятия его."));
				meta.setCustomModelData(1);
				fb.setItemMeta(meta);
				pl.getPlayer().getInventory().addItem(fb);
				return true;
			}
			if (args[0].equalsIgnoreCase("is3") && sender.hasPermission("SheffRpg.admin")) {
				ItemStack is3 = new ItemStack(Material.IRON_SWORD);
				ItemMeta swMeta = is3.getItemMeta();
				swMeta.setDisplayName(ChatColor.GRAY + "Длинный меч стражи");
				swMeta.setLore(Lists.newArrayList(" ", ChatColor.ITALIC + "Простой, длинный, незамысловатый", ChatColor.ITALIC + "длинный стальной меч."));
				swMeta.setCustomModelData(3);
				AttributeModifier attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 7, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
				AttributeModifier atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
				swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
				swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
				is3.setItemMeta(swMeta);
				pl.getPlayer().getInventory().addItem(is3);
				return true;
			}
			if (args.length == 2) {
				try {
					page = Integer.valueOf(args[1]);
				} catch(NumberFormatException exc) {
					sender.sendMessage("Номер страницы должен быть числом!");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("mobs")) {
				sortNprint(pl.mobsKilled, "mobsKilled", sender, page);
				return true;
			} else
			if (args[0].equalsIgnoreCase("kills")) {
				sortNprint(pl.playersKilled, "playersKilled", sender, page);
				return true;
			} else
			if (args[0].equalsIgnoreCase("blocks")) {
				sortNprint(pl.blocksDigged, "blocksDigged", sender, page);
				return true;
			} else
			if (args[0].equalsIgnoreCase("on")) {
				pl.sideBarStats = true;
				sender.sendMessage("Панель статистики включена");
				pl.updateScoreTable();
				return true;
			} else
			if (args[0].equalsIgnoreCase("off")) {
				pl.sideBarStats = false;
				sender.sendMessage("Панель статистики выключена");
				pl.updateScoreTable();
				return true;
			} else
			if (args[0].equalsIgnoreCase("thunder")) {
				pl.thunder = !pl.thunder;
				if (pl.thunder)
					sender.sendMessage("Удар молнии при повышении уровня включен");
				else
					sender.sendMessage("Удар молнии при повышении уровня выключен");
				return true;
			}
		} else {
			if (args.length != 0)
				return false;
			
			int lvl = pl.playerInfo.get("lvl");
			int exp = pl.playerInfo.get("exp");
			sender.sendMessage("\n" + ChatColor.YELLOW + "Статистика игрока " + ChatColor.RED + sender.getName());
			sender.sendMessage(ChatColor.YELLOW + "Уровень: " + ChatColor.BOLD +  lvl);
			sender.sendMessage(ChatColor.AQUA + "Опыт: " + ChatColor.BOLD + exp + "/" + RpgPlayer.getNextExp(lvl));
			sender.sendMessage(ChatColor.YELLOW + "Неиспользованных очков способностей: " + pl.points);
			sender.sendMessage(ChatColor.AQUA + "Убито мобов: " + ChatColor.BOLD + pl.mobsKilled.get("Total"));
			sender.sendMessage(ChatColor.YELLOW + "Поймано рыбы: " + ChatColor.BOLD + pl.mobsKilled.get("Fish"));
			sender.sendMessage(ChatColor.AQUA + "Выкопано блоков: " + ChatColor.BOLD + pl.blocksDigged.get("Total"));
			sender.sendMessage(ChatColor.YELLOW + "Поставлено блоков: " + ChatColor.BOLD + pl.blocksPlaced.get("Total"));
			return true;
		}
		return false;
	}
		
	private void sortNprint(HashMap<String, Integer> m, String n, CommandSender s, Integer page) {
		Set<String> keys = m.keySet();
		String[] r1 = keys.toArray(new String[keys.size()]);
		Integer[] r2 = m.values().toArray(new Integer[keys.size()]);
		
		Integer pages;
		Integer lastp = 0;
				
		for (int i = 0; i < r2.length; i++) {
	        int min = r2[i];
	        int min_i = i; 

	        for (int j = i+1; j < r2.length; j++) {
	            if (r2[j] < min) {
	                min = r2[j];
	                min_i = j;
	            }
	        }

	        if (i != min_i) {
	            int tmp = r2[i];
	            String tmp2 = r1[i];
	            r2[i] = r2[min_i];
	            r1[i] = r1[min_i];
	            r2[min_i] = tmp;
	            r1[min_i] = tmp2;
	        }
	    }
		
		if (r1.length <= 5)
			pages = 1;
		else
			pages = (int) (Math.ceil(((double) r1.length) / 5));
		
		if (page > pages || page == 0)
			page = 1;
		
		switch(n) {
		case("mobsKilled"):
			n = ChatColor.YELLOW + "__ Убитые мобы: __\nСтраница " + page + "/" + pages + " __";
			break;
		
		case("playersKilled"):
			n = ChatColor.YELLOW + "__ Убитые игроки: __\nСтраница " + page + "/" + pages + " __";
			break;
		
		case("blocksDigged"):
			n = ChatColor.YELLOW + "__ Выкопанные блоки: __\nСтраница " + page + "/" + pages + " __";
			break;
		}
		s.sendMessage(n);
		if (page != pages)
			lastp = (r2.length - 5) - ((page - 1) * 5);
		else
			lastp = 0;
		for (int i = (r2.length - 1) - ((page - 1) * 5); i >= lastp; i--) {
			if (r1[i].contains("Total"))
				s.sendMessage(ChatColor.BOLD + "Всего: " + r2[i]);
			else
				s.sendMessage(ChatColor.ITALIC + r1[i] + ": " + r2[i]);
		}
		s.sendMessage("\n");
	}
	
	public SheffRpg getPlugin() {
		return plugin;
	}

	public void setPlugin(SheffRpg plugin) {
		this.plugin = plugin;
	}

	private static Map<String, Integer> sortByValue(Map<String, Integer> m) {

		Set<String> keys = m.keySet();
		String[] r1 = keys.toArray(new String[keys.size()]);
		Integer[] r2 = m.values().toArray(new Integer[keys.size()]);
		int len = r2.length;
		for (int i = 0; i < r2.length; i++) {
	        int min = r2[i];
	        int min_i = i; 

	        for (int j = i+1; j < r2.length; j++) {
	            if (r2[j] < min) {
	                min = r2[j];
	                min_i = j;
	            }
	        }

	        if (i != min_i) {
	            int tmp = r2[i];
	            String tmp2 = r1[i];
	            r2[i] = r2[min_i];
	            r1[i] = r1[min_i];
	            r2[min_i] = tmp;
	            r1[min_i] = tmp2;
	        }
	    }
		
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < len; i++) {
			if (i > 10) {
				break;
			}
			sortedMap.put(r1[len - i - 1], r2[len - i - 1]);
		}
        
        return sortedMap;
    }
}
