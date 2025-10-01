package main.java.cipher.model;

// Класс для хранения позиции символа в матрице
public class Position {
    public final int row;       // номер строки
    public final int col;       // номер столбца
    public final boolean isLeft; // принадлежность к левой или правой матрице

    public Position(int row, int col, boolean isLeft) {
        this.row = row;
        this.col = col;
        this.isLeft = isLeft;
    }
}