package gost;

import gost.gui.MainApp;
import gost.occasion.AlienExceptions;

import javax.swing.*;

/**
 * Точка входа в приложение. Отвечает только за запуск GUI в потоке Swing.
 */
public class Main {
    public static void main(String[] args) {
        // Установка внешнего вида (Nimbus, если доступен)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Игнорируем, используем стандартный LookAndFeel
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Запускаем GUI
                new MainApp();
            } catch (AlienExceptions.IncorrectParametersException e) {
                // Критическая ошибка при инициализации менеджеров (проблема с файлами JSON)
                JOptionPane.showMessageDialog(null,
                        "Критическая ошибка: Не удалось загрузить параметры кривой. Проверьте файлы curve_params_xxx.json в resources.",
                        "Ошибка Инициализации", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}