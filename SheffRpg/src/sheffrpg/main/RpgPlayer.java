package sheffrpg.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Lists;

import jdk.internal.org.jline.utils.Log;

class RpgPlayer{	
	
	private Boolean dbld = false;
	public Boolean manaRegen = false;
	private int taskID = 0;
	
	int exp;
	int nextexp;
	int lvl;
	int points;
	int mana;
	Player p;
	String path;
	FileConfiguration conf;
	static FileConfiguration top;
	File playerData;
	static File topData;
	Scoreboard m;
	public Inventory skillMenu;
	public static Inventory serverMenu;
	public Inventory backpack;
	public boolean thunder = true;
	public boolean sideBarStats = true;
	
	static boolean isCfgLoaded = false;
	static Integer defaultExpGainPerMob = 1;
	static Integer defaultExpGainPerBlock = 1;
	static Integer ExpGainPerCatchFish = 1;
	static Integer ExpRemovePerFailedFish = 1;
	public static HashMap<String, Integer> skillMaxLvl = new HashMap<String, Integer>();
	
	public static TreeMap<String, Integer> topLvl = new TreeMap<String, Integer>();
	
	//public HashMap<String, HashMap<String, Integer>> stats = new HashMap<String, HashMap<String, Integer>>();
	public HashMap<String, Integer> mobsKilled = new HashMap<String, Integer>();
	public HashMap<String, Integer> blocksDigged = new HashMap<String, Integer>();
	public HashMap<String, Integer> blocksPlaced = new HashMap<String, Integer>();
	public HashMap<String, Integer> playersKilled = new HashMap<String, Integer>();
	public HashMap<String, Integer> playerInfo = new HashMap<String, Integer>();
	
	public HashMap<String, Integer> playerPassiveSkills = new HashMap<String, Integer>();
	public HashMap<String, Integer> playerActiveSkills = new HashMap<String, Integer>();
	public HashMap<String, Integer> skillsCost = new HashMap<String, Integer>();
	
	private static SheffRpg plugin;
	
	
	
	public SheffRpg getPlugin() {
		return plugin;
	}

	public static void setPlugin(SheffRpg _plugin) {
		plugin = _plugin;
	}
	
	public RpgPlayer(Player _pl) throws IOException, ClassNotFoundException {
		
		if (!isCfgLoaded) {
			try {
				skillMaxLvl.put("Шахтерские гены", 3);
				skillMaxLvl.put("Максимальное здоровье", 40);
				skillMaxLvl.put("Кулачный бой", 25);
				skillMaxLvl.put("Походный рюкзак", 6);
				skillMaxLvl.put("Интеллект", 200);
				//skillMaxLvl.put("Опытный сталевар", 10);
				skillMaxLvl.put("Опытный рудокоп", 32);
				
				skillMaxLvl.put("Серия огненных шаров", 8);
				skillMaxLvl.put("Леденящий снаряд", 8);
				//skillMaxLvl.put("", 8);
				skillMaxLvl.put("Пророчество Сатурна", 8);
				skillMaxLvl.put("Кошачье зрение", 24);
				skillMaxLvl.put("Стремительный рывок", 10);
				
				thunder = (boolean) SheffRpg.settings.getBoolean("settings.lvlUpThunderEffect");
				defaultExpGainPerBlock = SheffRpg.settings.getInt("settings.defaultExpGainPerBlock");
				defaultExpGainPerMob = SheffRpg.settings.getInt("settings.defaultExpGainPerMob");
				ExpGainPerCatchFish = SheffRpg.settings.getInt("settings.ExpGainPerCatchFish");
				ExpRemovePerFailedFish = SheffRpg.settings.getInt("settings.ExpRemovePerFailedFish");
				isCfgLoaded = true;
				
				performServerMenu();
				
				topData = new File("plugins" + System.getProperty("file.separator") + "SheffRpg" + System.getProperty("file.separator") + "top.yml");
				top = YamlConfiguration.loadConfiguration(topData);
		        if (!topData.exists()) {
		        	topData.createNewFile();
		        } else {
		        	
		        	for (String s : top.getConfigurationSection("top").getKeys(false)) {
		    			int value = top.getInt("top." + s);
		    			topLvl.put(s, value);
		    		}
		        }
			} catch (Exception exc) {
				Log.warn(exc.toString());
			}
		}
		thunder = (boolean) SheffRpg.settings.getBoolean("settings.lvlUpThunderEffect");
		p = _pl;
		path = "plugins" + System.getProperty("file.separator") + "SheffRpg" + System.getProperty("file.separator") + "players" + System.getProperty("file.separator") + p.getUniqueId() + ".yml";
		playerData = new File(path);
		if (!playerData.exists()) {
			playerData.createNewFile();
			exp = 0;
			lvl = 0;
			points = 10;
			mana = 8;
			nextexp = getNextExp(0);
			
			mobsKilled.put("Total", 0);
			mobsKilled.put("Fish", 0);
			playersKilled.put("Total", 0);
			blocksDigged.put("Total", 0);
			blocksPlaced.put("Total", 0);
			playerInfo.put("exp", exp);
			playerInfo.put("lvl", lvl);
			playerInfo.put("points", points);
			playerInfo.put("mana", mana);
			updateTop(p.getName(), 0);
			
			configureSkills();
			p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(12);
			conf = YamlConfiguration.loadConfiguration(playerData);
			
			conf.set("playerUuid", p.getUniqueId().toString());
			conf.set("playerName", p.getName());
			conf.set("playerInfo", playerInfo);
			
			conf.set("settings.sideBarStats", true);
			conf.set("settings.thunderLvlUp", true);
			
			conf.set("skills.passive", playerPassiveSkills);
			conf.set("skills.active", playerActiveSkills);
			conf.set("skills.cost", skillsCost);
			
			conf.set("mobsKilled", mobsKilled);
			conf.set("playersKilled", playersKilled);
			conf.set("blocksDigged", blocksDigged);
			conf.set("blocksPlaced", blocksPlaced);
			backpack = Bukkit.createInventory(null, 9, "Походный рюкзак");
			save();
			
		} else {
			load();
		}
		
		m = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = m.registerNewObjective("stats", "", ChatColor.RED + p.getName());
		
		if (sideBarStats) {
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			o.getScore(ChatColor.AQUA + "Уровень: " + ChatColor.LIGHT_PURPLE + lvl).setScore(11);
			o.getScore(ChatColor.AQUA + "Опыт: " + ChatColor.YELLOW + ChatColor.BOLD + exp + ChatColor.RESET + ChatColor.GOLD + "/" + ChatColor.YELLOW + nextexp).setScore(10);
			o.getScore(ChatColor.GREEN + "Опыта до " + ChatColor.GOLD + (lvl + 1) + ChatColor.GREEN + " ур.: " + ChatColor.DARK_GREEN + ChatColor.BOLD + (nextexp - exp)).setScore(9);
			o.getScore(ChatColor.AQUA + "Очков способностей: " + ChatColor.BOLD + ChatColor.GOLD + points).setScore(8);
			o.getScore("").setScore(7);
			o.getScore(ChatColor.DARK_AQUA + "Убито мобов: " + ChatColor.YELLOW + this.mobsKilled.get("Total")).setScore(6);
			o.getScore(ChatColor.DARK_AQUA + "Поймано рыбы: " + ChatColor.YELLOW + this.mobsKilled.get("Fish")).setScore(5);
			o.getScore(ChatColor.AQUA + "Выкопано блоков: "+ ChatColor.GOLD+ this.blocksDigged.get("Total")).setScore(4);
			o.getScore(ChatColor.AQUA + "Поставлено блоков: " + ChatColor.GOLD + this.blocksPlaced.get("Total")).setScore(3);
			o.getScore("").setScore(2);
			o.getScore(ChatColor.GOLD + "Мана: " + ChatColor.AQUA + mana + ChatColor.GOLD + "/" + ChatColor.AQUA + maxMana()).setScore(1);
			p.setScoreboard(m);
		}
		
		playerPassiveSkills.forEach((k, v) -> {
			useSkill(k, this, v);
		});
		
		performInvMenu();
		
		if (mana < maxMana()) {
			regenMana();
		}
	}
	
