package gost.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;



/**
 * Класс, отвечающий исключительно за создание и компоновку элементов GUI (Фабрика View)
 */
public class GuiBuilder {

    // Функциональные интерфейсы для привязки обработчиков
    private final Runnable resetStateOnChangeAction;
    private final Runnable resetPathsAction;
    private final Consumer<JTextField> browseFileAction;
    private final Runnable generateKeyAction;
    private final Runnable signAction;
    private final Runnable verifyAction;

    public GuiBuilder(
            Runnable resetStateOnChangeAction,
            Runnable resetPathsAction,
            Consumer<JTextField> browseFileAction,
            Runnable generateKeyAction,
            Runnable signAction,
            Runnable verifyAction)
    {
        this.resetStateOnChangeAction = resetStateOnChangeAction;
        this.resetPathsAction = resetPathsAction;
        this.browseFileAction = browseFileAction;
        this.generateKeyAction = generateKeyAction;
        this.signAction = signAction;
        this.verifyAction = verifyAction;
    }

    /**
     * Создает все компоненты и привязывает обработчики.
     */
    public GuiComponents build(JFrame frame, AppLogger logger) {
        GuiComponents comp = new GuiComponents();

        // 1. Создание элементов
        comp.rb256 = new JRadioButton("ГОСТ 34.10-2018 (256 бит)", true);
        comp.rb512 = new JRadioButton("ГОСТ 34.10-2018 (512 бит)");
        comp.tfMessageFile = new JTextField();
        comp.tfSecretKeyFile = new JTextField();
        comp.tfPublicKeyFile = new JTextField();
        comp.tfSignatureFile = new JTextField();
        comp.btnGenerateKey = new JButton("1. Генерировать Q");
        comp.btnSign = new JButton("2. Сформировать Подпись");
        comp.btnVerify = new JButton("3. Проверить Подпись");
        comp.logScroll = logger.getLogScroll();

        // 2. Добавление панелей на фрейм
        frame.add(comp.logScroll, BorderLayout.SOUTH);
        frame.add(createSettingsPanel(comp), BorderLayout.NORTH);
        frame.add(createOperationPanel(comp), BorderLayout.CENTER);

        return comp;
    }

    /**
     * Создает верхнюю панель для выбора режима и кнопки сброса путей.
     */
    public JPanel createSettingsPanel(GuiComponents comp) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 0, 10));

        // --- Левая часть: Выбор режима ---
        JPanel radioPanel = new JPanel(new GridLayout(1, 2));
        ButtonGroup group = new ButtonGroup();
        group.add(comp.rb256);
        group.add(comp.rb512);

        // Привязка обработчика
        comp.rb256.addActionListener(e -> resetStateOnChangeAction.run());
        comp.rb512.addActionListener(e -> resetStateOnChangeAction.run());

        radioPanel.add(comp.rb256);
        radioPanel.add(comp.rb512);
        panel.add(radioPanel, BorderLayout.WEST);

        // --- Правая часть: Кнопка Сброс путей ---
        JPanel resetPathWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnResetPaths = new JButton("Сброс путей");
        btnResetPaths.setPreferredSize(new Dimension(120, 25));
        btnResetPaths.setToolTipText("Восстанавливает стандартные пути в папке data/");

        // Привязка обработчика
        btnResetPaths.addActionListener(e -> resetPathsAction.run());

        resetPathWrapper.add(btnResetPaths);
        panel.add(resetPathWrapper, BorderLayout.EAST);
        return panel;
    }

    /**
     * Создает центральную панель с полями ввода и кнопками операций (1, 2, 3).
     */
    public JPanel createOperationPanel(GuiComponents comp) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        // --- 1. Панель ввода путей (GRID LAYOUT) ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 3, 10, 10));

        addInputRow(inputPanel, "Файл сообщения:", comp.tfMessageFile);
        addInputRow(inputPanel, "Секретный ключ (d):", comp.tfSecretKeyFile);
        addInputRow(inputPanel, "Публичный ключ (Q):", comp.tfPublicKeyFile);
        addInputRow(inputPanel, "Файл подписи:", comp.tfSignatureFile);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // --- 2. Панель кнопок операций (CENTER) ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // Привязка обработчиков
        comp.btnGenerateKey.addActionListener(e -> generateKeyAction.run());
        comp.btnSign.addActionListener(e -> signAction.run());
        comp.btnVerify.addActionListener(e -> verifyAction.run());

        buttonPanel.add(comp.btnGenerateKey);
        buttonPanel.add(comp.btnSign);
        buttonPanel.add(comp.btnVerify);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    /**
     * Вспомогательный метод для создания строки ввода с кнопкой "Обзор".
     */
    private void addInputRow(JPanel panel, String label, JTextField textField) {
        panel.add(new JLabel(label));
        panel.add(textField);

        JButton btnBrowse = new JButton("Обзор...");
        btnBrowse.setPreferredSize(new Dimension(80, 25));

        // Привязка обработчика
        btnBrowse.addActionListener(e -> browseFileAction.accept(textField));

        panel.add(btnBrowse);
    }
}