package main.java.cipher.matrix;

import main.java.cipher.model.Position;
import java.util.HashMap;
import java.util.Map;

public class DoubleSquareMatrix {
    private final char[][] left;   // левая матрица
    private final char[][] right;  // правая матрица
    private final Map<Character, Position> positions; // карта всех символов для быстрого поиска

    // Конструктор принимает левую и правую матрицы и инициализирует позиции символов
    public DoubleSquareMatrix(char[][] left, char[][] right) {
        this.left = left;
        this.right = right;
        this.positions = new HashMap<>();
        initPositions(left, true);
        initPositions(right, false);
    }

    // Заполняет карту позиций для каждой буквы в матрице
    private void initPositions(char[][] matrix, boolean isLeft) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                positions.put(matrix[row][col], new Position(row, col, isLeft));
            }
        }
    }

    // Возвращает позицию символа в любой матрице
    public Position getPosition(char c) {
        return positions.get(c);
    }

    // Ищет символ только в левой матрице
    public Position getPositionInLeft(char c) {
        for (int row = 0; row < left.length; row++) {
            for (int col = 0; col < left[row].length; col++) {
                if (left[row][col] == c) {
                    return new Position(row, col, true);
                }
            }
        }
        return null;
    }

    // Ищет символ только в правой матрице
    public Position getPositionInRight(char c) {
        for (int row = 0; row < right.length; row++) {
            for (int col = 0; col < right[row].length; col++) {
                if (right[row][col] == c) {
                    return new Position(row, col, false);
                }
            }
        }
        return null;
    }

    // Возвращает символ по позиции (из левой или правой матрицы)
    public char getChar(Position pos) {
        return (pos.isLeft ? left : right)[pos.row][pos.col];
    }

    // Выводит обе матрицы рядом для наглядности
    public void printMatricesSideBySide() {
        int rows = Math.max(left.length, right.length);

        for (int row = 0; row < rows; row++) {
            StringBuilder line = new StringBuilder();

            if (row < left.length) {
                for (int col = 0; col < left[row].length; col++) {
                    line.append(left[row][col]).append(' ');
                }
            } else {
                line.append("  ".repeat(left[0].length));
            }

            line.append(" ||  ");

            if (row < right.length) {
                for (int col = 0; col < right[row].length; col++) {
                    line.append(right[row][col]).append(' ');
                }
            }

            System.out.println(line.toString().trim());
        }
    }
}
