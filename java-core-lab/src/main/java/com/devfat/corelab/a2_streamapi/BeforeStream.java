package com.devfat.corelab.a2_streamapi;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * A2 — Bước "Đập": Dùng for-loop thường để filter/transform.
 * Chạy main() để thấy code dài, nhiều biến tạm.
 */
public class BeforeStream {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Lọc số chẵn bằng for-loop
        List<Integer> evenNumbers = new ArrayList<>();
        for (Integer n : numbers) {
            if (n % 2 == 0) {
                evenNumbers.add(n);
            }
        }
        System.out.println("Số chẵn: " + evenNumbers);

        // Tính tổng bình phương số chẵn
        int sumOfSquares = 0;
        for (Integer n : evenNumbers) {
            sumOfSquares += n * n;
        }
        System.out.println("Tổng bình phương số chẵn: " + sumOfSquares);

        // Nhóm tên theo chữ cái đầu — rất dài với for-loop
        List<String> names = Arrays.asList("An", "Anh", "Bình", "Ba", "Cường", "Chi");
        java.util.Map<Character, List<String>> grouped = new java.util.HashMap<>();
        for (String name : names) {
            char firstChar = name.charAt(0);
            grouped.computeIfAbsent(firstChar, k -> new ArrayList<>()).add(name);
        }
        System.out.println("Nhóm theo chữ cái đầu: " + grouped);
    }
}
