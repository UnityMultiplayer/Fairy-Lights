package me.paulf.fairylights.client.model.light;

import net.minecraft.util.math.AxisAlignedBB;

public class FairyLightModel extends ColorLightModel {
    public FairyLightModel() {
        final BulbBuilder bulb = this.createBulb();
        bulb.setUV(46, 0);
        bulb.addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F);
    }

    public AxisAlignedBB getBounds() {
        return this.getBounds(true);
    }
}
