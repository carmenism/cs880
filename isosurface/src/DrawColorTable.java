//-------------- DrawColorTable class ---------------------------
// DrawColorTable provides a canvas into which we will draw a color table
//  DrawColorTable extends Canvas, but all we need to redefine is the
// "paint" method.

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class DrawColorTable extends Canvas {
    private static final long serialVersionUID = -4077560183828202611L;

    private Color colorTable[] = new Color[256];

    public DrawColorTable(int[][] colors) {
        resetColors(colors);
    }

    public void resetColors(int[][] colors) {
        for (int i = 0; i < colorTable.length; i++) {
            int red = colors[i][0];
            int green = colors[i][1];
            int blue = colors[i][2];

            colorTable[i] = new Color(red, green, blue);
        }
    }

    public void paint(Graphics g) {
        for (int i = 0; i < 256; i++) {
            g.setColor(colorTable[i]);
            g.fillRect(i * 3, 0, 3, 100);
        }
    }
    
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
}
