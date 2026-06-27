package com.devfat.corelab.a2_streamapi;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A2 — Bước "Vá": Dùng Stream API (filter/map/collect/groupingBy).
 * So sánh với BeforeStream.java — code ngắn, khai báo ý định rõ ràng.
 */
public class AfterStream {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Lọc số chẵn bằng Stream
        List<Integer> evenNumbers = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("Số chẵn: " + evenNumbers);

        // Tính tổng bình phương số chẵn — 1 pipeline
        int sumOfSquares = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(n -> n * n)
                .sum();
        System.out.println("Tổng bình phương số chẵn: " + sumOfSquares);

        // Nhóm tên theo chữ cái đầu — groupingBy
        List<String> names = Arrays.asList("An", "Anh", "Bình", "Ba", "Cường", "Chi");
        Map<Character, List<String>> grouped = names.stream()
                .collect(Collectors.groupingBy(name -> name.charAt(0)));
        System.out.println("Nhóm theo chữ cái đầu: " + grouped);
    }
}
