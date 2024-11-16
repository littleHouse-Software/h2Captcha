package com.littleHouse.h2Captcha;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PanelPreview extends JPanel {

    private BufferedImage bufferedImage;

    public PanelPreview() {
    }

    public void setImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillRect(0,0,getWidth()-1,getHeight()-1);
        if (bufferedImage == null) return;
        float scale = (float) (bufferedImage.getHeight() > bufferedImage.getWidth() ? getHeight() : getWidth()) / (float) (Math.max(bufferedImage.getHeight(), bufferedImage.getWidth()));
        int width = (int) (bufferedImage.getWidth() * scale);
        int height = (int) (bufferedImage.getHeight() * scale);
        int leftMargin = height>width ? (getWidth()-width)/2 : 0;
        int topMargin = height<width ? (getHeight()-height)/2 : 0;
        g.drawImage(bufferedImage, leftMargin, topMargin, width-1, height-1, this);
    }

}
