package report;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.InputStream;

/**
 * 色・フォント・ボーダーなど、UI全体の見た目を統一するヘルパークラス。
 * Swingはデフォルトだと味気ないため、ここでスタイル指定をまとめておく。
 */
public class UIStyles {
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color DANGER_COLOR = new Color(231, 76, 60);

    public static final Color BACKGROUND_LIGHT = new Color(245, 246, 250);
    public static final Color PANEL_BACKGROUND = new Color(255, 255, 255);
    public static final Color PANEL_BORDER = new Color(220, 224, 229);

    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    public static final Color TEXT_ON_DARK = new Color(236, 240, 241);

    public static final Color TIMELINE_BG = new Color(44, 47, 51);
    public static final Color TIMELINE_TRACK_EVEN = new Color(54, 57, 63);
    public static final Color TIMELINE_TRACK_ODD = new Color(47, 49, 54);
    public static final Color TIMELINE_LABEL_BG = new Color(32, 34, 37);
    public static final Color TIMELINE_GRID = new Color(60, 63, 68);
    public static final Color TIMELINE_CURRENT = new Color(52, 152, 219);
    public static final Color KEYFRAME_COLOR = new Color(241, 196, 15);
    public static final Color KEYFRAME_SELECTED = new Color(231, 76, 60);

    // カスタムフォントをリソースから読み込む
    public static final Font FONT_REGULAR = loadFont("/resources/NotoSansJP-Regular.ttf", 12f);
    public static final Font FONT_BOLD = loadFont("/resources/NotoSansJP-Bold.ttf", 12f);
    public static final Font FONT_TITLE = loadFont("/resources/NotoSansJP-Bold.ttf", 16f);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);

    /**
     * フォントファイルをクラスパスから読み込む。
     * resourcesフォルダに配置されたTrueTypeフォント(.ttf)を読み込み、
     * 指定されたサイズで利用可能なFontオブジェクトとして返す。
     * 
     * @param path クラスパス上のフォントファイルのパス（例: "/resources/font.ttf"）
     * @param size フォントサイズ（ポイント単位）
     * @return 読み込んだFont。失敗時はDialog(Sans Serif)をフォールバックとして返す
     */
    private static Font loadFont(String path, float size) {
        try {
            // クラスパスからフォントファイルを読み込む
            InputStream is = UIStyles.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("フォントファイルが見つかりません: " + path);
                return new Font("Dialog", Font.PLAIN, (int) size);
            }

            // TrueTypeフォントとして登録し、指定サイズで派生させる
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);

            // システムのGraphicsEnvironmentに登録（これで他のコンポーネントでも利用可能に）
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            is.close();
            return font;
        } catch (Exception e) {
            System.err.println("フォントの読み込みに失敗しました: " + path);
            e.printStackTrace();
            // フォールバック: デフォルトのDialogフォントを返す
            return new Font("Dialog", Font.PLAIN, (int) size);
        }
    }

    private UIStyles() {
    }

    public static void styleButton(JButton button) {
        button.setFont(FONT_REGULAR);
        button.setForeground(TEXT_ON_DARK);
        button.setBackground(PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 16, 8, 16));

        addHoverEffect(button, PRIMARY_COLOR, new Color(52, 152, 219));
    }

    private static void addHoverEffect(JButton button, Color normal, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hover);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normal);
            }
        });
    }

    public static void stylePanel(JPanel panel, String title) {
        panel.setBackground(PANEL_BACKGROUND);
        if (title != null && !title.isEmpty()) {
            TitledBorder border = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(PANEL_BORDER, 1),
                    title,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    FONT_BOLD,
                    TEXT_PRIMARY);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    border,
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        }
    }

    public static void styleLabel(JLabel label) {
        label.setFont(FONT_REGULAR);
        label.setForeground(TEXT_PRIMARY);
    }

    public static void styleTitleLabel(JLabel label) {
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
    }

    public static void styleSlider(JSlider slider) {
        slider.setBackground(PANEL_BACKGROUND);
        slider.setForeground(PRIMARY_COLOR);
    }
}
