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

    private class LoginPanel extends JPanel {
        LoginPanel() {
            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);

            JPanel card = new JPanel();
            card.setOpaque(false);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(430, 640));

            JLabel logo = new JLabel(loadImage("assets/logo.png", 180, 180));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(Box.createVerticalStrut(12));
            card.add(logo);
            card.add(Box.createVerticalStrut(34));

            JTextField email = UiFactory.textField("Bilkent E-mail");
            JTextField password = UiFactory.textField("Password");
            JButton forgot = new JButton("Forgot Password?");
            forgot.setContentAreaFilled(false);
            forgot.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            forgot.setForeground(Theme.MUTED);
            forgot.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton signIn = UiFactory.primaryButton("Sign In");
            JButton createAccount = new JButton("Create Account");
            createAccount.setContentAreaFilled(false);
            createAccount.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            createAccount.setForeground(Theme.MUTED);
            createAccount.setAlignmentX(Component.CENTER_ALIGNMENT);

            for (JComponent c : List.of(email, password, signIn)) {
                c.setAlignmentX(Component.CENTER_ALIGNMENT);
                c.setPreferredSize(new Dimension(370, 46));
                c.setMaximumSize(new Dimension(370, 46));
                card.add(c);
                card.add(Box.createVerticalStrut(14));
            }

            forgot.setAlignmentX(Component.CENTER_ALIGNMENT);
            forgot.setFont(Theme.SMALL.deriveFont(14f));
            card.add(forgot);
            card.add(Box.createVerticalStrut(10));
            createAccount.setFont(Theme.SMALL.deriveFont(14f));
            card.add(createAccount);

            signIn.addActionListener(e -> showPage(PAGE_HOME));
            forgot.addActionListener(e -> showResetPasswordDialog());
            createAccount.addActionListener(e -> showRegistrationDialog());

            add(card);
        }
    }

     private class NavBar extends JPanel {
        NavBar() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
            add(new SmallLogo(), BorderLayout.WEST);

            JPanel links = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 6));
            links.setOpaque(false);
            links.add(nav("Home", PAGE_HOME, true));
            if (appState.getCurrentUser().isSeller()) {
                links.add(nav("Sell", PAGE_SELL, false));
            }
            links.add(nav("Leaderboard", PAGE_LEADERBOARD, false));
            links.add(nav("Profile", PAGE_PROFILE, false));
            if (appState.getCurrentUser().isAdmin()) {
                links.add(nav("Admin Panel", PAGE_ADMIN, false));
            }
            JButton signOut = nav("Sign Out", null, false);
            signOut.addActionListener(e -> showSignOutDialog());
            links.add(signOut);
            add(links, BorderLayout.EAST);
        }

        private JButton nav(String text, String target, boolean active) {
            JButton button = new JButton(text);
            button.setContentAreaFilled(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            button.setForeground(active ? Theme.PRIMARY_DARK : Theme.MUTED);
            button.setFont(Theme.SMALL.deriveFont(14f));
            button.setMargin(new Insets(8, 8, 8, 8));
            if (target != null) {
                button.addActionListener(e -> showPage(target));
            }
            return button;
        }
    }

    private class HomePanel extends JPanel {
        private final JPanel listingGrid = new JPanel(new GridLayout(0, 3, 26, 34));
        private final JComboBox<AppState.SortMode> sortBox = new JComboBox<>(AppState.SortMode.values());
        private final JTextField searchField = UiFactory.textField("Search...");

        HomePanel() {
            setBackground(Theme.FILTER_BG);
            setLayout(new BorderLayout());
            JPanel content = new JPanel();
            content.setBackground(Theme.FILTER_BG);
            content.setBorder(BorderFactory.createEmptyBorder(24, 42, 42, 42));
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

            JPanel topBar = new JPanel(new BorderLayout());
            topBar.setOpaque(false);
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            left.setOpaque(false);
            JButton filters = UiFactory.secondaryButton("Filters");
            filters.addActionListener(e -> showFiltersDialog());
            left.add(filters);
            searchField.setPreferredSize(new Dimension(220, 40));
            searchField.addActionListener(e -> {
                filterState.search = searchField.getText();
                refresh();
            });
            left.add(searchField);
            topBar.add(left, BorderLayout.WEST);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            right.setOpaque(false);
            right.add(label("Sort by", Theme.MUTED, 14, Font.PLAIN));
            sortBox.addActionListener(e -> {
                sortMode = (AppState.SortMode) sortBox.getSelectedItem();
                refresh();
            });
            sortBox.setPreferredSize(new Dimension(210, 40));
            sortBox.setPrototypeDisplayValue(AppState.SortMode.PRICE_HIGH_LOW);
            right.add(sortBox);
            topBar.add(right, BorderLayout.EAST);

            listingGrid.setOpaque(false);
            content.add(topBar);
            content.add(UiFactory.spacer(30));
            content.add(section("Premium Listings"));
            content.add(UiFactory.spacer(12));
            content.add(listingGrid);
            add(content, BorderLayout.NORTH);
        }

        private JPanel section(String text) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panel.setOpaque(false);
            panel.add(label(text, Theme.MUTED, 18, Font.BOLD));
            return panel;
        }

        void refresh() {
            listingGrid.removeAll();
            List<Listing> all = appState.getApprovedListings(filterState, sortMode);
            List<Listing> premium = all.stream().filter(l -> appState.getSeller(l).isPremium()).limit(2).toList();
            List<Listing> other = all.stream().filter(l -> !premium.contains(l)).toList();
            for (Listing listing : premium) {
                listingGrid.add(new ListingCard(listing, true));
            }
            if (!other.isEmpty()) {
                listingGrid.add(section("Other Listings"));
                for (Listing listing : other) {
                    listingGrid.add(new ListingCard(listing, false));
                }
            }
            listingGrid.revalidate();
            listingGrid.repaint();
        }
    }

}
