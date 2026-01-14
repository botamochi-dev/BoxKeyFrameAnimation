package report;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

/**
 * 長方形の剛体を模したクラス。位置・速度だけでなく角度や角速度も持ち、
 * 画面四辺との衝突では傾いた形のまま反射する。初心者が「状態を全部プロパティで管理する」
 * 感覚をつかめるよう、値ごとにgetter/setterを用意している。
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

    /**
     * 一時的にBoxの状態を保存するための小さなDTO。
     * 「シミュレーション → 判定 → 元に戻す」といったリハーサルに利用する。
     */
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

    // 物理パラメータのgetter/setter
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

        // 回転と平行移動を適用
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(angle);
        g2d.setTransform(transform);

        // ボックスを描画（中心が原点）
        g2d.fillRect((int) (-width / 2), (int) (-height / 2), (int) width, (int) height);

        // 境界線を描画
        g2d.setColor(Color.BLACK);
        g2d.drawRect((int) (-width / 2), (int) (-height / 2), (int) width, (int) height);

        g2d.setTransform(prevTransform);
        g2d.setColor(prevColor);
    }

    // ボックスを左下隅へ移動
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

    // 座標、速度、角度更新
    /**
     * 1フレームぶん Box を前進させるメインの物理計算。
     * 1. 位置・角度を速度に応じて更新
     * 2. 減衰をかけて徐々に減速
     * 3. 画面の壁と衝突していないかをSAT(分離軸定理)風にチェック
     * 4. 床で止まった場合は重力を止める
     */
    public void next() {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        // 位置更新（時間スケールを適用）
        x = x + vx * timeScale;
        y = y + vy * timeScale;

        // 角度更新（時間スケールを適用）
        angle = angle + angularVelocity * timeScale;

        // 移動速度に減衰を適用
        vx *= linearDamping;
        vy *= linearDamping;

        // 角速度に減衰を適用
        angularVelocity *= angularDamping;

        // OBBの頂点を取得（傾いた長方形でも判定できるようにする）
        double[][] vertices = getVertices();

        /*--- 壁との衝突判定（SAT） ---*/

        // 左壁との衝突
        boolean hitLeft = false;
        for (int i = 0; i < 4; i++) {
            if (vertices[i][0] < 0) {
                hitLeft = true;
                break;
            }
        }
        if (hitLeft) {
            // 最も左にある頂点（衝突点）を探す
            double minX = Double.MAX_VALUE;
            double contactY = y;
            int contactIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (vertices[i][0] < minX) {
                    minX = vertices[i][0];
                    contactY = vertices[i][1];
                    contactIndex = i;
                }
            }

            // 重心から衝突点へのベクトル r
            double rx = vertices[contactIndex][0] - x;
            double ry = vertices[contactIndex][1] - y;

            // 衝突点での水平方向の速度
            double vp_x = vx - angularVelocity * ry;
            double vp_y = vy + angularVelocity * rx;

            // 衝突点が壁に向かっている場合のみ処理（左壁: vp_x < 0）
            if (vp_x < 0) {
                // インパルス計算（左壁: 法線ベクトル (1, 0)）
                double impulseN = -(1.0 + restitution) * vp_x;
                double K_normal = (1.0 / mass) + (ry * ry / getInertia());
                double j_normal = impulseN / K_normal;

                // 摩擢によるインパルス（接線方向）
                double impulseTangent = -vp_y * friction;
                double K_tangent = (1.0 / mass) + (rx * rx / getInertia());
                double j_tangent = impulseTangent / K_tangent;

                // 速度と角速度を更新
                vx += j_normal / mass;
                vy += j_tangent / mass;
                angularVelocity += (rx * j_tangent - ry * j_normal) / getInertia();
            }

            // めり込み補正
            x = x - minX;
        }

        // 右壁との衝突
        boolean hitRight = false;
        for (int i = 0; i < 4; i++) {
            if (vertices[i][0] > panelWidth) {
                hitRight = true;
                break;
            }
        }
        if (hitRight) {
            // 最も右にある頂点（衝突点）を探す
            double maxX = -Double.MAX_VALUE;
            double contactY = y;
            int contactIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (vertices[i][0] > maxX) {
                    maxX = vertices[i][0];
                    contactY = vertices[i][1];
                    contactIndex = i;
                }
            }

            // 重心から衝突点へのベクトル r
            double rx = vertices[contactIndex][0] - x;
            double ry = vertices[contactIndex][1] - y;

            // 衝突点での速度
            double vp_x = vx - angularVelocity * ry;
            double vp_y = vy + angularVelocity * rx;

            // 衝突点が壁に向かっている場合のみ処理（右壁: vp_x > 0）
            if (vp_x > 0) {
                // インパルス計算（右壁: 法線ベクトル (-1, 0)）
                double impulseN = -(1.0 + restitution) * (-vp_x);
                double K_normal = (1.0 / mass) + (ry * ry / getInertia());
                double j_normal = impulseN / K_normal;

                // 摩擢によるインパルス
                double impulseTangent = -vp_y * friction;
                double K_tangent = (1.0 / mass) + (rx * rx / getInertia());
                double j_tangent = impulseTangent / K_tangent;

                // 速度と角速度を更新（法線方向が負なので符号に注意）
                vx -= j_normal / mass;
                vy += j_tangent / mass;
                angularVelocity += (rx * j_tangent + ry * j_normal) / getInertia();
            }

            // めり込み補正
            x = x - (maxX - panelWidth);
        }

        // 上壁との衝突
        boolean hitTop = false;
        for (int i = 0; i < 4; i++) {
            if (vertices[i][1] < 0) {
                hitTop = true;
                break;
            }
        }
        if (hitTop) {
            // 最も上にある頂点（衝突点）を探す
            double minY = Double.MAX_VALUE;
            double contactX = x;
            int contactIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (vertices[i][1] < minY) {
                    minY = vertices[i][1];
                    contactX = vertices[i][0];
                    contactIndex = i;
                }
            }

            // 重心から衝突点へのベクトル r
            double rx = vertices[contactIndex][0] - x;
            double ry = vertices[contactIndex][1] - y;

            // 衝突点での速度
            double vp_x = vx - angularVelocity * ry;
            double vp_y = vy + angularVelocity * rx;

            // 衝突点が壁に向かっている場合のみ処理（上壁: vp_y < 0）
            if (vp_y < 0) {
                // インパルス計算（上壁: 法線ベクトル (0, 1)）
                double impulseN = -(1.0 + restitution) * vp_y;
                double K_normal = (1.0 / mass) + (rx * rx / getInertia());
                double j_normal = impulseN / K_normal;

                // 摩擢によるインパルス
                double impulseTangent = -vp_x * friction;
                double K_tangent = (1.0 / mass) + (ry * ry / getInertia());
                double j_tangent = impulseTangent / K_tangent;

                // 速度と角速度を更新
                vx += j_tangent / mass;
                vy += j_normal / mass;
                angularVelocity += (rx * j_normal - ry * j_tangent) / getInertia();
            }

            // めり込み補正
            y = y - minY;
        }

        // 下壁との衝突
        boolean hitBottom = false;
        for (int i = 0; i < 4; i++) {
            if (vertices[i][1] > panelHeight) {
                hitBottom = true;
                break;
            }
        }
        if (hitBottom) {
            // 最も下にある頂点（衝突点）を探す
            double maxY = -Double.MAX_VALUE;
            double contactX = x;
            int contactIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (vertices[i][1] > maxY) {
                    maxY = vertices[i][1];
                    contactX = vertices[i][0];
                    contactIndex = i;
                }
            }

            // 重心から衝突点へのベクトル r
            double rx = vertices[contactIndex][0] - x;
            double ry = vertices[contactIndex][1] - y;

            // 衝突点での速度を計算
            // v_point = v_center + ω × r
            // 2Dの場合: vp_x = vx - ω * ry, vp_y = vy + ω * rx
            double vp_x = vx - angularVelocity * ry;
            double vp_y = vy + angularVelocity * rx;

            // 衝突点が壁に向かっている場合のみ処理（下壁: vp_y > 0）
            if (vp_y > 0) {
                // インパルスベースの衝突応答（下壁: 法線ベクトル (0, -1)）
                double impulseN = -(1.0 + restitution) * (-vp_y);
                double K_normal = (1.0 / mass) + (rx * rx / getInertia());
                double j_normal = impulseN / K_normal;

                // 摩擢によるインパルス（接線方向）
                double impulseTangent = -vp_x * friction;
                double K_tangent = (1.0 / mass) + (ry * ry / getInertia());
                double j_tangent = impulseTangent / K_tangent;

                // 速度と角速度を更新
                vx += j_tangent / mass;
                vy -= j_normal / mass; // 法線方向が負なので符号に注意
                angularVelocity += (rx * (-j_normal) - ry * j_tangent) / getInertia();
            }

            // めり込み補正
            y = y - (maxY - panelHeight);
        }

        // 静止判定：速度が十分小さく、床に接している場合は完全に停止
        // （エネルギー保存をテストする場合はこの判定をコメントアウト）

        double velocityThreshold = 0.2; // 速度の閾値
        double angularThreshold = 0.01; // 角速度の閾値

        // 床との接触判定
        boolean onGround = false;
        for (int i = 0; i < 4; i++) {
            if (Math.abs(vertices[i][1] - panelHeight) < 2) {
                onGround = true;
                break;
            }
        }

        boolean isStopped = false; // 静止状態フラグ
        if (onGround) {
            // 速度が閾値以下なら静止
            if (Math.abs(vx) < velocityThreshold && Math.abs(vy) < velocityThreshold) {
                vx = 0;
                vy = 0;
                isStopped = true; // 静止状態
            }
            // 角速度が閾値以下なら回転停止
            if (Math.abs(angularVelocity) < angularThreshold) {
                angularVelocity = 0;
            }
        }

        // 画面下向きの加速度（静止していない場合のみ適用）
        if (!isStopped) {
            vy = vy + g * timeScale;
        }

    }
}
