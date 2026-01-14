package report;

import java.util.TreeMap;

/**
 * タイムラインの生データを保持するクラス。
 * ParamTypeごとにTreeMap(frame -> value)を持ち、前後の値から線形補間して中間フレームの値を推定する。
 */
public class KeyFrameData {
    public enum ParamType {
        X("X座標"),
        Y("Y座標"),
        VX("X速度"),
        VY("Y速度"),
        ANGLE("角度"),
        ANGULAR_VELOCITY("角速度"),
        WIDTH("幅"),
        HEIGHT("高さ"),
        MASS("質量"),
        RESTITUTION("反発係数"),
        FRICTION("摩擦係数"),
        LINEAR_DAMPING("移動減衰"),
        ANGULAR_DAMPING("回転減衰"),
        GRAVITY("重力");

        private final String displayName;

        ParamType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private TreeMap<ParamType, TreeMap<Integer, Double>> keyFrames;
    private ParamType selectedParamType = null;
    private Integer selectedFrame = null;

    public KeyFrameData() {
        keyFrames = new TreeMap<>();
        for (ParamType type : ParamType.values()) {
            keyFrames.put(type, new TreeMap<>());
        }
    }

    public void registerKeyFrame(ParamType type, int frame, double value) {
        keyFrames.get(type).put(frame, value);
    }

    public void deleteKeyFrame(ParamType type, int frame) {
        keyFrames.get(type).remove(frame);
    }

    public void deleteSelectedKeyFrame() {
        if (selectedParamType != null && selectedFrame != null) {
            deleteKeyFrame(selectedParamType, selectedFrame);
            selectedParamType = null;
            selectedFrame = null;
        }
    }

    public Double getValue(ParamType type, int frame) {
        TreeMap<Integer, Double> frames = keyFrames.get(type);

        if (frames.isEmpty()) {
            return null;
        }

        if (frames.containsKey(frame)) {
            return frames.get(frame);
        }

        Integer prevFrame = frames.floorKey(frame);
        Integer nextFrame = frames.ceilingKey(frame);

        if (prevFrame != null && nextFrame != null) {
            return interpolate(frames.get(prevFrame), frames.get(nextFrame),
                    prevFrame, nextFrame, frame);
        } else if (prevFrame != null) {
            return frames.get(prevFrame);
        } else if (nextFrame != null) {
            return frames.get(nextFrame);
        }

        return null;
    }

    private double interpolate(double prevValue, double nextValue,
            int prevFrame, int nextFrame, int currentFrame) {
        double t = (double) (currentFrame - prevFrame) / (nextFrame - prevFrame);
        return prevValue + (nextValue - prevValue) * t;
    }

    public TreeMap<Integer, Double> getKeyFrames(ParamType type) {
        return keyFrames.get(type);
    }

    public boolean hasKeyFrame(ParamType type, int frame) {
        return keyFrames.get(type).containsKey(frame);
    }

    public void selectKeyFrame(ParamType type, int frame) {
        if (hasKeyFrame(type, frame)) {
            selectedParamType = type;
            selectedFrame = frame;
        }
    }

    public void clearSelection() {
        selectedParamType = null;
        selectedFrame = null;
    }

    public boolean isSelected(ParamType type, int frame) {
        return selectedParamType == type && selectedFrame != null && selectedFrame == frame;
    }

    public void registerAllFromBox(int frame, Box box) {
        registerKeyFrame(ParamType.X, frame, box.getX());
        registerKeyFrame(ParamType.Y, frame, box.getY());
        registerKeyFrame(ParamType.VX, frame, box.getVx());
        registerKeyFrame(ParamType.VY, frame, box.getVy());
        registerKeyFrame(ParamType.ANGLE, frame, box.getAngle());
        registerKeyFrame(ParamType.ANGULAR_VELOCITY, frame, box.getAngularVelocity());
        registerKeyFrame(ParamType.WIDTH, frame, box.getWidth());
        registerKeyFrame(ParamType.HEIGHT, frame, box.getHeight());
        registerKeyFrame(ParamType.MASS, frame, box.getMass());
        registerKeyFrame(ParamType.RESTITUTION, frame, box.getRestitution());
        registerKeyFrame(ParamType.FRICTION, frame, box.getFriction());
        registerKeyFrame(ParamType.LINEAR_DAMPING, frame, box.getLinearDamping());
        registerKeyFrame(ParamType.ANGULAR_DAMPING, frame, box.getAngularDamping());
        registerKeyFrame(ParamType.GRAVITY, frame, box.getG());
    }

    public void applyToBox(int frame, Box box) {
        applyIfNotNull(getValue(ParamType.WIDTH, frame), box::setWidth);
        applyIfNotNull(getValue(ParamType.HEIGHT, frame), box::setHeight);
        applyIfNotNull(getValue(ParamType.MASS, frame), box::setMass);
        applyIfNotNull(getValue(ParamType.RESTITUTION, frame), box::setRestitution);
        applyIfNotNull(getValue(ParamType.FRICTION, frame), box::setFriction);
        applyIfNotNull(getValue(ParamType.LINEAR_DAMPING, frame), box::setLinearDamping);
        applyIfNotNull(getValue(ParamType.ANGULAR_DAMPING, frame), box::setAngularDamping);
        applyIfNotNull(getValue(ParamType.GRAVITY, frame), box::setG);
    }

    private void applyIfNotNull(Double value, java.util.function.DoubleConsumer setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public ParamType getSelectedParamType() {
        return selectedParamType;
    }

    public Integer getSelectedFrame() {
        return selectedFrame;
    }
}
