package gost.gui;

import gost.manager.MessageManager;
import gost.occasion.AlienExceptions;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Главный класс GUI, использующий MessageManager для выполнения криптографических операций.
 */
public class MainApp extends JFrame {

    private final MessageManager manager256;
    private final MessageManager manager512;

    // Элементы управления
    private JRadioButton rb256;
    private JRadioButton rb512;
    private JTextField tfMessageFile;
    private JTextField tfSecretKeyFile;
    private JTextField tfPublicKeyFile;
    private JTextField tfSignatureFile;
    private JTextArea taLog;
    private JButton btnGenerateKey;
    private JButton btnSign;
    private JButton btnVerify;

    // Менеджер состояний для последовательности
    private boolean isKeyGenerated = false;
    private boolean isSigned = false;

    public MainApp() throws AlienExceptions.IncorrectParametersException {
        // Инициализация менеджеров для разных длин хэша
        manager256 = new MessageManager(256);
        manager512 = new MessageManager(512);

        setTitle("ГОСТ Р 34.10-2018 (ЭЦП)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10));

        // --- Панель логов ---
        taLog = new JTextArea();
        taLog.setEditable(false);
        taLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(taLog);
        logScroll.setPreferredSize(new Dimension(800, 150));
        add(logScroll, BorderLayout.SOUTH);

        // --- Верхняя панель (Настройки) ---
        JPanel settingsPanel = createSettingsPanel();
        add(settingsPanel, BorderLayout.NORTH);

        // --- Центральная панель (Операции) ---
        JPanel operationPanel = createOperationPanel();
        add(operationPanel, BorderLayout.CENTER);

        // Устанавливаем начальное состояние кнопок
        updateButtonStates();

        logMessage("Приложение запущено. Рабочая директория для файлов: /data", false);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Создает верхнюю панель для выбора режима и кнопки сброса путей.
     */
    private JPanel createSettingsPanel() {
        // Используем BorderLayout для позиционирования
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 0, 10));

        // --- Левая часть: Выбор режима ---
        JPanel radioPanel = new JPanel(new GridLayout(1, 2));
        rb256 = new JRadioButton("ГОСТ 34.10-2018 (256 бит)", true);
        rb512 = new JRadioButton("ГОСТ 34.10-2018 (512 бит)");
        ButtonGroup group = new ButtonGroup();
        group.add(rb256);
        group.add(rb512);

        // Добавляем слушателя для сброса состояния при смене режима
        rb256.addActionListener(e -> resetStateOnChange());
        rb512.addActionListener(e -> resetStateOnChange());

        radioPanel.add(rb256);
        radioPanel.add(rb512);

        panel.add(radioPanel, BorderLayout.WEST);

        // --- Правая часть: Кнопка Сброс путей ---
        JPanel resetPathWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnResetPaths = new JButton("Сброс путей");
        btnResetPaths.setPreferredSize(new Dimension(150, 25));

        btnResetPaths.setToolTipText("Восстанавливает стандартные пути в папке data/");
        btnResetPaths.addActionListener(e -> resetPathsAction());

        resetPathWrapper.add(btnResetPaths);

        panel.add(resetPathWrapper, BorderLayout.EAST);

        return panel;
    }

    /**
     * Создает центральную панель с полями ввода и кнопками операций (1, 2, 3).
     * Код очищен от ненужной обертки для кнопки сброса путей.
     */
    private JPanel createOperationPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        // --- 1. Панель ввода путей (GRID LAYOUT) ---
        // Панель для сетки (4x3 GridLayout)
        JPanel inputPanel = new JPanel(new GridLayout(4, 3, 10, 10));

        // Устанавливаем относительные пути по умолчанию с подпапками
        tfMessageFile = new JTextField("data/messages/message.txt");
        tfSecretKeyFile = new JTextField("data/private_keys/d_secret_256.key");
        tfPublicKeyFile = new JTextField("data/public_keys/Q_public_256.key");
        tfSignatureFile = new JTextField("data/signatures/message_256.sig");

        // Вспомогательный метод для добавления полей и кнопок обзора
        addInputRow(inputPanel, "Файл сообщения:", tfMessageFile);
        addInputRow(inputPanel, "Секретный ключ (d):", tfSecretKeyFile);
        addInputRow(inputPanel, "Публичный ключ (Q):", tfPublicKeyFile);
        addInputRow(inputPanel, "Файл подписи:", tfSignatureFile);

        // Панель ввода теперь напрямую идет в NORTH
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // --- 2. Панель кнопок операций (CENTER) ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        btnGenerateKey = new JButton("1. Генерировать Q");
        btnGenerateKey.addActionListener(e -> generateKeyAction());

        btnSign = new JButton("2. Сформировать Подпись");
        btnSign.addActionListener(e -> signAction());

        btnVerify = new JButton("3. Проверить Подпись");
        btnVerify.addActionListener(e -> verifyAction());

        buttonPanel.add(btnGenerateKey);
        buttonPanel.add(btnSign);
        buttonPanel.add(btnVerify);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Отдельный обработчик для сброса путей (вызывается кнопкой "Сброс путей").
     */
    private void resetPathsAction() {
        String suffix = rb256.isSelected() ? "256" : "512";

        tfMessageFile.setText("data/messages/message.txt");

        tfSecretKeyFile.setText("data/private_keys/d_secret_" + suffix + ".key");
        tfPublicKeyFile.setText("data/public_keys/Q_public_" + suffix + ".key");
        tfSignatureFile.setText("data/signatures/message_" + suffix + ".sig");

        logMessage("Пути файлов сброшены до стандартных в папке /data.", false);
    }

