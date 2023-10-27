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

    private static final VoxelShape SUMMON = makeSummonShape();

    // ModUtils plugin I used to use seems broken? this is made with VoxelShapes Generator instead.
    static VoxelShape makeSummonShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.125, 0.25, 0.125, 0.875, 0.4375, 0.875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.6875, 0, 1, 0.8125, 1), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.4375, 0.0625, 0.9375, 0.6875, 0.9375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.1875, 1, 0.75, 0.6875, 1), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.75, 0.5625, 0.5625, 1.25, 1.0625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.5, 0.8437499999999997, 0.7625, 0.8125000000000002, 1.3437500000000004, 0.825), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.4375, 0.8124999999999992, 0.825, 0.8125000000000002, 1.375, 0.825), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.8750000000000002, 0.8437499999999997, 0.7625, 1.1875000000000002, 1.3437500000000004, 0.825), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.8750000000000002, 0.8124999999999992, 0.825, 1.2500000000000002, 1.375, 0.825), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.8125000000000002, 0.8437499999999997, 0.7625, 0.8750000000000002, 1.3437500000000004, 0.825), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.8125000000000002, 0.8124999999999992, 0.825, 0.8750000000000002, 1.375, 0.825), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.6893846056004403, 0.8125, 0.8231881294789272, 1.0643846056004405, 1.0625, 0.8231881294789272), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.6893846056004403, 0.8125, 0.8231881294789272, 1.0643846056004405, 1.0625, 0.8231881294789272), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(1, 0.5, 0.0625, 1, 0.6875, 0.75), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 1.1875, 0.7625, 0.37499999999999994, 1.4375, 0.8875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 1.3125, 0.7625, 0.37499999999999994, 1.4375, 0.8875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.2962670289546422, 1.4375, 0.7919722005339951, 0.2962670289546422, 1.5, 0.8544722005339951), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.8125, 0.5625, 0.5625, 1.25, 1.0625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(-1, 0.0125, -1, 2, 0.0125, 2), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.1875, 0, 0.75, 0.6875, 0), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.375, 0, 0.4375, 0.8125, 0.4375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0, 0, 1, 0.125, 1), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(-0.4375, 0.0125, -0.4375, 1.4375, 0.0125, 1.4375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(-0.625, 0.0625, -0.3312499999999998, 0.125, 0.0625, -0.14375000000000016), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(-0.6437499999999998, 0.0625, 0, -0.45625000000000016, 0.0625, 1), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(-0.3312499999999998, 0.0625, 0.875, -0.14375000000000016, 0.0625, 1.625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(1.1437500000000003, 0.0625, 0.875, 1.3312499999999998, 0.0625, 1.625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(1.4562500000000003, 0.0625, 0, 1.6437499999999998, 0.0625, 1), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.875, 0.0625, -0.3312499999999998, 1.625, 0.0625, -0.14375000000000016), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.0625, -0.6437499999999998, 1, 0.0625, -0.45625000000000016), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.0625, 1.4187500000000002, 1, 0.0625, 1.6062499999999997), IBooleanFunction.OR);

        return shape;
    }

    public static VoxelShape rotatedChest(Direction facing){
        if (facing == Direction.EAST || facing == Direction.NORTH) return rotateAroundVertical(Direction.NORTH, facing, CHEST);
        else return rotateAroundVertical(Direction.WEST, facing, CHEST);
    }

    public static VoxelShape rotatedSummon(Direction facing){
        if (facing == Direction.EAST || facing == Direction.NORTH) return rotateAroundVertical(Direction.NORTH, facing, SUMMON);
        else return rotateAroundVertical(Direction.WEST, facing, SUMMON);
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
