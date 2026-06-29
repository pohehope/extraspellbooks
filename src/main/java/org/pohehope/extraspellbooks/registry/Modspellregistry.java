package org.pohehope.extraspellbooks.registry;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.pohehope.extraspellbooks.spells.blood.HemostasisSpell;
import org.pohehope.extraspellbooks.spells.fire.BurningSoulSpell;
import org.pohehope.extraspellbooks.spells.ice.*;
import org.pohehope.extraspellbooks.spells.night.MeteorShowerSpell;
import org.pohehope.extraspellbooks.spells.night.UnderMoonSpell;
import org.pohehope.extraspellbooks.spells.lightning.LowVoltagemissileSpell;
import org.pohehope.extraspellbooks.spells.nature.CarrotsSpell;
import org.pohehope.extraspellbooks.spells.nature.CucumberSpell;

public class Modspellregistry {

    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(
                    io.redspace.ironsspellbooks.api.registry.SpellRegistry.SPELL_REGISTRY_KEY,
                    "extraspellbooks"
            );

    public static final RegistryObject<AbstractSpell> BURNING_SOUL =
            SPELLS.register("burningsoul", BurningSoulSpell::new);

    public static final RegistryObject<AbstractSpell> LOW_VOLTAGE_MISSILE =
            SPELLS.register("lowvoltagemissile", LowVoltagemissileSpell::new);

    public static final RegistryObject<AbstractSpell> Cucumber =
            SPELLS.register("cucumber", CucumberSpell::new);

    public static final RegistryObject<AbstractSpell> FREEZESOUL =
            SPELLS.register("freezesoul", FreezeSoulSpell::new);

    public static final RegistryObject<AbstractSpell> CARROT =
            SPELLS.register("carrots", CarrotsSpell::new);

    public static final RegistryObject<AbstractSpell> UNDERMOON =
            SPELLS.register("undermoon", UnderMoonSpell::new);

    public static final RegistryObject<AbstractSpell> METEORSHOWER =
            SPELLS.register("meteor_shower", MeteorShowerSpell::new);

    public static final RegistryObject<AbstractSpell> MINIICEBOMB =
            SPELLS.register("mini_ice_bomb", MiniIceBombSpell::new);

    public static final RegistryObject<AbstractSpell> HEMOSTASIS =
            SPELLS.register("hemostasis", HemostasisSpell::new);

    public static final RegistryObject<AbstractSpell> SNOWFLAKE =
            SPELLS.register("snow_flake", SnowFlakeSpell::new);

    public static final RegistryObject<AbstractSpell> SWING_SNOW_BALL =
            SPELLS.register("swing_snow_ball", SwingSnowBallSpell::new);

    public static final RegistryObject<AbstractSpell> ICE_CAGE =
            SPELLS.register("ice_cage", IceCageSpell::new);

    public static final RegistryObject<AbstractSpell> FALL_SNOWBALL =
            SPELLS.register("fall_snowball", SnowBallFallSpell::new);
}