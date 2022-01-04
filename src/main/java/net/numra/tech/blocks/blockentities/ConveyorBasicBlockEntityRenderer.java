package net.numra.tech.blocks.blockentities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.numra.tech.blocks.ConveyorDirection;

import java.util.Objects;

import static net.numra.tech.NumraTechClient.logger_client;
import static net.numra.tech.blocks.ConveyorBasicBlock.DIRECTION;

public class ConveyorBasicBlockEntityRenderer implements BlockEntityRenderer<ConveyorBasicBlockEntity> {
    public DefaultedList<ItemStack> stacks;
    
    public ConveyorBasicBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    
    }
    
    private Quaternion getBeltRotationQuaternion(ConveyorBasicBlockEntity blockEntity) {
        ConveyorDirection directions = blockEntity.getSelfState().get(DIRECTION);
        if(directions.getFirstDirection() == directions.getSecondDirection()) {
            return switch (directions.getFirstDirection()) {
                case NORTH -> Vec3f.POSITIVE_Y.getDegreesQuaternion(180);
                case EAST -> Vec3f.POSITIVE_Y.getDegreesQuaternion(90);
                case SOUTH -> Vec3f.POSITIVE_Y.getDegreesQuaternion(0);
                default -> Vec3f.NEGATIVE_Y.getDegreesQuaternion(90);
            };
        } else {
            return Vec3f.POSITIVE_Y.getDegreesQuaternion(0); //temp
        }
    }
    
    private double getBeltTranslationValue(ConveyorBasicBlockEntity blockEntity, int index, String desiredValue) {
        if (Objects.equals(desiredValue, "y")) return 0.22;
        ConveyorDirection directions = blockEntity.getSelfState().get(DIRECTION);
        if (directions.getFirstDirection() == directions.getSecondDirection()) {
            switch (directions.getFirstDirection()) {
                case NORTH -> { if (Objects.equals(desiredValue, "x")) return 0.5; else return (double) -blockEntity.getProgress(index) / 1000 + 1; }
                case EAST -> { if (Objects.equals(desiredValue, "z")) return 0.5; else return (double) blockEntity.getProgress(index) / 1000; }
                case SOUTH -> { if (Objects.equals(desiredValue, "x")) return 0.5; else return (double) blockEntity.getProgress(index) / 1000; }
                default -> { if (Objects.equals(desiredValue, "z")) return 0.5; else return (double) -blockEntity.getProgress(index) / 1000 + 1; }
            }
        } else {
            return 0; //temp
        }
    }
    
    @Override
    public void render(ConveyorBasicBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        stacks = blockEntity.getStacks();
        int index = 0;
        for (ItemStack stack : stacks) {
            matrices.translate(getBeltTranslationValue(blockEntity, index, "x"), getBeltTranslationValue(blockEntity, index, "y"), getBeltTranslationValue(blockEntity, index, "z"));
            matrices.multiply(getBeltRotationQuaternion(blockEntity));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
            matrices.scale(0.9F, 0.9F, 0.9F);
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers,  0);
            index++;
        }
        matrices.pop();
    }
}
