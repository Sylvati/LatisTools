package lati.blocks;

import lati.blockentities.ToolTableBlockEntity;
import lati.init.BlockEntityInit;
import lati.menus.ToolTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ToolTableBlock extends Block implements EntityBlock {
    public ToolTableBlock(Properties props) { super(props); }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityInit.TOOL_TABLE.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : ($0, $1, $2, blockEntity) -> {
            if(blockEntity instanceof ToolTableBlockEntity tool_table) {
                tool_table.tick();
            }
        };
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide()) {
            if(level.getBlockEntity(pos) instanceof ToolTableBlockEntity tool_table) {
                MenuConstructor menuConstructor = ToolTableMenu.getServerMenu(tool_table, pos);
                SimpleMenuProvider provider = new SimpleMenuProvider(menuConstructor, ToolTableBlockEntity.TITLE);
                NetworkHooks.openScreen((ServerPlayer)player, provider, pos);
            }
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
    }


}
