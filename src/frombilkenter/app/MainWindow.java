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

    private class ListingCard extends JPanel {
        ListingCard(Listing listing, boolean premium) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel image = new JLabel(loadImage(listing.getImagePath(), 390, 250));
            image.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (premium) {
                image.setLayout(null);
                JLabel badge = new JLabel("Premium");
                badge.setOpaque(true);
                badge.setBackground(Theme.PRIMARY);
                badge.setForeground(Color.WHITE);
                badge.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                badge.setBounds(310, 12, 72, 24);
                image.add(badge);
            }
            add(image);
            add(Box.createVerticalStrut(16));
            add(wrapLabel(listing.getTitle(), Theme.TEXT, 16, Font.BOLD, 380));
            add(Box.createVerticalStrut(10));
            add(label(listing.getPrice() == 0 ? "FREE" : "TL " + listing.getPrice(), Theme.TEXT, 17, Font.BOLD));
            add(Box.createVerticalStrut(8));
            User seller = appState.getSeller(listing);
            add(label(seller.getName() + " " + seller.getSurname().charAt(0) + ".", Theme.MUTED, 13, Font.PLAIN));

            MouseAdapter open = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openListing(listing);
                }
            };
            addMouseListener(open);
            image.addMouseListener(open);
        }
    }


    private class DetailPanel extends JPanel {
        private final JLabel title = label("", Theme.TEXT, 24, Font.BOLD);
        private final JLabel price = label("", Theme.TEXT, 28, Font.BOLD);
        private final JLabel image = new JLabel();
        private final JButton favorite = new JButton("\u2661");
        private final JPanel info = new JPanel(new GridLayout(0, 2, 0, 18));
        private final JTextArea description = new JTextArea();

        DetailPanel() {
            setBackground(Color.WHITE);
            setLayout(new BorderLayout());
            JPanel content = new JPanel(new BorderLayout(40, 20));
            content.setBackground(Color.WHITE);
            content.setBorder(BorderFactory.createEmptyBorder(30, 50, 50, 50));
            content.add(image, BorderLayout.WEST);

            JPanel right = new JPanel();
            right.setOpaque(false);
            right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

            JPanel titleRow = new JPanel(new BorderLayout());
            titleRow.setOpaque(false);
            titleRow.add(title, BorderLayout.WEST);
            favorite.setContentAreaFilled(false);
            favorite.setBorder(BorderFactory.createEmptyBorder());
            favorite.setFont(new Font("Dialog", Font.PLAIN, 24));
            favorite.addActionListener(e -> {
                if (selectedListing != null) {
                    appState.toggleFavorite(selectedListing);
                    refresh();
                }
            });
            titleRow.add(favorite, BorderLayout.EAST);
            right.add(titleRow);
            right.add(Box.createVerticalStrut(14));
            right.add(price);
            right.add(Box.createVerticalStrut(18));
            JButton contact = UiFactory.primaryButton("Contact Seller");
            contact.setAlignmentX(Component.LEFT_ALIGNMENT);
            contact.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "An e-mail draft would be created for: " + appState.getSeller(selectedListing).getEmail()));
            right.add(contact);
            right.add(Box.createVerticalStrut(28));
            info.setOpaque(false);
            right.add(info);
            right.add(Box.createVerticalStrut(28));
            description.setLineWrap(true);
            description.setWrapStyleWord(true);
            description.setEditable(false);
            description.setOpaque(false);
            description.setFont(Theme.BODY);
            description.setForeground(Theme.TEXT);
            right.add(description);
            content.add(right, BorderLayout.CENTER);
            add(content, BorderLayout.NORTH);
        }

        void refresh() {
            if (selectedListing == null) {
                selectedListing = appState.getApprovedListings(filterState, sortMode).stream().findFirst().orElse(null);
            }
            if (selectedListing == null) {
                return;
            }
            User seller = appState.getSeller(selectedListing);
            image.setIcon(loadImage(selectedListing.getImagePath(), 520, 650));
            title.setText(selectedListing.getTitle());
            price.setText(selectedListing.getPrice() == 0 ? "FREE" : "TL " + selectedListing.getPrice());
            favorite.setText(appState.getCurrentUser().getFavoriteListingIds().contains(selectedListing.getListingId()) ? "\u2665" : "\u2661");
            info.removeAll();
            addInfo("Seller", seller.getFullName());
            addInfo("Email", seller.getEmail());
            addInfo("Department", seller.getDepartment());
            addInfo("Condition", selectedListing.getCondition());
            addInfo("Color", selectedListing.getColor());
            addInfo("Remaining Time", selectedListing.getRemainingDays() + " days");
            addInfo("Status", seller.isPremium() ? "Premium Seller" : "Standard Seller");
            description.setText(selectedListing.getDescription());
        }

        private void addInfo(String key, String value) {
            info.add(label(key, Theme.MUTED, 15, Font.PLAIN));
            JLabel val = label(value, value.contains("Premium") ? Theme.PRIMARY_DARK : Theme.TEXT, 15, Font.PLAIN);
            val.setHorizontalAlignment(SwingConstants.RIGHT);
            info.add(val);
        }
    }

    private class SellPanel extends JPanel {
        private final JTextField titleField = UiFactory.textField("MacBook Pro");
        private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Electronics", "Books / Course Materials", "Clothing", "Vehicles"});
        private final JTextField brandField = UiFactory.textField("Apple");
        private final JTextField priceField = UiFactory.textField("60000");
        private final JComboBox<String> conditionBox = new JComboBox<>(new String[]{"New", "Like New", "Good", "Fair", "Poor"});
        private final JTextArea description = new JTextArea("M3 chip. Very good condition.");
        private final UiFactory.DashedPanel upload = new UiFactory.DashedPanel();
        private final JLabel uploadPreview = new JLabel();
        private final JLabel uploadLabel = label("Upload Image", Theme.MUTED, 15, Font.PLAIN);
        private String selectedImagePath = "assets/macbook.jpeg";

        SellPanel() {
            setBackground(Color.WHITE);
            setLayout(new GridBagLayout());
            JPanel form = new JPanel();
            form.setOpaque(false);
            form.setPreferredSize(new Dimension(480, 720));
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
            form.add(Box.createVerticalStrut(10));
            form.add(label("Create Sale Ad Request", Theme.TEXT, 24, Font.BOLD));
            form.add(Box.createVerticalStrut(24));

            for (JComponent c : List.of(titleField, categoryBox, brandField, priceField, conditionBox)) {
                UiFactory.alignLeft(c);
                c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
                form.add(c);
                form.add(Box.createVerticalStrut(18));
            }

            description.setLineWrap(true);
            description.setWrapStyleWord(true);
            description.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
            description.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
            form.add(description);
            form.add(Box.createVerticalStrut(26));

            upload.setLayout(new GridBagLayout());
            upload.setPreferredSize(new Dimension(480, 140));
            upload.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
            upload.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            uploadPreview.setHorizontalAlignment(SwingConstants.CENTER);
            upload.add(uploadLabel);
            refreshUploadPreview();
            MouseAdapter chooserOpener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    chooseImageFile();
                }
            };
            upload.addMouseListener(chooserOpener);
            uploadPreview.addMouseListener(chooserOpener);
            uploadLabel.addMouseListener(chooserOpener);
            form.add(upload);
            form.add(Box.createVerticalStrut(26));

            JButton submit = UiFactory.primaryButton("Send Request");
            submit.addActionListener(e -> {
                int price = Integer.parseInt(priceField.getText().trim());
                appState.submitRequest(titleField.getText(), (String) categoryBox.getSelectedItem(), "Gray", brandField.getText(),
                    price, (String) conditionBox.getSelectedItem(), description.getText(), selectedImagePath, "", true,
                    false, "", "");
                JOptionPane.showMessageDialog(this, "Request submitted for admin approval.");
                showPage(PAGE_PROFILE);
            });
            form.add(submit);
            add(form);
        }

        private void chooseImageFile() {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select listing image");
            chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif", "bmp"));
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImagePath = chooser.getSelectedFile().getAbsolutePath();
                refreshUploadPreview();
            }
        }

        private void refreshUploadPreview() {
            upload.removeAll();
            if (selectedImagePath != null && !selectedImagePath.isBlank()) {
                uploadPreview.setIcon(loadImage(selectedImagePath, 150, 95));
                upload.add(uploadPreview);
                upload.add(Box.createHorizontalStrut(18));
                uploadLabel.setText(new File(selectedImagePath).getName());
                upload.add(uploadLabel);
            } else {
                uploadLabel.setText("Upload Image");
                upload.add(uploadLabel);
            }
            upload.revalidate();
            upload.repaint();
        }
    }

    private class LeaderboardPanel extends JPanel {
        private final JPanel rows = new JPanel(new GridLayout(0, 5, 24, 18));

        LeaderboardPanel() {
            setBackground(Color.WHITE);
            setLayout(new BorderLayout());
            JPanel content = new JPanel();
            content.setBackground(Color.WHITE);
            content.setBorder(BorderFactory.createEmptyBorder(38, 110, 38, 110));
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.add(label("Leaderboard", Theme.TEXT, 26, Font.BOLD));
            content.add(Box.createVerticalStrut(42));
            rows.setOpaque(false);
            content.add(rows);
            add(content, BorderLayout.NORTH);
        }

        void refresh() {
            rows.removeAll();
            for (String head : List.of("Rank", "Name", "Department", "Sales", "Status")) {
                rows.add(label(head, Theme.MUTED, 15, Font.BOLD));
            }
            int rank = 1;
            for (User user : appState.getLeaderboardUsers()) {
                rows.add(label(String.valueOf(rank++), Theme.TEXT, 15, Font.PLAIN));
                rows.add(label(user.getFullName(), Theme.TEXT, 15, Font.BOLD));
                rows.add(label(user.getDepartment(), Theme.MUTED, 15, Font.PLAIN));
                rows.add(label(String.valueOf(user.getCompletedSales()), Theme.TEXT, 15, Font.PLAIN));
                rows.add(label(user.isPremium() ? "Premium" : "â€”", user.isPremium() ? Theme.PRIMARY_DARK : Theme.MUTED, 15, Font.PLAIN));
            }
            rows.revalidate();
            rows.repaint();
        }
    }

    private class ProfilePanel extends JPanel {
        private final JPanel requestsPanel = new JPanel(new BorderLayout());
        private final JPanel listingsPanel = new JPanel(new GridLayout(0, 2, 26, 26));
        private final JPanel favoritesPanel = new JPanel(new GridLayout(0, 1, 18, 18));

        ProfilePanel() {
            setBackground(Color.WHITE);
            setLayout(new BorderLayout());
            JPanel content = new JPanel();
            content.setBackground(Color.WHITE);
            content.setBorder(BorderFactory.createEmptyBorder(34, 70, 50, 70));
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.add(label(appState.getCurrentUser().getFullName(), Theme.TEXT, 26, Font.BOLD));
            content.add(Box.createVerticalStrut(28));

            JPanel summary = new JPanel(new GridLayout(4, 2, 16, 18));
            summary.setOpaque(false);
            addSummary(summary, "Department", appState.getCurrentUser().getDepartment());
            addSummary(summary, "Active Listings", String.valueOf(appState.getMyListings().size()));
            addSummary(summary, "Completed Sales", String.valueOf(appState.getCurrentUser().getCompletedSales()));
            addSummary(summary, "Status", appState.getCurrentUser().isPremium() ? "Premium" : "Standard");
            content.add(summary);
            content.add(Box.createVerticalStrut(24));

            JButton edit = UiFactory.primaryButton("Edit Profile");
            edit.addActionListener(e -> JOptionPane.showMessageDialog(this, "Profile edit flow can be attached here."));
            JButton changePassword = UiFactory.secondaryButton("Change Password");
            changePassword.addActionListener(e -> showChangePasswordDialog());
            content.add(edit);
            content.add(Box.createVerticalStrut(10));
            content.add(changePassword);
            content.add(Box.createVerticalStrut(40));

            content.add(label("My Requests", Theme.TEXT, 24, Font.BOLD));
            content.add(Box.createVerticalStrut(16));
            requestsPanel.setOpaque(false);
            content.add(requestsPanel);
            content.add(Box.createVerticalStrut(40));

            content.add(label("My Listings", Theme.TEXT, 24, Font.BOLD));
            content.add(Box.createVerticalStrut(18));
            listingsPanel.setOpaque(false);
            content.add(listingsPanel);
            content.add(Box.createVerticalStrut(40));

            content.add(label("Favorites", Theme.TEXT, 24, Font.BOLD));
            content.add(Box.createVerticalStrut(18));
            favoritesPanel.setOpaque(false);
            content.add(favoritesPanel);
            add(content, BorderLayout.NORTH);
        }

        void refresh() {
            requestsPanel.removeAll();
            String[] columns = {"Title", "Category", "Price", "Status", "Submitted"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            for (ListingRequest request : appState.getRequests()) {
                if (request.getListing().getSellerId().equals(appState.getCurrentUser().getUserId())) {
                    model.addRow(new Object[]{
                        request.getListing().getTitle(),
                        request.getListing().getCategory(),
                        "â‚º" + request.getListing().getPrice(),
                        "Pending Admin Approval",
                        request.getSubmittedDate()
                    });
                }
            }
            for (Listing listing : appState.getMyListings()) {
                model.addRow(new Object[]{
                    listing.getTitle(),
                    listing.getCategory(),
                    "â‚º" + listing.getPrice(),
                    listing.getStatus().name(),
                    listing.getCreatedAt().toLocalDate()
                });
            }
            JTable table = new JTable(model);
            table.setRowHeight(34);
            requestsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            listingsPanel.removeAll();
            for (Listing listing : appState.getMyListings()) {
                listingsPanel.add(new CompactListingCard(listing, true));
            }
            favoritesPanel.removeAll();
            for (Listing listing : appState.getFavorites()) {
                favoritesPanel.add(new CompactListingCard(listing, false));
            }

            requestsPanel.revalidate();
            listingsPanel.revalidate();
            favoritesPanel.revalidate();
            repaint();
        }

        private void addSummary(JPanel panel, String key, String value) {
            panel.add(label(key, Theme.MUTED, 15, Font.PLAIN));
            JLabel valueLabel = label(value, value.equals("Premium") ? Theme.PRIMARY_DARK : Theme.TEXT, 15, Font.PLAIN);
            valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            panel.add(valueLabel);
        }
    }

    private class CompactListingCard extends JPanel {
        CompactListingCard(Listing listing, boolean showExpiry) {
            setLayout(new BorderLayout(18, 12));
            setBackground(Color.WHITE);
            add(new JLabel(loadImage(listing.getImagePath(), 110, 90)), BorderLayout.WEST);
            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(wrapLabel(listing.getTitle(), Theme.TEXT, 16, Font.BOLD, 300));
            text.add(Box.createVerticalStrut(8));
            text.add(label(listing.getPrice() == 0 ? "FREE" : "TL " + listing.getPrice(), Theme.TEXT, 16, Font.BOLD));
            text.add(Box.createVerticalStrut(8));
            if (showExpiry) {
                text.add(label("Expires in: " + listing.getRemainingDays() + " days", Theme.MUTED, 14, Font.PLAIN));
            } else {
                text.add(wrapLabel("Seller: " + appState.getSeller(listing).getFullName(), Theme.MUTED, 14, Font.PLAIN, 300));
                text.add(wrapLabel("Remaining Time: " + listing.getRemainingDays() + " days", Theme.MUTED, 14, Font.PLAIN, 300));
            }
            add(text, BorderLayout.CENTER);
        }
    }

    private class AdminPanel extends JPanel {
        private final JPanel cards = new JPanel();

        AdminPanel() {
            setBackground(Color.WHITE);
            setLayout(new BorderLayout());
            JPanel content = new JPanel();
            content.setBackground(Color.WHITE);
            content.setBorder(BorderFactory.createEmptyBorder(32, 60, 50, 60));
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.add(label("Admin Panel", Theme.TEXT, 28, Font.BOLD));
            content.add(Box.createVerticalStrut(36));
            content.add(label("Pending Sale Listings", Theme.TEXT, 22, Font.BOLD));
            content.add(Box.createVerticalStrut(20));
            cards.setOpaque(false);
            cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
            content.add(cards);
            add(content, BorderLayout.NORTH);
        }

        void refresh() {
            cards.removeAll();
            for (ListingRequest request : appState.getRequests()) {
                cards.add(new AdminRequestCard(request));
                cards.add(Box.createVerticalStrut(12));
            }
            cards.revalidate();
            cards.repaint();
        }
    }

    private class AdminRequestCard extends JPanel {
        AdminRequestCard(ListingRequest request) {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
            setLayout(new BorderLayout(16, 16));
            add(new JLabel(loadImage(request.getListing().getImagePath(), 70, 70)), BorderLayout.WEST);

            JPanel center = new JPanel(new GridLayout(1, 5, 18, 0));
            center.setOpaque(false);
            center.add(wrapLabel(request.getListing().getTitle(), Theme.TEXT, 15, Font.BOLD, 170));
            center.add(wrapLabel(appState.getSeller(request.getListing()).getFullName(), Theme.TEXT, 15, Font.PLAIN, 150));
            center.add(wrapLabel(request.getListing().getCategory(), Theme.TEXT, 15, Font.PLAIN, 140));
            center.add(label(request.getListing().getPrice() == 0 ? "FREE" : "TL " + request.getListing().getPrice(),
                request.getListing().getPrice() == 0 ? new Color(0xC13B3B) : Theme.TEXT, 15, Font.PLAIN));
            center.add(wrapLabel(request.getListing().getCondition(), Theme.TEXT, 15, Font.PLAIN, 120));
            add(center, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
            actions.setOpaque(false);
            JButton approve = UiFactory.primaryButton("Approve");
            JButton reject = UiFactory.secondaryButton("Reject");
            JButton delete = new JButton("Delete");
            delete.setContentAreaFilled(false);
            delete.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

            approve.addActionListener(e -> {
                appState.approveRequest(request);
                refreshAll();
            });
            reject.addActionListener(e -> {
                appState.rejectRequest(request, "Rejected by admin");
                refreshAll();
            });
            delete.addActionListener(e -> {
                appState.rejectRequest(request, "Deleted by admin");
                refreshAll();
            });

            actions.add(approve);
            actions.add(reject);
            actions.add(delete);
            add(actions, BorderLayout.EAST);
        }
    }

    private void showFiltersDialog() {
        JDialog dialog = baseDialog("Filters", 310, 620);
        JPanel body = dialogBody();
        body.add(filterGroup("Category", new String[]{"Books / Course Materials", "Electronics", "Clothing", "Vehicles"}, filterState.categories));
        body.add(Box.createVerticalStrut(20));
        body.add(filterGroup("Color", new String[]{"Black", "White", "Silver", "Red", "Blue", "Green"}, filterState.colors));
        body.add(Box.createVerticalStrut(20));
        body.add(filterGroup("Condition", new String[]{"New", "Like New", "Good", "Fair", "Poor"}, filterState.conditions));
        body.add(Box.createVerticalStrut(20));
        body.add(label("Premium Only", Theme.MUTED, 15, Font.BOLD));
        body.add(Box.createVerticalStrut(10));
        JCheckBox premiumOnly = new JCheckBox("Show only premium listings", filterState.premiumOnly);
        premiumOnly.setOpaque(false);
        premiumOnly.addActionListener(e -> filterState.premiumOnly = premiumOnly.isSelected());
        body.add(premiumOnly);
        dialog.add(body);
        dialog.setVisible(true);
        refreshAll();
    }

    private JPanel filterGroup(String title, String[] values, java.util.Set<String> target) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label(title, Theme.MUTED, 15, Font.BOLD));
        panel.add(Box.createVerticalStrut(10));
        for (String value : values) {
            JCheckBox box = new JCheckBox(value, target.contains(value));
            box.setOpaque(false);
            box.addActionListener(e -> {
                if (box.isSelected()) {
                    target.add(value);
                } else {
                    target.remove(value);
                }
            });
            panel.add(box);
            panel.add(Box.createVerticalStrut(6));
        }
        return panel;
    }

    private void showRegistrationDialog() {
        JDialog dialog = baseDialog("Create Account", 500, 560);
        JPanel body = dialogBody();
        for (String hint : List.of("Bilkent E-mail", "Name", "Surname", "Department", "Password", "Confirm Password")) {
            body.add(UiFactory.textField(hint));
            body.add(Box.createVerticalStrut(14));
        }
        JButton create = UiFactory.primaryButton("Create Account");
        create.addActionListener(e -> {
            dialog.dispose();
            showVerificationDialog();
        });
        body.add(create);
        dialog.add(body);
        dialog.setVisible(true);
    }

    private void showVerificationDialog() {
        JDialog dialog = baseDialog("Verification Code", 420, 220);
        JPanel body = dialogBody();
        body.add(label("Check your Bilkent e-mail to get your verification code", Theme.MUTED, 14, Font.PLAIN));
        body.add(Box.createVerticalStrut(18));
        body.add(UiFactory.textField("Enter verification code"));
        body.add(Box.createVerticalStrut(18));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton cancel = UiFactory.secondaryButton("Cancel");
        JButton ok = UiFactory.primaryButton("OK");
        cancel.addActionListener(e -> dialog.dispose());
        ok.addActionListener(e -> dialog.dispose());
        actions.add(cancel);
        actions.add(ok);
        body.add(actions);
        dialog.add(body);
        dialog.setVisible(true);
    }

    private void showResetPasswordDialog() {
        JDialog dialog = baseDialog("Forgot Password", 430, 210);
        JPanel body = dialogBody();
        body.add(label("Enter your Bilkent e-mail to receive a verification code", Theme.MUTED, 14, Font.PLAIN));
        body.add(Box.createVerticalStrut(18));
        JTextField emailField = UiFactory.textField("Bilkent E-mail");
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setPreferredSize(new Dimension(360, 46));
        emailField.setMaximumSize(new Dimension(360, 46));
        body.add(emailField);
        body.add(Box.createVerticalStrut(18));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton cancel = UiFactory.secondaryButton("Cancel");
        JButton ok = UiFactory.primaryButton("Send Code");
        cancel.addActionListener(e -> dialog.dispose());
        ok.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please enter your Bilkent e-mail.");
                return;
            }
            dialog.dispose();
            showResetPasswordVerificationDialog(email);
        });
        actions.add(cancel);
        actions.add(ok);
        body.add(actions);
        dialog.add(body);
        dialog.setVisible(true);
    }

    private void showResetPasswordVerificationDialog(String email) {
        JDialog dialog = baseDialog("Reset Password", 430, 320);
        JPanel body = dialogBody();
        body.add(label("Enter the verification code sent to " + email, Theme.MUTED, 14, Font.PLAIN));
        body.add(Box.createVerticalStrut(18));

        JTextField codeField = UiFactory.textField("Verification Code");
        JTextField newPasswordField = UiFactory.textField("New Password");
        JTextField confirmPasswordField = UiFactory.textField("Confirm New Password");

        for (JComponent component : List.of(codeField, newPasswordField, confirmPasswordField)) {
            component.setAlignmentX(Component.CENTER_ALIGNMENT);
            component.setPreferredSize(new Dimension(360, 46));
            component.setMaximumSize(new Dimension(360, 46));
            body.add(component);
            body.add(Box.createVerticalStrut(12));
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton cancel = UiFactory.secondaryButton("Cancel");
        JButton ok = UiFactory.primaryButton("OK");
        cancel.addActionListener(e -> dialog.dispose());
        ok.addActionListener(e -> {
            String code = codeField.getText().trim();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (code.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Confirm password must match the new password.");
                return;
            }
            JOptionPane.showMessageDialog(dialog, "Verification code sent to " + email + " and password reset completed.");
            dialog.dispose();
        });
        actions.add(cancel);
        actions.add(ok);
        body.add(actions);
        dialog.add(body);
        dialog.setVisible(true);
    }

    private void showSignOutDialog() {
        JDialog dialog = baseDialog("Confirm Sign Out", 320, 180);
        JPanel body = dialogBody();
        body.add(label("Are you sure you want to sign out?", Theme.MUTED, 14, Font.PLAIN));
        body.add(Box.createVerticalStrut(18));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton cancel = UiFactory.secondaryButton("Cancel");
        JButton signOut = UiFactory.primaryButton("Sign Out");
        cancel.addActionListener(e -> dialog.dispose());
        signOut.addActionListener(e -> {
            dialog.dispose();
            showPage(PAGE_LOGIN);
        });
        actions.add(cancel);
        actions.add(signOut);
        body.add(actions);
        dialog.add(body);
        dialog.setVisible(true);
    }

    private void showChangePasswordDialog() {
        JDialog dialog = baseDialog("Change Password", 420, 290);
        JPanel body = dialogBody();
        body.add(label("Enter your current password and choose a new one", Theme.MUTED, 14, Font.PLAIN));
        body.add(Box.createVerticalStrut(18));
        for (String hint : List.of("Current Password", "New Password", "New Password Again")) {
            body.add(UiFactory.textField(hint));
            body.add(Box.createVerticalStrut(12));
        }
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton cancel = UiFactory.secondaryButton("Cancel");
        JButton ok = UiFactory.primaryButton("OK");
        cancel.addActionListener(e -> dialog.dispose());
        ok.addActionListener(e -> dialog.dispose());
        actions.add(cancel);
        actions.add(ok);
        body.add(actions);
        dialog.add(body);
        dialog.setVisible(true);
    }

    private class SmallLogo extends JPanel {
        private final JLabel iconLabel;

        SmallLogo() {
            setOpaque(false);
            setPreferredSize(new Dimension(74, 74));
            setLayout(new BorderLayout());
            iconLabel = new JLabel(loadImage("assets/logo.png", 56, 56));
            add(iconLabel, BorderLayout.CENTER);
        }
    }

}

