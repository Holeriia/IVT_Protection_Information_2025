package gost.gui;

import gost.manager.MessageManager;
import gost.occasion.AlienExceptions;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

/**
 * Главный класс GUI, управляющий состоянием и действиями (Контроллер).
 */
public class MainApp extends JFrame {

    private final MessageManager manager256;
    private final MessageManager manager512;
    private final AppLogger appLogger;

    private GuiComponents components; // Хранит все GUI элементы

    // Менеджер состояний
    private boolean isKeyGenerated = false;
    private boolean isSigned = false;

    public MainApp() throws AlienExceptions.IncorrectParametersException {
        // Инициализация крипто-менеджеров
        manager256 = new MessageManager(256);
        manager512 = new MessageManager(512);

        setTitle("ГОСТ Р 34.10-2018 (ЭЦП)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10));

        // Инициализация логгера
        appLogger = new AppLogger();

        // Инициализация GUI через билдер, передавая ему ссылки на наши методы
        GuiBuilder builder = new GuiBuilder(
                this::resetStateOnChange,
                this::resetPathsAction,
                this::browseFile,
                this::generateKeyAction,
                this::signAction,
                this::verifyAction
        );

        // Билдер создает все компоненты и добавляет их на фрейм
        components = builder.build(this, appLogger);

        // Установка начальных состояний
        updateButtonStates();
        resetPathsAction(); // Устанавливаем пути при старте

        appLogger.log("Приложение запущено. Рабочая директория для файлов: /data", false);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Логика Управления Состоянием ---

    public void updateButtonStates() {
        if (components.btnGenerateKey != null) components.btnGenerateKey.setEnabled(true);
        if (components.btnSign != null) components.btnSign.setEnabled(isKeyGenerated);
        if (components.btnVerify != null) components.btnVerify.setEnabled(isSigned);
    }

    // --- Обработчики Действий (Action Handlers) ---

    public void resetPathsAction() {
        String suffix = components.rb256.isSelected() ? "256" : "512";

        components.tfMessageFile.setText("data/messages/message.txt");
        components.tfSecretKeyFile.setText("data/private_keys/d_secret_" + suffix + ".key");
        components.tfPublicKeyFile.setText("data/public_keys/Q_public_" + suffix + ".key");
        components.tfSignatureFile.setText("data/signatures/message_" + suffix + ".sig");

        appLogger.log("Пути файлов сброшены до стандартных в папке /data.", false);
    }

    public void resetStateOnChange() {
        String suffix = components.rb256.isSelected() ? "256" : "512";

        isKeyGenerated = false;
        isSigned = false;
        updateButtonStates();
        resetPathsAction();

        appLogger.log("Смена режима: " + suffix + " бит. Начните с шага 1.", false);
    }

    // Логика Обзора Файлов
    public void browseFile(JTextField textField) {
        File startDir = new File("data");
        if (!startDir.exists()) {
            startDir = new File(".");
        }
        JFileChooser fileChooser = new JFileChooser(startDir);

        // Определяем, это диалог сохранения или открытия
        boolean isSaveDialog = textField.getText().contains(".key") || textField.getText().contains(".sig");

        int result;
        if (isSaveDialog) {
            result = fileChooser.showSaveDialog(this);
        } else {
            result = fileChooser.showOpenDialog(this);
        }

        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (path.contains(File.separator + "data" + File.separator)) {
                path = path.substring(path.indexOf("data" + File.separator));
            }
            textField.setText(path);
        }
    }


    // --- Криптографические Действия ---

    private MessageManager getActiveManager() {
        return components.rb256.isSelected() ? manager256 : manager512;
    }

    public void generateKeyAction() {
        try {
            MessageManager manager = getActiveManager();
            String dPath = components.tfSecretKeyFile.getText();
            String QPath = components.tfPublicKeyFile.getText();

            manager.generatePublicKey(dPath, QPath);
            appLogger.log(String.format("Шаг 1 завершен: Публичный ключ Q успешно сгенерирован и записан в: %s", QPath), false);

            isKeyGenerated = true;
            isSigned = false;
            updateButtonStates();
        } catch (Exception e) {
            appLogger.log("Ошибка генерации ключа: " + e.getMessage(), true);
        }
    }

    public void signAction() {
        try {
            MessageManager manager = getActiveManager();
            String msgPath = components.tfMessageFile.getText();
            String dPath = components.tfSecretKeyFile.getText();
            String sigPath = components.tfSignatureFile.getText();

            manager.signFile(msgPath, dPath, sigPath);
            appLogger.log(String.format("Шаг 2 завершен: Подпись успешно сформирована для файла %s и записана в %s.", msgPath, sigPath), false);

            isSigned = true;
            updateButtonStates();
        } catch (Exception e) {
            appLogger.log("Ошибка формирования подписи: " + e.getMessage(), true);
        }
    }

    public void verifyAction() {
        try {
            MessageManager manager = getActiveManager();
            String msgPath = components.tfMessageFile.getText();
            String QPath = components.tfPublicKeyFile.getText();
            String sigPath = components.tfSignatureFile.getText();

            boolean isValid = manager.verifyFile(msgPath, QPath, sigPath);

            if (isValid) {
                appLogger.log(String.format("Шаг 3 завершен: ПОДПИСЬ ВЕРНА! Файл %s подтвержден.", msgPath), false);
            } else {
                appLogger.log("Шаг 3 завершен: ПОДПИСЬ НЕВЕРНА!", true);
            }
        } catch (Exception e) {
            appLogger.log("Общая ошибка верификации: " + e.getMessage(), true);
        }
    }
}