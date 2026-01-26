package report;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

/**
 * 長方形の剛体を模したクラス。位置・速度、角度や角速度も持つ。
 */
public class Box {
    private double width = 40.0;
    private double height = 40.0;

    private double x = 0.0;
    private double y = 0.0;

    private double vx = 16.0;
    private double vy = -30.0;

    private double initialVx = vx;
    private double initialVy = vy;

    private double angle = 30.0;
    private double initialAngle = angle;

    private double angularVelocity = 0.1;
    private double initialAngularVelocity = angularVelocity;

    private Color color = Color.BLUE;

    private double g = 0.3;
    private double mass = 1;
    private double restitution = 0.999;
    private double friction = 0.3;
    private double linearDamping = 0.99;
    private double angularDamping = 0.9;

    private JPanel panel;
    private double timeScale = 1.0;
    private static final double BASE_INTERVAL = 33.0;

    public static class BoxState {
        public double x, y, vx, vy, angle, angularVelocity;
        public double width, height;
        public double mass, restitution, friction, linearDamping, angularDamping, g;

        public BoxState(Box box) {
            this.x = box.x;
            this.y = box.y;
            this.vx = box.vx;
            this.vy = box.vy;
            this.angle = box.angle;
            this.angularVelocity = box.angularVelocity;
            this.width = box.width;
            this.height = box.height;
            this.mass = box.mass;
            this.restitution = box.restitution;
            this.friction = box.friction;
            this.linearDamping = box.linearDamping;
            this.angularDamping = box.angularDamping;
            this.g = box.g;
        }
    }

    public Box(JPanel panel) {
        this.panel = panel;
        this.initialVx = this.vx;
        this.initialVy = this.vy;
        this.initialAngle = this.angle;
        this.initialAngularVelocity = this.angularVelocity;
    }

    public Box(double width, double height, double x, double y, double vx, double vy, Color color, JPanel panel) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.initialVx = vx;
        this.initialVy = vy;
        this.initialAngle = this.angle;
        this.initialAngularVelocity = this.angularVelocity;
        this.color = color;
        this.panel = panel;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
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

