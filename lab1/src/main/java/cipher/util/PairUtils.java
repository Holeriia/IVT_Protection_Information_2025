package main.java.cipher.util;

import main.java.cipher.model.SymbolPair;
import java.util.ArrayList;
import java.util.List;

// Утилита для работы с парами символов (биграммами)
public class PairUtils {

    // Разбивает строку на пары символов.
    // Если два одинаковых символа идут подряд или один символ остаётся в конце, добавляет пробел
    public static List<SymbolPair> toPairs(String text) {
        List<SymbolPair> pairs = new ArrayList<>();
        int i = 0;
        while (i < text.length()) {
            char first = text.charAt(i);
            char second;

            if (i + 1 < text.length()) {
                second = text.charAt(i + 1);
                if (first == second) {
                    second = ' ';
                    i++;
                } else {
                    i += 2;
                }
            } else {
                second = ' ';
                i++;
            }
            pairs.add(new SymbolPair(first, second));
        }
        return pairs;
    }
}