	private static void performServerMenu() {
		ItemStack tech;
		ItemMeta meta;
		
		serverMenu = Bukkit.createInventory(null, 27, "Меню сервера Groza Reborn");
		
		tech = new ItemStack(Material.BLUE_ICE);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Телепорт на спавн");
		tech.setItemMeta(meta);
		serverMenu.setItem(0, tech);
		
		tech = new ItemStack(Material.PURPLE_BED);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Телепорт на свой /home");
		tech.setItemMeta(meta);
		serverMenu.setItem(1, tech);
		
		tech = new ItemStack(Material.LANTERN);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Телепорт в свой город");
		tech.setItemMeta(meta);
		serverMenu.setItem(2, tech);
		
		tech = new ItemStack(Material.DIAMOND);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Телепорт в банк");
		tech.setItemMeta(meta);
		serverMenu.setItem(3, tech);
		
		tech = new ItemStack(Material.CARROT);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Телепорт к Ерофею");
		tech.setItemMeta(meta);
		serverMenu.setItem(4, tech);
		
		tech = new ItemStack(Material.IRON_PICKAXE);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Телепорт к торговцу Эйдену");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		tech.setItemMeta(meta);
		serverMenu.setItem(5, tech);
		
		tech = new ItemStack(Material.IRON_SWORD);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Телепорт на ПВП арену");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		tech.setItemMeta(meta);
		serverMenu.setItem(6, tech);
		
		
		tech = new ItemStack(Material.SNOW);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Телепорт на Сплиф-арену");
		tech.setItemMeta(meta);
		serverMenu.setItem(7, tech);
		
		tech = new ItemStack(Material.CRAFTING_TABLE);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Телепорт к рецептам и крафтам");
		tech.setItemMeta(meta);
		serverMenu.setItem(22, tech);
		
		tech = new ItemStack(Material.PLAYER_HEAD);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Открыть меню способностей");
		tech.setItemMeta(meta);
		serverMenu.setItem(26, tech);
		
		tech = new ItemStack(Material.REDSTONE_BLOCK);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Выход");
		tech.setItemMeta(meta);
		serverMenu.setItem(18, tech);
		
	}

