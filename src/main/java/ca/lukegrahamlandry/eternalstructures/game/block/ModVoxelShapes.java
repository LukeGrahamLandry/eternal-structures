package ca.lukegrahamlandry.eternalstructures.game.block;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ModVoxelShapes {
    public static final VoxelShape POT = Stream.of(
            Block.box(3, 1, 3, 13, 11, 13),
            Block.box(4, 11, 4, 12, 13, 12),
            Block.box(3.4999999999999982, 0, 3.4999999999999982, 12.499999999999998, 1, 12.499999999999998)
            ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    public static final VoxelShape CHEST = Stream.of(
            Stream.of(
                    Block.box(-1, 6, 2.4999999999999982, 1, 12, 14.499999999999998),
                    Block.box(1, 6, 2.4999999999999982, 15, 12, 14.499999999999998),
                    Block.box(15, 6, 2.4999999999999982, 17, 12, 14.499999999999998),
                    Block.box(4, 3, 2, 12, 10, 3),
                    Block.box(12, 12, 8.499999999999998, 20, 20, 8.499999999999998),
                    Block.box(12, 12, 8.499999999999998, 20, 20, 8.499999999999998),
                    Block.box(4, 12, 4.499999999999998, 12, 20, 4.499999999999998),
                    Block.box(4, 12, 4.499999999999998, 12, 20, 4.499999999999998)
            ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get(),
            Block.box(0, 0, 3, 16, 6, 14)
            ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    public static VoxelShape rotatedChest(Direction facing){
        if (facing == Direction.EAST || facing == Direction.NORTH) return rotateAroundVertical(Direction.NORTH, facing, CHEST);
        else return rotateAroundVertical(Direction.WEST, facing, CHEST);
    }

    public static VoxelShape getSpikeShape(boolean isOut, Direction facing){
        return isOut ? SPIKES_OUT_SHAPE.get(facing) : SPIKES_IN_SHAPE.get(facing);
    }

    private static final Map<Direction, VoxelShape> SPIKES_IN_SHAPE = new HashMap<>();
    private static final Map<Direction, VoxelShape> SPIKES_OUT_SHAPE = new HashMap<>();

    static {
        VoxelShape in = Stream.of(
                Block.box(10, 0, 2, 14, 1, 6),
                Block.box(10, 0, 10, 14, 1, 14),
                Block.box(2, 0, 10, 6, 1, 14),
                Block.box(2, 0, 2, 6, 1, 6)
        ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

        VoxelShape out = Stream.of(
                Block.box(10, 0, 2, 14, 6, 6),
                Block.box(10, 0, 10, 14, 6, 14),
                Block.box(2, 0, 10, 6, 6, 14),
                Block.box(2, 0, 2, 6, 6, 6)
        ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

        rotations(out, SPIKES_OUT_SHAPE);
        rotations(in, SPIKES_IN_SHAPE);
    }

    public static void rotations(VoxelShape shape, Map<Direction, VoxelShape> allShapes){
        allShapes.put(Direction.UP, shape);
        allShapes.put(Direction.DOWN, flipUpDown(shape));

        allShapes.put(Direction.EAST, rotateAroundVertical(Direction.EAST, Direction.SOUTH, rotateToSide(shape)));

        allShapes.put(Direction.NORTH, rotateAroundVertical(Direction.EAST, Direction.NORTH, rotateToSide(shape)));
        allShapes.put(Direction.WEST, rotateAroundVertical(Direction.NORTH, Direction.WEST, rotateToSide(shape)));
        allShapes.put(Direction.SOUTH, rotateAroundVertical(Direction.NORTH, Direction.SOUTH, rotateToSide(shape)));
    }

    public static VoxelShape rotateAroundVertical(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        int times = (to.ordinal() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(buffer[1], VoxelShapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    public static VoxelShape flipUpDown(VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(buffer[1], VoxelShapes.box(maxZ, 1 - minY, minX, minZ, 1 - maxY, maxX)));
        buffer[0] = buffer[1];
        buffer[1] = VoxelShapes.empty();

        return buffer[0];
    }

    public static VoxelShape rotateToSide(VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(buffer[1], VoxelShapes.box(minY, minZ, minX, maxY, maxZ, maxX)));
        buffer[0] = buffer[1];
        buffer[1] = VoxelShapes.empty();

        return buffer[0];
    }
}
