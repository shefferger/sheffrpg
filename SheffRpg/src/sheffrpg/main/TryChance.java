package sheffrpg.main;



public class TryChance {
	
	public static boolean tryChance(int chance){
	    int x = (int) Math.round(Math.random() * 100);
	    //Bukkit.getServer().dispatchCommand(Iterables.get(Bukkit.getServer().getOnlinePlayers(), 0), "me " + x );
	    if (x > chance) {
	    	return false;
	    }
	    else {
	    	return true;
	    }
	}
	
	public static boolean trySmallChance(double chance){
	    double x = Math.random() * 100;
	    //Bukkit.getServer().dispatchCommand(Iterables.get(Bukkit.getServer().getOnlinePlayers(), 0), "me " + x );
	    if (x > chance) {
	    	return false;
	    }
	    else {
	    	return true;
	    }
	}
	
}
