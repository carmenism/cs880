import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Draws a color table. Based on code from Professor R. Daniel Bergeron.
 * 
 * @author R. Daniel Bergeron (rdb@unh.edu), Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class DrawColorTable extends Canvas {
    private static final long serialVersionUID = -4077560183828202611L;
    private Color[] colorTable;
    private RenderLake render;

    /**
     * Resets the colors according to a new array of colors.
     * 
     * @param colors
     *            An array of R, G, B colors. The size should be n rows and 3
     *            columns, where n is the number of colors.
     * @param render
     *            A reference back to the main RenderLake program.
     */
    public DrawColorTable(int[][] colors, RenderLake render) {
        this.render = render;

        resetColors(colors);
    }

    /**
     * Resets the colors according to a new array of colors.
     * 
     * @param colors
     *            An array of R, G, B colors. The size should be n rows and 3
     *            columns, where n is the number of colors.
     */
    public void resetColors(int[][] colors) {
        colorTable = new Color[colors.length];

        for (int i = 0; i < colorTable.length; i++) {
            int red = colors[i][0];
            int green = colors[i][1];
            int blue = colors[i][2];

            colorTable[i] = new Color(red, green, blue);
        }
    }

    /**
     * Reverses the colors.
     */
    public void reverseColors() {
        int numberColors = colorTable.length;
        int end = numberColors - 1;

        for (int i = 0; i < numberColors / 2; i++) {
            Color forward = colorTable[i];
            Color backward = colorTable[end - i];

            colorTable[i] = backward;
            colorTable[end - i] = forward;
        }
    }

    @Override
    public void paint(Graphics g) {
        double min = render.getScalarMin();
        double max = render.getScalarMax();

        double mid = (min + max) / 2.0;
        double minMid = (min + mid) / 2.0;
        double midMax = (mid + max) / 2.0;

        String sMin = String.format("%.2f", min);
        String sMinMid = String.format("%.2f", minMid);
        String sMid = String.format("%.2f", mid);
        String sMidMax = String.format("%.2f", midMax);
        String sMax = String.format("%.2f", max);

        for (int i = 0; i < 256; i++) {
            g.setColor(colorTable[i]);
            g.fillRect(10 + i * 3, 10, 3, 70);
        }

        g.setColor(Color.black);
        g.drawString(sMin, 0, 90);
        g.drawString(sMinMid, 256 * 3 / 4 - 10, 90);
        g.drawString(sMid, 256 * 3 / 2 - 10, 90);
        g.drawString(sMidMax, 3 * 256 * 3 / 4 - 10, 90);
        g.drawString(sMax, 256 * 3 - 10, 90);
    }

}
