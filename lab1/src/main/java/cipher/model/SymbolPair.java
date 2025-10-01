package main.java.cipher.model;

// Класс для хранения пары символов (биграммы)
public class SymbolPair {
    public final char first;   // первый символ пары
    public final char second;  // второй символ пары

    public SymbolPair(char first, char second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "" + first + second; // вывод пары как строки
    }
}