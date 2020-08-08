package sheffrpg.main;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jdk.internal.org.jline.utils.Log;


public class RpgCommand implements CommandExecutor{
	
	private SheffRpg plugin;
	
	public RpgCommand(SheffRpg plugin) {
		this.setPlugin(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Integer xp = 0;
		if (args.length == 4) {
			if (!(Bukkit.getServer().getPlayer(args[1]).isOnline())) {
				sender.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Игрок оффлайн!");
				return true;
			}
			Player p = Bukkit.getServer().getPlayer(args[1]);
			if (args[2].equals("exp")) {
				if (!(sender.hasPermission("SheffRpg.admin"))) {
					sender.sendMessage(ChatColor.DARK_RED + "[SRPG]" + ChatColor.RED + " Недостаточно прав!");
					return true;
				}
				try {
					xp = Integer.valueOf(args[3]);
				} catch(NumberFormatException exc) {
					sender.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + "Кол-во опыта должно быть числом!");
					return true;
				}
				if (args[0].equalsIgnoreCase("give")) {
					SheffRpg.players.get(Bukkit.getServer().getPlayer(args[1])).giveExp(xp);
					Bukkit.getServer().getPlayer(args[1]).sendMessage("Вам выдали " + xp + " опыта");
					sender.sendMessage("Вы выдали " + args[1] + " " + xp + " ед. опыта");
					return true;
				} else {
					if (args[0].equalsIgnoreCase("remove")) {
						SheffRpg.players.get(Bukkit.getServer().getPlayer(args[1])).removeExp(xp);
						Bukkit.getServer().getPlayer(args[1]).sendMessage("У вас забрали " + xp + " опыта");
						sender.sendMessage("Вы забрали у " + args[1] + " " + xp + " ед. опыта");
						return true;
					}
				}
			} else {
				if (!(sender.hasPermission("SheffRpg.admin"))) {
					sender.sendMessage(ChatColor.DARK_RED + "[SRPG]" + ChatColor.RED + " Недостаточно прав!");
					return true;
				}
				if (args[0].equalsIgnoreCase("give")) {
					if (args[2].equalsIgnoreCase("points")) {
						try {
							Integer amount = Integer.valueOf(args[3]);
							SheffRpg.players.get(p).givePoints(amount);
							p.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Вам выдали " + ChatColor.AQUA + amount + ChatColor.YELLOW + " очков способностей");
							sender.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Вы выдали " + ChatColor.AQUA + amount + ChatColor.YELLOW + " очков способностей игроку " + ChatColor.GOLD + p.getName());
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + "Кол-во очков должно быть числом!");
						}
						return true;
					}
					if (args[2].equalsIgnoreCase("lvl")) {
						try {
							Integer amount = Integer.valueOf(args[3]);
							SheffRpg.players.get(p).giveLvl(amount);
							p.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Вам выдали " + ChatColor.AQUA + amount + ChatColor.YELLOW + " уровней");
							sender.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Вы выдали " + ChatColor.AQUA + amount + ChatColor.YELLOW + " уровней игроку " + ChatColor.GOLD + p.getName());
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + "Кол-во уровней должно быть числом!");
						}
						return true;
					}
				}
			}
			
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("stats")) {
				try {
					if (Bukkit.getServer().getPlayer(args[1]) == null || !(Bukkit.getServer().getPlayer(args[1]).isOnline())) {
						sender.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Игрок оффлайн!");
						return true;
					}
				} catch (Exception e) {
					Log.warn("Exception on rpg stats occured");
					return true;
				}
				Player target = Bukkit.getServer().getPlayer(args[1]);
				int lvl = SheffRpg.players.get(target).playerInfo.get("lvl");
				int exp = SheffRpg.players.get(target).playerInfo.get("exp");
				sender.sendMessage("\n" + ChatColor.YELLOW + "Статистика игрока " + ChatColor.RED + target.getName());
				sender.sendMessage(ChatColor.GOLD + "Уровень: " + ChatColor.AQUA + ChatColor.BOLD +  lvl);
				sender.sendMessage(ChatColor.YELLOW + "Опыт: " + ChatColor.GOLD + ChatColor.BOLD + exp + "/" + RpgPlayer.getNextExp(lvl));
				sender.sendMessage(ChatColor.AQUA+ "Неиспользованных очков способностей: " + ChatColor.YELLOW + ChatColor.BOLD + SheffRpg.players.get(target).points);
				sender.sendMessage(ChatColor.YELLOW + "Убито мобов: " + ChatColor.BOLD + SheffRpg.players.get(target).mobsKilled.get("Total"));
				sender.sendMessage(ChatColor.GOLD + "Поймано рыбы: " + ChatColor.BOLD + SheffRpg.players.get(target).mobsKilled.get("Fish"));
				sender.sendMessage(ChatColor.YELLOW + "Выкопано блоков: " + ChatColor.BOLD + SheffRpg.players.get(target).blocksDigged.get("Total"));
				sender.sendMessage(ChatColor.GOLD + "Поставлено блоков: " + ChatColor.BOLD + SheffRpg.players.get(target).blocksPlaced.get("Total"));
				sender.sendMessage(ChatColor.YELLOW + "Пассивные cпособности:\n" + ChatColor.ITALIC + SheffRpg.players.get(target).playerPassiveSkills);
				sender.sendMessage(ChatColor.YELLOW + "Активные cпособности:\n" + ChatColor.ITALIC + SheffRpg.players.get(target).playerActiveSkills);

				return true;
			}
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("top")) {
				return true;
			}
			if (args[0].equalsIgnoreCase("items")) {
				if (sender instanceof Player && sender.hasPermission("sheffrpg.admin")) {
					Player p = (Player) sender;
					p.openInventory(SheffRpg.donateItemsInv);
				}
				return true;
			}
		}
		return false;
	}
	
	public SheffRpg getPlugin() {
		return plugin;
	}

	public void setPlugin(SheffRpg plugin) {
		this.plugin = plugin;
	}
}