	public void performInvMenu() {
		
		skillMenu = Bukkit.createInventory(null, 27, "Меню способностей");
		ItemMeta meta;
		
		ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		meta = border.getItemMeta();
		meta.setDisplayName(" ");
		border.setItemMeta(meta);
		for (int i = 0; i <= 2; i += 1) {
			skillMenu.setItem((i * 9) + 1, border);
			skillMenu.setItem((i * 9) + 5, border);
		}
		
		ItemStack tech;
		
		tech = new ItemStack(Material.REDSTONE_BLOCK);
		meta = tech.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Выход");
		meta.setLore(Lists.newArrayList(""));
		tech.setItemMeta(meta);
		skillMenu.setItem(18, tech);
				
		
		ItemStack skill;
		skill = new ItemStack(Material.ACACIA_STAIRS);
		int sCount = 0;
		String spellType = "";
		String skLore = "";
		String skLore2 = "";
		String skLore3 = "";
		String skLore4 = "";
		int slot;
		for (String ask : playerActiveSkills.keySet()){
			
			switch (ask) {	
				case ("Серия огненных шаров"):
					sCount = playerActiveSkills.get(ask);
					skill = new ItemStack(Material.FIRE_CHARGE);
					slot = 2;
					spellType = "(активная способность)";
					skLore = "научиться кастовать больше огненных шаров.";
					skLore2 = "На текущем ур. вы можете";
					if (sCount <= 4) {
						skLore3 = "скастовать " + ChatColor.GREEN + "1" + ChatColor.YELLOW + " шар с уроном " + ChatColor.GREEN + (8 + sCount) + ChatColor.YELLOW + ", за " + ChatColor.AQUA + ((sCount * 8) - ((sCount - 1 ) * 2)) + ChatColor.YELLOW+ " маны.";
					} else {
						skLore3 = "скастовать " + ChatColor.GREEN + "2" + ChatColor.YELLOW + " шара с уроном " + ChatColor.GREEN + (8 + sCount) + ChatColor.YELLOW + ", за " + ChatColor.AQUA + ((sCount * 8) - ((sCount - 1 ) * 2)) + ChatColor.YELLOW+ " маны.";
					}
					skLore4 = "Для каста нажмите Shift + F, выбрав 1-ый слот инвентаря";
				break;
				
				case ("Леденящий снаряд"):
					sCount = playerActiveSkills.get(ask);
					skill = new ItemStack(Material.SNOWBALL);
					slot = 3;
					spellType = "(активная способность)";
					skLore = "научиться кастовать замедляющие леденящие шары.";
					skLore2 = "На текущем ур. вы можете скастовать снаряд,";
					if (sCount <= 4) {
						skLore3 = "замедляющий на " + ChatColor.GREEN + "15%" + ChatColor.YELLOW + ", на " + ChatColor.GREEN + (sCount * 2) + " сек." + ChatColor.YELLOW + ", за " + ChatColor.AQUA + ((sCount * 6) - ((sCount - 1 ) * 2)) + ChatColor.YELLOW + " маны.";
					} else {
						skLore3 = "замедляющий на " + ChatColor.GREEN + "30%" + ChatColor.YELLOW + ", на " + ChatColor.GREEN + ((sCount * 2) - 6) + " сек." + ChatColor.YELLOW + ", за " + ChatColor.AQUA + ((sCount * 6) - ((sCount - 1 ) * 2)) + ChatColor.YELLOW + " маны.";
					}
					skLore4 = "Для каста нажмите Shift + F, выбрав 2-ой слот инвентаря";
				break;

				
				case ("Пророчество Сатурна"):
					sCount = playerActiveSkills.get(ask);
					skill = new ItemStack(Material.WHEAT);
					slot = 20;
					spellType = "(активная способность)";
					skLore = "способствовать росту культур.";
					skLore2 = "На текущем ур. вы можете";
					skLore3 = "способствовать росту семян в радиусе " + ChatColor.GREEN + playerActiveSkills.get(ask) + ChatColor.YELLOW + " за " + ChatColor.AQUA + (sCount * 4) + ChatColor.YELLOW + " маны.";
					skLore4 = "Для каста встаньте на пашню, нажмите Shift + F, выбрав 7-ой слот инвентаря";
				break;
				
				
				case ("Стремительный рывок"):
					sCount = playerActiveSkills.get(ask);
					skill = new ItemStack(Material.RABBIT_FOOT);
					slot = 21;
					spellType = "(активная способность)";
					skLore = "познать скорость древних (попробуй элитры).";
					skLore2 = "На текущем ур. вы можете";
					skLore3 = "совершить " + ChatColor.GREEN + (playerActiveSkills.get(ask) * 2) + ChatColor.YELLOW + " кратное ускорение за " + ChatColor.AQUA + (3 + (sCount * 3)) + ChatColor.YELLOW + " маны.";
					skLore4 = "Для каста нажмите Shift + F, выбрав 8-ой слот инвентаря";
				break;
				
				
				case ("Кошачье зрение"):
					sCount = playerActiveSkills.get(ask);
					skill = new ItemStack(Material.ENDER_EYE);
					slot = 22;
					spellType = "(активная способность)";
					skLore = "зреть во тьме.";
					skLore2 = "На текущем ур. вы можете";
					skLore3 = "получить ночное зрение на " + ChatColor.GREEN + (playerActiveSkills.get("Кошачье зрение") * 10) + ChatColor.YELLOW + " секунд за " + ChatColor.AQUA + (10 + (sCount * 2)) + ChatColor.YELLOW + " маны.";
					skLore4 = "Для каста нажмите Shift + F, выбрав 9-ый слот инвентаря";
				break;
				
				
				default:
					slot = 0;
				break;
			}
			
			int skillLvl;
			skillLvl = playerActiveSkills.get(ask);

			if (skillLvl == 0)
				skill.setAmount(1);
			else
				skill.setAmount(skillLvl);
			meta = skill.getItemMeta();
			meta.setDisplayName(ask + ", " + skillLvl + " уровень");
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			if (skillLvl < skillMaxLvl.get(ask)) {
				meta.setLore(Lists.newArrayList(
						ChatColor.DARK_GRAY + spellType, 
						ChatColor.GOLD + "Повышайте уровень способности, чтобы", 
						ChatColor.GOLD + skLore,
						ChatColor.YELLOW + skLore2,
						ChatColor.YELLOW + skLore3,
						ChatColor.YELLOW + skLore4,
						ChatColor.GREEN + "Для улучшения", 
						ChatColor.GREEN + "необходимо: " + ChatColor.AQUA + skillsCost.get(ask) + ChatColor.GREEN + " очков способностей", 
						ChatColor.DARK_GREEN + "Нажмите для повышения уровня"));
			} else {
				meta.setLore(Lists.newArrayList(
						ChatColor.DARK_GRAY + spellType, 
						ChatColor.GOLD + "Повышайте уровень способности, чтобы", 
						ChatColor.GOLD + skLore,
						ChatColor.YELLOW + skLore2,
						ChatColor.YELLOW + skLore3,
						ChatColor.YELLOW + skLore4,
						ChatColor.GREEN + "Достигнут максимальный", 
						ChatColor.GREEN + "уровень навыка!"));
			}
			skill.setItemMeta(meta);
			skillMenu.setItem(slot, skill);
		}
		for (String sk : playerPassiveSkills.keySet()){
			switch (sk) {		
				case ("Интеллект"):
					spellType = "(пассивная способность)";
					skill = new ItemStack(Material.PLAYER_HEAD);
					slot = 6;
					skLore = "повысить интеллект и запас маны на " + ChatColor.AQUA + "4 ед.";
					break;
			
				case ("Максимальное здоровье"):
					spellType = "(пассивная способность)";
					skill = new ItemStack(Material.BEETROOT);
					slot = 7;
					skLore = "повысить запас здоровья на " + ChatColor.AQUA + "1 ед.";
				break;
				
				case ("Шахтерские гены"):
					spellType = "(пассивная способность)";
					skill = new ItemStack(Material.GOLDEN_PICKAXE);
					slot = 8;
					skLore = "повысить навык владения киркой и увеличить скорость добычи блоков";
				break;
				
				case ("Опытный рудокоп"):
					spellType = "(пассивная способность)";
					skill = new ItemStack(Material.IRON_PICKAXE);
					slot = 15;
					skLore = "повысить шанс для добычи двойного кол-ва руды " + ChatColor.DARK_AQUA + " (на " + (((double) playerPassiveSkills.get("Опытный рудокоп")) * 3 / 4) + "%)";
				break;
				
				case ("Кулачный бой"):
					spellType = "(пассивная способность)";
					skill = new ItemStack(Material.TOTEM_OF_UNDYING);
					slot = 16;
					skLore = "наносить больший урон в рукопашном бою";
				break;
				
				case ("Походный рюкзак"):
					spellType = "(пассивная способность)";
					skill = new ItemStack(Material.CHEST);
					slot = 17;
					skLore = "носить с собой больше вещей " + ChatColor.AQUA + "(команда /bp)";
				break;
				
				default:
					slot = 0;
					skLore = "Ошибка";
				break;
			}
			int skillLvl;
			skillLvl = playerPassiveSkills.get(sk);

			if (skillLvl == 0)
				skill.setAmount(1);
			else
				skill.setAmount(skillLvl);
			meta = skill.getItemMeta();
			meta.setDisplayName(sk + ", " + skillLvl + " уровень");
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			if (skillLvl < skillMaxLvl.get(sk))
				meta.setLore(Lists.newArrayList(
						ChatColor.DARK_GRAY + spellType, 
						ChatColor.GOLD + "Повышайте уровень способности, чтобы", 
						ChatColor.GOLD + skLore,
						ChatColor.GREEN + "Для улучшения", 
						ChatColor.GREEN + "необходимо: " + ChatColor.AQUA + skillsCost.get(sk) + ChatColor.GREEN + " очков способностей", 
						ChatColor.DARK_GREEN + "Нажмите для повышения уровня"));
			else
				meta.setLore(Lists.newArrayList(
						ChatColor.DARK_GRAY + spellType, 
						ChatColor.GOLD + "Повышайте уровень способности, чтобы", 
						ChatColor.GOLD + skLore,
						ChatColor.GREEN + "Достигнут максимальный", 
						ChatColor.GREEN + "уровень навыка!"));

			skill.setItemMeta(meta);
			skillMenu.setItem(slot, skill);
		}
	}

	
	private void configureSkills() {
		//	ПАССИВНЫЕ			НАЗВАНИЕ СКИЛЛА,		ЛВЛ
		playerPassiveSkills.put("Максимальное здоровье", 6);
		playerPassiveSkills.put("Шахтерские гены", 0);
		playerPassiveSkills.put("Кулачный бой", 1);
		playerPassiveSkills.put("Походный рюкзак", 1);
		playerPassiveSkills.put("Интеллект", 2);
		playerPassiveSkills.put("Опытный рудокоп", 0);
		
		//  АКТИВНЫЕ
		playerActiveSkills.put("Серия огненных шаров", 0);
		playerActiveSkills.put("Леденящий снаряд", 0);
		playerActiveSkills.put("Кошачье зрение", 0);
		playerActiveSkills.put("Стремительный рывок", 0);
		playerActiveSkills.put("Пророчество Сатурна", 0);
		
		//	СТОИМОСТЬ		НАЗВАНИЕ СКИЛЛА,	ЦЕНА
		skillsCost.put("Максимальное здоровье", 6);
		skillsCost.put("Кулачный бой", 2);
		skillsCost.put("Шахтерские гены", 50);
		skillsCost.put("Походный рюкзак", 20);
		skillsCost.put("Интеллект", 2);
		skillsCost.put("Опытный рудокоп", 3);
		
		skillsCost.put("Серия огненных шаров", 10);
		skillsCost.put("Леденящий снаряд", 8);
		skillsCost.put("Кошачье зрение", 9);
		skillsCost.put("Стремительный рывок", 6);
		skillsCost.put("Пророчество Сатурна", 5);
		
	}

