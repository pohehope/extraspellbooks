package org.pohehope.extraspellbooks.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.pohehope.extraspellbooks.Extraspellbooks;
import org.pohehope.extraspellbooks.effect.BurningSoulEffect;
import org.pohehope.extraspellbooks.effect.FreezeSoulEffect;
import org.pohehope.extraspellbooks.effect.HemostasisEffect;
import org.pohehope.extraspellbooks.effect.UnderMoonEffect;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Extraspellbooks.MODID);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }

    public static final RegistryObject<MobEffect> BURNINGSOUL = MOB_EFFECTS.register("burningsoul",
            () -> new BurningSoulEffect(MobEffectCategory.BENEFICIAL,3311322)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "87733c95-909c-4fc3-9780-e35a89565666", BurningSoulEffect.ATTACK_DAMAGE_LEVEL, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,"87733c95-909c-4fc3-9780-e35a89565666", BurningSoulEffect.SPEED_LEVEL, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> FREEZESOUL = MOB_EFFECTS.register("freezesoul",
            () -> new FreezeSoulEffect(MobEffectCategory.BENEFICIAL, 3311322)
                    .addAttributeModifier(Attributes.ARMOR, "87733c95-909c-4fc3-9780-e35a89565666", FreezeSoulEffect.ARMOR, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "87733c95-909c-4fc3-9780-e35a89565666", FreezeSoulEffect.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> UNDERMOON = MOB_EFFECTS.register("undermoon",
            () -> new UnderMoonEffect(MobEffectCategory.BENEFICIAL, 3311322));

    public static final RegistryObject<MobEffect> HEMOSTASIS = MOB_EFFECTS.register( "hemostasis",
            () -> new HemostasisEffect(MobEffectCategory.BENEFICIAL, 3311322));
}
