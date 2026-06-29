package org.pohehope.extraspellbooks.registry;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.pohehope.extraspellbooks.Extraspellbooks;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Extraspellbooks.MODID);

    public static final RegistryObject<Attribute> NIGHT_SPELL_POWER = ATTRIBUTES.register("night_spell_power",
            () -> new RangedAttribute("attribute.name." + Extraspellbooks.MODID + ".night_spell_power", 1.0D, 0.0D, 100.0D).setSyncable(true));

    public static final RegistryObject<Attribute> NIGHT_MAGIC_RESIST = ATTRIBUTES.register("night_magic_resist",
            () -> new RangedAttribute("attribute.name." + Extraspellbooks.MODID + ".night_magic_resist", 0.0D, -100.0D, 1.0D).setSyncable(true));

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
