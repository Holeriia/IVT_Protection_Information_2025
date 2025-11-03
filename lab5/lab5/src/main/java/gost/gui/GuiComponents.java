package gost.gui;

import javax.swing.*;

/**
 * Класс для хранения всех элементов GUI, созданных билдером
 */
class GuiComponents {
    public JRadioButton rb256;
    public JRadioButton rb512;
    public JTextField tfMessageFile;
    public JTextField tfSecretKeyFile;
    public JTextField tfPublicKeyFile;
    public JTextField tfSignatureFile;
    public JButton btnGenerateKey;
    public JButton btnSign;
    public JButton btnVerify;
    public JScrollPane logScroll;
}