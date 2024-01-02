/**
 * SiC B2B2C Shop 使用 木兰公共许可证,第2版（Mulan PubL v2） 开源协议，请遵守相关条款，或者联系sicheng.net获取商用授权书。
 * Copyright (c) 2016 SiCheng.Net
 * SiC B2B2C Shop is licensed under Mulan PubL v2.
 * You can use this software according to the terms and conditions of the Mulan PubL v2.
 * You may obtain a copy of Mulan PubL v2 at:
 *          http://license.coscl.org.cn/MulanPubL-2.0
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PubL v2 for more details.
 */
package com.sicheng.common.security.keccak;

/**
 * A KeccakState with permutation width of 400 bits and lane length of 16 bits.
 */
final class KeccakState400 extends KeccakShortState {

    /*
     * The length in bits of each "lane" within the Keccak permutation state
     * array.
     */
    private static final byte LANE_LENGTH = 16;

    private static final int LANE_MASK = 0xffff;

    private static final byte NUMBER_OF_ROUNDS_PER_PERMUTATION = 20;

    private static final int[] ROUND_CONSTANTS_FOR_WIDTH_400;

    static {
        ROUND_CONSTANTS_FOR_WIDTH_400 = new int[]{
                1,
                32898,
                32906,
                32768,
                32907,
                1,
                32897,
                32777,
                138,
                136,
                32777,
                10,
                32907,
                139,
                32905,
                32771,
                32770,
                128,
                32778,
                10
        };
    }

    private static final byte[][] ROTATION_CONSTANTS_FOR_WIDTH_400;

    static {
        byte[][] rotOffsets = new byte[5][5];
        rotOffsets[0] = new byte[]{
                (byte) 0,
                (byte) 4,
                (byte) 3,
                (byte) 9,
                (byte) 2};
        rotOffsets[1] = new byte[]{
                (byte) 1,
                (byte) 12,
                (byte) 10,
                (byte) 13,
                (byte) 2};
        rotOffsets[2] = new byte[]{
                (byte) 14,
                (byte) 6,
                (byte) 11,
                (byte) 15,
                (byte) 13};
        rotOffsets[3] = new byte[]{
                (byte) 12,
                (byte) 7,
                (byte) 9,
                (byte) 5,
                (byte) 8};
        rotOffsets[4] = new byte[]{
                (byte) 11,
                (byte) 4,
                (byte) 7,
                (byte) 8,
                (byte) 14
        };
        ROTATION_CONSTANTS_FOR_WIDTH_400 = rotOffsets;
    }

    @Override
    final byte getLaneLengthInBits() {
        return LANE_LENGTH;
    }

    @Override
    final byte getNumberOfRoundsPerPermutation() {
        return NUMBER_OF_ROUNDS_PER_PERMUTATION;
    }

    @Override
    final int getLaneMask() {
        return LANE_MASK;
    }

    @Override
    byte getRotationConstantForLane(int x, int y) {
        assert x >= 0 && x < 5;
        assert y >= 0 && y < 5;
        return ROTATION_CONSTANTS_FOR_WIDTH_400[x][y];
    }

    @Override
    int getRoundConstantForRound(int roundIndex) {
        assert roundIndex >= 0 && roundIndex < NUMBER_OF_ROUNDS_PER_PERMUTATION;
        return ROUND_CONSTANTS_FOR_WIDTH_400[roundIndex];
    }
}
