package com.cjhxfund.step.util;

public class ByteArrayBuffer {

    private ByteArrayBuffer() {
    }

    public static byte[] toByteArray(byte[] array, int offset, int len) {
        if (array == null) {
            throw new IllegalArgumentException("array may not be null");
        }

        if (len < 0 || array.length < offset + len || offset < 0) {
            throw new IndexOutOfBoundsException();
        }

        byte[] b = new byte[len];
        System.arraycopy(array, offset, b, 0, len);
        return b;
    }

    public static byte[] toByteArray(byte[] array1, int offset1, int len1, byte[] array2, int offset2, int len2) {
        if (array1 == null || array2 == null) {
            throw new IllegalArgumentException("array may not be null");
        }

        if (len1 < 0 || len2 < 0 || offset1 < 0 || offset2 < 0 || array1.length < offset1 + len1
                || array2.length < offset2 + len2) {
            throw new IndexOutOfBoundsException();
        }

        byte[] b = new byte[len1 + len2];

        System.arraycopy(array1, offset1, b, 0, len1);
        System.arraycopy(array2, offset1, b, len1, len2);

        return b;
    }

    public static int fixByteOffset(byte[] arrays, byte[] subarrays) {
        if (arrays == null || subarrays == null) {
            throw new IllegalArgumentException("array may not be null");
        }

        if (arrays.length == 0 || subarrays.length == 0) {
            throw new IllegalArgumentException("array may not be zero");
        }

        for (int j = 0; j < arrays.length - subarrays.length + 1; j++) {
            for (int i = 0; i < subarrays.length; i++) {
                if (arrays[j + i] != subarrays[i]) {
                    break;
                }

                if (i == subarrays.length - 1) {
                    return j;
                }
            }
        }

        return -1;
    }

    public static int fixByteLastOffset(byte[] arrays, byte[] subarrays) {
        if (arrays == null || subarrays == null) {
            throw new IllegalArgumentException("array may not be null");
        }

        if (arrays.length == 0 || subarrays.length == 0) {
            throw new IllegalArgumentException("array may not be zero");
        }

        for (int j = arrays.length - subarrays.length - 1; j >= 0; j--) {
            for (int i = 0; i < subarrays.length; i++) {
                if (arrays[j + i] != subarrays[i]) {
                    break;
                }

                if (i == subarrays.length - 1) {
                    return j;
                }
            }
        }

        return -1;
    }

}
