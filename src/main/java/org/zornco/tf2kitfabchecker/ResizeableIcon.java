// 
// Decompiled by Procyon v0.5.36
// 

package org.zornco.tf2kitfabchecker;

import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Component;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

public class ResizeableIcon implements Icon
{
    private final BufferedImage image;
    private final int width;
    private final int height;
    
    public ResizeableIcon(final File img, final int width, final int height) throws IOException {
        this.image = ImageIO.read(img);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        g.drawImage(this.image, x, y, this.width, this.height, c);
    }
    
    @Override
    public int getIconWidth() {
        return this.width;
    }
    
    @Override
    public int getIconHeight() {
        return this.height;
    }
}
