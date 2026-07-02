package org.pohehope.extraspellbooks.entity.spells.frostenstrosity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FrostEnstrosityRenderer extends EntityRenderer<FrostEnstrosityEntity> {
    public FrostEnstrosityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(FrostEnstrosityEntity frostEnstrosity) {
        return null;
    }
}
