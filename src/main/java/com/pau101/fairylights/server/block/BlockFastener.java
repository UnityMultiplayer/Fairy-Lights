package com.pau101.fairylights.server.block;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.ServerEventHandler;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorBlock;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public final class BlockFastener extends DirectionalBlock {
	public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

	private static final VoxelShape NORTH_AABB = Block.makeCuboidShape(6.0D, 6.0D, 12.0D, 10.0D, 10.0D, 16.0D);

	private static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 4.0D);

	private static final VoxelShape WEST_AABB = Block.makeCuboidShape(12.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

	private static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 4.0D, 10.0D, 10.0D);

	private static final VoxelShape DOWN_AABB = Block.makeCuboidShape(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D);

	private static final VoxelShape UP_AABB = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D);

	public BlockFastener(Block.Properties properties) {
		super(properties);
		setDefaultState(stateContainer.getBaseState()
			.with(FACING, Direction.NORTH)
			.with(TRIGGERED, false)
		);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, TRIGGERED);
	}

	@Override
	public BlockState rotate(final BlockState state, final Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(final BlockState state, final Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}

	@Override
	public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext context) {
		switch (state.get(FACING)) {
			case NORTH:
				return NORTH_AABB;
			case SOUTH:
				return SOUTH_AABB;
			case WEST:
				return WEST_AABB;
			case EAST:
				return EAST_AABB;
			case DOWN:
				return DOWN_AABB;
			case UP:
			default:
				return UP_AABB;
		}
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return new BlockEntityFastener();
	}

	@Override
	public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity entity = world.getTileEntity(pos);
			if (entity instanceof BlockEntityFastener) {
				entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.dropItems(world, pos));
			}
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean isValidPosition(final BlockState state, final IWorldReader world, final BlockPos pos) {
		final Direction facing = state.get(FACING);
		final BlockPos attachedPos = pos.offset(facing.getOpposite());
		return world.getBlockState(attachedPos).func_224755_d(world, attachedPos, facing);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		BlockState result = this.getDefaultState();
		final IWorldReader world = context.getWorld();
		final BlockPos pos = context.getPos();
		for (final Direction dir : context.getNearestLookingDirections()) {
			result = result.with(FACING, dir.getOpposite());
			if (result.isValidPosition(world, pos)) {
				return result;
			}
		}
		return null;
	}

	@Override
	public BlockState updatePostPlacement(final BlockState state, final Direction facing, final BlockState facingState, final IWorld world, final BlockPos currentPos, final BlockPos facingPos) {
		if (facing.getOpposite() == state.get(FACING) && !state.isValidPosition(world, currentPos)) {
			return Blocks.AIR.getDefaultState();
		}
		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
		if (oldState.getBlock() != state.getBlock()) {
			if (world.isBlockPowered(pos.offset(state.get(FACING).getOpposite()))) {
				world.setBlockState(pos, state.with(TRIGGERED, true), 3);
			}
		}
	}

	@Override
	public void neighborChanged(final BlockState state, final World world, final BlockPos pos, final Block blockIn, final BlockPos fromPos, final boolean isMoving) {
		if (state.isValidPosition(world, pos)) {
			boolean receivingPower = world.isBlockPowered(pos);
			boolean isPowered = state.get(TRIGGERED);
			if (receivingPower && !isPowered) {
				world.getPendingBlockTicks().scheduleTick(pos, this, tickRate(world));
				world.setBlockState(pos, state.with(TRIGGERED, true), 4);
			} else if (!receivingPower && isPowered) {
				world.setBlockState(pos, state.with(TRIGGERED, false), 4);
			}
		} else {
			TileEntity entity = world.getTileEntity(pos);
			spawnDrops(state, world, pos, entity);
			world.removeBlock(pos, false);
		}
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntity entity = world.getTileEntity(pos);
		if (!(entity instanceof BlockEntityFastener)) {
			return super.getComparatorInputOverride(state, world, pos);
		}
		return entity.getCapability(CapabilityHandler.FASTENER_CAP).map(f -> f.getConnections().entrySet().stream()).orElse(Stream.empty())
			.filter(e -> {
				Connection connection = e.getValue();
				return connection.getType() == ConnectionType.HANGING_LIGHTS && connection.getDestination().isLoaded(world);
			})
			.flatMap(e -> {
				Connection connection = e.getValue();
				if (connection.isOrigin()) {
					return Stream.of(connection);
				}
				BlockPos to = connection.getDestination().get(world).getPos();
				TileEntity toEntity = world.getTileEntity(to);
				if (!(toEntity instanceof BlockEntityFastener)) {
					return Stream.empty();
				}
				return toEntity.getCapability(CapabilityHandler.FASTENER_CAP)
					.map(toFastener -> Stream.of(toFastener.getConnections().get(e.getKey())))
					.orElse(Stream.empty());
			})
			.mapToInt(c -> (int) Math.ceil(((ConnectionHangingLights) c).getJingleProgress() * 15))
			.max().orElse(0);
	}

	@Override
	public int tickRate(IWorldReader world) {
		return 2;
	}

	@Override
	public void tick(final BlockState state, final World world, final BlockPos pos, final Random random) {
		if (!world.isRemote) {
			jingle(world, pos);
		}
	}

	private boolean jingle(World world, BlockPos pos) {
		TileEntity entity = world.getTileEntity(pos);
		if (!(entity instanceof BlockEntityFastener)) {
			return false;
		}
		return entity.getCapability(CapabilityHandler.FASTENER_CAP).map(f -> f.getConnections().entrySet().stream()).orElse(Stream.empty())
			.filter(e -> {
				Connection connection = e.getValue();
				return connection.getType() == ConnectionType.HANGING_LIGHTS && connection.getDestination().isLoaded(world);
			})
			.flatMap(e -> {
				Connection connection = e.getValue();
				BlockPos to = connection.getDestination().get(world).getPos();
				if (!connection.isDestination(new FastenerAccessorBlock(to))) {
					return Stream.empty();
				}
				if (!world.getBlockState(to).get(TRIGGERED)) {
					return Stream.empty();
				}
				if (connection.isOrigin()) {
					return Stream.of(connection);
				}
				TileEntity toEntity = world.getTileEntity(to);
				if (!(toEntity instanceof BlockEntityFastener)) {
					return Stream.empty();
				}
				return toEntity.getCapability(CapabilityHandler.FASTENER_CAP).map(f -> Stream.of(f.getConnections().get(e.getKey()))).orElse(Stream.empty());
			})
			.anyMatch(connection -> {
				ConnectionHangingLights logic = (ConnectionHangingLights) connection;
				return logic.canCurrentlyPlayAJingle() && ServerEventHandler.tryJingle(world, connection, logic, FairyLights.randomJingles);
			});
	}

	public Vec3d getOffset(Direction facing, float offset) {
		return getFastenerOffset(facing, offset);
	}

	public static Vec3d getFastenerOffset(Direction facing, float offset) {
		double x = offset, y = offset, z = offset;
		switch (facing) {
			case DOWN:
				y += 0.75F;
			case UP:
				x += 0.375F;
				z += 0.375F;
				break;
			case WEST:
				x += 0.75F;
			case EAST:
				z += 0.375F;
				y += 0.375F;
				break;
			case NORTH:
				z += 0.75F;
			case SOUTH:
				x += 0.375F;
				y += 0.375F;
		}
		return new Vec3d(x, y, z);
	}
}