    public double getAngle() {
        return angle;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
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

    public void setAngle(double angle) {
        this.angle = angle;
        this.initialAngle = angle;
    }

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
        this.initialAngularVelocity = angularVelocity;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getG() {
        return g;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getRestitution() {
        return restitution;
    }

    public void setRestitution(double restitution) {
        this.restitution = restitution;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public double getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(double linearDamping) {
        this.linearDamping = linearDamping;
    }

    public double getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(double angularDamping) {
        this.angularDamping = angularDamping;
    }

    public void setFrameInterval(int intervalMs) {
        this.timeScale = intervalMs / BASE_INTERVAL;
    }

    private double getInertia() {
        return (1.0 / 12.0) * mass * (width * width + height * height);
    }

    private double[][] getVertices() {
        double hw = width / 2.0;
        double hh = height / 2.0;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double[][] vertices = new double[4][2];

        vertices[0][0] = x + (-hw * cos - (-hh) * sin);
        vertices[0][1] = y + (-hw * sin + (-hh) * cos);

        vertices[1][0] = x + (hw * cos - (-hh) * sin);
        vertices[1][1] = y + (hw * sin + (-hh) * cos);

        vertices[2][0] = x + (hw * cos - hh * sin);
        vertices[2][1] = y + (hw * sin + hh * cos);

        vertices[3][0] = x + (-hw * cos - hh * sin);
        vertices[3][1] = y + (-hw * sin + hh * cos);

        return vertices;
    }

    public void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        Color prevColor = g2d.getColor();
        AffineTransform prevTransform = g2d.getTransform();

        g2d.setColor(color);

        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(angle);
        g2d.setTransform(transform);

        g2d.fillRect((int) (-width / 2), (int) (-height / 2), (int) width, (int) height);

        g2d.setColor(Color.BLACK);
        g2d.drawRect((int) (-width / 2), (int) (-height / 2), (int) width, (int) height);

        g2d.setTransform(prevTransform);
        g2d.setColor(prevColor);
    }

    public void goHome() {
        x = width / 2.0 + 10;
        y = panel.getHeight() - height / 2.0 - 10;
        vx = initialVx;
        vy = initialVy;
        angle = initialAngle;
        angularVelocity = initialAngularVelocity;
    }

    public BoxState saveState() {
        return new BoxState(this);
    }

    public void restoreState(BoxState state) {
        this.x = state.x;
        this.y = state.y;
        this.vx = state.vx;
        this.vy = state.vy;
        this.angle = state.angle;
        this.angularVelocity = state.angularVelocity;
        this.width = state.width;
        this.height = state.height;
        this.mass = state.mass;
        this.restitution = state.restitution;
        this.friction = state.friction;
        this.linearDamping = state.linearDamping;
        this.angularDamping = state.angularDamping;
        this.g = state.g;
    }

    public void next() {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        x = x + vx * timeScale;
        y = y + vy * timeScale;
        angle = angle + angularVelocity * timeScale;

        vx *= linearDamping;
        vy *= linearDamping;
        angularVelocity *= angularDamping;

        double[][] vertices = getVertices();

        boolean hitLeft = false;
        for (int i = 0; i < 4; i++) {
            if (vertices[i][0] < 0) {
                hitLeft = true;
                break;
            }
        }
        if (hitLeft) {
            double minX = Double.MAX_VALUE;
            int contactIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (vertices[i][0] < minX) {
                    minX = vertices[i][0];
                    contactIndex = i;
                }
            }

            double rx = vertices[contactIndex][0] - x;
            double ry = vertices[contactIndex][1] - y;

            double contactVelocityX = vx + angularVelocity * ry;
            double contactVelocityY = vy - angularVelocity * rx;

            if (contactVelocityX < 0) {
                double normalImpulse = -(1.0 + restitution) * contactVelocityX;
                double normalK = (1.0 / mass) + (ry * ry / getInertia());
                double normalJ = normalImpulse / normalK;

                double tangentialImpulse = -contactVelocityY * friction;
                double tangentialK = (1.0 / mass) + (rx * rx / getInertia());
                double tangentialJ = tangentialImpulse / tangentialK;

                vx += normalJ / mass;
                vy += tangentialJ / mass;
                angularVelocity -= (rx * tangentialJ - ry * normalJ) / getInertia();
            }

            x = x - minX;
        }

        boolean hitRight = false;
        for (int i = 0; i < 4; i++) {
            if (vertices[i][0] > panelWidth) {
                hitRight = true;
                break;
            }
        }
        if (hitRight) {
            double maxX = -Double.MAX_VALUE;
            int contactIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (vertices[i][0] > maxX) {
                    maxX = vertices[i][0];
                    contactIndex = i;
                }
            }

            double rx = vertices[contactIndex][0] - x;
            double ry = vertices[contactIndex][1] - y;

            double vp_x = vx + angularVelocity * ry;
            double vp_y = vy - angularVelocity * rx;

            if (vp_x > 0) {
                double impulseN = -(1.0 + restitution) * (-vp_x);
                double K_normal = (1.0 / mass) + (ry * ry / getInertia());
                double j_normal = impulseN / K_normal;

                double impulseTangent = -vp_y * friction;
                double K_tangent = (1.0 / mass) + (rx * rx / getInertia());
                double j_tangent = impulseTangent / K_tangent;

                vx -= j_normal / mass;
                vy += j_tangent / mass;
                angularVelocity -= (rx * j_tangent + ry * j_normal) / getInertia();
            }

            x = x - (maxX - panelWidth);
        }

        boolean hitTop = false;
        for (int i = 0; i < 4; i++) {
            if (vertices[i][1] < 0) {
                hitTop = true;
                break;
            }
        }
        if (hitTop) {
            double minY = Double.MAX_VALUE;
            int contactIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (vertices[i][1] < minY) {
                    minY = vertices[i][1];
                    contactIndex = i;
                }
            }

            double rx = vertices[contactIndex][0] - x;
            double ry = vertices[contactIndex][1] - y;

            double vp_x = vx + angularVelocity * ry;
            double vp_y = vy - angularVelocity * rx;

            if (vp_y < 0) {
                double impulseN = -(1.0 + restitution) * vp_y;
                double K_normal = (1.0 / mass) + (rx * rx / getInertia());
                double j_normal = impulseN / K_normal;

                double impulseTangent = -vp_x * friction;
                double K_tangent = (1.0 / mass) + (ry * ry / getInertia());
                double j_tangent = impulseTangent / K_tangent;

                vx += j_tangent / mass;
                vy += j_normal / mass;
                angularVelocity -= (rx * j_normal - ry * j_tangent) / getInertia();
            }

            y = y - minY;
        }

        boolean hitBottom = false;
        for (int i = 0; i < 4; i++) {
            if (vertices[i][1] > panelHeight) {
                hitBottom = true;
                break;
            }
        }
        if (hitBottom) {
            double maxY = -Double.MAX_VALUE;
            int contactIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (vertices[i][1] > maxY) {
                    maxY = vertices[i][1];
                    contactIndex = i;
                }
            }

            double rx = vertices[contactIndex][0] - x;
            double ry = vertices[contactIndex][1] - y;

            double vp_x = vx + angularVelocity * ry;
            double vp_y = vy - angularVelocity * rx;

            if (vp_y > 0) {
                double impulseN = -(1.0 + restitution) * (-vp_y);
                double K_normal = (1.0 / mass) + (rx * rx / getInertia());
                double j_normal = impulseN / K_normal;

                double impulseTangent = -vp_x * friction;
                double K_tangent = (1.0 / mass) + (ry * ry / getInertia());
                double j_tangent = impulseTangent / K_tangent;

                vx += j_tangent / mass;
                vy -= j_normal / mass;
                angularVelocity -= (rx * (-j_normal) - ry * j_tangent) / getInertia();
            }

            y = y - (maxY - panelHeight);
        }

        double velocityThreshold = 0.2;
        double angularThreshold = 0.01;

        boolean onGround = false;
        for (int i = 0; i < 4; i++) {
            if (Math.abs(vertices[i][1] - panelHeight) < 2) {
                onGround = true;
                break;
            }
        }

        boolean isStopped = false;
        if (onGround) {
            if (Math.abs(vx) < velocityThreshold && Math.abs(vy) < velocityThreshold) {
                vx = 0;
                vy = 0;
                isStopped = true;
            }

            if (Math.abs(angularVelocity) < angularThreshold) {
                angularVelocity = 0;
            }
        }

        if (!isStopped) {
            vy = vy + g * timeScale;
        }

    }
}
