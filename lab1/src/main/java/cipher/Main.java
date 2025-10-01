package main.java.cipher;

import main.java.cipher.matrix.DoubleSquareMatrix;
import main.java.cipher.matrix.MatrixFactory;
import main.java.cipher.util.TextPreprocessor;

// Основной класс программы — точка входа
public class Main {

    // Обрабатывает одно сообщение:
    // 1. Приводит текст к верхнему регистру и заменяет специфические символы
    // 2. Проверяет, что все символы есть в левой матрице
    // 3. Добавляет пробел, если длина текста нечётная
    // 4. Шифрует текст
    // 5. Выводит подготовленный и зашифрованный текст
    private static void processMessage(String message, DoubleSquareCipher cipher, DoubleSquareMatrix matrix, int index) {
        System.out.println("\n--- СООБЩЕНИЕ " + index + " ---");
        System.out.println("Исходный текст: " + message);

        try {
            String preprocessed = TextPreprocessor.toUpperAndReplace(message);
            String validated = TextPreprocessor.validateAndClean(preprocessed, matrix);
            String cleaned = TextPreprocessor.padIfOdd(validated);
            String encrypted = cipher.encrypt(cleaned);

            System.out.println("Подготовленный текст: " + cleaned);
            System.out.println("Зашифрованный текст: " + encrypted);

        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка обработки: " + e.getMessage());
        }
    }

    // Главный метод программы
    // 1. Создаёт стандартные матрицы для кириллицы
    // 2. Создаёт объект шифратора
    // 3. Определяет массив сообщений
    // 4. Выводит матрицы и обрабатывает каждое сообщение
    public static void main(String[] args) {
        var matrix = MatrixFactory.createDefaultCyrillicMatrix();
        var cipher = new DoubleSquareCipher(matrix);

        String[] messages = {
                "Прилетаю шестого",
                "Нижегородский государственный технический университет",
                "Серова Валерия Павловна"
        };

        System.out.println("Матрицы:");
        matrix.printMatricesSideBySide();

        for (int i = 0; i < messages.length; i++) {
            processMessage(messages[i], cipher, matrix, i + 1);
        }
    }
}
