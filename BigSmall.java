
/*
 * Increase the number in line 60 to generate more tiles. In line 217 change the number to change the size ofhe tiles.
 *          */
package com.company;
import java.awt.*;
import java.util.List;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import static java.lang.Math.*;
import static java.util.stream.Collectors.toList;

public class BigSmall extends JPanel {
    // ignores missing hash code
    class Tile {
        double x, y, angle, size, sign;
        Type type;

        Tile(Type t, double x, double y, double a, double s, double z) {
            type = t;
            this.x = x;
            this.y = y;
            angle = a;
            size = s;
            sign = z;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Tile) {
                Tile t = (Tile) o;
                return type == t.type && x == t.x && y == t.y && angle == t.angle;
            }
            return false;
        }
    }

    enum Type {
        Big, Small
    }

    static final double G = (1 + sqrt(5)) / 2; // golden ratio
    static final double T = toRadians(36); // theta

    List<Tile> tiles = new ArrayList<>();

    public BigSmall() {
        int w = 700, h = 450;
        setPreferredSize(new Dimension(w, h));
        setBackground(Color.white);

        tiles = deflateTiles(setupPrototiles(w, h), 10); //generation


    }

    List<Tile> setupPrototiles(int w, int h) {
        List<Tile> proto = new ArrayList<>();

        // sun
        // for (double a = PI / 2 + T; a < 415 * PI/180; a += 2 * T)
        //    proto.add(new Tile(Type.Big, w / 2, h / 2, a, w / 2.5));
        proto.add(new Tile(Type.Small, w / 2, h / 2, 0, w / 2.5, -1));

        return proto;
    }

    List<Tile> deflateTiles(List<Tile> tls, int generation) {
        if (generation <= 0)
            return tls;

        List<Tile> next = new ArrayList<>();

        for (Tile tile : tls) {
            double x = tile.x, y = tile.y, a = tile.angle, nx, ny, mx, my;
            double size = tile.size / sqrt(G);

            if (tile.type == Type.Small) { //adding new tiles information into arrays for next generation
                next.add(new Tile(Type.Big, x, y, a, size, tile.sign));

            } else {

                nx = tile.x + (Math.pow(G,2.5) * tile.size - G * size) * cos(tile.angle);
                ny = tile.y - (Math.pow(G,2.5) * tile.size - G * size) * sin(tile.angle);
                mx = tile.x + Math.pow(G,2.5) * tile.size * cos(tile.angle);
                my = tile.y - Math.pow(G,2.5) * tile.size * sin(tile.angle);
                next.add(new Tile(Type.Small, nx, ny, tile.angle - PI, size,-1 * tile.sign));
                next.add(new Tile(Type.Big, mx, my, tile.angle - tile.sign * PI/2, size, tile.sign));

            }
          //  next.add(tile);
        }

        // remove duplicates
        tls = next.stream().distinct().collect(toList());

        return deflateTiles(tls, generation - 1);
    }

    void drawTiles(Graphics2D g) {
        BufferedImage BIbig=new BufferedImage(1,1,1);
        BufferedImage BIsmall=new BufferedImage(1,1,1);
        BufferedImage BIbig_r=new BufferedImage(1,1,1);
        BufferedImage BIsmall_r=new BufferedImage(1,1,1);
        try {
            BIbig = ImageIO.read(this.getClass().getResource("Big_modified.png"));
            BIsmall = ImageIO.read(this.getClass().getResource("Small_modified.png"));
            BIbig_r = ImageIO.read(this.getClass().getResource("Big_modified_reflected.png"));
            BIsmall_r = ImageIO.read(this.getClass().getResource("Small_modified_reflected.png"));//import tiles
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        for (Tile tile : tiles) {
            int ord = tile.type.ordinal();
            double z1 = tile.sign;
            /*
            double angle = tile.angle - T;
            Path2D path = new Path2D.Double();
            path.moveTo(tile.x, tile.y);


            for (int i = 0; i < 3; i++) {
                double x = tile.x + dist[ord][i] * tile.size * cos(angle);
                double y = tile.y - dist[ord][i] * tile.size * sin(angle);
                path.lineTo(x, y);
                angle += T;

            }

            path.closePath();
            g.setColor(ord == 0 ? Color.orange : Color.yellow);
            g.fill(path);
            g.setColor(Color.darkGray);
            g.draw(path);
      */
            double e1, e2, es;
            e1=0;
            e2=0;
            es=0.0333;

            if(ord==0) {      //big
                // create the transform, note that the transformations happen
                // in reversed order (so check them backwards)
                AffineTransform at = new AffineTransform();

                // 4. translate it to the center of the component
                at.translate(tile.x-e1*tile.size* cos(tile.angle),tile.y+e1*tile.size* sin(tile.angle));

                // 3. do the actual rotation
                at.rotate(-tile.angle);

                // 2. just a scale because this image is big
                at.scale(tile.size*es, tile.sign*tile.size*es);


                // 1. translate the object so that you rotate it around the
                //    center (easier :))
                if (z1==-1){ //check if reflected
                at.translate(-BIbig.getWidth()/2, -BIbig.getHeight()/2);
                    g.drawImage(BIbig, at ,null); //modified
                } else {at.translate(-BIbig_r.getWidth()/2, -BIbig_r.getHeight()/2);
                    g.drawImage(BIbig_r, at ,null);} // modified + reflected






            }
            else if(ord==1)// Small
            {

                // create the transform, note that the transformations happen
                // in reversed order (so check them backwards)
                AffineTransform at = new AffineTransform();

                // 4. translate it to the center of the component
                at.translate(tile.x-e2*tile.size* cos(tile.angle),tile.y+e2*tile.size* sin(tile.angle));

                // 3. do the actual rotation
                at.rotate(-tile.angle);

                // 2. just a scale because this image is big
                at.scale(tile.size*es, tile.sign*tile.size*es);


                // 1. translate the object so that you rotate it around the
                //    center (easier :))
                if (z1==-1){
                    at.translate(-BIsmall.getWidth()/2, -BIsmall.getHeight()/2);
                    g.drawImage(BIsmall, at ,null); //modified
                }

                else {
                    at.translate(-BIsmall_r.getWidth()/2, -BIsmall_r.getHeight()/2);
                    g.drawImage(BIsmall_r, at ,null); //modified + reflected
                }



            }
            /*
             BufferedImage BIbig=new BufferedImage(1,1,1);
        	try {
        		BIbig = ImageIO.read(this.getClass().getResource("Big.png"));
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
            g.drawImage(BIbig,10,100,null);
            g.drawImage(BIbig,0,0,null);
            */
        }



    }



    @Override
    public void paintComponent(Graphics og) {
        super.paintComponent(og);
        Graphics2D g = (Graphics2D) og;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(-150, 275);//change this number to move image around
        g.scale(0.7,0.7); //change this number to change the size of the image
        drawTiles(g);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("Ammann Tiling");
            f.setResizable(true);

            f.add(new BigSmall(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);

            f.setVisible(true);
        });
    }
}