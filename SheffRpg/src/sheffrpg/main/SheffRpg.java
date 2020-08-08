package sheffrpg.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.io.*;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import sheffrpg.main.RpgPlayer;
import sheffrpg.main.StatsCommand;
import sheffrpg.main.RpgCommand;
import sheffrpg.main.SkillCommand;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import sheffrpg.main.enchantments.CrusherEnchantment;
import sheffrpg.main.enchantments.ThunderEnchantment;
import sheffrpg.main.enchantments.TryChance;

public class SheffRpg extends JavaPlugin implements Listener{
	
	public static HashMap<String, Integer> mobs = new HashMap<String, Integer>();
	public static HashMap<String, Integer> blocks = new HashMap<String, Integer>();
	public static HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	static HashMap<Player, RpgPlayer> players = new HashMap<Player, RpgPlayer>();
	Logger logger = Logger.getLogger("Minecraft"); 
	public ThunderEnchantment zeus = new ThunderEnchantment(new NamespacedKey(this, "zeus"), 101);
	public static Inventory donateItemsInv;
	public CrusherEnchantment crusher = new CrusherEnchantment(new NamespacedKey(this, "CRUSHER"), 103);
	private Towny towny;
	public static FileConfiguration settings;
	public static StateFlag SaveXP = new StateFlag("SaveXP", true);
	
