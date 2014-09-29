/* source: http://mii-prakse.googlecode.com/svn/prakse/AtteluApstrade/Subprojects/PixelMaster/trunk/src/lv/lumii/pixelmaster/core/api/gui/SliderWithSpinner.java */
package fr.ign.util.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

/**
 * This class represents a slider with a spinner. The spinner and the slider are
 * synchronized: user can use either of them to adjust the value in the text
 * field. When the value in the editor is modified using the spinner, the state
 * of the slider is changed accordingly.
 * 
 * @author Jevgeny Jonas
 */
public final class SliderWithSpinner extends JPanel {

    private final JSpinner spinner;
    private final JSlider slider;
    private final SliderWithSpinnerModel model;

    // private final int orientation;

    /*
     * MVC in Swing: Models
     * 
     * JSpinner uses SpinnerModel, JSlider uses BoundedRangeModel. The first
     * idea was to create a class that would implement both interfaces. But it
     * turned out to be impossible because of these methods:
     * 
     * int BoundedRangeModel.getValue() Object SpinnerModel.getValue()
     * 
     * Instead, an inner class is used. Thus, the spinner and the slider
     * "contain a reference to one main model with data"
     * (http://www.developer.com/java/ent/article.php/
     * 10933_3336761_2/Creating-Interactive-GUIs-with-Swings-MVC-Architecture.htm)
     * 
     * When user adjusts a component (the slider or the spinner), this component
     * updates the model and sends a notification to all change listeners. The
     * model then notifies the spinner and the slider. The spinner then updates
     * the editor and also sends a notification to its change listeners. The
     * slider also sends a notification to its change listeners.
     * 
     * The SliderWithSpinner registers its change listeners as the spinner's
     * change listeners, but not as the slider's change listeners. This helps
     * ensure that the listeners of class SliderWithSpinner will not receive
     * redundant notifications when user adjusts the slider or the spinner.
     */

