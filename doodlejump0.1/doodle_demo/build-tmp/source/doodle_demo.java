/* autogenerated by Processing revision 1290 on 2023-02-10 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class doodle_demo extends PApplet {

int winwid = 400;
int winhei = 400;
int ground = 250;
Guy g;
Gameboard bd;
Button restart;

public void setup(){
    /* size commented out by preprocessor */;
    frameRate(120);
    draw_bg();
    restart = new Button(10, winhei + 40, 100, 40);
    restart.set_text("RESTART");
    new_game();
}

public void draw(){
    if (restart.on_click()){
        new_game();
    }
    draw_bg();
    strokeWeight(3);
    bd.touch(g);
    int score = floor(bd.move_board());
    bd.remove_block();
    //println(f);
    bd.show();
    g.update();
    bd.gen_game(80, 10, 100);
    show_score(score);
    restart.show();
}

public void draw_bg(){
    int inix = 5;
    int iniy = 5;
    background(230, 240, 255);
    noStroke();
    fill(240, 255, 255);
    rect(inix, iniy, winwid, winhei);
    stroke(230, 240, 255);
    strokeWeight(1);
    for(int i = iniy; i < winhei; i+=20){
        line(inix, i, inix+winwid, i);
    }
    for(int i = inix; i < winwid; i+=20){
        line(i, iniy, i, iniy+winhei);
    }

}

public void show_score(int score){
    textAlign(LEFT, BASELINE);
    textSize(20);
    fill(100, 150, 150);
    text("SCORE :" + score, 10, winhei + 30);
}

public void new_game(){
    g = new Guy(205, 380, 0, 400);
    g.set_jumpmax(100);
    bd = new Gameboard(5, 5, winwid, winhei, 205, 380);
    bd.set_bias_speed(g);
    bd.gen_block(0, 0);
    bd.gen_game(80, 10);
}

public void keyPressed(){
    if (keyCode == LEFT){
        g.set_xspeed(-1);
    }
    if (keyCode == RIGHT){
        g.set_xspeed(1);
    }
}

public void keyReleased(){
    g.set_xspeed(0);
}
public class Block{
    float left;
    float y;
    float wid;
    float hei;

    public Block(float left, float y, float wid, float hei){
        this.left = left;
        this.y = y;
        this.wid = wid;
        this.hei = hei;
    }

    public Block(float left, float y, float wid){
        this(left, y, wid, 5);
    }

    public Block(float x, float y){
        this(x - 20, y, 40, 10);
    }

}
public class Button{
    private float x;
    private float y;
    private float wid;
    private float hei;
    private String txt;

    public Button(float x, float y, float wid, float hei){
        this.x = x;
        this.y = y;
        this.wid = wid;
        this.hei = hei;
    }

    public Button(float x, float y){
        this(x, y, 10, 10);
    }

    public void set_pos(float x, float y, float wid, float hei){
        this.x = x;
        this.y = y;
        this.wid = wid;
        this.hei = hei;
    }

    public void set_pos(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void set_size(float wid, float hei){
        this.wid = wid;
        this.hei = hei;
    }

    public void set_text(String str){
        this.txt = str;
    }

    public boolean on_touch(){
        return (    mouseX < x + wid
                &&  mouseX > x
                &&  mouseY < y + hei
                &&  mouseY > y
                );
    }

    public boolean on_click(){
        return (mousePressed && on_touch());
    }

    public void show(){
        if(!on_touch()){
            fill(230, 230, 230, 50);
        }
        else{
            fill(255, 255, 255, 95);
        }
        rect(x, y, wid, hei);
        fill(0);
        textSize(hei/2);
        textAlign(CENTER, BASELINE);
        text(txt, x + wid/2, y + hei * 2/3);
    }


}


public class Gameboard{
    private LinkedList<Block> q = new LinkedList<Block>();
    private float bd_LTx;
    private float bd_LTy;
    private float bd_wid;
    private float bd_hei;
    private float zero_x;
    private float zero_y;
    private float bias;

    private float last_y;
    private float bias_speed;
    private float bias_target;

    public Gameboard(float ltx, float lty, float wid, float hei, float zero_x, float zero_y){
        this.bd_LTx = ltx;
        this.bd_LTy = lty;
        this.bd_wid = wid;
        this.bd_hei = hei;
        this.zero_x = zero_x;
        this.zero_y = zero_y;
        this.last_y = -1;
    }

    public void set_bias_speed(Guy g){
        float jmx = g.get_jumpmax();
        float acc = g.get_acc();
        float t = sqrt(2 * jmx / acc);
        bias_speed = jmx / t;
    }


    public void remove_block(){
        float bottom = win2bd_y(bd_LTy + bd_hei);
        while(q.getFirst().y < bottom){
            q.poll();
        }
    }

    public float move_board(){
        if (bias < bias_target){
            bias += bias_speed;
        }
        return bias;
    }

    public void gen_block(float x, float y){
        Block b = new Block(x, y);
        q.add(b);
    }

