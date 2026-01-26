/**
 * J2300061 窪田渚侑
 * 課題A
 */
package report;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 実際に物体を描画し、1フレームごとの物理シミュレーションを行うパネル。
 * SwingのTimerで一定間隔ごとにactionPerformedが呼ばれ、Boxの状態を更新する。
 */
public class AnimationPanel extends JPanel implements ActionListener {
    private final Box box;
    private final Timer timer;
    private int frameCount;
    private KeyFrameTimeline timeline;

    public AnimationPanel() {
        this.timer = new Timer(AnimationConfig.FRAME_INTERVAL_MS, this);
        this.box = new Box(this);
        this.frameCount = 0;
        setBackground(Color.WHITE);
    }

    public Box getBox() {
        return box;
    }

    public void setTimeline(KeyFrameTimeline timeline) {
        this.timeline = timeline;
    }

    public boolean isPlaying() {
        return timer.isRunning();
    }

    public void play() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void playFromBeginning() {
        stop();
        box.goHome();
        frameCount = 0;
        if (timeline != null) {
            timeline.setCurrentFrame(0);
        }
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void setFrameCount(int count) {
        this.frameCount = count;
    }

    public int getFrameCount() {
        return frameCount;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawGridAndAxes(g2d);
        box.draw(g);
        drawStatusInfo(g);
    }

    private void drawGridAndAxes(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        g2d.setColor(UIStyles.PANEL_BORDER);
        g2d.setStroke(new BasicStroke(1));
        int gridSpacing = 50;

        for (int x = 0; x < width; x += gridSpacing) {
            g2d.drawLine(x, 0, x, height);
        }

        for (int y = 0; y < height; y += gridSpacing) {
            g2d.drawLine(0, y, width, y);
        }

        g2d.setColor(UIStyles.DANGER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0, 0, width, 0);

        g2d.setColor(UIStyles.SUCCESS_COLOR);
        g2d.drawLine(0, 0, 0, height);

        g2d.setColor(UIStyles.TEXT_PRIMARY);
        g2d.setFont(UIStyles.FONT_BOLD);
        g2d.drawString("X", width - 20, 15);
        g2d.drawString("Y", 5, height - 5);

        g2d.setStroke(new BasicStroke(1));
    }

    private void drawStatusInfo(Graphics g) {
        g.setColor(UIStyles.TEXT_PRIMARY);
        g.setFont(UIStyles.FONT_MONO);

        int x = 10;
        int y = 20;
        int lineHeight = 20;

        g.drawString("Frame: " + frameCount, x, y);
        y += lineHeight;
        g.drawString(String.format("Position: (%.1f, %.1f)", box.getX(), box.getY()), x, y);
        y += lineHeight;
        g.drawString(String.format("Velocity: (%.1f, %.1f)", box.getVx(), box.getVy()), x, y);
        y += lineHeight;
        g.drawString(String.format("Rotation: %.1f°", Math.toDegrees(box.getAngle())), x, y);
        y += lineHeight;
        g.drawString(String.format("Angular Velocity: %.2f", box.getAngularVelocity()), x, y);
    }

    public void updatePhysicsAndRender() {
        if (frameCount >= AnimationConfig.MAX_FRAME) {
            stop();
            box.goHome();
            frameCount = 0;
            if (timeline != null) {
                timeline.updatePlayButtonText("再生");
                timeline.setCurrentFrame(0);
            }
            repaint();
            return;
        }

        if (timeline != null) {
            timeline.applyKeyFrameData(frameCount);
        }

        box.next();
        frameCount++;

        if (timeline != null) {
            timeline.setCurrentFrame(frameCount);
        }

        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updatePhysicsAndRender();
    }
}
