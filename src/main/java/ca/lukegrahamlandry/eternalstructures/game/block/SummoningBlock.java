package ca.lukegrahamlandry.eternalstructures.game.block;

import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.game.tile.ProtectionTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.SummoningTile;
import ca.lukegrahamlandry.eternalstructures.network.NetworkHandler;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenProtectionSettings;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenSummonSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

// TODO: this is a copy-paste from protection
public class SummoningBlock extends Block {
    public SummoningBlock() {
        super(Properties.copy(Blocks.BEDROCK));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModRegistry.Tiles.SUMMONING_ALTAR.get().create();
    }

    @Override
    public ActionResultType use(BlockState p_225533_1_, World level, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        if (!level.isClientSide()) {
            TileEntity tile = level.getBlockEntity(pos);
            if (tile instanceof SummoningTile) {
                if (player.isCreative() && player.isShiftKeyDown()){
                    NetworkHandler.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                            new OpenSummonSettings(pos, ((SummoningTile)tile).getSettings())
                    );
                } else {
                    ((SummoningTile)tile).rightClick((ServerPlayerEntity) player);
                }
            } else {
                player.displayClientMessage(new StringTextComponent("Invalid tile entity. Report bug to Eternal Structures."), true);
            }
        }

        return ActionResultType.SUCCESS;
    }
}
