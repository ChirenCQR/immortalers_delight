package com.renyigesai.immortalers_delight.util.task;

import com.renyigesai.immortalers_delight.ImmortalersDelightMod;
import com.renyigesai.immortalers_delight.util.task.ScheduledExecuteTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber
public class TimekeepingTask {
    private static Long ImmortalTickTime = 0L;
    public static Long getImmortalTickTime() {return ImmortalTickTime;}
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (ImmortalTickTime == 0) ImmortalTickTime = System.currentTimeMillis();

        }
    }
    @SubscribeEvent
    public static void onTick(@Nonnull TickEvent.ServerTickEvent evt) {
        if (evt.phase.equals(TickEvent.Phase.START)) {
            run();
        }
    }
    private static void run() {
        ImmortalTickTime = ImmortalTickTime + 50L;
        //ImmortalersDelightMod.LOGGER.info("现在的TICK时间是：" + ImmortalTickTime);
    }
}
