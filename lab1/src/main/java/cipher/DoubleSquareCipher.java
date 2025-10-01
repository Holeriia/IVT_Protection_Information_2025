package main.java.cipher;

import main.java.cipher.matrix.DoubleSquareMatrix;
import main.java.cipher.model.Position;
import main.java.cipher.model.SymbolPair;
import main.java.cipher.util.PairUtils;
import java.util.List;

// Класс, реализующий шифр двойного квадрата Уитстона
public class DoubleSquareCipher {

    private final DoubleSquareMatrix matrix; // матрицы для шифрования

    // Конструктор принимает матрицы
    public DoubleSquareCipher(DoubleSquareMatrix matrix) {
        this.matrix = matrix;
    }

    // Шифрует текст
    // 1. Разбивает текст на пары символов
    // 2. Шифрует каждую пару
    // 3. Объединяет результат в строку
    public String encrypt(String text) {
        List<SymbolPair> pairs = PairUtils.toPairs(text);
        StringBuilder sb = new StringBuilder();

        for (SymbolPair pair : pairs) {
            sb.append(encryptPair(pair));
        }

        return sb.toString();
    }

    // Шифрует одну пару символов (биграмму)
    // Применяет правила шифра: если символы в одной строке — обмен столбцов, иначе — перекрёстный выбор
    private String encryptPair(SymbolPair pair) {
        Position pos1 = matrix.getPositionInLeft(pair.first);
        Position pos2 = matrix.getPositionInRight(pair.second);

        char C1, C2;

        if (pos1.row == pos2.row) {
            C1 = matrix.getChar(new Position(pos1.row, pos2.col, true));
            C2 = matrix.getChar(new Position(pos2.row, pos1.col, false));
        } else {
            C1 = matrix.getChar(new Position(pos1.row, pos2.col, false));
            C2 = matrix.getChar(new Position(pos2.row, pos1.col, true));
        }

        return "" + C1 + C2;
    }
}
