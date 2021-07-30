package com.finderfeed.solarforge.for_future_library;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.system.CallbackI;

public class FinderfeedMathHelper {


    public static double getDistanceBetween(Vec3 tile,Vec3 start){
        return new Vec3(tile.x - start.x,tile.y - start.y,tile.z - start.z).length();
    }

    public static double getDistanceBetween(BlockPos tile,BlockPos start){
        Vec3 startPos = new Vec3(start.getX()+0.5,start.getY()+0.5,start.getZ()+0.5);
        Vec3 tileEntityPos = new Vec3(tile.getX()+0.5,tile.getY()+0.5,tile.getZ()+0.5);
        return new Vec3(tileEntityPos.x - startPos.x,tileEntityPos.y - startPos.y,tileEntityPos.z - startPos.z).length();
    }

    public static double getDistanceBetween(BlockEntity tile,BlockEntity start){
        Vec3 startPos = new Vec3(start.getBlockPos().getX()+0.5,start.getBlockPos().getY()+0.5,start.getBlockPos().getZ()+0.5);
        Vec3 tileEntityPos = new Vec3(tile.getBlockPos().getX()+0.5,tile.getBlockPos().getY()+0.5,tile.getBlockPos().getZ()+0.5);
        return new Vec3(tileEntityPos.x - startPos.x,tileEntityPos.y - startPos.y,tileEntityPos.z - startPos.z).length();
    }


    public static boolean canSeeTileEntity(BlockEntity tile, BlockEntity start,double radius){
        Vec3 startPos = new Vec3(start.getBlockPos().getX()+0.5,start.getBlockPos().getY()+0.5,start.getBlockPos().getZ()+0.5);
        Vec3 tileEntityPos = new Vec3(tile.getBlockPos().getX()+0.5,tile.getBlockPos().getY()+0.5,tile.getBlockPos().getZ()+0.5);

        double raznitsa = new Vec3(tileEntityPos.x - startPos.x,tileEntityPos.y - startPos.y,tileEntityPos.z - startPos.z).length();
        if (raznitsa <= radius) {
            ClipContext ctx = new ClipContext(startPos, tileEntityPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
            BlockHitResult res = start.getLevel().clip(ctx);
            if (equalsBlockPos(tile.getBlockPos(), res.getBlockPos())) {
                return true;
            }
        }
        return false;
    }


    public static boolean canSeeTileEntity(BlockEntity tile, Player player){
        Vec3 playerHeadPos = player.position().add(0,player.getStandingEyeHeight(player.getPose(),player.getDimensions(player.getPose())),0);
        Vec3 tileEntityPos = new Vec3(tile.getBlockPos().getX()+0.5,tile.getBlockPos().getY()+0.5,tile.getBlockPos().getZ()+0.5);
        ClipContext ctx = new ClipContext(playerHeadPos,tileEntityPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,null);
        BlockHitResult res = player.level.clip(ctx);
        if (equalsBlockPos(tile.getBlockPos(),res.getBlockPos())){
            return true;
        }
        return false;
    }

    public static boolean equalsBlockPos(BlockPos pos1, BlockPos pos2){
        return (pos1.getX() == pos2.getX()) && (pos1.getY() == pos2.getY()) && (pos1.getZ() == pos2.getZ());
    }

}
