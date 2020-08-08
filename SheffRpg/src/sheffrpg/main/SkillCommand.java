package sheffrpg.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommand implements CommandExecutor{
	
	private SheffRpg plugin;
	
	public SkillCommand(SheffRpg plugin) {
		this.setPlugin(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player p = (Player) sender;
		if (args.length == 0) {
			String strCmd = cmd.getName().toString().toLowerCase();
			if (strCmd.contentEquals("skill")) {
				p.openInventory(SheffRpg.players.get(p).skillMenu);
			} else {
				if (strCmd.contentEquals("menu")) {
					p.openInventory(RpgPlayer.serverMenu);
				}
			}
			return true;
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
