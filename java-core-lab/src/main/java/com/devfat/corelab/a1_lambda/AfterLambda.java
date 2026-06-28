package com.devfat.corelab.a1_lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A1 — Bước "Vá": Dùng lambda + method reference (Java 8+).
 * So sánh với BeforeLambda.java để thấy code gọn hơn bao nhiêu.
 */
public class AfterLambda {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("Hoa", "An", "Bình", "Cường", "Dũng");

        // Lambda expression thay cho anonymous class
        Collections.sort(names, (o1, o2) -> o1.compareTo(o2));
        System.out.println("Sorted (lambda): " + names);

        // Method reference — gọn hơn nữa
        names.sort(String::compareTo);
        System.out.println("Sorted (method reference): " + names);

        // Lọc bằng Stream + lambda
        System.out.print("Tên dài hơn 2 ký tự: ");
        names.stream()
                .filter(name -> name.length() > 2)
                .forEach(name -> System.out.print(name + " "));
        System.out.println();
    }


}