    /**
     * Creates a new slider with spinner which uses the supplied model.
     * 
     * @param model
     *            non-null pointer (ownership: callee)
     * @param orientation
     *            orientation of the slider: either
     *            {@link javax.swing.SwingConstants.VERTICAL} or
     *            {@link javax.swing.SwingConstants.HORIZONTAL}.
     * @param drawLabels
     *            Specifies whether to draw labels on the slider. If
     *            <code>true</code>, numeric labels will be put at the minimum
     *            nad the maximum values.
     */
    public SliderWithSpinner(SliderWithSpinnerModel model, int orientation,
            boolean drawLabels) {
        assert model != null;
        assert orientation == SwingConstants.HORIZONTAL
                || orientation == SwingConstants.VERTICAL;

        this.model = model;
        // this.orientation = orientation;

        this.setAlignmentY(Component.TOP_ALIGNMENT);

        // using inner objects as models
        this.spinner = createSpinner(model.getSpinnerModel());
        this.slider = createSlider(model.getBoundedRangeModel(), orientation,
                drawLabels);

        if (orientation == SwingConstants.VERTICAL) {
            this.setLayout(new GridBagLayout());
            this.add(this.slider, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(2, 2, 2, 2), 0, 0));
            this.add(this.spinner, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(2, 2, 2, 2), 0, 0));
        } else {
            this.setLayout(new GridBagLayout());
            this.add(this.slider, new GridBagConstraints(0, 0, 1, 1, 0.5, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(2, 2, 2, 2), 0, 0));
            this.add(this.spinner, new GridBagConstraints(1, 0, 1, 1, 0.5, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(2, 2, 2, 2), 0, 0));
        }

    }

    /**
     * Creates a new slider with spinner which uses the supplied model.
     * 
     * @param model
     *            non-null pointer (ownership: callee)
     * @param orientation
     *            orientation of the slider: either
     *            {@link javax.swing.SwingConstants.VERTICAL} or
     *            {@link javax.swing.SwingConstants.HORIZONTAL}.
     * @param drawLabels
     *            Specifies whether to draw labels on the slider. If
     *            <code>true</code>, numeric labels will be put at the minimum
     *            nad the maximum values.
     */
    public SliderWithSpinner(SliderWithSpinnerModel model) {
        this(model, SwingConstants.HORIZONTAL, false);
    }

    /**
     * Changes the current value of the slider with spinner.
     * 
     * @param value
     *            the new value (must be in range [minvalue..maxvalue])
     */
    public void setValue(int value) {
        assert this.model.getBoundedRangeModel().getMinimum() <= value
                && value <= this.model.getBoundedRangeModel().getMaximum();
        this.model.getBoundedRangeModel().setValue(value);
        // model.getSpinnerModel().setValue(value);
    }

    /**
     * Adds a listener to the list that is notified each time the spinner with
     * slider changes its current value.
     * 
     * @param listener
     *            the <code>ChangeListener</code> to add
     */
    public void addChangeListener(ChangeListener listener) {
        this.spinner.addChangeListener(listener);
    }

    /**
     * Removes a <code>ChangeListener</code> from this spinner.
     * 
     * @param listener
     *            the <code>ChangeListener</code> to remove
     */
    public void removeChangeListener(ChangeListener listener) {
        this.spinner.removeChangeListener(listener);
    }

    private static JSlider createSlider(BoundedRangeModel model,
            int orientation, boolean drawLabels) {
        JSlider slider = new JSlider(model);

        slider.setOrientation(orientation);
        slider.setAlignmentY(Component.TOP_ALIGNMENT);

        if (drawLabels) {
            Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
            labelTable.put(new Integer(model.getMinimum()), new JLabel(
                    ((Integer) model.getMinimum()).toString()));
            labelTable.put(new Integer(model.getMaximum()), new JLabel(
                    ((Integer) model.getMaximum()).toString()));
            slider.setLabelTable(labelTable);
            slider.setPaintLabels(true);
        }

        // slider.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));

        return slider;
    }

    private static JSpinner createSpinner(SpinnerNumberModel model) {

        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.NumberEditor(spinner));
        // JFormattedTextField hor_ftf = ((JSpinner.DefaultEditor) (spinner
        // .getEditor())).getTextField();
        // hor_ftf.setEditable(false);

        return spinner;
    }

    /**
     * This class represents the model for the {@link SliderWithSpinner}. The
     * model's state is defined by the minimal, the maximal and the current
     * position.
     * 
     * @author Jevgeny Jonas (modified by JeT to handle double values)
     */
    public static class SliderWithSpinnerModel {

        private final BoundedRangeModel boundedRangeModel;
        private double precision = 0.001;
        private double increment = 1;
        private final SpinnerNumberModel spinnerModel = new SpinnerModelImpl();

        private final class SpinnerModelImpl extends SpinnerNumberModel {
            private SpinnerModelImpl() {
            }

            @Override
            public Object getValue() {
                return SliderWithSpinnerModel.this.boundedRangeModel.getValue()
                        * SliderWithSpinnerModel.this.precision;
            }

            @Override
            public void setValue(Object value) {
                double v = ((Double) value / SliderWithSpinnerModel.this.precision);
                SliderWithSpinnerModel.this.boundedRangeModel
                        .setValue(new Integer((int) v));
            }

            @Override
            public Object getNextValue() {
                return (SliderWithSpinnerModel.this.boundedRangeModel
                        .getValue() + SliderWithSpinnerModel.this.increment
                        / SliderWithSpinnerModel.this.precision)
                        * SliderWithSpinnerModel.this.precision;
            }

            @Override
            public Object getPreviousValue() {
                return (SliderWithSpinnerModel.this.boundedRangeModel
                        .getValue() - SliderWithSpinnerModel.this.increment
                        / SliderWithSpinnerModel.this.precision)
                        * SliderWithSpinnerModel.this.precision;
            }

            @Override
            public void addChangeListener(ChangeListener l) {
                SliderWithSpinnerModel.this.boundedRangeModel
                        .addChangeListener(l);
            }

            @Override
            public void removeChangeListener(ChangeListener l) {
                SliderWithSpinnerModel.this.boundedRangeModel
                        .removeChangeListener(l);
            }
        }

        /**
         * Constructs a new model. Precondition:
         * <code>minimum &lt;= value &lt;= maximum</code>.
         */
        public SliderWithSpinnerModel(double value, double minimum,
                double maximum, double increment, double precision) {
            minimum = Math.min(minimum, value);
            maximum = Math.max(maximum, value);
            this.precision = precision;
            this.increment = increment;
            this.boundedRangeModel = new DefaultBoundedRangeModel(
                    (int) (value / precision), 0, (int) (minimum / precision),
                    (int) (maximum / precision));
        }

        /**
         * Constructs a new model. Precondition:
         * <code>minimum &lt;= value &lt;= maximum</code>.
         */
        public SliderWithSpinnerModel(double value, double minimum,
                double maximum, double increment) {
            this(value, minimum, maximum, increment, 0.001);
        }

        SpinnerNumberModel getSpinnerModel() {
            return this.spinnerModel;
        }

        BoundedRangeModel getBoundedRangeModel() {
            return this.boundedRangeModel;
        }

        /**
         * Returns the current position of the slider and the spinner.
         */
        public Double getValue() {
            return this.boundedRangeModel.getValue() * this.precision;
        }
    }

    public JComponent getEditor() {
        return this.spinner.getEditor();
    }

    public Double getValue() {
        return this.model.getValue();
    }

}
