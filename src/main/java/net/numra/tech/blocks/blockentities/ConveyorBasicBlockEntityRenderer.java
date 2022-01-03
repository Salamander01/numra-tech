package net.numra.tech.blocks.blockentities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import static net.numra.tech.NumraTechClient.logger_client;

public class ConveyorBasicBlockEntityRenderer implements BlockEntityRenderer<ConveyorBasicBlockEntity> {
    public DefaultedList<ItemStack> stacks;
    
    public ConveyorBasicBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    
    }
    
    @Override
    public void render(ConveyorBasicBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        //matrices.push();
        stacks = blockEntity.getStacks();
        for (ItemStack stack : stacks) {
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers,  0);
        }
        //matrices.pop();
    }
}
