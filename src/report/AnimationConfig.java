package report;

/**
 * 定数管理
 */
public class AnimationConfig {
    public static final int FRAME_INTERVAL_MS = 33;
    public static final int MAX_FRAME = 600;

    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;
    public static final int TIMELINE_WIDTH = 800;
    public static final int TIMELINE_HEIGHT = 600;
    public static final double SPLIT_PANE_HORIZONTAL_WEIGHT = 0.5;
    public static final double SPLIT_PANE_VERTICAL_WEIGHT = 0.7;

    public static final int TIMELINE_FRAME_WIDTH = 25;
    public static final int TIMELINE_ROW_HEIGHT = 25;
    public static final int TIMELINE_LABEL_WIDTH = 100;
    public static final int TIMELINE_KEYFRAME_SIZE = 10;

    public static final int POSITION_MIN = 0;
    public static final int POSITION_X_MAX = 800;
    public static final int POSITION_Y_MAX = 600;
    public static final int VELOCITY_MIN = -50;
    public static final int VELOCITY_MAX = 50;
    public static final int ANGLE_MIN = 0;
    public static final int ANGLE_MAX = 360;
    public static final int ANGULAR_VELOCITY_MIN = -100;
    public static final int ANGULAR_VELOCITY_MAX = 100;
    public static final int MASS_MIN = 10;
    public static final int MASS_MAX = 500;
    public static final int COEFFICIENT_MIN = 0;
    public static final int COEFFICIENT_MAX = 100;
    public static final int GRAVITY_MIN = 0;
    public static final int GRAVITY_MAX = 200;

    public static final double DEFAULT_MASS = 1.0;
    public static final double DEFAULT_RESTITUTION = 0.999;
    public static final double DEFAULT_FRICTION = 0.3;
    public static final double DEFAULT_LINEAR_DAMPING = 0.99;
    public static final double DEFAULT_ANGULAR_DAMPING = 0.9;
    public static final double DEFAULT_GRAVITY = 0.3;

    public static final double VELOCITY_THRESHOLD = 0.2;
    public static final double ANGULAR_VELOCITY_THRESHOLD = 0.01;
    public static final double GROUND_CONTACT_TOLERANCE = 2.0;

    private AnimationConfig() {
    }
}
