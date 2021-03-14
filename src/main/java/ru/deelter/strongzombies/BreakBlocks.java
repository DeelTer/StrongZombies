package ru.deelter.strongzombies;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 @author DeelTer
 Zombies can break blocks
 */
public class BreakBlocks {

    /* Main timer */
    public static void runTimer() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {

            /* Get all players online */
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode() != GameMode.SURVIVAL)
                    continue;

                if (player.isDead())
                    continue;

                /* Get all zombies in radius 20 */
                for (Monster monster : player.getLocation().getNearbyEntitiesByType(Zombie.class, 20)) {
                    /* Only aggressive */
                    if (monster.getTarget() == null) {
                        //monster.setTarget(player);
                        continue;
                    }

                    /* Check zombie that he can break blocks */
                    if (monster.hasMetadata("canBreak"))
                        continue;

                    /* 85 percent chance */
                    if (Math.random() <= 0.15D)
                        continue;

                    /* Spawn protection */
                    World world = monster.getWorld();
                    if (world.getSpawnLocation().distance(monster.getLocation()) < 30)
                        continue;

                    /* Break target block */
                    Block block = monster.getTargetBlock(3);
                    if (block == null)
                        continue;

                    block.breakNaturally();
                    world.playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.5F, 0F);

                    monster.setMetadata("canBreak", new FixedMetadataValue(Main.getInstance(), true));
                    startBreakTimer(monster);

                    startWallFalling(monster.getLocation().getDirection(), block.getLocation().add(0, 1, 0));
                }
            }
        }, 0, 6 * 20L);
    }

    private static void startWallFalling(Vector vector, Location location) {
        final int zombies = location.getNearbyEntitiesByType(Zombie.class, 5).size();
        double multiply = 0.1;

        for (int i = 0; i < zombies * 2; i++) {

            final BlockData blockData = location.getBlock().getBlockData();
            location.getBlock().setType(Material.AIR);

            FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location.toCenterLocation(), blockData);
            fallingBlock.setHurtEntities(true);
            fallingBlock.setDropItem(true);
            fallingBlock.setVelocity(vector.normalize().multiply(multiply));

            location = location.add(0, 1, 0);
            multiply = multiply + 0.02;
        }
    }

    private static void startBreakTimer(Monster monster) {

        int random = new Random().nextInt(6);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            if (!monster.isDead() && monster.hasMetadata("canBreak"))
                monster.removeMetadata("canBreak", Main.getInstance());
        }, random * 20L);
    }
}