    // --- Управление состоянием и блокировкой кнопок ---

    /**
     * Сбрасывает состояние цикла и обновляет пути при смене режима (256/512).
     */
    private void resetStateOnChange() {
        String suffix = rb256.isSelected() ? "256" : "512";

        // Сброс состояния
        isKeyGenerated = false;
        isSigned = false;
        updateButtonStates();

        // Обновление путей (сброс)
        tfSecretKeyFile.setText("data/private_keys/d_secret_" + suffix + ".key");
        tfPublicKeyFile.setText("data/public_keys/Q_public_" + suffix + ".key");
        tfSignatureFile.setText("data/signatures/message_" + suffix + ".sig");

        logMessage("Смена режима: " + suffix + " бит. Начните с шага 1.", false);
    }

    private void updateButtonStates() {
        // Шаг 1 всегда доступен
        btnGenerateKey.setEnabled(true);

        // Шаг 2 доступен только после Шага 1
        btnSign.setEnabled(isKeyGenerated);

        // Шаг 3 доступен только после Шага 2
        btnVerify.setEnabled(isSigned);
    }

    // --- Вспомогательный метод для создания строки ввода с кнопкой "Обзор" ---
    private void addInputRow(JPanel panel, String label, JTextField textField) {
        panel.add(new JLabel(label));
        panel.add(textField);

        JButton btnBrowse = new JButton("Обзор...");
        btnBrowse.setPreferredSize(new Dimension(80, 25));
        btnBrowse.addActionListener(e -> browseFile(textField, label.contains("для записи")));
        panel.add(btnBrowse);
    }

    // --- Логика Обзора Файлов ---
    private void browseFile(JTextField textField, boolean isSaveDialog) {
        // Начинаем обзор с папки data или текущего каталога
        File startDir = new File("data");
        if (!startDir.exists()) {
            startDir = new File(".");
        }
        JFileChooser fileChooser = new JFileChooser(startDir);

        int result;
        if (isSaveDialog) {
            result = fileChooser.showSaveDialog(this);
        } else {
            result = fileChooser.showOpenDialog(this);
        }

        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            // Преобразование в относительный путь, если путь содержит "data"
            if (path.contains(File.separator + "data" + File.separator)) {
                path = path.substring(path.indexOf("data" + File.separator));
            }
            textField.setText(path);
        }
    }

    // --- Логика Логирования ---
    private void logMessage(String message, boolean isError) {
        String tag = isError ? "[ОШИБКА]" : "[УСПЕХ]";
        taLog.append(String.format("%s %s\n", tag, message));
        // Прокрутка вниз
        taLog.setCaretPosition(taLog.getDocument().getLength());
    }

    // --- Метод получения активного менеджера ---
    private MessageManager getActiveManager() {
        return rb256.isSelected() ? manager256 : manager512;
    }


    // --- Обработчики Действий ---
    private void generateKeyAction() {
        try {
            MessageManager manager = getActiveManager();
            String dPath = tfSecretKeyFile.getText();
            String QPath = tfPublicKeyFile.getText();

            manager.generatePublicKey(dPath, QPath);
            logMessage(String.format("Шаг 1 завершен: Публичный ключ Q успешно сгенерирован и записан в: %s", QPath), false);

            isKeyGenerated = true;
            isSigned = false; // Сброс подписи, если была (т.к. ключ новый)
            updateButtonStates();
        } catch (AlienExceptions.InvalidKeyException e) {
            logMessage("Ошибка ключа: " + e.getMessage(), true);
        } catch (Exception e) {
            logMessage("Ошибка генерации ключа: " + e.getMessage(), true);
        }
    }

    private void signAction() {
        try {
            MessageManager manager = getActiveManager();
            String msgPath = tfMessageFile.getText();
            String dPath = tfSecretKeyFile.getText();
            String sigPath = tfSignatureFile.getText();

            manager.signFile(msgPath, dPath, sigPath);
            logMessage(String.format("Шаг 2 завершен: Подпись успешно сформирована для файла %s и записана в %s.", msgPath, sigPath), false);

            isSigned = true;
            updateButtonStates();
        } catch (Exception e) {
            logMessage("Ошибка формирования подписи: " + e.getMessage(), true);
        }
    }

    private void verifyAction() {
        try {
            MessageManager manager = getActiveManager();
            String msgPath = tfMessageFile.getText();
            String QPath = tfPublicKeyFile.getText();
            String sigPath = tfSignatureFile.getText();

            boolean isValid = manager.verifyFile(msgPath, QPath, sigPath);

            if (isValid) {
                logMessage(String.format("Шаг 3 завершен: ПОДПИСЬ ВЕРНА! Файл %s подтвержден публичным ключом %s.", msgPath, QPath), false);
            } else {
                logMessage("Шаг 3 завершен: ПОДПИСЬ НЕВЕРНА! Целостность файла нарушена или ключ не соответствует.", true);
            }
        } catch (AlienExceptions.SignatureUnreadableException e) {
            logMessage("Ошибка верификации: Невозможно прочитать подпись. Проверьте путь и формат.", true);
        } catch (Exception e) {
            logMessage("Общая ошибка верификации: " + e.getMessage(), true);
        }
    }
}