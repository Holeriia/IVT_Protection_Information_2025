package gost.gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс, инкапсулирующий логику вывода сообщений в JTextArea.
 */
public class AppLogger {
    private final JTextArea taLog;
    private final JScrollPane logScroll;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public AppLogger() {
        taLog = new JTextArea();
        taLog.setEditable(false);
        taLog.setFont(new Font("Monospaced", Font.PLAIN, 12));

        logScroll = new JScrollPane(taLog);
        logScroll.setPreferredSize(new Dimension(800, 150));
    }

    public JScrollPane getLogScroll() {
        return logScroll;
    }

    public void log(String message, boolean isError) {
        String tag = isError ? "[ОШИБКА]" : "[УСПЕХ]";
        String timestamp = DATE_FORMAT.format(new Date());

        taLog.append(String.format("%s %s: %s\n", timestamp, tag, message));

        // Прокрутка вниз
        taLog.setCaretPosition(taLog.getDocument().getLength());
    }
}