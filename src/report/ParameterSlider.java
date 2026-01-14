package report;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * ラベル、テキストフィールド、スライダーをセットで扱うユーティリティ。
 * scaleを使って「UIは整数、値は小数」のような変換を簡単にする。
 */
public class ParameterSlider {
    private final JLabel label;
    private final JSlider slider;
    private final JTextField textField;
    private final String paramName;
    private final double scale;
    private boolean updating = false;

    public ParameterSlider(String paramName, int min, int max, int initialValue,
            double scale, Consumer<Double> onChange) {
        this.paramName = paramName;
        this.scale = scale;

        this.label = new JLabel(paramName + ":");
        UIStyles.styleLabel(this.label);

        this.slider = new JSlider(min, max, initialValue);
        UIStyles.styleSlider(this.slider);

        this.textField = new JTextField(6);
        this.textField.setFont(UIStyles.FONT_REGULAR);
        this.textField.setHorizontalAlignment(JTextField.RIGHT);
        this.textField.setText(formatValue(initialValue / scale));

        this.textField.addActionListener(e -> {
            if (!updating) {
                try {
                    double value = parseValue(textField.getText());
                    int sliderValue = (int) (value * scale);
                    updating = true;
                    if (sliderValue >= min && sliderValue <= max) {
                        slider.setValue(sliderValue);
                    }
                    textField.setText(formatValue(value));
                    updating = false;
                    onChange.accept(value);
                } catch (NumberFormatException ex) {
                    updating = true;
                    textField.setText(formatValue(slider.getValue() / scale));
                    updating = false;
                }
            }
        });

        this.slider.addChangeListener(e -> {
            if (!updating) {
                double value = slider.getValue() / scale;
                updating = true;
                textField.setText(formatValue(value));
                updating = false;
                onChange.accept(value);
            }
        });
    }

    private String formatValue(double value) {
        if (paramName.contains("角度")) {
            return String.format("%.1f°", value);
        } else if (scale >= 100) {
            return String.format("%.2f", value);
        } else {
            return String.format("%.1f", value);
        }
    }

    private double parseValue(String text) {
        text = text.replaceAll("[^0-9.\\-]", "");
        return Double.parseDouble(text);
    }

    public void setValue(int value) {
        updating = true;
        slider.setValue(value);
        textField.setText(formatValue(value / scale));
        updating = false;
    }

    public void setValue(double value) {
        setValue((int) (value * scale));
    }

    public JLabel getLabel() {
        return label;
    }

    public JTextField getTextField() {
        return textField;
    }

    public JSlider getSlider() {
        return slider;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }
}
