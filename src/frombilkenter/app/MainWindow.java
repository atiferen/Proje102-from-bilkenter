package frombilkenter.app;

import frombilkenter.data.AppState;
import frombilkenter.model.Listing;
import frombilkenter.model.ListingRequest;
import frombilkenter.model.User;
import frombilkenter.ui.Theme;
import frombilkenter.ui.components.UiFactory;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class MainWindow extends JFrame{
    private static final String PAGE_LOGIN = "login";
    private static final String PAGE_HOME = "home";
    private static final String PAGE_DETAIL = "detail";
    private static final String PAGE_SELL = "sell";
    private static final String PAGE_LEADERBOARD = "leaderboard";
    private static final String PAGE_PROFILE = "profile";
    private static final String PAGE_ADMIN = "admin";

    private final AppState appState;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);
    private final AppState.FilterState filterState = new AppState.FilterState();
    private AppState.SortMode sortMode = AppState.SortMode.ALPHABETICAL;
    private Listing selectedListing;

    private final HomePanel homePanel;
    private final DetailPanel detailPanel;
    private final SellPanel sellPanel;
    private final LeaderboardPanel leaderboardPanel;
    private final ProfilePanel profilePanel;
    private final AdminPanel adminPanel;

    public MainWindow(AppState appState) {
        this.appState = appState;
        this.homePanel = new HomePanel();
        this.detailPanel = new DetailPanel();
        this.sellPanel = new SellPanel();
        this.leaderboardPanel = new LeaderboardPanel();
        this.profilePanel = new ProfilePanel();
        this.adminPanel = new AdminPanel();
        setTitle("From Bilkenter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1440, 900);
        setMinimumSize(new Dimension(1320, 820));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.PAGE);
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);

        pages.add(new LoginPanel(), PAGE_LOGIN);
        pages.add(withShell(homePanel), PAGE_HOME);
        pages.add(withShell(detailPanel), PAGE_DETAIL);
        pages.add(withShell(sellPanel), PAGE_SELL);
        pages.add(withShell(leaderboardPanel), PAGE_LEADERBOARD);
        pages.add(withShell(profilePanel), PAGE_PROFILE);
        pages.add(withShell(adminPanel), PAGE_ADMIN);

        add(pages);
        showPage(PAGE_LOGIN);
    }

    private JPanel withShell(JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.PAGE);
        panel.add(new NavBar(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void showPage(String page) {
        refreshAll();
        cardLayout.show(pages, page);
    }

    private void refreshAll() {
        homePanel.refresh();
        detailPanel.refresh();
        leaderboardPanel.refresh();
        profilePanel.refresh();
        adminPanel.refresh();
    }

    private void openListing(Listing listing) {
        selectedListing = listing;
        showPage(PAGE_DETAIL);
    }

    private JLabel label(String text, Color color, int size, int style) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", style, size));
        label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JLabel wrapLabel(String text, Color color, int size, int style, int width) {
        JLabel label = new JLabel("<html><div style='width:" + width + "px;'>" + escapeHtml(text) + "</div></html>");
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", style, size));
        label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private ImageIcon loadImage(String path, int width, int height) {
        try {
            Image image = ImageIO.read(new File(path)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(image);
        } catch (Exception e) {
            return new ImageIcon(new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB));
        }
    }

    private JDialog baseDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);
        return dialog;
    }

    private JPanel dialogBody() {
        JPanel body = new JPanel();
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        return body;
    }
}
