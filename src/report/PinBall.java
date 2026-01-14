package report;

import javax.swing.*;

/**
 * エントリーポイント。Swingアプリを起動し、メインフレームを表示するだけのクラス。
 */
public class PinBall {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ApplicationFrame frame = new ApplicationFrame();
            frame.setVisible(true);
        });
    }
}

class ApplicationFrame extends JFrame {
    private final AnimationPanel animationPanel;
    private final KeyFrameTimeline timeline;

    /**
     * レイアウト構築とBox初期化を担当するアプリ本体。
     */
    public ApplicationFrame() {
        super("Box KeyFrame Animation");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(AnimationConfig.WINDOW_WIDTH, AnimationConfig.WINDOW_HEIGHT);

        animationPanel = new AnimationPanel();
        timeline = new KeyFrameTimeline(animationPanel.getBox(), animationPanel);
        animationPanel.setTimeline(timeline);

        setupLayout();
        initializeBoxPosition();
    }

    private void setupLayout() {
        getContentPane().setBackground(UIStyles.BACKGROUND_LIGHT);

        JSplitPane topSplitPane = createTopSplitPane();
        JPanel bottomPanel = timeline.getParameterPanel();
        JSplitPane mainSplitPane = createMainSplitPane(topSplitPane, bottomPanel);

        add(mainSplitPane);
    }

    private JSplitPane createTopSplitPane() {
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                timeline.getTimelineViewPanel(),
                animationPanel);
        splitPane.setDividerLocation(AnimationConfig.TIMELINE_WIDTH);
        splitPane.setResizeWeight(AnimationConfig.SPLIT_PANE_HORIZONTAL_WEIGHT);
        splitPane.setBorder(null);
        return splitPane;
    }

    private JSplitPane createMainSplitPane(JSplitPane topPane, JPanel bottomPane) {
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                topPane,
                bottomPane);
        splitPane.setDividerLocation(AnimationConfig.TIMELINE_HEIGHT);
        splitPane.setResizeWeight(AnimationConfig.SPLIT_PANE_VERTICAL_WEIGHT);
        splitPane.setBorder(null);
        return splitPane;
    }

    private void initializeBoxPosition() {
        SwingUtilities.invokeLater(() -> {
            animationPanel.getBox().goHome();
            timeline.setCurrentFrame(0);
            animationPanel.repaint();
        });
    }
}