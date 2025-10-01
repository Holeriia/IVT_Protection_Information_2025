package main.java.cipher.util;

import main.java.cipher.matrix.DoubleSquareMatrix;
import main.java.cipher.model.Position;

// Класс для подготовки текста перед шифрованием
public class TextPreprocessor {

    // Преобразует текст в верхний регистр и заменяет специфические символы (Ё→Е, Й→И)
    public static String toUpperAndReplace(String text) {
        return text.toUpperCase()
                .replace("Ё", "Е")
                .replace("Й", "И");
    }

    // Проверяет, что все символы текста присутствуют в левой матрице
    // Если какой-то символ отсутствует, выбрасывает исключение
    public static String validateAndClean(String text, DoubleSquareMatrix matrix) {
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (matrix.getPositionInLeft(c) != null) {
                result.append(c);
            } else {
                throw new IllegalArgumentException(
                        "Ошибка: Символ '" + c + "' не найден в левой матрице. " +
                                "Шифрование невозможно."
                );
            }
        }
        return result.toString();
    }

    // Если длина текста нечётная, добавляет пробел для корректного шифрования
    public static String padIfOdd(String text) {
        if (text.length() % 2 != 0) {
            return text + " ";
        }
        return text;
    }
}