	public void updateScoreTable() {
		if (sideBarStats) {
			p.setScoreboard(m);
			Scoreboard s = p.getScoreboard();
			for (String e: s.getEntries())
				s.resetScores(e);
			Objective o = s.getObjective("stats");
			
			o.getScore(ChatColor.AQUA + "Уровень: " + ChatColor.LIGHT_PURPLE + lvl).setScore(11);
			o.getScore(ChatColor.AQUA + "Опыт: " + ChatColor.YELLOW + ChatColor.BOLD + exp + ChatColor.RESET + ChatColor.GOLD + "/" + ChatColor.YELLOW + nextexp).setScore(10);
			o.getScore(ChatColor.GREEN + "Опыта до " + ChatColor.GOLD + (lvl + 1) + ChatColor.GREEN + " ур.: " + ChatColor.DARK_GREEN + ChatColor.BOLD + (nextexp - exp)).setScore(9);
			o.getScore(ChatColor.AQUA + "Очков способностей: " + ChatColor.BOLD + ChatColor.GOLD + points).setScore(8);
			o.getScore("").setScore(7);
			o.getScore(ChatColor.DARK_AQUA + "Убито мобов: " + ChatColor.YELLOW + this.mobsKilled.get("Total")).setScore(6);
			o.getScore(ChatColor.DARK_AQUA + "Поймано рыбы: " + ChatColor.YELLOW + this.mobsKilled.get("Fish")).setScore(5);
			o.getScore(ChatColor.AQUA + "Выкопано блоков: "+ ChatColor.GOLD+ this.blocksDigged.get("Total")).setScore(4);
			o.getScore(ChatColor.AQUA + "Поставлено блоков: " + ChatColor.GOLD + this.blocksPlaced.get("Total")).setScore(3);
			o.getScore("").setScore(2);
			o.getScore(ChatColor.GOLD + "Мана: " + ChatColor.AQUA + mana + ChatColor.GOLD + "/" + ChatColor.AQUA + maxMana()).setScore(1);
		} else {
			p.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
		}
	}

	private int maxMana() {
		int maxmana = 8 + (playerPassiveSkills.get("Интеллект") * 4 - 4);
		return maxmana;
	}
	
	public void save() throws FileNotFoundException, IOException {
		if (playerActiveSkills.containsKey("Временная скорость")) {
			playerActiveSkills.remove("Временная скорость");
		}
		if (skillsCost.containsKey("Временная скорость")) {
			skillsCost.remove("Временная скорость");
		}
		
		playerInfo.replace("mana", mana);
		conf.set("playerInfo", playerInfo);
		conf.set("mobsKilled", mobsKilled);
		conf.set("playersKilled", playersKilled);
		conf.set("blocksDigged", blocksDigged);
		conf.set("blocksPlaced", blocksPlaced);
		
		conf.set("skills.passive", playerPassiveSkills);
		conf.set("skills.active", playerActiveSkills);
		conf.set("skills.cost", skillsCost);
		
		ItemStack[] items = backpack.getContents();
		conf.set("backpack", items);
				
		conf.set("settings.thunderLvlUp", thunder);
		conf.set("settings.sideBarStats", sideBarStats);
		
		conf.save(playerData);
		
		top.set("top", topLvl);
		top.save(topData);
	}
	
