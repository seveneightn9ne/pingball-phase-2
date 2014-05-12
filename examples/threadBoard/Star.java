package threadBoard;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author fenec
 */
public class Star {
    Image starImage;
    int x,y;
    int destinationX=200,destinationY=226;
    boolean lockY=true;
    int delay;


    public Star(int delay,int initialX,int initialY){
        ImageIcon ii = new ImageIcon(this.getClass().getResource("marioStar.png"));
        starImage = ii.getImage();
        x=initialX;
        y=initialY;
        this.delay=delay;
    }


    void moveToX(int destX){
        this.x += 1;
    }


    boolean validDestinatonX(){
        if(this.x==this.destinationX){
                this.lockY=false;
                return true;
            }
        else
            return false;
    }

    void moveToY(int destY){
        this.y += 1;
    }


    boolean validDestinatonY(){
        if(this.y==this.destinationY)
            return true;
        else
            return false;
    }

    void move(){

        if(!this.validDestinatonX() )
            x+=1;
        if(!this.validDestinatonY() && !this.lockY)
            y+=1;

        /*if(!this.validDestinatonY())
            y+=1;
        */
    }


}
