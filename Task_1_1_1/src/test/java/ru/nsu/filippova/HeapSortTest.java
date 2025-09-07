package ru.nsu.filippova;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HeapSortTest {
    @Test
    void testSimple() {
        int[] arr = {3, 1, 2};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{1,2,3}, arr);
    }

    @Test
    void testEmpty() {
        int[] arr = {};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testOneElement() {
        int[] arr = {42};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    void testReverse() {
        int[] arr = {5,4,3,2,1};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{1,2,3,4,5}, arr);
    }
}