	public void load() throws IOException, ClassNotFoundException {
		conf = YamlConfiguration.loadConfiguration(playerData);
		top = YamlConfiguration.loadConfiguration(topData);
		for (String s : conf.getConfigurationSection("mobsKilled").getKeys(false)) {
			int value = conf.getInt("mobsKilled." + s);
			mobsKilled.put(s, value);
		}
		for (String s : conf.getConfigurationSection("playersKilled").getKeys(false)) {
			int value = conf.getInt("playersKilled." + s);
			playersKilled.put(s, value);
		}
		for (String s : conf.getConfigurationSection("blocksDigged").getKeys(false)) {
			int value = conf.getInt("blocksDigged." + s);
			blocksDigged.put(s, value);
		}
		if (conf.contains("blocksPlaced")) {
			for (String s : conf.getConfigurationSection("blocksPlaced").getKeys(false)) {
				int value = conf.getInt("blocksPlaced." + s);
				blocksPlaced.put(s, value);
			}
		} else {
			blocksPlaced.put("Total", 0);
		}
		for (String s : conf.getConfigurationSection("playerInfo").getKeys(false)) {
			int value = conf.getInt("playerInfo." + s);
			playerInfo.put(s, value);
		}
		for (String s : conf.getConfigurationSection("skills.passive").getKeys(false)) {
			int value = conf.getInt("skills.passive." + s);
			playerPassiveSkills.put(s, value);
		}
		if (conf.contains("skills.active")) {
			
			for (String s : conf.getConfigurationSection("skills.active").getKeys(false)) {
				int value = conf.getInt("skills.active." + s);
				playerActiveSkills.put(s, value);
			}
			if (playerActiveSkills.containsKey("Временная скорость")) {
				playerActiveSkills.remove("Временная скорость");
			}
		}
		for (String s : conf.getConfigurationSection("skills.cost").getKeys(false)) {
			int value = conf.getInt("skills.cost." + s);
			skillsCost.put(s, value);
		}
		String hpName = "Максимальное здоровье";
		if (playerPassiveSkills.get(hpName) > skillMaxLvl.get(hpName)) {
			int lvlDiff = playerPassiveSkills.get(hpName) - skillMaxLvl.get(hpName);
			for (int i = 0; i < lvlDiff; i++) {
				skillsCost.replace(hpName, skillsCost.get(hpName));
				givePoints(skillsCost.get(hpName));
			}
			playerPassiveSkills.replace(hpName, skillMaxLvl.get(hpName));
			useSkill(hpName, this, playerPassiveSkills.get(hpName));
		}
		
		if (!playerPassiveSkills.containsKey("Шахтерские гены")) {
			skillsCost.put("Шахтерские гены", 30);
			playerPassiveSkills.put("Шахтерские гены", 0);
		}
		if (!playerActiveSkills.containsKey("Кошачье зрение")) {
			skillsCost.put("Кошачье зрение", 8);
			playerActiveSkills.put("Кошачье зрение", 0);
		}
		if (!playerActiveSkills.containsKey("Стремительный рывок")) {
			skillsCost.put("Стремительный рывок", 6);
			playerActiveSkills.put("Стремительный рывок", 0);
		}
		if (!playerActiveSkills.containsKey("Леденящий снаряд")) {
			skillsCost.put("Леденящий снаряд", 8);
			playerActiveSkills.put("Леденящий снаряд", 0);
		}
		if (!playerActiveSkills.containsKey("Пророчество Сатурна")) {
			skillsCost.put("Пророчество Сатурна", 5);
			playerActiveSkills.put("Пророчество Сатурна", 0);
		}
		if (!playerPassiveSkills.containsKey("Кулачный бой")) {
			skillsCost.put("Кулачный бой", 5);
			playerPassiveSkills.put("Кулачный бой", 1);
		}
		if (!playerPassiveSkills.containsKey("Опытный рудокоп")) {
			skillsCost.put("Опытный рудокоп", 4);
			playerPassiveSkills.put("Опытный рудокоп", 0);
		}
		if (!playerPassiveSkills.containsKey("Интеллект")) {
			skillsCost.put("Интеллект", 2);
			playerPassiveSkills.put("Интеллект", 1);
			mana = 10;
		}
		
		if (!playerActiveSkills.containsKey("Серия огненных шаров")) {
			skillsCost.put("Серия огненных шаров", 10);
			playerActiveSkills.put("Серия огненных шаров", 0);
		}
		
		
		if (!skillsCost.containsKey("Временная скорость")) {
			skillsCost.remove("Временная скорость");
		}
		if (!playerInfo.containsKey("mana")) {
			playerInfo.put("mana", mana);
		} else {
			mana = playerInfo.get("mana");
		}
		
		if (conf.contains("backpack")) {
			backpack = Bukkit.createInventory(null, playerPassiveSkills.get("Походный рюкзак") * 9, "Походный рюкзак");
			@SuppressWarnings("unchecked")
			ArrayList<ItemStack> content = (ArrayList<ItemStack>) conf.getList("backpack");
			ItemStack[] items = new ItemStack[content.size()];
			for (int i = 0; i < content.size(); i++) {
			    ItemStack item = content.get(i);
			    if (item != null) {
			        items[i] = item;
			    } else {
			        items[i] = new ItemStack(Material.AIR);
			    }
			}
			backpack.setContents(items);
		}
		if (!playerPassiveSkills.containsKey("Походный рюкзак")) {
			skillsCost.put("Походный рюкзак", 20);
			playerPassiveSkills.put("Походный рюкзак", 1);
			backpack = Bukkit.createInventory(null, 9, "Походный рюкзак");
			for (int i = 0; i <= 9; i++) {
				backpack.addItem(new ItemStack(Material.AIR));
			}
			save();
		}

		thunder = conf.getBoolean("settings.thunderLvlUp");
		sideBarStats = conf.getBoolean("settings.sideBarStats");
		lvl = playerInfo.get("lvl");
		exp = playerInfo.get("exp");
		points = playerInfo.get("points");
		nextexp = getNextExp(lvl);
		
		updateTop(this.Name(), lvl);
	}
			
