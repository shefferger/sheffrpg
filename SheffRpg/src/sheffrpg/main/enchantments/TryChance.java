package sheffrpg.main.enchantments;

public class TryChance {
  public static boolean tryChance(int chance) {
    int x = (int)Math.round(Math.random() * 100.0D);
    if (x > chance)
      return false; 
    return true;
  }
}
