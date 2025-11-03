package gost.manager;

import gost.occasion.AlienExceptions;
import gost.signature.*;
import gost.stribog.Hash;
import gost.utils.FileManager;

import java.math.BigInteger;

/**
 * Класс-фасад, управляющий всем криптографическим процессом (ECC и Stribog).
 * Обеспечивает единый интерфейс для GUI.
 */
public class MessageManager {
    private final FileManager fileManager = new FileManager();
    private SignatureParameters parameters;
    private final int hashLength;

    /**
     * Инициализирует менеджер, загружая параметры кривой.
     * @param hashLength Длина хэша (256 или 512).
     */
    public MessageManager(int hashLength) throws AlienExceptions.IncorrectParametersException {
        this.hashLength = hashLength;
        // Загрузка параметров кривой из ресурсов
        this.parameters = fileManager.readParameters(hashLength);
    }

    // --- ГЕНЕРАЦИЯ КЛЮЧЕЙ ---

    /**
     * Генерирует публичный ключ Q по заданному секретному ключу d и сохраняет Q в файл.
     * @param secretKeyPath Путь к файлу с секретным ключом d.
     * @param publicKeyPath Путь для записи публичного ключа Q.
     */
    public void generatePublicKey(String secretKeyPath, String publicKeyPath) throws Exception {
        // 1. Читаем секретный ключ d
        BigInteger d = fileManager.readKey(secretKeyPath);

        // 2. Проверяем d: 0 < d < q
        if (d.compareTo(BigInteger.ZERO) <= 0 || d.compareTo(parameters.q()) >= 0) {
            throw new AlienExceptions.InvalidKeyException("Секретный ключ d вне диапазона (0, q).");
        }

        // 3. Вычисляем публичный ключ Q = d * P
        EllipticCurve curve = new EllipticCurve(parameters);
        Point Q = curve.scalar(d, parameters.P());

        // 4. Записываем Q в файл
        fileManager.writePublicKey(publicKeyPath, Q);
    }

    // --- ФОРМИРОВАНИЕ ПОДПИСИ ---

    /**
     * Формирует ЭЦП для файла сообщения и записывает ее в файл.
     * @param messagePath Путь к исходному файлу сообщения.
     * @param secretKeyPath Путь к файлу секретного ключа d.
     * @param signaturePath Путь для записи подписи.
     */
    public void signFile(String messagePath, String secretKeyPath, String signaturePath) throws Exception {
        // 1. Хэшируем сообщение
        int[] messageBytes = fileManager.readFileMessage(messagePath);
        Hash stribog = new Hash(hashLength);
        BigInteger messageHash = stribog.getHash(messageBytes);

        // 2. Читаем секретный ключ d
        BigInteger d = fileManager.readKey(secretKeyPath);

        // 3. Формируем подпись (r || s)
        Sign signer = new Sign();
        String signature = signer.signing(messageHash, d, parameters);

        // 4. Записываем подпись в файл
        fileManager.writeSignature(signaturePath, signature);
    }

    // --- ПРОВЕРКА ПОДПИСИ ---

    /**
     * Проверяет ЭЦП для файла сообщения.
     * @param messagePath Путь к исходному файлу сообщения.
     * @param publicKeyPath Путь к файлу публичного ключа Q.
     * @param signaturePath Путь к файлу подписи.
     * @return true, если подпись верна.
     */
    public boolean verifyFile(String messagePath, String publicKeyPath, String signaturePath) throws Exception {
        // 1. Хэшируем сообщение
        int[] messageBytes = fileManager.readFileMessage(messagePath);
        Hash stribog = new Hash(hashLength);
        BigInteger messageHash = stribog.getHash(messageBytes);

        // 2. Читаем публичный ключ Q и подпись (r || s)
        Point Q = fileManager.readPublicKey(publicKeyPath);
        String signature = fileManager.readSignature(signaturePath);

        // 3. Проверяем подпись
        Verify verifier = new Verify();
        return verifier.check(signature, Q, messageHash, parameters);
    }
}