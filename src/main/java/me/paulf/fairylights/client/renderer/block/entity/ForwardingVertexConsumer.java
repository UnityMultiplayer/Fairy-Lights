package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;

public abstract class ForwardingVertexConsumer implements VertexConsumer {
    protected abstract VertexConsumer delegate();

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        return delegate().addVertex(x, y, z);
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return delegate().setColor(red, green, blue, alpha);
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return delegate().setUv(u, v);
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return delegate().setUv1(u, v);
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return delegate().setUv2(u, v);
    }

    @Override
    public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
        return delegate().setNormal(normalX, normalY, normalZ);
    }

}
