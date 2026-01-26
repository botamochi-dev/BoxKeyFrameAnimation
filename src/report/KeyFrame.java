package report;

/**
 * キーフレームクラス。
 * Boxのあらゆる状態を丸ごと保存し、任意のフレームでぴったり同じ姿に戻せるようにする。
 * タイムライン上ではこの KeyFrame の一覧を使って補間を行う。
 */
public class KeyFrame {
    private int frameNumber;

    private double x;
    private double y;
    private double vx;
    private double vy;
    private double angle;
    private double angularVelocity;

    private double mass;
    private double restitution;
    private double friction;
    private double linearDamping;
    private double angularDamping;
    private double gravity;

    public KeyFrame(int frameNumber, double x, double y, double vx, double vy,
            double angle, double angularVelocity, double mass,
            double restitution, double friction, double linearDamping,
            double angularDamping, double gravity) {
        this.frameNumber = frameNumber;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.angularVelocity = angularVelocity;
        this.mass = mass;
        this.restitution = restitution;
        this.friction = friction;
        this.linearDamping = linearDamping;
        this.angularDamping = angularDamping;
        this.gravity = gravity;
    }

    public static KeyFrame fromBox(int frameNumber, Box box) {
        return new KeyFrame(
                frameNumber,
                box.getX(),
                box.getY(),
                box.getVx(),
                box.getVy(),
                box.getAngle(),
                box.getAngularVelocity(),
                box.getMass(),
                box.getRestitution(),
                box.getFriction(),
                box.getLinearDamping(),
                box.getAngularDamping(),
                box.getG());
    }

    public void applyToBox(Box box) {
        box.setXY(x, y);
        box.setVxVy(vx, vy);
        box.setAngle(angle);
        box.setAngularVelocity(angularVelocity);
        box.setMass(mass);
        box.setRestitution(restitution);
        box.setFriction(friction);
        box.setLinearDamping(linearDamping);
        box.setAngularDamping(angularDamping);
        box.setG(gravity);
    }

    public static KeyFrame interpolate(KeyFrame kf1, KeyFrame kf2, int targetFrame) {
        if (targetFrame <= kf1.frameNumber) {
            return kf1;
        }
        if (targetFrame >= kf2.frameNumber) {
            return kf2;
        }

        double t = (double) (targetFrame - kf1.frameNumber) / (kf2.frameNumber - kf1.frameNumber);

        return new KeyFrame(
                targetFrame,
                lerp(kf1.x, kf2.x, t),
                lerp(kf1.y, kf2.y, t),
                lerp(kf1.vx, kf2.vx, t),
                lerp(kf1.vy, kf2.vy, t),
                lerpAngle(kf1.angle, kf2.angle, t),
                lerp(kf1.angularVelocity, kf2.angularVelocity, t),
                lerp(kf1.mass, kf2.mass, t),
                lerp(kf1.restitution, kf2.restitution, t),
                lerp(kf1.friction, kf2.friction, t),
                lerp(kf1.linearDamping, kf2.linearDamping, t),
                lerp(kf1.angularDamping, kf2.angularDamping, t),
                lerp(kf1.gravity, kf2.gravity, t));
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double lerpAngle(double a, double b, double t) {
        double diff = b - a;
        while (diff > Math.PI)
            diff -= 2 * Math.PI;
        while (diff < -Math.PI)
            diff += 2 * Math.PI;
        return a + diff * t;
    }

    public int getFrameNumber() {
        return frameNumber;
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

    public double getMass() {
        return mass;
    }

    public double getRestitution() {
        return restitution;
    }

    public double getFriction() {
        return friction;
    }

    public double getLinearDamping() {
        return linearDamping;
    }

    public double getAngularDamping() {
        return angularDamping;
    }

    public double getGravity() {
        return gravity;
    }
}
