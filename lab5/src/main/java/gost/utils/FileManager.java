package gost.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import gost.occasion.AlienExceptions;
import gost.signature.Point;
import gost.signature.SignatureParameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

/**
 * Класс для управления операциями ввода/вывода: чтение/запись файлов
 * сообщений, ключей, подписей и параметров кривой (JSON).
 */
public class FileManager {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Читает параметры эллиптической кривой из JSON-файла в ресурсах.
     * @param digit Длина хэша (256 или 512).
     * @return Объект SignatureParameters.
     */
    public SignatureParameters readParameters(int digit) throws AlienExceptions.IncorrectParametersException {
        String filename = "curve_params_" + digit + ".json";
        URL resource = getClass().getClassLoader().getResource(filename);

        if (resource == null) {
            throw new AlienExceptions.IncorrectParametersException();
        }

        try {
            File file = new File(resource.toURI());
            // Используем Jackson для десериализации JSON в объект Record
            return mapper.readValue(file, SignatureParameters.class);
        } catch (Exception e) {
            throw new AlienExceptions.IncorrectParametersException();
        }
    }

    /**
     * Читает содержимое файла сообщения и возвращает его в виде массива int (беззнаковые байты).
     * @param path Путь к файлу сообщения.
     * @return Массив int, представляющий байты файла.
     */
    public int[] readFileMessage(String path) throws AlienExceptions.FileReadingException {
        try {
            byte[] bytes = Files.readAllBytes(Path.of(path));
            // Преобразуем signed byte в int (0..255)
            return IntStream.range(0, bytes.length)
                    .map(i -> bytes[i] & 0xFF)
                    .toArray();
        } catch (IOException e) {
            throw new AlienExceptions.FileReadingException(path);
        }
    }

    /**
     * Читает секретный ключ d или случайное число k из файла.
     * @param path Путь к файлу ключа.
     * @return BigInteger, содержащий значение ключа.
     */
    public BigInteger readKey(String path) throws AlienExceptions.FileReadingException, AlienExceptions.FileCorruptedException {
        try {
            String content = Files.readString(Path.of(path)).trim();
            // Ключи обычно хранятся в файлах в шестнадцатеричном формате
            return new BigInteger(content, 16);
        } catch (IOException e) {
            throw new AlienExceptions.FileReadingException(path);
        } catch (NumberFormatException e) {
            throw new AlienExceptions.FileCorruptedException(path);
        }
    }

    /**
     * Записывает подпись (r || s) в файл.
     * @param path Путь для записи.
     * @param signature Подпись в виде Hex-строки.
     */
    public void writeSignature(String path, String signature) throws AlienExceptions.FileWritingException {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(signature);
        } catch (IOException e) {
            throw new AlienExceptions.FileWritingException(path);
        }
    }

    /**
     * Записывает публичный ключ Q (x || y) в файл в формате BigInteger.
     * @param path Путь для записи.
     * @param Q Публичный ключ.
     */
    public void writePublicKey(String path, Point Q) throws AlienExceptions.FileWritingException {
        // Мы можем записать его как конкатенацию Hex-строк Xq и Yq или просто как JSON.
        // Для простоты записи в файл - записываем как (X_Q : Y_Q)
        String content = Q.x().toString(16) + "\n" + Q.y().toString(16);
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
        } catch (IOException e) {
            throw new AlienExceptions.FileWritingException(path);
        }
    }

    /**
     * Читает подпись из файла в виде Hex-строки.
     * @param path Путь к файлу подписи.
     * @return Подпись в виде Hex-строки (r || s).
     */
    public String readSignature(String path) throws AlienExceptions.SignatureUnreadableException {
        try {
            // Удаляем пробелы и переводы строк, предполагая, что это Hex-строка
            return Files.readString(Path.of(path)).replaceAll("\\s", "");
        } catch (IOException e) {
            throw new AlienExceptions.SignatureUnreadableException();
        }
    }

    /**
     * Читает публичный ключ Q из файла (две строки: Xq и Yq в Hex).
     * @param path Путь к файлу публичного ключа.
     * @return Точка Q.
     */
    public Point readPublicKey(String path) throws AlienExceptions.FileCorruptedException, AlienExceptions.FileReadingException {
        try {
            var lines = Files.readAllLines(Path.of(path));
            if (lines.size() < 2) {
                throw new AlienExceptions.FileCorruptedException(path);
            }
            // Первая строка - Xq, вторая - Yq
            BigInteger x = new BigInteger(lines.get(0).trim(), 16);
            BigInteger y = new BigInteger(lines.get(1).trim(), 16);
            return new Point(x, y);
        } catch (IOException e) {
            throw new AlienExceptions.FileReadingException(path);
        } catch (NumberFormatException e) {
            throw new AlienExceptions.FileCorruptedException(path);
        }
    }
}