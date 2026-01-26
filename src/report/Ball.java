package report;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * 円形のボールを単純な反射物理で動かすクラス。
 * Boxよりもシンプルなモデルなので、物理更新の流れを練習したいときに役立つ。
 */
public class Ball {
    private double radius = 10.0;
    private double x = 0.0;
    private double y = 0.0;
    private double vx = 15.0;
    private double vy = -15.0;
    private Color color = Color.BLACK;
    private double gravity = 0.5;
    private double restitutionCoefficient = 0.8;
    private JPanel panel;

    public Ball(JPanel panel) {
        this.panel = panel;
    }

    public Ball(double radius, double x, double y, double vx, double vy, Color color, JPanel panel) {
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.panel = panel;
    }

    public double getRadius() {
        return radius;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setVxVy(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public void setRestitutionCoefficient(double restitutionCoefficient) {
        this.restitutionCoefficient = restitutionCoefficient;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(Graphics graphics) {
        Color prevColor = graphics.getColor();
        graphics.setColor(color);
        graphics.fillOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
        graphics.setColor(prevColor);
    }

    public void goHome() {
        x = radius;
        y = panel.getHeight() - radius;
    }

    // 座標、速度更新
    public void next() {
        int width = panel.getWidth();
        int height = panel.getHeight();

        x = x + vx;
        y = y + vy;
        if (x < radius) {
            x = radius;
            vx = -vx * restitutionCoefficient;
        }

        if (x + radius > width) {
            x = width - radius;
            vx = -vx * restitutionCoefficient;
        }

        if (y < radius) {
            y = radius;
            vy = -vy * restitutionCoefficient;
        }

        if (y + radius > height) {
            y = height - radius;
            vy = -vy * restitutionCoefficient;
        }

        vy = vy + gravity;
    }
}
