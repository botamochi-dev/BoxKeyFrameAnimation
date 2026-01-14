package report;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * タイムライン画面とパラメータスライダーをまとめて管理するクラス。
 * UIコンポーネントの生成、ボタンのハンドリング、Boxへの値反映などを1か所に集約している。
 */
public class KeyFrameTimeline {
    private int currentFrame = 0;
    private int maxFrame = AnimationConfig.MAX_FRAME;
    private final KeyFrameData keyFrameData;

    private JLabel frameLabel;
    private TimelinePanel timelinePanel;
    private JButton playButton;

    private final Box box;
    private final AnimationPanel animationPanel;

    private ParameterSlider xSlider, ySlider, vxSlider, vySlider;
    private ParameterSlider angleSlider, angularVelocitySlider;
    private ParameterSlider widthSlider, heightSlider;
    private ParameterSlider massSlider, restitutionSlider, frictionSlider;
    private ParameterSlider linearDampingSlider, angularDampingSlider, gravitySlider;

    private boolean updatingSliders = false;

    private JPanel timelineViewPanel;
    private JPanel parameterPanel;

    public KeyFrameTimeline(Box box, AnimationPanel animationPanel) {
        this.box = box;
        this.animationPanel = animationPanel;
        this.keyFrameData = new KeyFrameData();

        keyFrameData.registerAllFromBox(0, box);

        createTimelineViewPanel();
        createParameterPanel();

        updateSlidersFromBox();
    }

    public JPanel getTimelineViewPanel() {
        return timelineViewPanel;
    }

    public JPanel getParameterPanel() {
        return parameterPanel;
    }

    private void createTimelineViewPanel() {
        timelineViewPanel = new JPanel(new BorderLayout());
        UIStyles.stylePanel(timelineViewPanel, "タイムライン");

        timelineViewPanel.add(createControlPanel(), BorderLayout.NORTH); // 再生・登録ボタン周り
        timelinePanel = new TimelinePanel(keyFrameData);
        timelinePanel.addPropertyChangeListener("currentFrame", evt -> {
            int frame = timelinePanel.getCurrentFrame();
            setCurrentFrame(frame);
        });
        JScrollPane timelineScroll = new JScrollPane(timelinePanel);
        timelinePanel.setParentScrollPane(timelineScroll);

        // JScrollPaneのキー操作を無効化して、TimelinePanelにイベントを渡す
        timelineScroll.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);

        timelineViewPanel.add(timelineScroll, BorderLayout.CENTER);

