package gost.occasion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Набор специализированных исключений для обработки ошибок I/O и криптографических ошибок.
 */
public class AlienExceptions {
    private final static Logger log = LogManager.getLogger(AlienExceptions.class.getName());

    /**
     * Неверно заданные аргументы или входные данные.
     */
    public static class IllegalArgumentException extends Exception {
        public IllegalArgumentException() {
            super("Неверно заданы аргументы.");
            log.warn("Invalid arguments used.");
        }
    }

    /**
     * Отсутствие файла, куда должна быть записана информация
     */
    public static class DestinationFileNotFoundException extends Exception {
        public DestinationFileNotFoundException() {
            super("Отсутствует файл назначения.");
            log.warn("Missing destination file.");
        }
    }

    /**
     * Если подпись не может быть прочитана или имеет неверный формат
     */
    public static class SignatureUnreadableException extends Exception {
        public SignatureUnreadableException() {
            super("Подпись нечитаема или имеет неверный формат.");
            log.error("The signature is unreadable.");
        }
    }

    /**
     * Неверные или невалидные параметры эллиптической кривой
     */
    public static class IncorrectParametersException extends Exception {
        public IncorrectParametersException() {
            super("Параметры эллиптической кривой неверны.");
            log.error("Curve parameters are incorrect.");
        }
    }

    /**
     * Общая ошибка ввода/вывода (I/O).
     */
    public static class IOException extends Exception {
        public IOException(String path) {
            super("Ошибка ввода/вывода. Проверьте путь: " + path);
            log.error("IO error: " + path);
        }
    }

    /**
     * Содержимое файла повреждено или имеет неожиданный формат.
     */
    public static class FileCorruptedException extends Exception {
        public FileCorruptedException(String path) {
            super("Файл повреждён или имеет неверный формат: " + path);
            log.error("The file is corrupted: " + path);
        }
    }

    /**
     * Ошибка чтения данных из файла.
     */
    public static class FileReadingException extends Exception {
        public FileReadingException(String path) {
            super("Ошибка чтения файла: " + path);
            log.error("Read error: " + path);
        }
    }

    /**
     * Ошибка записи данных в файл.
     */
    public static class FileWritingException extends Exception {
        public FileWritingException(String path) {
            super("Ошибка записи файла: " + path);
            log.error("Write error: " + path);
        }
    }

    /**
     * Ключ (например, d)
     * не соответствует заданным криптографическим требованиям (например, диапазон).
     */
    public static class InvalidKeyException extends Exception {
        public InvalidKeyException(String message) {
            super(message);
        }
    }
}