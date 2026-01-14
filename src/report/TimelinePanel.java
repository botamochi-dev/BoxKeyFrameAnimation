package report;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * タイムライン（パラメータごとの横並びトラック）を描画するパネル。
 * クリック位置からフレーム・トラックを割り出し、KeyFrameDataへの登録や選択を行う。
 */
public class TimelinePanel extends JPanel {
    private final KeyFrameData keyFrameData;
    private int currentFrame = 0;
    private int maxFrame = AnimationConfig.MAX_FRAME;
    private JScrollPane parentScrollPane;

    private static final int FRAME_WIDTH = AnimationConfig.TIMELINE_FRAME_WIDTH;
    private static final int ROW_HEIGHT = AnimationConfig.TIMELINE_ROW_HEIGHT;
    private static final int LABEL_WIDTH = AnimationConfig.TIMELINE_LABEL_WIDTH;
    private static final int KEYFRAME_SIZE = AnimationConfig.TIMELINE_KEYFRAME_SIZE;

    public TimelinePanel(KeyFrameData keyFrameData) {
        this.keyFrameData = keyFrameData;
        int panelWidth = LABEL_WIDTH + maxFrame * FRAME_WIDTH + 50;
        setPreferredSize(new Dimension(panelWidth, ROW_HEIGHT * KeyFrameData.ParamType.values().length + 40));
        setBackground(UIStyles.TIMELINE_BG);

        // キーイベントを受け取れるようにする
        setFocusable(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }

            // マウスクリック時にフォーカスを取得
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    private void handleMouseClick(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (y < 30) {
            int frame = (x - LABEL_WIDTH + FRAME_WIDTH / 2) / FRAME_WIDTH;
            if (frame >= 0 && frame <= maxFrame) {
                currentFrame = frame;
                firePropertyChange("currentFrame", -1, currentFrame);
                repaint();
            }
            return;
        }

        int row = (y - 30) / ROW_HEIGHT;
        if (row >= 0 && row < KeyFrameData.ParamType.values().length) {
            KeyFrameData.ParamType type = KeyFrameData.ParamType.values()[row];
            int frame = (x - LABEL_WIDTH + FRAME_WIDTH / 2) / FRAME_WIDTH;

            if (frame >= 0 && frame <= maxFrame) {
                if (keyFrameData.hasKeyFrame(type, frame)) {
                    keyFrameData.selectKeyFrame(type, frame);
                } else {
                    keyFrameData.clearSelection();
                }
                repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawHeader(g2d);
        drawTracks(g2d);
        drawKeyFrames(g2d);
        drawCurrentFrameLine(g2d);
    }

    private void drawHeader(Graphics2D g2d) {
        g2d.setColor(UIStyles.TIMELINE_LABEL_BG);
        g2d.fillRect(0, 0, getWidth(), 30);

        g2d.setColor(UIStyles.TEXT_ON_DARK);
        g2d.setFont(UIStyles.FONT_REGULAR);

        for (int i = 0; i <= maxFrame; i += 5) {
            // 現在のフレームと重ならない場合のみ表示
            if (i != currentFrame) {
                int x = LABEL_WIDTH + i * FRAME_WIDTH;
                g2d.drawString(String.valueOf(i), x - 5, 18);
            }
        }
    }

    private void drawTracks(Graphics2D g2d) {
        KeyFrameData.ParamType[] types = KeyFrameData.ParamType.values();

        for (int i = 0; i < types.length; i++) {
            int y = 30 + i * ROW_HEIGHT;

            if (i % 2 == 0) {
                g2d.setColor(UIStyles.TIMELINE_TRACK_EVEN);
            } else {
                g2d.setColor(UIStyles.TIMELINE_TRACK_ODD);
            }
            g2d.fillRect(0, y, getWidth(), ROW_HEIGHT);

            g2d.setColor(UIStyles.TIMELINE_LABEL_BG);
            g2d.fillRect(0, y, LABEL_WIDTH, ROW_HEIGHT);

            g2d.setColor(UIStyles.TEXT_ON_DARK);
            g2d.setFont(UIStyles.FONT_REGULAR);
            g2d.drawString(types[i].getDisplayName(), 8, y + 22);

            g2d.setColor(UIStyles.TIMELINE_GRID);
            g2d.drawLine(0, y, getWidth(), y);
            g2d.drawLine(LABEL_WIDTH, y, LABEL_WIDTH, y + ROW_HEIGHT);

            for (int f = 0; f <= maxFrame; f++) {
                int x = LABEL_WIDTH + f * FRAME_WIDTH;
                if (f % 5 == 0) {
                    g2d.setColor(new Color(255, 165, 0));
                } else {
                    g2d.setColor(UIStyles.TIMELINE_GRID);
                }
                g2d.drawLine(x, y, x, y + ROW_HEIGHT);
            }
        }
    }

    private void drawKeyFrames(Graphics2D g2d) {
        KeyFrameData.ParamType[] types = KeyFrameData.ParamType.values();

        for (int i = 0; i < types.length; i++) {
            KeyFrameData.ParamType type = types[i];
            int y = 30 + i * ROW_HEIGHT + ROW_HEIGHT / 2;

            TreeMap<Integer, Double> frames = keyFrameData.getKeyFrames(type);
            for (Map.Entry<Integer, Double> entry : frames.entrySet()) {
                int frame = entry.getKey();
                int x = LABEL_WIDTH + frame * FRAME_WIDTH;

                if (keyFrameData.isSelected(type, frame)) {
                    g2d.setColor(UIStyles.KEYFRAME_SELECTED);
                } else {
                    g2d.setColor(UIStyles.KEYFRAME_COLOR);
                }

                int[] xPoints = { x, x + KEYFRAME_SIZE / 2, x, x - KEYFRAME_SIZE / 2 };
                int[] yPoints = { y - KEYFRAME_SIZE / 2, y, y + KEYFRAME_SIZE / 2, y };
                g2d.fillPolygon(xPoints, yPoints, 4);

                g2d.setColor(Color.BLACK);
                g2d.drawPolygon(xPoints, yPoints, 4);
            }
        }
    }

    private void drawCurrentFrameLine(Graphics2D g2d) {
        int x = LABEL_WIDTH + currentFrame * FRAME_WIDTH;

        g2d.setColor(UIStyles.TIMELINE_CURRENT);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x, 30, x, getHeight());

        int[] xPoints = { x - 5, x + 5, x };
        int[] yPoints = { 22, 22, 30 };
        g2d.fillPolygon(xPoints, yPoints, 3);

        // 現在のフレーム数を太字のオレンジ色で表示
        String frameText = String.valueOf(currentFrame);
        g2d.setFont(UIStyles.FONT_BOLD);
        g2d.setColor(new Color(255, 165, 0));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(frameText);
        g2d.drawString(frameText, x - textWidth / 2, 18);
    }

    public void setCurrentFrame(int frame) {
        if (frame >= 0 && frame <= maxFrame) {
            this.currentFrame = frame;

            if (parentScrollPane != null) {
                int frameX = LABEL_WIDTH + frame * FRAME_WIDTH;
                Rectangle visibleRect = parentScrollPane.getViewport().getViewRect();
                int viewportWidth = visibleRect.width;

                if (frameX > visibleRect.x + viewportWidth - 100) {
                    int newX = Math.max(0, frameX - viewportWidth / 2);
                    parentScrollPane.getViewport().setViewPosition(new Point(newX, visibleRect.y));
                } else if (frameX < visibleRect.x + LABEL_WIDTH) {
                    int newX = Math.max(0, frameX - LABEL_WIDTH - 50);
                    parentScrollPane.getViewport().setViewPosition(new Point(newX, visibleRect.y));
                }
            }

            repaint();
        }
    }

    public void setParentScrollPane(JScrollPane scrollPane) {
        this.parentScrollPane = scrollPane;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setMaxFrame(int maxFrame) {
        this.maxFrame = maxFrame;
        setPreferredSize(new Dimension(LABEL_WIDTH + maxFrame * FRAME_WIDTH + 50,
                ROW_HEIGHT * KeyFrameData.ParamType.values().length + 40));
        revalidate();
        repaint();
    }
}