    public void show(){
        stroke(0);
        for(Block b : q){
            if(b.y < win2bd_y(bd_LTy)){
                show_block(b);
            }
        }
    }

    public void bd_rect(float x, float y, float wid, float hei){
        float rx = bd2win_x(x);
        float ry = bd2win_y(y);
        rect(rx, ry, wid, hei);
    }


    public float touch(Guy g){
        float gy = win2bd_y(g.get_centre_y());
        if(g.get_jump_state() != -1){
            last_y = gy;
            return -1;
        }
        float gx = win2bd_x(g.get_centre_x());
        
        for (Block b : q){
            if(     b.left < gx + g.wid/2 
                &&  b.left + b.wid > gx - g.wid/2
                &&  this.last_y >= b.y 
                &&  gy <= b.y)
            {
                //println("by: " + b.y + ", last_y: " + this.last_y + ", " + "gy: " + gy);
                last_y = gy;
                g.reset_jump(b.y - bias);
                bias_target = b.y;
                return b.y;
            }
        }
        last_y = gy;
        return -1;
    }

    public void gen_game(float dist, int num, float threshold){
        Block b = q.getLast();
        if (b.y < win2bd_y(bd_LTy) + threshold){
            gen_game(dist, num);
        }
    }

    public void gen_game(float dist, int num){
        if (num == 0){
            return;
        }
        Block b = q.getLast();
        float left;
        float y;
        float l_edge = win2bd_x(bd_LTx);
        float r_edge = win2bd_x(bd_LTx + bd_wid);
        float angle;
        float d;

        do{
            angle = random(0, PI);
            d = random(b.wid * 1.5f, dist);
            left = b.left + d * cos(angle);
        }while(left < l_edge || left + b.wid > r_edge);
        //println(angle + ", " + d);

        y = b.y + d * sin(angle);
        gen_block(left + b.wid/2, y);
        gen_game(dist, num-1);
    }


    private void show_block(Block b){
        fill(200, 200, 230);
        bd_rect(b.left, b.y, b.wid, b.hei);
    }

    private float win2bd_x(float x){
        return x - this.zero_x;
    }
    private float win2bd_y(float y){
        return this.zero_y + this.bias - y;
    }
    private float bd2win_x(float x){
        return this.zero_x + x;
    }
    private float bd2win_y(float y){
        return this.zero_y + this.bias - y;
    }
}
public class Guy{

    private float jumpmax;
    private float acc;
    private float x;
    private float y;
    private float ini_x;
    private float ini_y;
    private int jump_state = 1; // '1' for upward, '-1' for downward
    private float speed = 0;
    private int LEFT_EDGE;
    private int RIGHT_EDGE;
    private float wid;
    private float ini_speed;
    private float xspeed = 0;

    public Guy(float x, float y, int left_edge, int right_edge, float wid, float jumpmax, float acc){
        this.x = x;
        this.ini_y = y;
        this.y = 0;
        this.LEFT_EDGE = left_edge;
        this.RIGHT_EDGE = right_edge;
        this.wid = wid;
        this.jumpmax = jumpmax;
        this.acc = acc;
        this.ini_speed = sqrt(2 * jumpmax * acc);
        this.speed = ini_speed;
    }

    public Guy(float x, float y, int left_edge, int right_edge, float wid){
        this(x, y, left_edge, right_edge, 10, 60, 0.1f);
    }

    public Guy(float x, float y, int left_edge, int right_edge){
        this(x, y, left_edge, right_edge, 10, 60, 0.1f);
    }

    public void set_jumpmax(float jumpmax){
        this.jumpmax = jumpmax;
        this.ini_speed = sqrt(2 * jumpmax * acc);
        this.speed = ini_speed;
    }

    public void set_acc(float acc){
        this.acc = acc;
    }

    public void jump(){
        if(speed < 0){
            jump_state = -1;
        }
        speed -= acc;
        y += speed;
    }

    public void reset_jump(float y){
        jump_state = 1;
        this.y = y;
        speed = (y <= 0) ? sqrt(2 * (jumpmax - y) * acc) : -speed;
    }

    public void set_xspeed(float dir){
        xspeed = dir;
    }

    public void move(float dir){
        x += dir;
        if (x < LEFT_EDGE){
            x = RIGHT_EDGE;
        }
        if (x > RIGHT_EDGE){
            x = LEFT_EDGE;
        }
    }

    public void update(){
        jump();
        move(xspeed);
        fill(200);
        stroke(0);
        ellipse(x, trans_pos(y), wid, wid);
    }

    public int get_jump_state(){
        return jump_state;
    }

    public float get_centre_x(){
        return x;
    }

    public float get_centre_y(){
        return trans_pos(y) + wid/2;
    }

    public float get_jumpmax(){
        return jumpmax;
    }

    public float get_acc(){
        return acc;
    }

    private float trans_pos(float y){
        return ini_y - y;
    }
}


  public void settings() { size(411, 501); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "doodle_demo" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
