package sheffrpg.main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class BackpackCommand implements CommandExecutor{
	
	private SheffRpg plugin;
	
	public BackpackCommand(SheffRpg plugin) {
		this.setPlugin(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player p = (Player) sender;
		if (args.length == 0) {
			p.openInventory(SheffRpg.players.get(p).backpack);
			return true;
		} else {
			if (args.length == 1) {
				if (p.hasPermission("SheffRpg.admin")) {
					try {
						if (Bukkit.getServer().getPlayer(args[0]).isOnline()) {
							p.openInventory(SheffRpg.players.get(Bukkit.getServer().getPlayer(args[0])).backpack);
						} else {
							p.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Игрок с ником " + ChatColor.GOLD + args[0] + ChatColor.YELLOW + " не найден или оффлайн!");
						}
					} catch (NullPointerException exc) {
						p.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Игрок с ником " + ChatColor.GOLD + args[0] + ChatColor.YELLOW + " не найден или оффлайн!");
					}
				} else {
					p.sendMessage(ChatColor.RED + "[SRPG]" + ChatColor.YELLOW + " Недостаточно прав!");
				}
			}
			return true;
		}
	}
	


	public SheffRpg getPlugin() {
		return plugin;
	}

	public void setPlugin(SheffRpg plugin) {
		this.plugin = plugin;
	}
}