	public String Name() {
	    return p.getName();
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public static Integer getNextExp(Integer l) {
		//((x * 2) ^ (1 / 2)) * 60 + 100
		//(x * 3) ^ (1 / 2) * 65 + 200
		//
		//200 + (((x * 15) ^ (1 / 3)) * 160) red
		//200 + (((x * 10) ^ (1 / 2)) * 50) green
		//200 + (((x * 3) ^ (1 / 2)) * 80) purple
		//
		//100 + 50 * ((x * 15) ^ ( 1 / 2))
		return (100 + ((int) (long) Math.round(Math.sqrt(l * 15) * 40)));
	}
			
	public void envKill(String mobName) {
		if (mobsKilled.containsKey(mobName))
			mobsKilled.replace(mobName, mobsKilled.get(mobName) + 1);
		else
			mobsKilled.put(mobName, 1);
		mobsKilled.replace("Total", mobsKilled.get("Total") + 1);
		
		//p.sendMessage(mobName);
		if (SheffRpg.mobs.containsKey(mobName))
			giveExp(SheffRpg.mobs.get(mobName));
		else
			giveExp(defaultExpGainPerMob);
	}
	
	public void blockBreak(String block, BlockBreakEvent e) {
		Material blt = e.getBlock().getType();
		Location oreLoc = e.getBlock().getLocation();
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
		    @Override
		     public void run() {
		    	if (e.isCancelled())
					return;
				if (blocksDigged.containsKey(block))
					blocksDigged.replace(block, blocksDigged.get(block) + 1);
				else
					blocksDigged.put(block, 1);
				blocksDigged.replace("Total", blocksDigged.get("Total") + 1);
				//p.sendMessage("Block: " + ChatColor.GOLD + block);
				if ((playerPassiveSkills.containsKey("Опытный рудокоп")) && (blt.equals(Material.IRON_ORE) || blt.equals(Material.DIAMOND_ORE) || blt.equals(Material.GOLD_ORE) || blt.equals(Material.LAPIS_ORE) || blt.equals(Material.EMERALD_ORE) || blt.equals(Material.NETHER_QUARTZ_ORE))) {
					if (blocksPlaced.containsKey(block)) {
						if (blocksPlaced.get(block) <= 0) {
							if (TryChance.trySmallChance(((double) playerPassiveSkills.get("Опытный рудокоп")) * 3 / 4)) {
								e.getPlayer().getWorld().dropItemNaturally(oreLoc, new ItemStack(blt, 1));
								oreLoc.getWorld().playSound(oreLoc, Sound.ENTITY_LEASH_KNOT_BREAK, 0.75f, 0.75f);
								oreLoc.getWorld().spawnParticle(Particle.CRIT_MAGIC, oreLoc, 12, 0.7f, 0.7f, 0.7f, 0.5f);
							}
						} else {
							blocksPlaced.replace(block, blocksPlaced.get(block) - 1);
						}
					} else {
						if (TryChance.trySmallChance(((double) playerPassiveSkills.get("Опытный рудокоп")) * 3 / 4)) {
							e.getPlayer().getWorld().dropItemNaturally(oreLoc, new ItemStack(blt, 1));
							oreLoc.getWorld().playSound(oreLoc, Sound.ENTITY_LEASH_KNOT_BREAK, 0.75f, 0.75f);
							oreLoc.getWorld().spawnParticle(Particle.CRIT_MAGIC, oreLoc, 12, 0.7f, 0.7f, 0.7f, 0.5f);
						}
					}
				}
				if (SheffRpg.blocks.containsKey(block)) {
					if (!e.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) { 
						giveExp(SheffRpg.blocks.get(block));
					} else {
						giveExp(defaultExpGainPerBlock);
					}
				}
				else {
					giveExp(defaultExpGainPerBlock);
				}
		     }
		}, 1);
	}
	
	public void blockPlace(String block, BlockPlaceEvent e) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
		    @Override
		     public void run() {
		    	if (e.isCancelled())
					return;
				if (blocksPlaced.containsKey(block))
					blocksPlaced.replace(block, blocksPlaced.get(block) + 1);
				else
					blocksPlaced.put(block, 1);
				blocksPlaced.replace("Total", blocksPlaced.get("Total") + 1);
				if (SheffRpg.blocks.containsKey(block)) {
					if (SheffRpg.blocks.get(block) != 0) {
						giveExp(1);
					}
				} else {
					giveExp(1);
				}
				/*if (SheffRpg.blocks.containsKey(block)) {
					if (!e.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) { 
						giveExp(SheffRpg.blocks.get(block));
					} else {
						giveExp(defaultExpGainPerBlock);
					}
				}
				else {
					giveExp(defaultExpGainPerBlock);
				}*/
		     }
		}, 1);
	}
	
	
	public void plKill(String pl, Integer expGain) {
		if (playersKilled.containsKey(pl))
			playersKilled.replace(pl, playersKilled.get(pl) + 1);
		else
			playersKilled.put(pl, 1);
		giveExp(expGain);
		p.sendMessage(ChatColor.YELLOW + "\nВы получили " + ChatColor.RED + expGain + ChatColor.YELLOW + " ед. опыта за убийство");
		playersKilled.replace("Total", playersKilled.get("Total") + 1);
		
		if (!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = p.getInventory().getItemInMainHand();
			if (item.getType().equals(Material.DIAMOND_SWORD) && item.getItemMeta().hasCustomModelData()) {
				if (item.getItemMeta().getCustomModelData() == 6) {
					p.playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.55f, 1);
					Player vicPl = Bukkit.getServer().getPlayer(pl);
					if ((SheffRpg.players.get(vicPl).lvl < 20) || (SheffRpg.players.get(vicPl).points <= 0)) {
						p.sendMessage(ChatColor.RED + "Душа " + ChatColor.YELLOW + pl + ChatColor.RED + " еще слишком слаба для поглощения " + ChatColor.DARK_RED + "Мурамасой...");
						return;
					}
					p.sendMessage(ChatColor.RED + "Ваш кровавый " + ChatColor.DARK_RED + "Мурамаса" + ChatColor.RED + " набирает силы...");
					ItemMeta meta = item.getItemMeta();
					List<String> lore = meta.getLore();
					String kills = lore.get(lore.size() - 1);
					int k = Integer.valueOf(kills.split(" ", 2)[1]);
					k += 1;
					lore.remove(lore.size() - 1);
					lore.add(ChatColor.RED + "Убийств: " + k);
					meta.setLore(lore);
					if (!meta.hasEnchant(Enchantment.VANISHING_CURSE)) {
						meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
					}
					if (k <= 100) {
						AttributeModifier attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 0.24, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
						meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
					}
					item.setItemMeta(meta);					
				}
			}
		}
	}
	
	public Integer plDead() {
		if (p.hasPermission("SheffRpg.noexploss")) {
			p.sendMessage(ChatColor.YELLOW + "\nБлагодаря привилегии вы потеряли " + ChatColor.RED + "0" + ChatColor.YELLOW + " ед. опыта при смерти");
			return 0;
		} else {
			if (p.getInventory().getItemInMainHand().equals(SheffRpg.amulet_exp) || p.getInventory().getItemInOffHand().equals(SheffRpg.amulet_exp)) {
				p.sendMessage(ChatColor.RED + "[Анубис]: " + ChatColor.GOLD + "Сегодня тебе повзело...");
				return 0;
			}
			Integer expLoss = lvl * 15;
			p.sendMessage(ChatColor.YELLOW + "\nВы потеряли " + ChatColor.RED + expLoss + ChatColor.YELLOW + " ед. опыта при смерти");
			removeExp(expLoss);
			p.sendMessage(ChatColor.YELLOW + "Ваш уровень теперь: " + ChatColor.RED + lvl);
			return ((expLoss / 4) * 3);
		}
	}
	
	
	public static void updateEffects(RpgPlayer pl) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
		    @Override
		     public void run() {
		    	pl.playerPassiveSkills.forEach((k, v) -> {
		    	useSkill(k, pl, v);
			});
		    }
		}, 40);
	}
	
	public void fish(String state) {
		switch (state) {
		case("CAUGHT_FISH"):
			giveExp(ExpGainPerCatchFish * 5);
			this.envKill("Fish");
			break;
		case("FAILED_ATTEMPT"):
			removeExp(ExpRemovePerFailedFish);
			break;
		}
	}
	
	public void enchant(Integer cost) {
		giveExp(cost * 3);
	}
	
	public void furnace(Integer xpAmount) {
		giveExp(xpAmount * 2);
	}
	
	private static void updateTop(String name, int level) {
		if (topLvl.containsKey(name)) {
			topLvl.replace(name, level);
		} else {
			topLvl.put(name, level);
		}
	}
	
	public void giveExp(Integer gexp) {
		if (getPlayer().hasPermission("SheffRpg.doublexp") && !dbld) {
			gexp += gexp;
			dbld = true;
		}
		exp += gexp;	
		if (exp >= nextexp) {	
			lvl += 1;
			
			exp = exp - nextexp;
			playerInfo.replace("lvl", lvl);
			nextexp = getNextExp(lvl);
			p.sendMessage(ChatColor.RED + "[SRPG] " + ChatColor.AQUA + "Поздравляем! Вы получили " + ChatColor.YELLOW + lvl + ChatColor.AQUA + " уровень." + "\nОпыта до следующего уровня: " + ChatColor.YELLOW + exp + "/" + nextexp);
			points += 1;
			if (thunder) {
				p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2, 1));
				p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 2);
			}
			if (exp >= nextexp) {
				giveExp(0);
			}
			updateTop(this.Name(), lvl);
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		dbld = false;
		updateScoreTable();
		playerInfo.replace("exp", exp);
		playerInfo.replace("points", points);
	}
	
	public void removeExp(Integer remexp) {
		exp -= remexp;
		if (exp < 0) {
			lvl -=  1;
			updateTop(this.getPlayer().getName(), lvl);
			points -= 1;
			if (lvl < 0) {
				lvl = 0;
				if (exp < 0)
					exp = 0;
				updateScoreTable();
				playerInfo.replace("exp", exp);
				playerInfo.replace("lvl", lvl);
				return;
			}
			Integer tmp = Math.abs(exp);	
			nextexp = getNextExp(lvl);
			exp = nextexp;
			removeExp(tmp);
		}
		updateScoreTable();
		playerInfo.replace("exp", exp);
		playerInfo.replace("lvl", lvl);
		try {
			save();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void givePoints(int _points) {
		points += _points;
		updateScoreTable();
	}
	
	public void giveLvl(int _lvl) {
		for (int i = lvl; i < (lvl + _lvl); ) {
			giveExp(100);
			i = lvl;
		}
	}
	
	public void activeSkillLvlUp(String skillName) {
		Integer cost = skillsCost.get(skillName);
		Integer skillLvl = playerActiveSkills.get(skillName);
		ItemMeta meta;
		if (skillLvl >= skillMaxLvl.get(skillName)) {
			p.sendMessage(ChatColor.RED + "[SRPG] " + ChatColor.GOLD + "Навык \"" + ChatColor.AQUA + skillName + ChatColor.GOLD + "\" достиг максимального уровня! (" + ChatColor.AQUA + skillLvl + ChatColor.GOLD + ")");
			return;
		}
		if (cost <= points) {
			ItemStack skill;
			int slot;
			int addPrice;
			skillLvl += 1;
			playerActiveSkills.replace(skillName, skillLvl);
			p.sendMessage(ChatColor.RED + "[SRPG] " + ChatColor.GOLD + "Навык \"" + ChatColor.AQUA + skillName + ChatColor.GOLD + "\" повышен до " + ChatColor.AQUA + skillLvl + ChatColor.GOLD + " уровня");
			points -= cost;
			
			//useSkill(skillName, this, skillLvl);
			updateScoreTable();
			int sCount = 0;
			String skLore = "";
			String skLore2 = "";
			String skLore3 = "";
			String skLore4 = "";
			switch (skillName) {
			case ("Серия огненных шаров"):
				sCount = playerActiveSkills.get("Серия огненных шаров");
				slot = 2;
				skLore = "научиться кастовать больше огненных шаров.";
				skLore2 = "На текущем ур. вы можете";
				if (sCount <= 4) {
					skLore3 = "скастовать " + ChatColor.GREEN + "1" + ChatColor.YELLOW + " шар с уроном " + ChatColor.GREEN + (8 + sCount) + ChatColor.YELLOW + ", за " + ChatColor.AQUA + ((sCount * 8) - ((sCount - 1 ) * 2)) + ChatColor.YELLOW+ " маны.";
				} else {
					skLore3 = "скастовать " + ChatColor.GREEN + "2" + ChatColor.YELLOW + " шара с уроном " + ChatColor.GREEN + (8 + sCount) + ChatColor.YELLOW + ", за " + ChatColor.AQUA + ((sCount * 8) - ((sCount - 1 ) * 2)) + ChatColor.YELLOW+ " маны.";
				}
				skLore4 = "Для каста нажмите Shift + F, выбрав 1-ый слот инвентаря";
				addPrice = 8;
				break;
				
			case ("Леденящий снаряд"):
				sCount = playerActiveSkills.get(skillName);
				skill = new ItemStack(Material.FIRE_CHARGE);
				slot = 3;
				skLore = "научиться кастовать замедляющие леденящие шары.";
				skLore2 = "На текущем ур. вы можете скастовать снаряд,";
				if (sCount <= 4) {
					skLore3 = "замедляющий на " + ChatColor.GREEN + "15%" + ChatColor.YELLOW + ", на " + ChatColor.GREEN + (sCount * 2) + " сек." + ChatColor.YELLOW + ", за " + ChatColor.AQUA + ((sCount * 6) - ((sCount - 1 ) * 2)) + ChatColor.YELLOW + " маны.";
				} else {
					skLore3 = "замедляющий на " + ChatColor.GREEN + "30%" + ChatColor.YELLOW + ", на " + ChatColor.GREEN + ((sCount * 2) - 6) + " сек." + ChatColor.YELLOW + ", за " + ChatColor.AQUA + ((sCount * 6) - ((sCount - 1 ) * 2)) + ChatColor.YELLOW + " маны.";
				}
				skLore4 = "Для каста нажмите Shift + F, выбрав 2-ой слот инвентаря";
				addPrice = 6;
				break;
				
			case ("Кошачье зрение"):
				sCount = playerActiveSkills.get("Кошачье зрение");
				skill = new ItemStack(Material.ENDER_EYE);
				slot = 22;
				skLore = "зреть во тьме.";
				skLore2 = "На текущем ур. вы можете";
				skLore3 = "получить ночное зрение на " + ChatColor.GREEN + (playerActiveSkills.get("Кошачье зрение") * 10) + ChatColor.YELLOW + " секунд за " + ChatColor.AQUA + (10 + (sCount * 2)) + ChatColor.YELLOW + " маны.";
				skLore4 = "Для каста нажмите Shift + F, выбрав 9-ый слот инвентаря";
				addPrice = 1;
				break;
				
			case ("Стремительный рывок"):
				sCount = playerActiveSkills.get(skillName);
				skill = new ItemStack(Material.RABBIT_FOOT);
				slot = 21;
				skLore = "познать скорость древних (попробуй элитры).";
				skLore2 = "На текущем ур. вы можете";
				skLore3 = "совершить " + ChatColor.GREEN + (playerActiveSkills.get(skillName) * 2) + ChatColor.YELLOW + " кратное ускорение за " + ChatColor.AQUA + (3 + (sCount * 3)) + ChatColor.YELLOW + " маны.";
				skLore4 = "Для каста нажмите Shift + F, выбрав 8-ой слот инвентаря";
				addPrice = 6;
				break;
				
			case ("Пророчество Сатурна"):
				sCount = playerActiveSkills.get(skillName);
				skill = new ItemStack(Material.WHEAT);
				slot = 20;
				skLore = "способствовать росту культур.";
				skLore2 = "На текущем ур. вы можете";
				skLore3 = "способствовать росту семян в радиусе " + ChatColor.GREEN + playerActiveSkills.get(skillName) + ChatColor.YELLOW + " за " + ChatColor.AQUA + (sCount * 4) + ChatColor.YELLOW + " маны.";
				skLore4 = "Для каста встаньте на пашню, нажмите Shift + F, выбрав 7-ой слот инвентаря";
				addPrice = 5;
			break;
				
			default:
				slot = 0;
				skLore = "Произошла ошибка";
				addPrice = 0;
				break;
			}
			skillsCost.replace(skillName, cost + addPrice);
			playerInfo.replace("points", points);
			skill = new ItemStack(skillMenu.getItem(slot));
			skill.setAmount(playerActiveSkills.get(skillName));
			meta = skill.getItemMeta();
			meta.setDisplayName(skillName + ", " + playerActiveSkills.get(skillName) + " уровень");
			if (skillLvl < skillMaxLvl.get(skillName)) {
				meta.setLore(Lists.newArrayList(
						ChatColor.DARK_GRAY + "(активная способность)", 
						ChatColor.GOLD + "Повышайте уровень способности, чтобы", 
						ChatColor.GOLD + skLore,
						ChatColor.YELLOW + skLore2,
						ChatColor.YELLOW + skLore3,
						ChatColor.YELLOW + skLore4,
						ChatColor.GREEN + "Для улучшения", 
						ChatColor.GREEN + "необходимо: " + ChatColor.AQUA + skillsCost.get(skillName) + ChatColor.GREEN + " очков способностей", 
						ChatColor.DARK_GREEN + "Нажмите для повышения уровня"));
			} else {
				meta.setLore(Lists.newArrayList(
						ChatColor.DARK_GRAY + "(активная способность)", 
						ChatColor.GOLD + "Повышайте уровень способности, чтобы", 
						ChatColor.GOLD + skLore,
						ChatColor.YELLOW + skLore2,
						ChatColor.YELLOW + skLore3,
						ChatColor.YELLOW + skLore4,
						ChatColor.GREEN + "Достигнут максимальный", 
						ChatColor.GREEN + "уровень навыка!"));
			}
			skill.setItemMeta(meta);
			skillMenu.setItem(slot, skill);
			
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			p.sendMessage(ChatColor.RED + "[SRPG] " + ChatColor.YELLOW + "Недостаточно очков способностей!");
		}
	}
	
	public void passiveSkillLvlUp(String skillName) {
		Integer cost = skillsCost.get(skillName);
		Integer skillLvl = playerPassiveSkills.get(skillName);
		ItemMeta meta;
		if (skillLvl >= skillMaxLvl.get(skillName)) {
			p.sendMessage(ChatColor.RED + "[SRPG] " + ChatColor.GOLD + "Навык \"" + ChatColor.AQUA + skillName + ChatColor.GOLD + "\" достиг максимального уровня! (" + ChatColor.AQUA + skillLvl + ChatColor.GOLD + ")");
			return;
		}
		if (cost <= points) {
			ItemStack skill;
			String skillLore;
			int slot;
			int addPrice;
			skillLvl += 1;
			playerPassiveSkills.replace(skillName, skillLvl);
			p.sendMessage(ChatColor.RED + "[SRPG] " + ChatColor.GOLD + "Навык \"" + ChatColor.AQUA + skillName + ChatColor.GOLD + "\" повышен до " + ChatColor.AQUA + skillLvl + ChatColor.GOLD + " уровня");
			points -= cost;
			
			useSkill(skillName, this, skillLvl);
			updateScoreTable();
			
			switch (skillName) {
			case ("Интеллект"):
				slot = 6;
				skillLore = "повысить интеллект и запас маны на " + ChatColor.AQUA + "4 ед.";
				addPrice = 1;
				break;
			
			case ("Максимальное здоровье"):
				slot = 7;
				skillLore = "повысить запас здоровья на " + ChatColor.AQUA + "1 ед.";
				addPrice = 2;
				break;
				
			case ("Шахтерские гены"):
				slot = 8;
				skillLore = "повысить навык владения киркой и увеличить скорость добычи блоков";
				addPrice = 75;
				break;
				
			case ("Опытный рудокоп"):
				slot = 15;
				skillLore = "повысить шанс для добычи двойного кол-ва руды " + ChatColor.DARK_AQUA + " (на " + (((double) playerPassiveSkills.get("Опытный рудокоп")) * 3 / 4) + "%)";
				addPrice = 6;
				break;
				
			case ("Кулачный бой"):
				slot = 16;
				skillLore = "наносить больший урон в рукопашном бою";
				addPrice = 4;
				break;
				
			case ("Походный рюкзак"):
				slot = 17;
				skillLore = "носить с собой больше вещей " + ChatColor.AQUA + "(команда /bp)";
				addPrice = 20;
				ItemStack[] tempItems = backpack.getContents();
				backpack = Bukkit.createInventory(null, playerPassiveSkills.get(skillName) * 9, skillName);
				for (ItemStack it : tempItems) {
					if (it != null && it != new ItemStack(Material.AIR)) {
						backpack.addItem(it);
					}
				}
				try {
					save();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			default:
				slot = 0;
				skillLore = "Произошла ошибка";
				addPrice = 0;
				break;
			}
			skillsCost.replace(skillName, cost + addPrice);
			playerInfo.replace("points", points);
			skill = new ItemStack(skillMenu.getItem(slot));
			skill.setAmount(playerPassiveSkills.get(skillName));
			meta = skill.getItemMeta();
			meta.setDisplayName(skillName + ", " + playerPassiveSkills.get(skillName) + " уровень");
			if (skillLvl >= skillMaxLvl.get(skillName)) {
				meta.setLore(Lists.newArrayList(ChatColor.DARK_GRAY + "(пассивная способность)", ChatColor.GOLD + "Повышайте уровень способности, чтобы", ChatColor.GOLD + skillLore, ChatColor.GREEN + "Достигнут максимальный", ChatColor.GREEN + "уровень навыка! "));
			} else {
				meta.setLore(Lists.newArrayList(ChatColor.DARK_GRAY + "(пассивная способность)", ChatColor.GOLD + "Повышайте уровень способности, чтобы", ChatColor.GOLD + skillLore, ChatColor.GREEN + "Для улучшения", ChatColor.GREEN + "необходимо: " + ChatColor.AQUA + skillsCost.get(skillName) + ChatColor.GREEN + " очков способностей", ChatColor.DARK_GREEN + "Нажмите для повышения уровня"));
			}
			skill.setItemMeta(meta);
			skillMenu.setItem(slot, skill);
			
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			p.sendMessage(ChatColor.RED + "[SRPG] " + ChatColor.YELLOW + "Недостаточно очков способностей!");
		}
	}
			
	public static void useSkill(String skillName, RpgPlayer pl, int lvl) {
		
		switch(skillName) {
		case("Интеллект"):
			pl.regenMana();
			//pl.mana = pl.maxMana();
			break;
		
		case("Максимальное здоровье"):
			pl.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(lvl * 2);
			break;
			
		case("Шахтерские гены"):
			try {
				pl.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
			} catch (Exception exc) {
				//getLogger().info(exc);
			}
			if (lvl != 0) {
				PotionEffect pot = new PotionEffect(PotionEffectType.FAST_DIGGING, 1000000, lvl - 1, false, false);
				pl.getPlayer().addPotionEffect(pot);
			}
			break;
			
		default:
			break;
		}
	}	
	
	public void restoreMana(int amount) {
		if (mana < maxMana()) {
			mana += amount;
			if (mana >= maxMana()) {
				mana = maxMana();
				manaRegen = false;
			}
			updateScoreTable();
		}
	}
	
	public void regenMana() {
		//mana = 1;
		if (!manaRegen && mana < maxMana()) {
			manaRegen = true;
			RpgPlayer rpgp = this;
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.getPlugin(), new Runnable() {

				@Override
				public void run() {
					if (rpgp.mana < rpgp.maxMana()) {
						rpgp.mana += 1;
					} else {
						rpgp.mana = rpgp.maxMana();
						rpgp.manaRegen = false;
						Bukkit.getScheduler().cancelTask(taskID);
					}
					rpgp.updateScoreTable();
				}}, 50, 50);
		}
	}
}