        // 矢印キーでフレームを移動できるようにキーバインディングを設定
        setupKeyBindings();
    }

    /**
     * 左右の矢印キーでフレームを移動できるようにキーバインディングを設定する。
     * TimelinePanel自体にフォーカスがある状態で動作する。
     */
    private void setupKeyBindings() {
        // TimelinePanel自身にキーバインディングを設定（WHEN_FOCUSEDを使用）
        InputMap inputMap = timelinePanel.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = timelinePanel.getActionMap();

        // 左矢印キー: 前のフレームへ移動
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "previousFrame");
        actionMap.put("previousFrame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!animationPanel.isPlaying()) {
                    setCurrentFrame(Math.max(0, currentFrame - 1));
                }
            }
        });

        // 右矢印キー: 次のフレームへ移動
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "nextFrame");
        actionMap.put("nextFrame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!animationPanel.isPlaying()) {
                    setCurrentFrame(Math.min(maxFrame, currentFrame + 1));
                }
            }
        });
    }

    private void createParameterPanel() {
        parameterPanel = new JPanel(new BorderLayout());
        UIStyles.stylePanel(parameterPanel, "オブジェクトの設定");

        // 多数のスライダーをスクロールできるようにまとめて配置
        JScrollPane scrollPane = createParameterScrollPane();
        parameterPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        UIStyles.stylePanel(panel, "フレーム操作");

        // 現在のフレーム表示
        frameLabel = new JLabel("現在のフレーム: 0");
        frameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        UIStyles.styleTitleLabel(frameLabel);
        panel.add(frameLabel);

        JPanel navigationPanel = new JPanel(new GridLayout(1, 4, 5, 5));

        JButton firstButton = new JButton("<<");
        firstButton.addActionListener(e -> setCurrentFrame(0));
        UIStyles.styleButton(firstButton);

        JButton prevButton = new JButton("<");
        prevButton.addActionListener(e -> setCurrentFrame(Math.max(0, currentFrame - 1)));
        UIStyles.styleButton(prevButton);

        JButton nextButton = new JButton(">");
        nextButton.addActionListener(e -> setCurrentFrame(Math.min(maxFrame, currentFrame + 1)));
        UIStyles.styleButton(nextButton);

        JButton lastButton = new JButton(">>");
        lastButton.addActionListener(e -> setCurrentFrame(maxFrame));
        UIStyles.styleButton(lastButton);

        navigationPanel.add(firstButton);
        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);
        navigationPanel.add(lastButton);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        JButton registerAllButton = new JButton("全登録"); // 現在のBox状態を一括で記録
        registerAllButton.addActionListener(e -> {
            if (!animationPanel.isPlaying()) {
                registerAllKeyFrames();
            }
        });
        UIStyles.styleButton(registerAllButton);

        JButton deleteButton = new JButton("削除"); // タイムライン上で選択したキーフレームを削除
        deleteButton.addActionListener(e -> {
            if (!animationPanel.isPlaying()) {
                deleteSelectedKeyFrame();
            }
        });
        UIStyles.styleButton(deleteButton);

        JButton clearButton = new JButton("全削除"); // すべてのキーフレームを空にする
        clearButton.addActionListener(e -> {
            if (!animationPanel.isPlaying()) {
                clearAllKeyFrames();
            }
        });
        UIStyles.styleButton(clearButton);

        playButton = new JButton("再生"); // タイムラインの手動再生ボタン
        UIStyles.styleButton(playButton);
        playButton.addActionListener(e -> {
            if (animationPanel.isPlaying()) {
                animationPanel.stop();
                playButton.setText("再生");
            } else {
                animationPanel.play();
                playButton.setText("停止");
            }
        });

        JButton resetButton = new JButton("リセット"); // 初期状態に戻したいときに利用
        UIStyles.styleButton(resetButton);
        resetButton.addActionListener(e -> {
            animationPanel.stop();
            playButton.setText("再生");
            box.goHome();
            setCurrentFrame(0);
            updateSlidersFromBox();
            animationPanel.repaint();
        });

        buttonPanel.add(registerAllButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(playButton);
        buttonPanel.add(resetButton);

        panel.add(javax.swing.Box.createVerticalStrut(5));
        panel.add(navigationPanel);
        panel.add(javax.swing.Box.createVerticalStrut(5));
        panel.add(buttonPanel);

        return panel;
    }

    private JScrollPane createParameterScrollPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 6, 10, 10));
        panel.setBackground(UIStyles.PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);

        xSlider = new ParameterSlider("初期X",
                AnimationConfig.POSITION_MIN, AnimationConfig.POSITION_X_MAX, 0, 1.0,
                val -> {
                    if (!updatingSliders && !animationPanel.isPlaying()) {
                        box.setX(val);
                        animationPanel.repaint();
                    }
                });

        ySlider = new ParameterSlider("初期Y",
                AnimationConfig.POSITION_MIN, AnimationConfig.POSITION_Y_MAX, 0, 1.0,
                val -> {
                    if (!updatingSliders && !animationPanel.isPlaying()) {
                        box.setY(val);
                        animationPanel.repaint();
                    }
                });

        panel.add(createCompactParamGroup("初期座標",
                xSlider.getLabel(), xSlider.getTextField(), xSlider.getSlider(),
                ySlider.getLabel(), ySlider.getTextField(), ySlider.getSlider()));

        vxSlider = new ParameterSlider("初期Vx",
                AnimationConfig.VELOCITY_MIN, AnimationConfig.VELOCITY_MAX, 0, 1.0,
                val -> {
                    if (!updatingSliders && !animationPanel.isPlaying()) {
                        box.setVx(val);
                    }
                });

        vySlider = new ParameterSlider("初期Vy",
                AnimationConfig.VELOCITY_MIN, AnimationConfig.VELOCITY_MAX, 0, 1.0,
                val -> {
                    if (!updatingSliders && !animationPanel.isPlaying()) {
                        box.setVy(val);
                    }
                });

        panel.add(createCompactParamGroup("初期速度",
                vxSlider.getLabel(), vxSlider.getTextField(), vxSlider.getSlider(),
                vySlider.getLabel(), vySlider.getTextField(), vySlider.getSlider()));

        angleSlider = new ParameterSlider("初期角度",
                AnimationConfig.ANGLE_MIN, AnimationConfig.ANGLE_MAX, 30, 1.0,
                val -> {
                    if (!updatingSliders && !animationPanel.isPlaying()) {
                        box.setAngle(Math.toRadians(val));
                        animationPanel.repaint();
                    }
                });

        angularVelocitySlider = new ParameterSlider("初期角速度",
                AnimationConfig.ANGULAR_VELOCITY_MIN, AnimationConfig.ANGULAR_VELOCITY_MAX, 10, 100.0,
                val -> {
                    if (!updatingSliders && !animationPanel.isPlaying()) {
                        box.setAngularVelocity(val);
                    }
                });

        panel.add(createCompactParamGroup("初期回転",
                angleSlider.getLabel(), angleSlider.getTextField(), angleSlider.getSlider(),
                angularVelocitySlider.getLabel(), angularVelocitySlider.getTextField(),
                angularVelocitySlider.getSlider()));

        widthSlider = createPhysicsParameterCompact("幅",
                10, 200, 40, 1.0,
                val -> {
                    box.setWidth(val);
                    animationPanel.repaint();
                }, KeyFrameData.ParamType.WIDTH, panel);

        heightSlider = createPhysicsParameterCompact("高さ",
                10, 200, 40, 1.0,
                val -> {
                    box.setHeight(val);
                    animationPanel.repaint();
                }, KeyFrameData.ParamType.HEIGHT, panel);

        massSlider = createPhysicsParameterCompact("質量",
                AnimationConfig.MASS_MIN, AnimationConfig.MASS_MAX, 100, 100.0,
                val -> box.setMass(val), KeyFrameData.ParamType.MASS, panel);

        restitutionSlider = createPhysicsParameterCompact("反発係数",
                AnimationConfig.COEFFICIENT_MIN, AnimationConfig.COEFFICIENT_MAX, 50, 100.0,
                val -> box.setRestitution(val), KeyFrameData.ParamType.RESTITUTION, panel);

        frictionSlider = createPhysicsParameterCompact("摩擦係数",
                AnimationConfig.COEFFICIENT_MIN, AnimationConfig.COEFFICIENT_MAX, 30, 100.0,
                val -> box.setFriction(val), KeyFrameData.ParamType.FRICTION, panel);

        linearDampingSlider = createPhysicsParameterCompact("移動減衰",
                AnimationConfig.COEFFICIENT_MIN, AnimationConfig.COEFFICIENT_MAX, 99, 100.0,
                val -> box.setLinearDamping(val), KeyFrameData.ParamType.LINEAR_DAMPING, panel);

        angularDampingSlider = createPhysicsParameterCompact("回転減衰",
                AnimationConfig.COEFFICIENT_MIN, AnimationConfig.COEFFICIENT_MAX, 90, 100.0,
                val -> box.setAngularDamping(val), KeyFrameData.ParamType.ANGULAR_DAMPING, panel);

        gravitySlider = createPhysicsParameterCompact("重力",
                AnimationConfig.GRAVITY_MIN, AnimationConfig.GRAVITY_MAX, 30, 100.0,
                val -> box.setG(val), KeyFrameData.ParamType.GRAVITY, panel);

        return scrollPane;
    }

    private JPanel createCompactParamGroup(String title, Component... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        UIStyles.stylePanel(panel, title);

        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            if (comp instanceof JLabel && i + 1 < components.length && components[i + 1] instanceof JTextField) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                row.setOpaque(false);
                row.add(comp);
                row.add(components[i + 1]);
                panel.add(row);
                i++;
            } else {
                panel.add(comp);
                if (comp instanceof JSlider) {
                    ((JSlider) comp).setPreferredSize(new Dimension(180, 30));
                }
            }
        }
        return panel;
    }

    private ParameterSlider createPhysicsParameterCompact(String name, int min, int max, int init, double scale,
            java.util.function.Consumer<Double> setter, KeyFrameData.ParamType paramType, JPanel parentPanel) {
        // キーフレームへ登録できる物理パラメータ用の汎用スライダー
        ParameterSlider slider = new ParameterSlider(name, min, max, init, scale, val -> {
            if (!updatingSliders && !animationPanel.isPlaying()) {
                setter.accept(val);
            }
        });

        JButton registerButton = new JButton("登録");
        registerButton.setFont(UIStyles.FONT_REGULAR);
        UIStyles.styleButton(registerButton);
        registerButton.addActionListener(e -> {
            if (!animationPanel.isPlaying()) {
                double value = 0;
                switch (paramType) {
                    case WIDTH:
                        value = box.getWidth();
                        break;
                    case HEIGHT:
                        value = box.getHeight();
                        break;
                    case MASS:
                        value = box.getMass();
                        break;
                    case RESTITUTION:
                        value = box.getRestitution();
                        break;
                    case FRICTION:
                        value = box.getFriction();
                        break;
                    case LINEAR_DAMPING:
                        value = box.getLinearDamping();
                        break;
                    case ANGULAR_DAMPING:
                        value = box.getAngularDamping();
                        break;
                    case GRAVITY:
                        value = box.getG();
                        break;
                }
                registerParameter(paramType, value);
            }
        });

        parentPanel.add(createCompactParamGroup(name, slider.getLabel(), slider.getTextField(), slider.getSlider(),
                registerButton));

        return slider;
    }

    private JPanel createParamGroup(String title, Component... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        UIStyles.stylePanel(panel, title);
        for (Component comp : components) {
            panel.add(comp);
        }
        return panel;
    }

    public void setCurrentFrame(int frame) {
        if (frame < 0)
            frame = 0;
        if (frame > maxFrame)
            frame = maxFrame;

        if (animationPanel.isPlaying()) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            boolean calledFromActionPerformed = false;
            for (StackTraceElement element : stackTrace) {
                if (element.getClassName().equals("report.AnimationPanel")
                        && element.getMethodName().equals("actionPerformed")) {
                    calledFromActionPerformed = true;
                    break;
                }
            }

            if (!calledFromActionPerformed) {
                animationPanel.stop();
                if (playButton != null) {
                    playButton.setText("再生");
                }
            }
        }

        currentFrame = frame;
        frameLabel.setText("現在のフレーム: " + currentFrame);
        timelinePanel.setCurrentFrame(currentFrame);

        if (!animationPanel.isPlaying()) {
            simulateToFrame(frame); // 再生中以外は、Boxの姿をこのフレームに合わせて確認できるようにする
        }

        updateSlidersFromBox();
        animationPanel.setFrameCount(frame);
        animationPanel.repaint();
    }

    public void applyKeyFrameData(int frame) {
        keyFrameData.applyToBox(frame, box);
    }

    public void updatePlayButtonText(String text) {
        if (playButton != null) {
            playButton.setText(text);
        }
    }

    /**
     * Boxを0フレーム目から順番に再生して、ターゲットフレームの状態を再現する。
     * 途中にもキーフレームがあれば applyToBox で即時反映されるため、線形補間と組み合わせたプレビューができる。
     */
    private void simulateToFrame(int targetFrame) {
        box.goHome();
        keyFrameData.applyToBox(0, box);

        for (int i = 0; i < targetFrame; i++) {
            keyFrameData.applyToBox(i, box);
            box.next();
        }

        keyFrameData.applyToBox(targetFrame, box);
    }

    // Boxの現在値を各スライダーへ反映。ユーザーが値を直接書き換えても視覚的にズレないようにする
    private void updateSlidersFromBox() {
        updatingSliders = true;

        xSlider.setValue(box.getX());
        ySlider.setValue(box.getY());
        vxSlider.setValue(box.getVx());
        vySlider.setValue(box.getVy());
        angleSlider.setValue(Math.toDegrees(box.getAngle()) % 360);
        angularVelocitySlider.setValue(box.getAngularVelocity());
        widthSlider.setValue(box.getWidth());
        heightSlider.setValue(box.getHeight());
        massSlider.setValue(box.getMass());
        restitutionSlider.setValue(box.getRestitution());
        frictionSlider.setValue(box.getFriction());
        linearDampingSlider.setValue(box.getLinearDamping());
        angularDampingSlider.setValue(box.getAngularDamping());
        gravitySlider.setValue(box.getG());

        updatingSliders = false;
    }

    // ボタン1つで位置・速度・物理パラメータを全部登録するヘルパー
    private void registerAllKeyFrames() {
        keyFrameData.registerAllFromBox(currentFrame, box);
        timelinePanel.repaint();
        JOptionPane.showMessageDialog(timelineViewPanel,
                "フレーム " + currentFrame + " に全パラメータを登録しました",
                "キーフレーム登録", JOptionPane.INFORMATION_MESSAGE);
    }

    private void registerParameter(KeyFrameData.ParamType type, double value) {
        keyFrameData.registerKeyFrame(type, currentFrame, value);
        timelinePanel.repaint();
    }

    private double getValueFromBox(KeyFrameData.ParamType type) {
        switch (type) {
            case MASS:
                return box.getMass();
            case RESTITUTION:
                return box.getRestitution();
            case FRICTION:
                return box.getFriction();
            case LINEAR_DAMPING:
                return box.getLinearDamping();
            case ANGULAR_DAMPING:
                return box.getAngularDamping();
            case GRAVITY:
                return box.getG();
            default:
                return 0.0;
        }
    }

    private void deleteSelectedKeyFrame() {
        if (keyFrameData.getSelectedParamType() != null && keyFrameData.getSelectedFrame() != null) {
            String paramName = keyFrameData.getSelectedParamType().getDisplayName();
            int frame = keyFrameData.getSelectedFrame();

            if (frame == 0) {
                JOptionPane.showMessageDialog(timelineViewPanel,
                        "0フレーム目は削除できません。\n初期値にリセットするには、値を変更して登録してください。",
                        "エラー", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int result = JOptionPane.showConfirmDialog(timelineViewPanel,
                    "フレーム " + frame + " の " + paramName + " を削除しますか?",
                    "キーフレーム削除", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                keyFrameData.deleteSelectedKeyFrame();
                timelinePanel.repaint();
            }
        } else {
            JOptionPane.showMessageDialog(timelineViewPanel,
                    "削除するキーフレームを選択してください",
                    "エラー", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAllKeyFrames() {
        int result = JOptionPane.showConfirmDialog(timelineViewPanel,
                "全てのキーフレームを削除しますか?\n（0フレーム目の初期値は保持されます）",
                "全削除", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            for (KeyFrameData.ParamType type : KeyFrameData.ParamType.values()) {
                var frames = keyFrameData.getKeyFrames(type);
                frames.entrySet().removeIf(entry -> entry.getKey() != 0);
            }
            keyFrameData.clearSelection();
            timelinePanel.repaint();
            JOptionPane.showMessageDialog(timelineViewPanel, "0フレーム目以外のキーフレームを削除しました");
        }
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public KeyFrameData getKeyFrameData() {
        return keyFrameData;
    }
}
