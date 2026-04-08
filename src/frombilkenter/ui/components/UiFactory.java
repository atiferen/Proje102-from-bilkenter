package frombilkenter.ui.components;

import frombilkenter.ui.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class UiFactory {
    private UiFactory() {
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(Theme.PRIMARY);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        button.setFont(Theme.BODY.deriveFont(14f));
        button.setMargin(new java.awt.Insets(12, 18, 12, 18));
        button.setAlignmentY(Component.CENTER_ALIGNMENT);
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(Theme.TEXT);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        button.setFont(Theme.BODY.deriveFont(14f));
        button.setMargin(new java.awt.Insets(10, 14, 10, 14));
        button.setAlignmentY(Component.CENTER_ALIGNMENT);
        return button;
    }

    public static JTextField textField(String placeholder) {
        return new HintTextField(placeholder);
    }

    public static JPanel spacer(int height) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(1, height));
        return panel;
    }

    public static void alignLeft(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private static class HintTextField extends JTextField {
        private final String hint;

        HintTextField(String hint) {
            this.hint = hint;
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            setFont(Theme.BODY);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.MUTED);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(hint, 12, (getHeight() + fm.getAscent()) / 2 - 3);
                g2.dispose();
            }
        }
    }

    public static class DashedPanel extends JPanel {
        public DashedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Theme.BORDER);
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{5f, 4f}, 0f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
        }
    }
}