	ItemStack mana_potion;
	ItemStack mana_potion2;
	ItemStack mana_potion3;
	public static ItemStack amulet_exp;
	ItemStack essence_hell;
	ItemStack essence_end;
	ItemStack essence_water;
	ItemStack essence_balance;
	ItemStack essence_concentrate;
	ItemStack thunder_charge;
	ItemStack white_matter;
	ItemStack dark_matter;
	ItemStack elder_ingot;
	ItemStack ws1;
	ItemStack gs1;
	ItemStack is2;
	ItemStack is3;
	ItemStack is4; // hell sword
	ItemStack is5;
	ItemStack is6; // end sword
	ItemStack ia3;
	ItemStack da2; // purple axe
	ItemStack da6;
	ItemStack da7; // end pur axe
	ItemStack ds6; // muramasa
	ItemStack ds3;
	ItemStack ds5;
	ItemStack sa2;
	ItemStack fb_wand1;
	ItemStack zeus_wand1;
    
	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	@Override
	public void onDisable() {
		players.forEach((k, v) -> {
			try {
				players.get(k).save();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		getLogger().info("Все игроки сохранены");
		try {
		      Field byIdField = Enchantment.class.getDeclaredField("byId");
		      Field byNameField = Enchantment.class.getDeclaredField("byName");
		      byIdField.setAccessible(true);
		      byNameField.setAccessible(true);
		      HashMap<Integer, Enchantment> byId = (HashMap<Integer, Enchantment>)byIdField.get(null);
		      HashMap<Integer, Enchantment> byName = (HashMap<Integer, Enchantment>)byNameField.get(null);
		      if (byId.containsKey(Integer.valueOf(this.zeus.getId())))
		        byId.remove(Integer.valueOf(this.zeus.getId())); 
		      if (byName.containsKey(this.zeus.getName()))
		        byName.remove(this.zeus.getName()); 
		      if (byId.containsKey(Integer.valueOf(this.crusher.getId())))
		        byId.remove(Integer.valueOf(this.crusher.getId())); 
		      if (byName.containsKey(this.crusher.getName()))
		        byName.remove(this.crusher.getName()); 
		    } catch (Exception exception) {}
	}
	
	@Override
	public void onEnable() {
		towny = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
		donateItemsInv = Bukkit.createInventory(null, 54, "Все предметы SheffRpg");
		ItemStack exitBtn = new ItemStack(Material.REDSTONE_BLOCK, 1);
		ItemMeta BtnMeta = exitBtn.getItemMeta();
		BtnMeta.setDisplayName(ChatColor.RED + "Выход");
		exitBtn.setItemMeta(BtnMeta);
		donateItemsInv.setItem(53, exitBtn);
		registerCrafts();
		
		File cDir = new File("plugins" + System.getProperty("file.separator") + "SheffRpg");
        if (!cDir.exists()) {
        	cDir.mkdir();
        	getLogger().info("Папка SheffRpg создана в /plugins/");
        }
        File config = new File("plugins" + System.getProperty("file.separator") + "SheffRpg" + System.getProperty("file.separator") + "config.yml");
        if (!config.exists()) {
        	getConfig().options().copyDefaults(true);
        	saveDefaultConfig();
        	getLogger().info("Конфигурационный файл создан в /plugins/SheffRpg/");
        }
        File playersDir = new File("plugins" + System.getProperty("file.separator") + "SheffRpg" + System.getProperty("file.separator") + "players");
        if (!playersDir.exists()) {
        	playersDir.mkdir();
        	getLogger().info("Папка players создана в /plugins/SheffRpg/");
        }
        
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("rpg").setExecutor(new RpgCommand(this));
        getCommand("skill").setExecutor(new SkillCommand(this));
        getCommand("bp").setExecutor(new BackpackCommand(this));
        getCommand("menu").setExecutor(new SkillCommand(this));
        getCommand("useactiveskill").setExecutor(new UseActiveSkill(this));
		Bukkit.getPluginManager().registerEvents(this, this);
		
		Bukkit.getPluginManager().registerEvents(this.zeus, this);
	    Bukkit.getPluginManager().registerEvents(this.crusher, this);
	    
		settings = this.getConfig();
		try {
			for (String s : getConfig().getConfigurationSection("mobs").getKeys(false)) {
				   int value = getConfig().getInt("mobs." + s);
				   mobs.put(s, value);
				}
			for (String s : getConfig().getConfigurationSection("blocks").getKeys(false)) {
				   int value = getConfig().getInt("blocks." + s);
				   blocks.put(s, value);
				}
		} catch(Exception e) {
			getLogger().info("mob " + mobs.size() + " | blocks " + blocks.size());
		}		
			
		RpgPlayer.setPlugin(this);
		onWorldSave();
		
		getLogger().info("Плагин SheffRpg успешно загружен!");
	}
	
	private void registerCrafts() {
		PotionMeta pmeta;
		ItemMeta meta;
		ItemMeta swMeta;
		List<String> lore;
		ShapedRecipe s;
		ShapelessRecipe slr;
		AttributeModifier atckspd;
		AttributeModifier attck;
		AttributeModifier mvspeed;
		AttributeModifier mvspeedOffHand;
		
		
		mana_potion = new ItemStack(Material.POTION, 1);
		pmeta = (PotionMeta) mana_potion.getItemMeta();
		pmeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.ITALIC + "Слабое зелье маны");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Восстанавливает");
		lore.add(ChatColor.AQUA + "30" + ChatColor.GRAY + " ед. маны");
		pmeta.setLore(lore);
		pmeta.setCustomModelData(1);
		pmeta.addEnchant(Enchantment.DURABILITY, 1, true);
		pmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		pmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		pmeta.setColor(Color.fromRGB(55, 215, 245));
		mana_potion.setItemMeta(pmeta);
		slr = new ShapelessRecipe(new NamespacedKey(this, "mana_potion"), mana_potion);
		slr.addIngredient(Material.HONEY_BOTTLE);
		slr.addIngredient(Material.DRAGON_BREATH);
		Bukkit.getServer().addRecipe(slr);
		
		
		mana_potion2 = new ItemStack(Material.POTION, 1);
		pmeta = (PotionMeta) mana_potion2.getItemMeta();
		pmeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Крепкое зелье маны");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Восстанавливает");
		lore.add(ChatColor.AQUA + "120" + ChatColor.GRAY + " ед. маны");
		pmeta.setLore(lore);
		pmeta.setCustomModelData(1);
		pmeta.addEnchant(Enchantment.DURABILITY, 1, true);
		pmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		pmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		pmeta.setColor(Color.fromRGB(45, 115, 175));
		mana_potion2.setItemMeta(pmeta);
		slr = new ShapelessRecipe(new NamespacedKey(this, "mana_potion2"), mana_potion2);
		slr.addIngredient(Material.HONEY_BOTTLE);
		slr.addIngredient(Material.DRAGON_BREATH);
		slr.addIngredient(Material.GLOWSTONE_DUST);
		slr.addIngredient(Material.HONEY_BOTTLE);
		slr.addIngredient(Material.DRAGON_BREATH);
		slr.addIngredient(Material.GLOWSTONE_DUST);
		slr.addIngredient(Material.HONEY_BOTTLE);
		slr.addIngredient(Material.DRAGON_BREATH);
		slr.addIngredient(Material.GLOWSTONE_DUST);
		Bukkit.getServer().addRecipe(slr);
		
		
		mana_potion3 = new ItemStack(Material.POTION, 1);
		pmeta = (PotionMeta) mana_potion3.getItemMeta();
		pmeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.ITALIC + "Наилучшее зелье маны");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Восстанавливает");
		lore.add(ChatColor.AQUA + "500" + ChatColor.GRAY + " ед. маны");
		pmeta.setLore(lore);
		pmeta.setCustomModelData(1);
		pmeta.addEnchant(Enchantment.DURABILITY, 1, true);
		pmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		pmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		pmeta.setColor(Color.fromRGB(20, 35, 130));
		mana_potion3.setItemMeta(pmeta);
		s = new ShapedRecipe(new NamespacedKey(this, "mana_potion3"), mana_potion3);
		s.shape(new String[] {	"CCC", 
								"BAB", 
								"CCC"});
		s.setIngredient('A', Material.POTION);
		s.setIngredient('B', Material.GOLD_NUGGET);
		s.setIngredient('C', Material.NETHER_WART);
		Bukkit.getServer().addRecipe(s);
		
		
		amulet_exp = new ItemStack(Material.CLAY_BALL, 1);
		meta = amulet_exp.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "" + ChatColor.ITALIC + "Амулет Анубиса");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY+ "Дает носителю возможность");
		lore.add(ChatColor.GRAY + "сохранить накопленный опыт при смерти.");
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Для активации, поместите амулет");
		lore.add(ChatColor.GRAY + "в слот левой или правой руки.");
		lore.add(ChatColor.GRAY + "Исчезает при смерти.");
		meta.setLore(lore);
		meta.setCustomModelData(1);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		amulet_exp.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "amulet_exp"), amulet_exp);
		s.shape(new String[] {	"CCC", 
								"BAB", 
								"CCC"});
		s.setIngredient('A', Material.DRAGON_BREATH);
		s.setIngredient('B', Material.BLACK_DYE);
		s.setIngredient('C', Material.GOLD_INGOT);
		Bukkit.getServer().addRecipe(s);
		
		
		essence_hell = new ItemStack(Material.IRON_NUGGET, 8);
		meta = essence_hell.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Эссенция Ада");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Порой кажется, что ");
		lore.add(ChatColor.GRAY + "эти кусочки как-то ");
		lore.add(ChatColor.GRAY + "странно пищат...");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.setCustomModelData(1);
		essence_hell.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "hell_essense"), essence_hell);
		s.shape(new String[] {	"ACA", 
								"DBD", 
								"ACA"});
		s.setIngredient('A', Material.NETHER_WART);
		s.setIngredient('B', Material.NETHER_STAR);
		s.setIngredient('C', Material.GHAST_TEAR);
		s.setIngredient('D', Material.BLAZE_POWDER);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(essence_hell);
		
		
		essence_end = new ItemStack(Material.GOLD_NUGGET, 8);
		meta = essence_end.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_AQUA + "Эссенция Края");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Порой кажется, что ");
		lore.add(ChatColor.GRAY + "эти кусочки как-то ");
		lore.add(ChatColor.GRAY + "странно перемещаются...");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.setCustomModelData(1);
		essence_end.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "end_essense"), essence_end);
		s.shape(new String[] {	"ACA", 
								"DBD", 
								"ACA"});
		s.setIngredient('A', Material.ENDER_PEARL);
		s.setIngredient('B', Material.DRAGON_EGG);
		s.setIngredient('C', Material.POPPED_CHORUS_FRUIT);
		s.setIngredient('D', Material.DRAGON_BREATH);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(essence_end);
		
		
		essence_water = new ItemStack(Material.PRISMARINE_CRYSTALS, 8);
		meta = essence_water.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Эссенция Моря");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Порой кажется, что ");
		lore.add(ChatColor.GRAY + "эти кусочки как-то ");
		lore.add(ChatColor.GRAY + "плавают, словно в воде...");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.setCustomModelData(1);
		essence_water.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "essence_water"), essence_water);
		s.shape(new String[] {	"ACA", 
								"BDB", 
								"ACA"});
		s.setIngredient('A', Material.NAUTILUS_SHELL);
		s.setIngredient('B', Material.WATER_BUCKET);
		s.setIngredient('C', Material.PRISMARINE_SHARD);
		s.setIngredient('D', Material.HEART_OF_THE_SEA);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(essence_water);
		
		
		essence_balance = new ItemStack(Material.PURPLE_DYE, 1);
		meta = essence_balance.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.MAGIC + "x" + ChatColor.RESET + ChatColor.ITALIC + ChatColor.GOLD + "Квинтессенция" + ChatColor.RESET + ChatColor.DARK_RED + ChatColor.MAGIC + "x");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Удивительный симбиоз");
		lore.add(ChatColor.GRAY + "двух эссенций.");
		lore.add(" ");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.setCustomModelData(1);
		essence_balance.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "balance_essense"), essence_balance);
		s.shape(new String[] {	"AAA", 
								"BAC", 
								"AAA"});
		s.setIngredient('A', Material.DIAMOND);
		s.setIngredient('B', Material.GOLD_NUGGET);
		s.setIngredient('C', Material.IRON_NUGGET);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(essence_balance);
		
		
		essence_concentrate = new ItemStack(Material.PINK_DYE, 1);
		meta = essence_concentrate.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.MAGIC + "x" + ChatColor.RESET + ChatColor.ITALIC + ChatColor.BLUE + "Сконцентрированный алмаз" + ChatColor.RESET + ChatColor.AQUA + ChatColor.MAGIC + "x");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Восьмикратно");
		lore.add(ChatColor.GRAY + "сконцентрированный");
		lore.add(ChatColor.GRAY + "алмаз.");
		lore.add(" ");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.setCustomModelData(1);
		essence_concentrate.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "essence_concentrate"), essence_concentrate);
		s.shape(new String[] {	"AAA", 
								"ABA", 
								"AAA"});
		s.setIngredient('A', Material.DIAMOND_BLOCK);
		s.setIngredient('B', Material.PURPLE_DYE);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(essence_concentrate);
		
		
		thunder_charge = new ItemStack(Material.SLIME_BALL, 1);
		meta = thunder_charge.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE + "Грозовой заряд");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Удивительная концентрация");
		lore.add(ChatColor.GRAY + "электрической энергии");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.setCustomModelData(1);
		thunder_charge.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "thunder_charge"), thunder_charge);
		s.shape(new String[] {	"AAA", 
								"BCB", 
								"AAA"});
		s.setIngredient('A', Material.PRISMARINE_CRYSTALS);
		s.setIngredient('B', Material.LIGHT_GRAY_DYE);
		s.setIngredient('C', Material.PURPLE_DYE);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(thunder_charge);
		
		
		white_matter = new ItemStack(Material.LIGHT_GRAY_DYE, 1);
		meta = white_matter.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Светлая материя");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Благославленная " + ChatColor.WHITE + "светом");
		lore.add(ChatColor.GRAY + "материя");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.setCustomModelData(1);
		white_matter.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "white_matter"), white_matter);
		s.shape(new String[] {	"ACA", 
								"DBD", 
								"ACA"});
		s.setIngredient('A', Material.IRON_BLOCK);
		s.setIngredient('B', Material.GOLD_BLOCK);
		s.setIngredient('C', Material.GLISTERING_MELON_SLICE);
		s.setIngredient('D', Material.GOLD_NUGGET);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(white_matter);
		
		
		dark_matter = new ItemStack(Material.BLACK_DYE, 1);
		meta = dark_matter.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.UNDERLINE + "Темная материя");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Проклятая " + ChatColor.DARK_GRAY + "тьмой");
		lore.add(ChatColor.GRAY + "материя");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.setCustomModelData(1);
		dark_matter.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "dark_matter"), dark_matter);
		s.shape(new String[] {	"ACA", 
								"DBD", 
								"ACA"});
		s.setIngredient('A', Material.IRON_BLOCK);
		s.setIngredient('B', Material.GOLD_BLOCK);
		s.setIngredient('C', Material.FERMENTED_SPIDER_EYE);
		s.setIngredient('D', Material.IRON_NUGGET);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(dark_matter);
		

		elder_ingot = new ItemStack(Material.BRICK, 1);
		meta = elder_ingot.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Древний слиток");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Немыслимая сила скрыта");
		lore.add(ChatColor.GRAY + "в этом маленьком слитке.");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
		meta.setCustomModelData(1);
		elder_ingot.setItemMeta(meta);
		s = new ShapedRecipe(new NamespacedKey(this, "elder_ingot"), elder_ingot);
		s.shape(new String[] {"ABA",
							  "CEC",
							  "DCD"});
		s.setIngredient('A', Material.PRISMARINE_CRYSTALS);
		s.setIngredient('B', Material.PINK_DYE);
		s.setIngredient('C', Material.LIGHT_GRAY_DYE);
		s.setIngredient('D', Material.BLACK_DYE);
		s.setIngredient('E', Material.SLIME_BALL);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(elder_ingot);
		

		donateItemsInv.addItem(mana_potion);
		donateItemsInv.addItem(mana_potion2);
		donateItemsInv.addItem(mana_potion3);
		donateItemsInv.addItem(amulet_exp);
		
		
		ws1 = new ItemStack(Material.WOODEN_SWORD);
		swMeta = ws1.getItemMeta();
		swMeta.setDisplayName(ChatColor.YELLOW + "Тренировочный меч");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "— У древних нам учиться,");
		lore.add(ChatColor.GRAY + "не в книжном прахе гнить!");
		lore.add(ChatColor.GRAY + "Как греки веселиться,");
		lore.add(ChatColor.GRAY + "как римляне рубить!");
		swMeta.setLore(lore);
		swMeta.setCustomModelData(1);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		//mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", 0.01, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		//swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ws1.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "train_sword"), ws1);
		s.shape(new String[] {" B ",
							  "ACA",
							  "ADA"});
		s.setIngredient('A', Material.STRING);
		s.setIngredient('B', Material.STICK);
		s.setIngredient('C', Material.LIGHT_GRAY_DYE);
		s.setIngredient('D', Material.WOODEN_SWORD);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(ws1);
		
		
		is4 = new ItemStack(Material.IRON_SWORD);
		swMeta = is4.getItemMeta();
		swMeta.setDisplayName(ChatColor.RED + "Багряный клинок");
		swMeta.setLore(Lists.newArrayList(" ", ChatColor.DARK_RED + "Этот клинок пропитан", ChatColor.DARK_RED + "кровью павшей нечисти"));
		swMeta.setCustomModelData(4);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 12, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is4.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "crimson_blade"), is4);
		s.shape(new String[] {"ABA", "ABA", "ACA"});
		s.setIngredient('A', Material.IRON_NUGGET);
		s.setIngredient('B', Material.IRON_INGOT);
		s.setIngredient('C', Material.STICK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(is4);

		
		is5 = new ItemStack(Material.IRON_SWORD);
		swMeta = is5.getItemMeta();
		swMeta.setDisplayName(ChatColor.AQUA + "Бич семи морей");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "— Нас прокляли клир и паства,");
		lore.add(ChatColor.GRAY + "На реи вешают власти.");
		lore.add(ChatColor.GRAY + "Но верность Братству храни -");
		lore.add(ChatColor.GRAY + "И в шторм, и в бою, и в пути!");
		swMeta.setLore(lore);
		swMeta.setCustomModelData(5);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.15, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", 0.014, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is5.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "pirate_blade"), is5);
		s.shape(new String[] {"ABA",
							  "ABA",
							  "ACA"});
		s.setIngredient('A', Material.PRISMARINE_CRYSTALS);
		s.setIngredient('B', Material.IRON_INGOT);
		s.setIngredient('C', Material.STICK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(is5);
		
		
		is6 = new ItemStack(Material.IRON_SWORD);
		swMeta = is6.getItemMeta();
		swMeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "\"Эндерион\"");
		swMeta.setLore(Lists.newArrayList(" ", ChatColor.LIGHT_PURPLE + "Так прозвал этот клинок", ChatColor.LIGHT_PURPLE + "первый воин края"));
		swMeta.setCustomModelData(6);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -1.8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is6.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "end_blade"), is6);
		s.shape(new String[] {"ABA", "ABA", "ACA"});
		s.setIngredient('A', Material.GOLD_NUGGET);
		s.setIngredient('B', Material.IRON_INGOT);
		s.setIngredient('C', Material.STICK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(is6);
		
		
		ia3 = new ItemStack(Material.IRON_AXE);
		swMeta = ia3.getItemMeta();
		swMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.ITALIC + "Аннигилятор големов");
		swMeta.setLore(Lists.newArrayList(" ", ChatColor.GRAY + "Эта булава поможет", ChatColor.GRAY + "вам справится с целой", ChatColor.GRAY + "ордой железных големов."));
		swMeta.setCustomModelData(3);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ia3.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "golem_smasher"), ia3);
		s.shape(new String[] {"ADA",
							  "ABA",
						      "ACA"});
		s.setIngredient('A', Material.BLACK_DYE);
		s.setIngredient('B', Material.SLIME_BALL);
		s.setIngredient('C', Material.STICK);
		s.setIngredient('D', Material.IRON_BLOCK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(ia3);
		
		
		da2 = new ItemStack(Material.DIAMOND_AXE);
		swMeta = da2.getItemMeta();
		swMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Молот вестника бури");
		swMeta.setLore(Lists.newArrayList(" ", ChatColor.LIGHT_PURPLE + "Молот бури.", ChatColor.LIGHT_PURPLE + "Молот Рока.", ChatColor.LIGHT_PURPLE +  "Молот погибели."));
		swMeta.setCustomModelData(2);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 19, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -3.25, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", -0.02, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		da2.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "purple_hammer"), da2);
		s.shape(new String[] {"AAA", 
							  "ABA", 
							  " B "});
		s.setIngredient('A', Material.PURPLE_DYE);
		s.setIngredient('B', Material.STICK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(da2);
		
		
		da7 = new ItemStack(Material.DIAMOND_AXE);
		swMeta = da7.getItemMeta();
		swMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Булава разума");
		swMeta.setLore(Lists.newArrayList(" ", ChatColor.BLUE + "Прочти следующую строку.", ChatColor.BLUE + "Прочти предыдущую строку."));
		swMeta.setCustomModelData(7);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 16, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", -0.008, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		da7.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "ender_smasher"), da7);
		s.shape(new String[] {"BAB", 
							  "ACA", 
							  " D "});
		s.setIngredient('A', Material.BLACK_DYE);
		s.setIngredient('B', Material.GOLD_NUGGET);
		s.setIngredient('C', Material.PURPLE_DYE);
		s.setIngredient('D', Material.STICK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(da7);
		
		
		ds6 = new ItemStack(Material.DIAMOND_SWORD);
		swMeta = ds6.getItemMeta();
		swMeta.setDisplayName(ChatColor.DARK_RED + "Кровавый Мурамаса");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "С каждым убийством");
		lore.add(ChatColor.GRAY + "эта катана все сильнее");
		lore.add(ChatColor.GRAY + "пропитывается кровью жертв.");
		lore.add(ChatColor.RED + "Убийств: 0");
		//lore.remove(lore.size() - 1);
		swMeta.setLore(lore);
		swMeta.setCustomModelData(6);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.25, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", 0.006, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ds6.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "ds6"), ds6);
		s.shape(new String[] {"ACA",
							  "BDB",
							  "AEA"});
		s.setIngredient('A', Material.BLACK_DYE);
		s.setIngredient('B', Material.MAGMA_CREAM);
		s.setIngredient('C', Material.PINK_DYE);
		s.setIngredient('D', Material.IRON_SWORD);
		s.setIngredient('E', Material.NETHER_WART_BLOCK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(ds6);
		
		
		sa2 = new ItemStack(Material.STONE_AXE);
		swMeta = sa2.getItemMeta();
		swMeta.setDisplayName(ChatColor.WHITE + "Тяжелый молот");
		swMeta.setLore(Lists.newArrayList(" ", ChatColor.GRAY + "Очень тяжелый", ChatColor.GRAY + "молот"));
		swMeta.setCustomModelData(2);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 26, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -3.75, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", -0.05, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeedOffHand = new AttributeModifier(UUID.randomUUID(), "generic.Speed", -0.05, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -3.75, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeedOffHand);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		sa2.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "stone_axe_2"), sa2);
		s.shape(new String[] {"AAA", 
							  "ABA", 
							  " C "});
		s.setIngredient('A', Material.IRON_BLOCK);
		s.setIngredient('B', Material.LIGHT_GRAY_DYE);
		s.setIngredient('C', Material.STICK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(sa2);
		
		
		fb_wand1 = new ItemStack(Material.STICK);
		swMeta = fb_wand1.getItemMeta();
		swMeta.setDisplayName(ChatColor.WHITE + "Посох " + ChatColor.GOLD + "Огненного шара");
		swMeta.setLore(Lists.newArrayList("", ChatColor.RED + "Да познают неверные", ChatColor.RED + "огня свет,", ChatColor.RED + "да облачатся они,", ChatColor.RED + "в объятия его."));
		swMeta.setCustomModelData(1);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		fb_wand1.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "fb_wand1"), fb_wand1);
		s.shape(new String[] {"ABA", 
							  "ACA", 
							  "ACA"});
		s.setIngredient('A', Material.FIRE_CHARGE);
		s.setIngredient('B', Material.PURPLE_DYE);
		s.setIngredient('C', Material.STICK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(fb_wand1);
		
		
		zeus_wand1 = new ItemStack(Material.STICK);
		swMeta = zeus_wand1.getItemMeta();
		swMeta.setDisplayName(ChatColor.WHITE + "Посох " + ChatColor.AQUA + "Громовержца");
		swMeta.setLore(Lists.newArrayList("", ChatColor.DARK_AQUA + "Да познают неверные", ChatColor.DARK_AQUA + "электричества силу,", ChatColor.DARK_AQUA + "да облачатся они", ChatColor.DARK_AQUA + "в объятия его."));
		swMeta.setCustomModelData(2);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		zeus_wand1.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "zeus_wand1"), zeus_wand1);
		s.shape(new String[] {"BAB", 
							  "CAC", 
							  " C "});
		s.setIngredient('A', Material.SLIME_BALL);
		s.setIngredient('B', Material.DIAMOND_BLOCK);
		s.setIngredient('C', Material.STICK);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(zeus_wand1);
		
		
		ds5 = new ItemStack(Material.DIAMOND_SWORD);
		swMeta = ds5.getItemMeta();
		swMeta.setDisplayName(ChatColor.AQUA + "Клеймор " + ChatColor.GOLD + ChatColor.BOLD + "короля");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + " - Легендарный клеймор");
		lore.add(ChatColor.GRAY + "правящей династии,");
		lore.add(ChatColor.GRAY + "который пережил несколько");
		lore.add(ChatColor.GRAY + "поколений.");
		//lore.remove(lore.size() - 1);
		swMeta.setLore(lore);
		swMeta.setCustomModelData(5);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 36, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", -0.0085, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ds5.setItemMeta(swMeta);
		donateItemsInv.addItem(ds5);
	
		
		da6 = new ItemStack(Material.DIAMOND_AXE);
		swMeta = da6.getItemMeta();
		swMeta.setDisplayName(ChatColor.GOLD + "Боевой молот истинного " + ChatColor.WHITE + ChatColor.BOLD + "паладина");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + " - Легендарный молот");
		lore.add(ChatColor.GRAY + "одного из паладина из");
		lore.add(ChatColor.GRAY + "первой гвардии короля.");
		swMeta.setLore(lore);
		swMeta.setCustomModelData(6);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 28, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -3.15, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", -0.023, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		da6.setItemMeta(swMeta);
		donateItemsInv.addItem(da6);
				
		
		is2 = new ItemStack(Material.IRON_SWORD);
		swMeta = is2.getItemMeta();
		swMeta.setDisplayName("Когти " + ChatColor.RED + "" + ChatColor.ITALIC + "Хирсина");
		swMeta.setLore(Lists.newArrayList(" ", ChatColor.GRAY + "Кромсайте своих противников на кусочки!", ChatColor.GRAY + "Каждая пролитая капля крови", ChatColor.GRAY + "восстановит ваш запас здоровья.", ChatColor.GRAY + "Эффект удваивается при атаке с 2-х рук."));
		swMeta.setCustomModelData(2);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -1.3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is2.setItemMeta(swMeta);
		donateItemsInv.addItem(is2);

		
		gs1 = new ItemStack(Material.GOLDEN_SWORD);
		swMeta = gs1.getItemMeta();
		swMeta.setDisplayName(ChatColor.GOLD + "Гладиус Мидаса");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Каждое убийство дает вам");
		lore.add(ChatColor.GRAY + "шанс разбогатеть.");
		swMeta.setLore(lore);
		swMeta.setCustomModelData(1);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", -0.004, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		gs1.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "gs1"), gs1);
		s.shape(new String[] {"AAA",
							  "ABA",
							  "DCD"});
		s.setIngredient('A', Material.GOLD_BLOCK);
		s.setIngredient('B', Material.PINK_DYE);
		s.setIngredient('C', Material.GOLDEN_SWORD);
		s.setIngredient('D', Material.LIGHT_GRAY_DYE);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(gs1);
		
		
		is3 = new ItemStack(Material.IRON_SWORD);
		swMeta = is3.getItemMeta();
		swMeta.setDisplayName(ChatColor.GRAY + "Остывший длинный меч");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Вытащенный из лавы");
		lore.add(ChatColor.GRAY + "длинный меч стражи.");
		swMeta.setLore(lore);
		swMeta.setCustomModelData(3);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", -0.008, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is3.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "is3"), is3);
		s.shape(new String[] {" AB",
							  "ABA",
							  "CA "});
		s.setIngredient('A', Material.IRON_INGOT);
		s.setIngredient('B', Material.NETHER_BRICK);
		s.setIngredient('C', Material.IRON_SWORD);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(is3);
		
		
		ds3 = new ItemStack(Material.DIAMOND_SWORD);
		swMeta = ds3.getItemMeta();
		swMeta.setDisplayName(ChatColor.WHITE + "Клинок " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "Валафара");
		lore = new ArrayList<String>();
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Удивительный клинок, способный");
		lore.add(ChatColor.GRAY + "перевернуть любое сражение");
		lore.add(ChatColor.GRAY + "в пользу хозяина.");
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Нажмите ПКМ, чтобы переместиться на");
		lore.add(ChatColor.GRAY + "несколько блоков вперед.");
		lore.add(ChatColor.GRAY + "Расходует " + ChatColor.AQUA + "4" + ChatColor.GRAY + " маны.");
		swMeta.setLore(lore);
		swMeta.setCustomModelData(3);
		attck = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 20, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		atckspd = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.65, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		mvspeed = new AttributeModifier(UUID.randomUUID(), "generic.Speed", 0.006, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attck);
		swMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, atckspd);
		swMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mvspeed);
		swMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ds3.setItemMeta(swMeta);
		s = new ShapedRecipe(new NamespacedKey(this, "ds3"), ds3);
		s.shape(new String[] {" B ",
							  "CBC",
							  "CAC"});
		s.setIngredient('A', Material.STICK);
		s.setIngredient('B', Material.BRICK);
		s.setIngredient('C', Material.ENDER_PEARL);
		Bukkit.getServer().addRecipe(s);
		donateItemsInv.addItem(ds3);
	}
	
	@EventHandler
	public void onItemCraft(CraftItemEvent e) {
		if (e.getRecipe().getResult().equals(is4)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();	
			int[] mPos = new int[] {0, 2, 3, 5, 6, 8};
			for (int i : mPos) {
				if (!shouldBe[i].getItemMeta().equals(essence_hell.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			return;
		}
		if (e.getRecipe().getResult().equals(is5)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos = new int[] {0, 2, 3, 5, 6, 8};
			for (int i : mPos) {
				if (!shouldBe[i].getItemMeta().equals(essence_water.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			return;
		}
		if (e.getRecipe().getResult().equals(is6)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos = new int[] {0, 2, 3, 5, 6, 8};
			for (int i : mPos) {
				if (!shouldBe[i].getItemMeta().equals(essence_end.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			return;
		}
		if (e.getRecipe().getResult().equals(essence_balance)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			if ((shouldBe[5].getItemMeta().equals(essence_hell.getItemMeta()) && shouldBe[3].getItemMeta().equals(essence_end.getItemMeta())) || (shouldBe[3].getItemMeta().equals(essence_hell.getItemMeta()) && shouldBe[5].getItemMeta().equals(essence_end.getItemMeta()))) {
				return;
			} else {
				e.setCancelled(true);
			}
			return;
		}
		
		if (e.getRecipe().getResult().equals(thunder_charge)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos = new int[] {0, 1, 2, 6, 7, 8};
			for (int i : mPos) {
				if (!shouldBe[i].getItemMeta().equals(essence_water.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			int[] mPos2 = new int[] {3, 5};
			for (int i : mPos2) {
				if (!shouldBe[i].getItemMeta().equals(white_matter.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			if (!shouldBe[4].getItemMeta().equals(essence_balance.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			return;
		}
		if (e.getRecipe().getResult().equals(da2)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos1 = new int[] {0, 1, 2, 3, 5};
			for (int i : mPos1) {
				if (!shouldBe[i].getItemMeta().equals(essence_balance.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			return;
		}
		if (e.getRecipe().getResult().equals(ds6)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos1 = new int[] {0, 2, 6, 8};
			for (int i : mPos1) {
				if (!shouldBe[i].getItemMeta().equals(dark_matter.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			if (!shouldBe[1].getItemMeta().equals(essence_concentrate.getItemMeta())) {
				e.setCancelled(true);
				return;
			}	
			if (shouldBe[4].getType().equals(Material.IRON_SWORD) && shouldBe[4].getItemMeta().hasCustomModelData()) {
				if (!(shouldBe[4].getItemMeta().getCustomModelData() == 4)) {
					e.setCancelled(true);
					return;
				}
			} else {
				e.setCancelled(true);
				return;
			}
			return;
		}
		if (e.getRecipe().getResult().equals(white_matter)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();	
			int[] mPos = new int[] {3, 5};
			for (int i : mPos) {
				if (!shouldBe[i].getItemMeta().equals(essence_end.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			return;
		}
		if (e.getRecipe().getResult().equals(dark_matter)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos = new int[] {3, 5};
			for (int i : mPos) {
				if (!shouldBe[i].getItemMeta().equals(essence_hell.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			return;
		}
		if (e.getRecipe().getResult().equals(essence_concentrate)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			if (!shouldBe[4].getItemMeta().equals(essence_balance.getItemMeta())) {
				e.setCancelled(true);
				return;
			}			
			return;
		}
		if (e.getRecipe().getResult().equals(fb_wand1)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			if (!shouldBe[1].getItemMeta().equals(essence_balance.getItemMeta())) {
				e.setCancelled(true);
				return;
			}			
			return;
		}
		if (e.getRecipe().getResult().equals(zeus_wand1)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos = new int[] {1, 4};
			for (int i : mPos) {
				if (!shouldBe[i].getItemMeta().equals(thunder_charge.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}			
			return;
		}
		
		if (e.getRecipe().getResult().equals(da7)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos1 = new int[] {1, 3, 5};
			int[] mPos2 = new int[] {0, 2};
			for (int i : mPos1) {
				if (!shouldBe[i].getItemMeta().equals(dark_matter.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			for (int i : mPos2) {
				if (!shouldBe[i].getItemMeta().equals(essence_end.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			if (!shouldBe[4].getItemMeta().equals(essence_balance.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			return;
		}
		
		if (e.getRecipe().getResult().equals(sa2)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			if (!shouldBe[4].getItemMeta().equals(white_matter.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			return;
		}
		if (e.getRecipe().getResult().equals(ws1)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			if (!shouldBe[4].getItemMeta().equals(white_matter.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			return;
		}
		if (e.getRecipe().getResult().equals(gs1)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			if (!shouldBe[4].getItemMeta().equals(this.essence_concentrate.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			if (!shouldBe[6].getItemMeta().equals(white_matter.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			if (!shouldBe[8].getItemMeta().equals(white_matter.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			return;
		}
		if (e.getRecipe().getResult().equals(ia3)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos1 = new int[] {0, 2, 3, 5, 6, 8};
			for (int i : mPos1) {
				if (!shouldBe[i].getItemMeta().equals(dark_matter.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			if (!shouldBe[4].getItemMeta().equals(thunder_charge.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			return;
		}
		
		
		if (e.getRecipe().getResult().equals(ds3)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos1 = new int[] {1, 4};
			for (int i : mPos1) {
				if (!shouldBe[i].getItemMeta().equals(elder_ingot.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			e.getWhoClicked().getWorld().playSound(e.getWhoClicked().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.65f, 0.65f);
			return;
		}

		if (e.getRecipe().getResult().equals(elder_ingot)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos1 = new int[] {0, 2};
			int[] mPos3 = new int[] {3, 5, 7};
			int[] mPos4 = new int[] {6, 8};
			for (int i : mPos1) {
				if (!shouldBe[i].getItemMeta().equals(essence_water.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			if (!shouldBe[1].getItemMeta().equals(essence_concentrate.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			if (!shouldBe[4].getItemMeta().equals(thunder_charge.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			for (int i : mPos3) {
				if (!shouldBe[i].getItemMeta().equals(white_matter.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			for (int i : mPos4) {
				if (!shouldBe[i].getItemMeta().equals(dark_matter.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			return;
		}
		
		if (e.getRecipe().getResult().equals(mana_potion3)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos1 = new int[] {3, 5};
			for (int i : mPos1) {
				if (!shouldBe[i].getItemMeta().equals(essence_end.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			if (!shouldBe[4].getItemMeta().equals(mana_potion2.getItemMeta())) {
				e.setCancelled(true);
				return;
			}
			return;
		}
		
		if (e.getRecipe().getResult().equals(amulet_exp)) {
			ItemStack[] shouldBe = e.getInventory().getMatrix();
			int[] mPos1 = new int[] {3, 5};
			for (int i : mPos1) {
				if (!shouldBe[i].getItemMeta().equals(dark_matter.getItemMeta())) {
					e.setCancelled(true);
					return;
				}
			}
			return;
		}
		
		
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, new Runnable() {
			@Override
			public void run() {
				// Твой код тут
			}
		}, 20, 5000);
		
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
		    @Override
		     public void run() {
		    	if (e.isCancelled())
					return;
		    	Location loc = e.getEntity().getLocation();
		    	loc.setY(loc.getY() + 0.3);
		    	if (e.getDamager() instanceof Player) {
		    		Player pl = (Player) e.getDamager();
		    		if (pl.getInventory().getItemInMainHand().getType().equals(Material.AIR)|| pl.getInventory().getItemInMainHand().equals(null)) {
		    			int fightLvl = players.get(pl).playerPassiveSkills.get("Кулачный бой");
		    			if (fightLvl > 1) {
		    				Entity ent = e.getEntity();
		    				if(ent != null && ent instanceof Damageable && ent instanceof Attributable){
		    				    double damage = fightLvl / 2;
		    				    ((Damageable) ent).damage(damage);
		    				}
		    			}
		    			return;
		    		}
					if (pl.getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD)) {
						if (pl.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()) {
							switch (pl.getInventory().getItemInMainHand().getItemMeta().getCustomModelData()) {
							case 2:
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity le = (LivingEntity) e.getEntity();
									le.getWorld().playSound(loc, Sound.ENTITY_BEE_STING, 0.8f, 0.8f);
									le.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 0, false, false));
								}
								
								double plHealthAttr = pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
								if (pl.getHealth() < plHealthAttr) {
									double healMult = 1;
									double defaultHeal = 0.75;
									if (pl.getInventory().getItemInOffHand().equals(is2)) {
										healMult = 4;
									}
									if (pl.getHealth() + (defaultHeal * healMult) < plHealthAttr) {
										pl.setHealth(pl.getHealth() + (defaultHeal * healMult));
									} else {
										pl.setHealth(plHealthAttr);
									}
								}
								pl.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, loc, 2, 0.4, 0.6, 0.4, 0);
								pl.getWorld().spawnParticle(Particle.HEART, pl.getEyeLocation(), 2, 0.4, 0.6, 0.4, 0);
					   			return;
							
							case 4:
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity le = (LivingEntity) e.getEntity();
									le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1, false, false));
									le.getWorld().playSound(loc, Sound.BLOCK_LAVA_EXTINGUISH, 0.4f, 0.8f);
								}
								
								pl.getWorld().spawnParticle(Particle.FLAME, loc, 6, 0.4, 0.5, 0.4, 0);
					   			return;
					   			
							case 5:
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity le = (LivingEntity) e.getEntity();
									le.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 100, false, false));
									le.getWorld().playSound(loc, Sound.ENTITY_DOLPHIN_SPLASH, 0.6f, 0.8f);
								}	
								pl.getWorld().spawnParticle(Particle.WATER_SPLASH, loc, 6, 0.4, 0.5, 0.4, 0);
								pl.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 200, 0, false, false));
								return;
								
							case 6:
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity le = (LivingEntity) e.getEntity();
									le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, false, false));
									le.getWorld().playSound(loc, Sound.ENTITY_ENDER_EYE_DEATH, 0.45f, 1);
								}
								pl.getWorld().spawnParticle(Particle.PORTAL, loc, 12, 0.4, 0.5, 0.4, 0);
					   			return;
							}
						}
					}
					
					if (pl.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE)) {
						if (pl.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()) {
							switch (pl.getInventory().getItemInMainHand().getItemMeta().getCustomModelData()) {
							case 3:		
								if (e.getEntity() instanceof LivingEntity) {			
									LivingEntity le = (LivingEntity) e.getEntity();
									if (le.getType().equals(EntityType.IRON_GOLEM)) {
										le.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 40, 10, false, false));
										pl.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, loc, 6, 0.6, 1.1, 0.5, 0);
									}
									le.getWorld().playSound(loc, Sound.BLOCK_LANTERN_STEP, 0.85f, 1);
								}
								return;
							}
						}
					}
					
					if (pl.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_SWORD)) {
						if (pl.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()) {
							switch (pl.getInventory().getItemInMainHand().getItemMeta().getCustomModelData()) {
							case 1:		
								if (e.getEntity() instanceof LivingEntity) {			
									LivingEntity le = (LivingEntity) e.getEntity();
									int gold_c = tryToSpawnGold(0, 30);
									if (gold_c > 0) {
										le.getLocation().getWorld().dropItemNaturally(le.getLocation(), new ItemStack(Material.GOLD_NUGGET, gold_c));
									}
									pl.getWorld().spawnParticle(Particle.END_ROD, loc, 2, 0.3, 0.4, 0.3, 1);
									le.getWorld().playSound(loc, Sound.ENTITY_BLAZE_HURT, 0.65f, 0.5f);
								}							
				   			return;
							}
						}
					}
					
					if (pl.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD)) {
						if (pl.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()) {
							switch (pl.getInventory().getItemInMainHand().getItemMeta().getCustomModelData()) {
							
							case 6:					
								pl.getWorld().spawnParticle(Particle.DRIP_LAVA, loc, 10, 0.3, 0.5, 0.3, 0);
								pl.getWorld().playSound(loc, Sound.ENTITY_VEX_HURT, 0.85f, 1);
				   			return;
				   			
							case 5:
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity le = (LivingEntity) e.getEntity();
									le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 90, 0, false, false));
									le.getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.65f, 2);
									}
								pl.getWorld().spawnParticle(Particle.SPELL, loc, 24, 0.4, 0.6, 0.4, 24);
							return;
							
							case 3:
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity le = (LivingEntity) e.getEntity(); // illusioner mirror move для тп
									le.getWorld().playSound(le.getLocation(), Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.6f, 2);
								}
								pl.getWorld().spawnParticle(Particle.SPELL, loc, 8, 0.4, 0.6, 0.4, 24);
							return;
							}
						}
					}
					
					if (pl.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) && pl.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()) {
						switch (pl.getInventory().getItemInMainHand().getItemMeta().getCustomModelData()) {
						case 2:
							if (e.getEntity() instanceof LivingEntity) {
								LivingEntity le = (LivingEntity) e.getEntity();
								le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 4, 40, false, false));
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 4, false, false));
								le.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 0.85f, 1.1f);
							}
							pl.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 6, 0.6, 1.4, 0.5, 0);
				   			return;
								
						case 6:
							if (e.getEntity() instanceof LivingEntity) {
								LivingEntity le = (LivingEntity) e.getEntity();
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 45, 7, false, false));
								le.getWorld().playSound(loc, Sound.ENTITY_RAVAGER_STEP, 0.85f, 1);
							}
							pl.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 4, 0.6, 0.8, 0.5, 0);
				   			return;
				   			
						case 7:
							if (TryChance.tryChance(40)) {
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity vic = (LivingEntity) e.getEntity();
									int turn = 0;
									if (TryChance.tryChance(60)) {
										turn = 80;
									} else {
										turn = -130;
									}
									float newYaw = vic.getLocation().getYaw() + turn;
									if (newYaw >= 360) {
										newYaw -= 360;
									}
									if (newYaw < 0) {
										newYaw += 360;
									}
									Location rloc = vic.getLocation();
									rloc.setYaw(newYaw);
									vic.getLocation().setYaw(newYaw);
									pl.getWorld().spawnParticle(Particle.SQUID_INK, loc, 18, 0.3, 0.4, 0.4, 0);
									vic.teleport(rloc, TeleportCause.PLUGIN);
								}
								pl.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_HURT, 0.75f, 0.8f);
								return;
							}
						break;
						}
					}
						
					if (pl.getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) && pl.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()){
						if (pl.getInventory().getItemInMainHand().getItemMeta().getCustomModelData() == 2) {
							if (e.getEntity() instanceof LivingEntity) {
								LivingEntity le = (LivingEntity) e.getEntity();
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 2, false, false));
								le.getWorld().playSound(loc, Sound.BLOCK_ANVIL_LAND, 0.75f, 0.6f);
							}
							pl.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 14, 0.4, 0.5, 0.4, 0);
							pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 0, false, false));
							return;
						}
					}
				
					if (pl.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_SWORD) && pl.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()){
						if (pl.getInventory().getItemInMainHand().getItemMeta().getCustomModelData() == 1) {
							pl.getWorld().spawnParticle(Particle.COMPOSTER, loc, 7, 0.5, 0.6, 0.5, 0);
							pl.getWorld().playSound(loc, Sound.BLOCK_BAMBOO_FALL, 1f, 1f);
							pl.giveExp(2);
							players.get(pl).giveExp(2);
						}
					}
		    		
		    	}
		    }
		}, 1);
	}	
	
	private int tryToSpawnGold(int _count, int chance) {
		int count = _count;
		if (sheffrpg.main.TryChance.tryChance(chance)) {
			count += 1;
			count = tryToSpawnGold(count, chance);
		}
		return count;
	}
	
	@EventHandler
	public void onAnvilPrepare(PrepareAnvilEvent e) {
		if (e.getResult() != null || e.getResult().getType() != Material.AIR) {
			if (e.getResult().hasItemMeta() && e.getResult().getItemMeta().hasCustomModelData()) {
				ItemStack item = e.getResult();
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(e.getInventory().getItem(0).getItemMeta().getDisplayName());
				item.setItemMeta(meta);
				e.setResult(item);
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) throws IOException, ClassNotFoundException {
		players.put(e.getPlayer(), new RpgPlayer(e.getPlayer()));
	}
	
	@EventHandler
	public void onExit(PlayerQuitEvent e) throws FileNotFoundException, IOException {
		players.get(e.getPlayer()).save();
		players.remove(e.getPlayer());
	}
		
	@EventHandler
	public void projectileHit(ProjectileHitEvent e) {
		if (e.getHitEntity() instanceof LivingEntity && e.getEntity() instanceof Projectile) {
			if (e.getEntity().hasMetadata("lvl")) {
				LivingEntity le = (LivingEntity) e.getHitEntity();
				if (e.getEntity().getType().equals(EntityType.SNOWBALL)) {	
					if (le.hasPotionEffect(PotionEffectType.SLOW)) {
						le.removePotionEffect(PotionEffectType.SLOW);
					}
					int lvl = e.getEntity().getMetadata("lvl").get(0).asInt();
					int amp = 0;
					if (lvl > 4) {
						amp = 1;
						lvl -= 3;
					}
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (lvl * 40), amp, false, false, false));
					getLogger().info("Meta: " + e.getEntity().getMetadata("lvl").get(0).asInt());
					return;
				}
				if (e.getEntity().getType().equals(EntityType.FIREBALL)) {
					String name = e.getEntity().getMetadata("atacker").get(0).asString();
					if ((Bukkit.getServer().getPlayer(name) == null) || !(Bukkit.getServer().getPlayer(name).isOnline())) {
						return;
					}
					//Player atacker = Bukkit.getServer().getPlayer(name);
					//atacker.sendMessage("fireball попадание в " + le.getName());
					double dmg = 8 + e.getEntity().getMetadata("lvl").get(0).asDouble();
					if (le instanceof Damageable) {
						if (le.getHealth() > 1) {
							Player atacker = Bukkit.getServer().getPlayer(name);
							((Damageable) le).damage(dmg, atacker);
							//atacker.sendMessage("fireball попадание в " + le.getName() + ", доп урон: " + dmg);
						}
					}
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		try {
		String mob = WordUtils.capitalize(e.getEntity().getType().toString().replace("_", " ").toLowerCase());
		if (!(e.getEntity() instanceof Player) && (e.getEntity().getKiller() instanceof Player))
			players.get(e.getEntity().getKiller()).envKill(mob);
		} catch(Exception exc) {
			getLogger().info(exc.getLocalizedMessage());
		}
	}
		
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getPlayer() == null || e.getBlock() == null) {
			return;
		}
		try {
			players.get(e.getPlayer()).blockBreak(WordUtils.capitalize(e.getBlock().getType().toString().replace("_", " ").toLowerCase()), e);
		} catch (Exception exc) {
			getLogger().info(exc.getLocalizedMessage());
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getPlayer() == null || e.getBlock() == null) {
			return;
		}
		try {
			players.get(e.getPlayer()).blockPlace(WordUtils.capitalize(e.getBlock().getType().toString().replace("_", " ").toLowerCase()), e);
		} catch (Exception exc) {
			getLogger().info(exc.getLocalizedMessage());
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!players.containsKey(e.getEntity().getPlayer())) {
			return;
		}
		Player pl = e.getEntity().getPlayer();
		if (e.getEntity().getPlayer().getInventory().getItemInMainHand().equals(amulet_exp) || e.getEntity().getPlayer().getInventory().getItemInOffHand().equals(amulet_exp)) {
			e.setKeepLevel(true);
			e.setDroppedExp(0);
			
			pl.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, pl.getLocation(), 16, 0.5f, 0.8f, 0.5f, 0f);
			pl.getInventory().removeItem(amulet_exp);
		}
		if (e.getEntity().getKiller() instanceof Player) {
			if (!players.containsKey(e.getEntity().getKiller())) {
				return;
			}
			players.get(e.getEntity().getKiller()).plKill(e.getEntity().getName(), players.get(pl).plDead());
		} else {
			players.get(pl).plDead();
		}		
	}
	
	@EventHandler
	public void onFishCaught(PlayerFishEvent e) {
		players.get(e.getPlayer()).fish(e.getState().toString());
	}
	
	@EventHandler
	public void onItemEnchant(EnchantItemEvent e) {
		players.get(e.getEnchanter()).enchant(e.getExpLevelCost());
	}
	
	@EventHandler
	public void onItemPickUpFromFurnace(FurnaceExtractEvent e) {
		players.get(e.getPlayer()).furnace(e.getExpToDrop());
	}
	
	@EventHandler
	public void onPlayerTp(PlayerTeleportEvent e) {
		//getLogger().info(e.getCause().toString());
		if (e.getCause().toString().equals("COMMAND")) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				  @Override
				  public void run() {
					  e.getPlayer().getWorld().playSound(e.getTo(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
				  }
			}, 3);
		}
	}
	
	@EventHandler
	public void onPlayerEat(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		RpgPlayer pl = players.get(p);
		if (e.getItem().getType().equals(Material.MILK_BUCKET)) {
			RpgPlayer.updateEffects(pl);
			return;
		}
		if (e.getItem().equals(mana_potion)) {
			pl.restoreMana(30);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SQUID_SQUIRT, 0.8f, 0.9f);
			p.getWorld().spawnParticle(Particle.FALLING_WATER, p.getEyeLocation(), 4, 0.2f, 0.2f, 0.2f, 0.5f);
			return;
		}
		if (e.getItem().equals(mana_potion2)) {
			pl.restoreMana(120);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SQUID_SQUIRT, 0.8f, 0.6f);
			p.getWorld().spawnParticle(Particle.FALLING_WATER, p.getEyeLocation(), 8, 0.2f, 0.2f, 0.2f, 0.7f);
			return;
		} 
		if (e.getItem().equals(mana_potion3)) {
			pl.restoreMana(500);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SQUID_SQUIRT, 0.8f, 0.2f);
			p.getWorld().spawnParticle(Particle.FALLING_WATER, p.getEyeLocation(), 12, 0.2f, 0.2f, 0.2f, 0.9f);
			return;
		} 
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getPlayer() instanceof Player) {
			Player p = e.getPlayer();
			if (p.getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
        		if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contentEquals(ChatColor.YELLOW + "Меню")) {
        			e.setCancelled(true);
        			Bukkit.dispatchCommand(p, "menu");
        			return;
        		}
			}
			
            if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
            	if (p.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()) {	
            		switch (p.getInventory().getItemInMainHand().getItemMeta().getCustomModelData()) {
        				case 1:
        					e.setCancelled(true);
        					if (p.hasPermission("SheffRpg.wand.fireball")) {
        						int fireball_wand_cooldown = 1500;
        						if (cooldowns.containsKey(p.getName() + ",item=" + "wand.fireball")) {
        							Long currentTime = System.currentTimeMillis();
        							Long endTime = cooldowns.get(p.getName() + ",item=" + "wand.fireball");
        							if (currentTime > endTime) {
        								cooldowns.replace(p.getName() + ",item=" + "wand.fireball", currentTime + fireball_wand_cooldown);
       									p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 1);
       									p.launchProjectile(Fireball.class).setVelocity(p.getLocation().getDirection().normalize().multiply(1.75));
       								} else {
       									String textToP = (ChatColor.RED + "[SRPG] " + ChatColor.YELLOW + "Посох перезаряжается, осталось: " + ChatColor.AQUA + "" + (Math.round((endTime - currentTime) / 1000) + 1) + ChatColor.YELLOW + " сек.");
       									sendActionbar(p, textToP);
       								}
        						} else {
        							p.launchProjectile(Fireball.class).setVelocity(p.getLocation().getDirection().normalize().multiply(1.75));
        							p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 1);
        							cooldowns.put(p.getName() + ",item=" + "wand.fireball", System.currentTimeMillis() + fireball_wand_cooldown);
        						}
        					} else {
        						p.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Этот посох отключен на сервере.");
       						}
       					break;
        					
        				case 2:
        					e.setCancelled(true);
        					if (p.hasPermission("SheffRpg.wand.zeus")) {
        						int zeus_wand_cooldown = 1800;
        						//Bukkit.dispatchCommand(p, "fireball");
       							if (cooldowns.containsKey(p.getName() + ",item=" + "wand.zeus")) {
       								RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        							RegionQuery query = container.createQuery();
        							Long currentTime = System.currentTimeMillis();
        							Long endTime = cooldowns.get(p.getName() + ",item=" + "wand.zeus");
        							if (currentTime > endTime) {
        								cooldowns.replace(p.getName() + ",item=" + "wand.zeus", currentTime + zeus_wand_cooldown);	
        								Collection<Entity> ents = getTarget(p);
        								
        								if (!ents.isEmpty()) {
       										
       										Entity ent = ents.iterator().next();
       										
       										ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(ent.getLocation()));
        									if (!set.testState(null, Flags.PVP)) {
        										p.sendMessage(ChatColor.DARK_RED + "[SRPG] " + ChatColor.YELLOW + "Здесь нельзя использовать этот посох!");
        									    return;
        									}
        									if (!CombatUtil.preventDamageCall(towny, p, ent)) {
        										p.getLocation().getWorld().strikeLightning(ent.getLocation());
        										return;
        									} else {
        										p.sendMessage(ChatColor.DARK_RED + "[SRPG] " + ChatColor.YELLOW + "Здесь нельзя использовать этот посох!");
        										return;
       										}
       									} else {
       										Block bl = p.getTargetBlock(null, 64);
       										ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(bl.getLocation()));
        									if (!set.testState(null, Flags.PVP)) {
        										p.sendMessage(ChatColor.DARK_RED + "[SRPG] " + ChatColor.YELLOW + "Здесь нельзя использовать этот посох!");
        									    return;
        									}
        									if (PlayerCacheUtil.getCachePermission(p, bl.getLocation(), bl.getType(), TownyPermission.ActionType.DESTROY)) {
        										p.getLocation().getWorld().strikeLightning(bl.getLocation());
        										return;
        									} else {
        										p.sendMessage(ChatColor.DARK_RED + "[SRPG] " + ChatColor.YELLOW + "Здесь нельзя использовать этот посох!");
       											return;
       										}
       									}
       								} else {
       									String textToP = (ChatColor.RED + "[SRPG] " + ChatColor.YELLOW + "Посох перезаряжается, осталось: " + ChatColor.AQUA + "" + (Math.round((endTime - currentTime) / 1000) + 1) + ChatColor.YELLOW + " сек.");
        								sendActionbar(p, textToP);
        							}
        						} else {
        							RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        							RegionQuery query = container.createQuery();
        							Collection<Entity> ents = getTarget(p);
       								if (!ents.isEmpty()) {	
    									Entity ent = ents.iterator().next();
   										ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(ent.getLocation()));
   										if (!set.testState(null, Flags.PVP)) {
    									    return;
    									}
    									if (!CombatUtil.preventDamageCall(towny, p, ent)) {
    										p.getLocation().getWorld().strikeLightning(ent.getLocation());
    									}
    								} else {
    									Block bl = p.getTargetBlock(null, 64);
    									ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(bl.getLocation()));
    									if (!set.testState(null, Flags.PVP)) {
   										    return;
   										}
    									if (PlayerCacheUtil.getCachePermission(p, bl.getLocation(), bl.getType(), TownyPermission.ActionType.DESTROY)) {
    										p.getLocation().getWorld().strikeLightning(bl.getLocation());
    									}
    								}
        							cooldowns.put(p.getName() + ",item=" + "wand.zeus", System.currentTimeMillis() + zeus_wand_cooldown);
        						}
        					} else {
       							p.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Этот посох отключен на сервере.");
       						}
       					break;
        			}
				}
			}	
            if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD)) {
            	if (p.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()) {	
            		switch (p.getInventory().getItemInMainHand().getItemMeta().getCustomModelData()) {
        				case 3:
        					int cd = 500;
        					Long currentTime = System.currentTimeMillis();
        					Long endTime;
        					if (!cooldowns.containsKey(p.getName() + ",item=" + "sword.blink")) {
        						endTime = currentTime + cd;
        						cooldowns.put(p.getName() + ",item=" + "sword.blink", endTime);
        					} else {
        						endTime = cooldowns.get(p.getName() + ",item=" + "sword.blink");
        						if (endTime > currentTime) {
    								return;
    							} else {
    								cooldowns.replace(p.getName() + ",item=" + "sword.blink", currentTime + cd);
    							}
        					}
							
        					int pMana = players.get(p).mana;
        					if (pMana < 4) {
        						return;
        					}
        					if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        						Location oldLoc = p.getLocation();
	        					Location loc;
	        					double x, y, z;
	        					x = p.getLocation().getX();
	        					y = p.getEyeLocation().getY();
	        					z = p.getLocation().getZ();
	        					for (int i = 0; i <= 6; i++) {
	        						//p.sendMessage(x + " " + y + " " + z);
	        						x += p.getLocation().getDirection().normalize().getX();
	        						y += p.getLocation().getDirection().normalize().getY();
	        						z += p.getLocation().getDirection().normalize().getZ();
	        						if (p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)) != null && p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)).getType() != Material.AIR && p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)).getType() != Material.CAVE_AIR && p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)).getType() != Material.SNOW) {
	        							//p.sendMessage(ChatColor.YELLOW + "" + x + " " + y + " " + z);
	        							//p.sendMessage("is not null or air: " + p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)).getType().toString());
	        							x -= p.getLocation().getDirection().normalize().getX();
	        							y -= p.getLocation().getDirection().normalize().getY();
	        							z -= p.getLocation().getDirection().normalize().getZ();
	        							if (i == 0) {
	        								return;
	        							}
	        							i = 6;
	        						}
	        						if (i == 6) {
	        							if (p.getWorld().getBlockAt(new Location(p.getWorld(), x, y + 1, z)) != null && p.getWorld().getBlockAt(new Location(p.getWorld(), x, y + 1, z)).getType() != Material.AIR && p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)).getType() != Material.CAVE_AIR && p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)).getType() != Material.SNOW) {
	        								y += 1;
	        							}
	        							//y += 1;
	        							for (int j = 0; j < 7; j++) {
	        								y -= 1;
	        								if (p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)) != null && p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)).getType() != Material.AIR && p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)).getType() != Material.CAVE_AIR && p.getWorld().getBlockAt(new Location(p.getWorld(), x, y, z)).getType() != Material.SNOW) {
	        									y += 1;
	        									loc = new Location(p.getWorld(), x, y, z);
	        									if (loc.getBlockX() == oldLoc.getBlockX() && loc.getBlockZ() == oldLoc.getBlockZ()) {
	        										return;
	        									}
	        									loc.setYaw(p.getLocation().getYaw());
	        									loc.setPitch(p.getLocation().getPitch());
	        									p.teleport(loc);
	    	        							//p.playSound(loc, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f, 0.5f);
	    	        							p.getWorld().playSound(loc, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f, 0.5f);
	    	        							p.spawnParticle(Particle.END_ROD, oldLoc, 10, 0.3f, 0.5f, 0.4f, 0);
	    	        							p.spawnParticle(Particle.END_ROD, loc, 8, 0.4f, 0.3f, 0.5f, 0.2f);
	    	        							players.get(p).mana -= 4;
	    	        							players.get(p).updateScoreTable();
	    	        							players.get(p).regenMana();
	    	        							ItemStack sw = p.getInventory().getItemInMainHand();
	    	        							Damageable sm = (Damageable) sw.getItemMeta();
	    	        							sm.damage(1);
	    	        							sw.setItemMeta((ItemMeta) sm);
	        									return;
	        								}
	        							}
	        						}
	        					}
        					}
        				break;
        			}
				}
            }
		} 
	}
	
	private Collection<Entity> getTarget(Player _p) {
		Location loc = _p.getLocation().add(0,1.5,0);
		Collection<Entity> nearbyEntites = new ArrayList<Entity>();
		for(int i = 0; i < 64; i++) {
			Vector durect = loc.getDirection().multiply(i);
			loc.add(durect);
			
			nearbyEntites.addAll(loc.getWorld().getNearbyEntities(loc, 1, 1, 1));
			nearbyEntites.remove(_p);
			if(nearbyEntites.size() != 0) {
				break;
			}
		}
		return nearbyEntites;
	}
	
	public void onWorldSave() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    @Override
		    public void run() {
		    	Bukkit.getServer().getOnlinePlayers().forEach((k) -> {
					try {
						players.get(k).save();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				getLogger().info("Все игроки сохранены");
		    }
		}, 0L, 12000L);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		RpgPlayer.updateEffects(players.get(e.getPlayer()));
	}
	
	public static RpgPlayer getRpgPlayer(CommandSender pl) {
		return players.get(pl);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void invHandler(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		RpgPlayer pl = SheffRpg.players.get(e.getWhoClicked());
		if (inv.equals(pl.skillMenu)) {
			if ((e.getCursor().getType() == (Material.AIR) || e.getCursor() == null || e.getCurrentItem().getType() != (Material.AIR) || e.getCurrentItem() != null)) {
				e.setCancelled(true);
				switch (e.getSlot()) {
					case (2):
						pl.activeSkillLvlUp("Серия огненных шаров");
					break;
					case (3):
						pl.activeSkillLvlUp("Леденящий снаряд");
					break;
					case (6):
						pl.passiveSkillLvlUp("Интеллект");
					break;
					case (7):
						pl.passiveSkillLvlUp("Максимальное здоровье");
					break;
					case (8):
						pl.passiveSkillLvlUp("Шахтерские гены");
					break;
					case (15):
						pl.passiveSkillLvlUp("Опытный рудокоп");
					break;
					case (16):
						pl.passiveSkillLvlUp("Кулачный бой");
					break;
					case (17):
						pl.passiveSkillLvlUp("Походный рюкзак");
					break;
					case (18):
						Bukkit.getScheduler().runTask(this, new Runnable() {

							@Override
							public void run() {
								e.getWhoClicked().closeInventory();
							}
						});
					break;
					case (20):
						pl.activeSkillLvlUp("Пророчество Сатурна");
					break;
					case (21):
						pl.activeSkillLvlUp("Стремительный рывок");
					break;
					case (22):
						pl.activeSkillLvlUp("Кошачье зрение");
					break;
					
				}
			}
			return;
		}
		if (inv.equals(RpgPlayer.serverMenu)) {
			if ((e.getCursor().getType() == (Material.AIR) || e.getCursor() == null || e.getCurrentItem().getType() != (Material.AIR) || e.getCurrentItem() != null)) {
				e.setCancelled(true);
				Player p = (Player) e.getWhoClicked();
				switch (e.getSlot()) {
					case (0):
						Bukkit.dispatchCommand(p, "spawn");
					break;
					case (1):
						Bukkit.dispatchCommand(p, "home");
					break;
					case (2):
						Bukkit.dispatchCommand(p, "t spawn");
					break;
					case (3):
						Bukkit.dispatchCommand(p, "warp bank");
					break;
					case (4):
						Bukkit.dispatchCommand(p, "warp erofei");
					break;
					case (5):
						Bukkit.dispatchCommand(p, "warp eiden");
					break;
					case (6):
						Bukkit.dispatchCommand(p, "warp pvp");
					break;
					case (7):
						Bukkit.dispatchCommand(p, "warp spleef");
					break;
					case (22):
						Bukkit.dispatchCommand(p, "warp craft");
					break;
					
					case (26):
						p.closeInventory();
						p.openInventory(pl.skillMenu);
					break;
					
					case (18):
						Bukkit.getScheduler().runTask(this, new Runnable() {

							@Override
							public void run() {
								p.closeInventory();
							}
						});	
					return;
				}
			}
			return;
		}
		if (inv.equals(donateItemsInv)) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot()) {		
				case (53):
					Bukkit.getScheduler().runTask(this, new Runnable() {

						@Override
						public void run() {
							p.closeInventory();
						}
					});	
				break;
				
				default:
					if (e.getCurrentItem() != null && e.getSlot() < 54) {
						p.getInventory().addItem(e.getCurrentItem());
					}
				break;
			}
			e.setCancelled(true);
			return;
		}
	}
	
	
	public static void sendActionbar(Player _p, String message) {
		_p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}



