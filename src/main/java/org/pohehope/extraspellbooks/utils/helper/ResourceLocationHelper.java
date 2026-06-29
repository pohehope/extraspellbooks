package org.pohehope.extraspellbooks.utils.helper;

import net.minecraft.resources.ResourceLocation;
import org.pohehope.extraspellbooks.Extraspellbooks;

@SuppressWarnings("removal")
public class ResourceLocationHelper {
    public static ResourceLocation getResouceLocation(String path) {
        return new ResourceLocation(Extraspellbooks.MODID, path);
    }
}
