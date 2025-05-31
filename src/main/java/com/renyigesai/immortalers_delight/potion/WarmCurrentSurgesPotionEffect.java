package com.renyigesai.immortalers_delight.potion;

import com.renyigesai.immortalers_delight.event.DifficultyModeHelper;
import com.renyigesai.immortalers_delight.init.ImmortalersDelightMobEffect;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber
public class WarmCurrentSurgesPotionEffect {
    @SubscribeEvent
    public static void onEntityAddEffect(MobEffectEvent.Applicable event) {
        if (event != null && event.getEntity() != null) {
            Entity entity = event.getEntity();
            if (entity instanceof LivingEntity livingEntity
                    && livingEntity.hasEffect(ImmortalersDelightMobEffect.WARM_CURRENT_SURGES.get())
                    && event.getEffectInstance().getEffect() == MobEffects.MOVEMENT_SLOWDOWN) {
                //int time = Objects.requireNonNull(livingEntity.getEffect(ImmortalersDelightMobEffect.WARM_CURRENT_SURGES.get())).getDuration();
                int lv = Objects.requireNonNull(livingEntity.getEffect(ImmortalersDelightMobEffect.WARM_CURRENT_SURGES.get())).getAmplifier();
                //int timeEvt = event.getEffectInstance().getDuration();
                int lvEvt = event.getEffectInstance().getAmplifier();
                if (lv >= lvEvt){
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCreatureHurt(LivingHurtEvent evt) {
        if (evt.isCanceled() || evt.getSource().is(DamageTypeTags.BYPASSES_RESISTANCE)) {
            return;
        }
        LivingEntity hurtOne = evt.getEntity();
        LivingEntity attacker = null;
        Boolean isPowerful = DifficultyModeHelper.isPowerBattleMode();
        if (evt.getSource().getEntity() instanceof LivingEntity livingEntity){
            attacker = livingEntity;
        }

        if (!hurtOne.level().isClientSide) {
            if (attacker != null){
                if (attacker.hasEffect(ImmortalersDelightMobEffect.WARM_CURRENT_SURGES.get())) {
                    int lv = attacker.getEffect(ImmortalersDelightMobEffect.WARM_CURRENT_SURGES.get()).getAmplifier();
                    float damage = (hurtOne.getRemainingFireTicks() > 1 ? 4 << lv : 2 << lv)
                            + (isPowerful ? (float)(hurtOne.getRemainingFireTicks() > (lv+1) * 160 ? (hurtOne.getRemainingFireTicks()/20) : (lv+1) * 8) : 0);
                    hurtOne.invulnerableTime = 0;
                    hurtOne.hurt(hurtOne.damageSources().onFire(), damage);
                    hurtOne.invulnerableTime = 0;
                    if (isPowerful) hurtOne.setRemainingFireTicks(0);
                }
            }
        }
    }
}
