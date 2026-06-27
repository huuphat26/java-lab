package com.devfat.corelab.a1_lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A1 — Bước "Đập": Dùng anonymous class (cách cũ, trước Java 8).
 * Chạy main() để thấy code dài dòng, khó đọc khi dùng anonymous class.
 */
public class BeforeLambda {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("Hoa", "An", "Bình", "Cường", "Dũng");

        // Sắp xếp bằng anonymous class — dài dòng
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        System.out.println("Sorted (anonymous class): " + names);

        // Lọc tên dài hơn 2 ký tự — phải viết loop
        System.out.print("Tên dài hơn 2 ký tự: ");
        for (String name : names) {
            if (name.length() > 2) {
                System.out.print(name + " ");
            }
        }
        System.out.println();
    }
}
