import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HeadControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    private static final long serialVersionUID = 1572416246500100902L;

    private Head head;
    private Actor actor;

    private JCheckBox checkBoxBone, checkBoxSkin;
    private RangeSlider sliderHue, sliderSat, sliderValue, sliderAlpha,
            contour;
    private JSlider backgroundR, backgroundG, backgroundB, numberContours;
    private JRadioButton radioActorBone, radioActorSkin;

    private static final int RES = 100;
    private static final double MIN = 0.0;
    private static final double MAX = 1.0;
    private static final int ROW_H = 30;

    private boolean stateChangeFromUser = true;

    public HeadControls(Head head) {
        super();

        this.head = head;
        this.actor = head.getBone();

        JPanel visibilityPanel = makeVisibilityPanel();
        JPanel backgroundPanel = makeBackgroundPanel();
        JPanel actorPanel = makeActorRadioPanel();
        JPanel rangeSliderPanel = makeRangeSliderPanel();
        JPanel contourPanel = makeContourPanel();

        add(backgroundPanel, "0, 0");
        add(visibilityPanel, "1, 0");
        add(actorPanel, "2, 0");
        add(rangeSliderPanel, "3, 0");
        add(contourPanel, "4, 0");
    }

    private JPanel makeContourPanel() {
        double[][] size = { { 0.25, 0.75 }, { ROW_H, ROW_H } };

        JPanel panel = new JPanel(new TableLayout(size));

        JLabel rangeLabel = new JLabel("Range");
        JLabel numLabel = new JLabel("Number");

        contour = makeRangeSlider(0, 100, actor.getContour().getRange());
        numberContours = new JSlider(JSlider.HORIZONTAL, 1, 5, actor
                .getContour().GetNumberOfContours());

        panel.add(rangeLabel, "0, 0");
        panel.add(numLabel, "0, 1");

        panel.add(contour, "1, 0");
        panel.add(numberContours, "1, 1");

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Contours"));

        contour.addChangeListener(this);
        numberContours.addChangeListener(this);

        return panel;
    }

    private JPanel makeVisibilityPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        checkBoxBone = new JCheckBox("Bone");
        checkBoxBone.setSelected(head.getBone().isVisible());

        checkBoxSkin = new JCheckBox("Skin");
        checkBoxSkin.setSelected(head.getSkin().isVisible());

        panel.add(checkBoxBone);
        panel.add(checkBoxSkin);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Actor Visibility"));

        checkBoxBone.addItemListener(this);
        checkBoxSkin.addItemListener(this);

        return panel;
    }

    private JPanel makeActorRadioPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        radioActorBone = new JRadioButton("Bone", true);
        radioActorSkin = new JRadioButton("Skin", false);

        ButtonGroup buttonGroupActor = new ButtonGroup();
        buttonGroupActor.add(radioActorBone);
        buttonGroupActor.add(radioActorSkin);

        panel.add(radioActorBone);
        panel.add(radioActorSkin);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Active Actor"));

        radioActorBone.addActionListener(this);
        radioActorSkin.addActionListener(this);

        return panel;
    }

    private JPanel makeRangeSliderPanel() {
        double[][] size = { { 0.25, 0.75 }, { ROW_H, ROW_H, ROW_H, ROW_H } };

        JPanel panel = new JPanel(new TableLayout(size));

        JLabel hLabel = new JLabel("Hue");
        JLabel sLabel = new JLabel("Saturation");
        JLabel vLabel = new JLabel("Value");
        JLabel aLabel = new JLabel("Alpha");

        Interval h, s, v, a;
        h = actor.getLookupTable().getHue();
        s = actor.getLookupTable().getSat();
        v = actor.getLookupTable().getValue();
        a = actor.getLookupTable().getAlpha();

        sliderHue = makeRangeSlider(MIN, MAX, h);
        sliderSat = makeRangeSlider(MIN, MAX, s);
        sliderValue = makeRangeSlider(MIN, MAX, v);
        sliderAlpha = makeRangeSlider(MIN, MAX, a);

        panel.add(hLabel, "0, 0");
        panel.add(sLabel, "0, 1");
        panel.add(vLabel, "0, 2");
        panel.add(aLabel, "0, 3");

        panel.add(sliderHue, "1, 0");
        panel.add(sliderSat, "1, 1");
        panel.add(sliderValue, "1, 2");
        panel.add(sliderAlpha, "1, 3");

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Actor Color"));

        sliderHue.addChangeListener(this);
        sliderSat.addChangeListener(this);
        sliderValue.addChangeListener(this);
        sliderAlpha.addChangeListener(this);

        return panel;
    }

    private JPanel makeBackgroundPanel() {
        double[][] size = { { 0.25, 0.75 }, { ROW_H, ROW_H, ROW_H } };

        JPanel panel = new JPanel(new TableLayout(size));

        JLabel rLabel = new JLabel("Red");
        JLabel gLabel = new JLabel("Green");
        JLabel bLabel = new JLabel("Blue");

        backgroundR = makeJSlider(MIN, MAX, head.getBackgroundR());
        backgroundG = makeJSlider(MIN, MAX, head.getBackgroundG());
        backgroundB = makeJSlider(MIN, MAX, head.getBackgroundB());

        panel.add(rLabel, "0, 0");
        panel.add(gLabel, "0, 1");
        panel.add(bLabel, "0, 2");

        panel.add(backgroundR, "1, 0");
        panel.add(backgroundG, "1, 1");
        panel.add(backgroundB, "1, 2");

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Background Color"));

        backgroundR.addChangeListener(this);
        backgroundG.addChangeListener(this);
        backgroundB.addChangeListener(this);

        return panel;
    }

    private static int doubleToInt(double d) {
        return (int) (RES * d);
    }

    private static double intToDouble(int i) {
        return i / ((double) RES);
    }

    private RangeSlider makeRangeSlider(double min, double max,
            Interval interval) {
        int minI = doubleToInt(min);
        int maxI = doubleToInt(max);

        RangeSlider rs = new RangeSlider(minI, maxI);

        setRangeSliderToInterval(min, max, rs, interval);

        return rs;
    }

    private void setRangeSliderToInterval(double min, double max, RangeSlider rs, Interval interval) {
        rs.setValue(doubleToInt(max));
        rs.setValue(doubleToInt(min));

        rs.setValue(rs.getMaximum());
        rs.setValue(rs.getMinimum());

        rs.setUpperValue(doubleToInt(interval.getMax()));
        rs.setValue(doubleToInt(interval.getMin()));
    }

    private void setRangeSlidersToActor() {
        Interval h, s, v, a;

        h = actor.getLookupTable().getHue();
        s = actor.getLookupTable().getSat();
        v = actor.getLookupTable().getValue();
        a = actor.getLookupTable().getAlpha();

        setRangeSliderToInterval(MIN, MAX, sliderHue, h);
        setRangeSliderToInterval(MIN, MAX, sliderSat, s);
        setRangeSliderToInterval(MIN, MAX, sliderValue, v);
        setRangeSliderToInterval(MIN, MAX, sliderAlpha, a);

        setRangeSliderToInterval(0, 100, contour, actor.getContour().getRange());
        setRangeSliderToInterval(0, 100, contour, actor.getContour().getRange());
    }

    private JSlider makeJSlider(double min, double max, double init) {
        int minI = doubleToInt(min);
        int maxI = doubleToInt(max);
        int initI = doubleToInt(init);

        return new JSlider(JSlider.HORIZONTAL, minI, maxI, initI);
    }

    private double getSliderValue(JSlider slider) {
        return intToDouble(slider.getValue());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        if (source == checkBoxBone) {
            if (checkBoxSkin.isSelected() || checkBoxBone.isSelected()) {
                head.getBone().flipVisible();
            } else {
                checkBoxBone.setSelected(true);
            }
        } else if (source == checkBoxSkin) {
            if (checkBoxSkin.isSelected() || checkBoxBone.isSelected()) {
                head.getSkin().flipVisible();
            } else {
                checkBoxSkin.setSelected(true);
            }
        }

        head.display();
    }

    private void setIntervalFromRangeSlider(Interval interval, RangeSlider rs) {
        interval.setMax(intToDouble(rs.getUpperValue()));
        interval.setMin(intToDouble(rs.getValue()));
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (stateChangeFromUser) {
            Object source = e.getSource();

            if (source instanceof JSlider) {
                JSlider jslider = (JSlider) source;
                if (!jslider.getValueIsAdjusting()) {

                    if (source == backgroundR) {
                        head.setBackground(getSliderValue(backgroundR),
                                head.getBackgroundG(), head.getBackgroundB());
                    } else if (source == backgroundG) {
                        head.setBackground(head.getBackgroundR(),
                                getSliderValue(backgroundG),
                                head.getBackgroundB());
                    } else if (source == backgroundB) {
                        head.setBackground(head.getBackgroundR(),
                                head.getBackgroundG(),
                                getSliderValue(backgroundB));
                    } else if (source == sliderHue) {
                        setIntervalFromRangeSlider(actor.getLookupTable()
                                .getHue(), sliderHue);
                    } else if (source == sliderSat) {
                        setIntervalFromRangeSlider(actor.getLookupTable()
                                .getSat(), sliderSat);
                    } else if (source == sliderValue) {
                        setIntervalFromRangeSlider(actor.getLookupTable()
                                .getValue(), sliderValue);
                    } else if (source == sliderAlpha) {
                        setIntervalFromRangeSlider(actor.getLookupTable()
                                .getAlpha(), sliderAlpha);
                    } else if (source == contour) {
                        setIntervalFromRangeSlider(actor.getContour()
                                .getRange(), contour);
                    }

                    actor.display();
                    head.display();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        stateChangeFromUser = false;

        if (source == radioActorBone) {
            actor = head.getBone();
            setRangeSlidersToActor();
        } else if (source == radioActorSkin) {
            actor = head.getSkin();
            setRangeSlidersToActor();
        }

        stateChangeFromUser = true;
    }
}
