package jp.ac.kansai_u.k403662.mobile;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import shiffman.box2d.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.joints.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class MainActivity extends PApplet {
    Box2DProcessing box2d;

    ArrayList<Boundary> boundaries;
    Ball[] balls = new Ball[100];
    Spring spring;

    float time = 0;
    int flag = 0;
    int[] balls_add = new int[100];
    boolean[] balls_clear = new boolean[100];
    PImage img_main, img_hole;
    PImage[] img_country = new PImage[6];
    ArrayList<Integer> list = new ArrayList<Integer>();
    int score = 0;
    int combo = 0;
    int clear_judge = 0;


    public void setup() {
        smooth();
        frameRate(60);
        box2d = new Box2DProcessing(this);
        box2d.createWorld();
        box2d.setGravity(0, 0);
        spring = new Spring();

        int aspect = displayWidth / displayHeight;
        int width_boundary = displayWidth / 30;
        int height_boundary = width_boundary / aspect;

        boundaries = new ArrayList<Boundary>();
        boundaries.add(new Boundary(width / 4 + (width_boundary/2), height - (height_boundary/2), width / 2 - (width_boundary*3), height_boundary));
        boundaries.add(new Boundary(width * 3 / 4 - (width_boundary/2), height - (height_boundary/2), width / 2 - (width_boundary*3), height_boundary));
        boundaries.add(new Boundary(width - (width_boundary/2), height / 2, width_boundary, height - (height_boundary*4)));
        boundaries.add(new Boundary(width_boundary/2, height / 2, width_boundary, height - (height_boundary*4)));
        boundaries.add(new Boundary(width / 4 + (width_boundary/2), width_boundary/2, width / 2 - (width_boundary*3), height_boundary));
        boundaries.add(new Boundary(width * 3 / 4 - (width_boundary/2), width_boundary/2, width / 2 - (width_boundary*3), height_boundary));
        balls[0] = new Ball(random(width/10, width*9/10), random(height/10, height*9/10), displayWidth/20);
        balls_add[0] = 0;
        for(int i = 1; i < 100; i++) {
            if(i < 10) {
                balls_add[i] = balls_add[i - 1] + 10;
                balls_add[i] -= i - 1;
            }else{
                balls_add[i] = balls_add[i-1] + 2;
            }
        }

        img_main = loadImage("main.png");
        img_hole = loadImage("hole.png");
        img_country[0] = loadImage("america.png");
        img_country[1] = loadImage("australia.png");
        img_country[2] = loadImage("brazil.png");
        img_country[3] = loadImage("china.png");
        img_country[4] = loadImage("nigeria.png");
        img_country[5] = loadImage("russia.png");
        for(int i = 1; i <= 90; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
    }

    public void draw() {
        stroke(255);
        billiards();
        box2d.step();

        for(Boundary wall: boundaries) {
            wall.display();
        }

        if(flag == 0) {
            textAlign(CENTER);
            fill(255, 255, 0);
            textSize(displayHeight / 10);
            text("課題（仮題）", width / 2, height / 3);
            text("START", width / 2, height / 2);
            text("HOW TO PLAY", width / 2, height * 2 / 3);

            stroke(10);
            //line(width/2*0.8f, height/2*0.8f, width/2*1.2f, height/2);
            //line(width/2*0.6f, height/3*1.7f, width/2*1.4f, height/3*2);

            if(mouseX > width/2*0.8 && mouseX < width/2*1.2 && mouseY > height/2*0.8 && mouseY < height/2) {
                if(mousePressed) flag = 2;
            }
            if(mouseX > width/2*0.6 && mouseX < width/2*1.4 && mouseY > height/3*1.7 && mouseY < height/3*2) {
                if(mousePressed) flag = 1;
            }
        }

        if(flag == 1) {
            textAlign(CENTER);
            fill(255, 255, 0);
            textSize(displayWidth/30);
            text("各地域で人口一位の国の国旗の球を、", width/2, height/6);
            text("白い球を操作してそれぞれの穴に落とそう！", width/2, height/6*2);
            text("連続で正しい穴に落とすと高得点です。", width/2, height/6*3);
            textSize(displayHeight/10);
            text("START", width/2, height/6*5);
            textSize(displayWidth/50);
            text("↖ヨーロッパ（ロシア）", width/8, height/8);
            text("↑アジア（中国）", width/2, height/8);
            text("↗北アメリカ（アメリカ）", width/8*7, height/8);
            text("↘南アメリカ（ブラジル）", width/8*7, height/8*7);
            text("↓オセアニア（オーストラリア）", width/2, height/8*7);
            text("↙アフリカ（ナイジェリア）", width/8, height/8*7);

            //line(width/2*0.8f, height/6*5*0.9f, width/2*1.2f, height/6*5);
            if(mouseX > width/2*0.8 && mouseX < width/2*1.2 && mouseY > height/6*5*0.9 && mouseY < height/6*5) {
                if(mousePressed) flag = 2;
            }
        }

        if(flag == 2) {
            time = time + (1/frameRate);

            spring.update(mouseX, mouseY);

            balls[0].display(img_main);
            spring.display();

            textAlign(CENTER);
            textSize(height/20);
            fill(255, 255, 0);
            text("FPS: " + (int)frameRate, width/4, height/20);
            text("SCORE: " + score + " , " + combo + " COMBO", width*3/4, height/20);
            for(int i = 1; i <= 90; i++) {
                if(time >= balls_add[i]) {
                    ball_display(list.get(i-1));
                }
            }

            float main_x = balls[0].body.getPosition().x*10;
            float main_y = balls[0].body.getPosition().y*10;
            if(main_x < -width/2 || main_x > width/2 || main_y < -height/2 || main_y > height/2) {
                if(spring.mouseJoint == null) {
                    gameover();
                }
            }

            for(int i = 1; i <= 90; i++) {
                if(balls[i] != null) {
                    float ball_x = balls[i].body.getPosition().x*10;
                    float ball_y = balls[i].body.getPosition().y*10;
                    if(ball_x > -width/5 && ball_x < width/5 && ball_y > height/2) {
                        asia(i);
                    }else if(ball_x > -width/5 && ball_x < width/5 && ball_y < -height/2) {
                        oceania(i);
                    }else if(ball_x < -width/3 && ball_y > height/3) {
                        if(ball_x < -width/2 || ball_y > height/2) europe(i);
                    }else if(ball_x < -width/3 && ball_y < -height/3) {
                        if(ball_x < -width/2 || ball_y < -height/2) africa(i);
                    }else if(ball_x > width/3 && ball_y > height/3) {
                        if(ball_x > width/2 || ball_y > height/2) north_america(i);
                    }else if(ball_x > width/3 && ball_y < -height/3) {
                        if(ball_x > width/2 || ball_y < -height/2) south_america(i);
                    }
                }
            }
            if(clear_judge >= 89) {
                gameclear();
            }
        }
    }

    public final int sketchWidth() {
        return displayWidth;
    }

    public final int sketchHeight() {
        return displayHeight;
    }

    public void billiards() {
        background(0xffC0C0C0);
        rectMode(LEFT);
        strokeWeight(0);
        fill(0xff2F6459);
        int aspect = displayWidth / displayHeight;
        int width_boundary = displayWidth / 30;
        int height_boundary = width_boundary / aspect;
        rect(width_boundary, height_boundary, displayWidth - width_boundary, displayHeight - height_boundary);
        fill(0xffC0C0C0);
        rect(0, 0, width_boundary*2, height_boundary*2);
        rect(0, height-(height_boundary*2), width_boundary*2, height);
        rect(width/2-(width_boundary), 0, width/2+(width_boundary), height_boundary);
        rect(width/2-(width_boundary), height-height_boundary, width/2+width_boundary, height);
        rect(width-(width_boundary*2), 0, width, height_boundary*2);
        rect(width-(width_boundary*2), height-(height_boundary*2), width, height);
        image(img_hole, 0, 0, width_boundary*2, height_boundary*2);
        image(img_hole, 0, height-(height_boundary*2), width_boundary*2, height_boundary*2);
        image(img_hole, width/2 - width_boundary, -height_boundary, width_boundary*2, height_boundary*2);
        image(img_hole, width/2 - width_boundary, height - height_boundary, width_boundary*2, height_boundary*2);
        image(img_hole, width - (width_boundary*2), 0, width_boundary*2, height_boundary*2);
        image(img_hole, width - (width_boundary*2), height - (height_boundary*2), width_boundary*2, height_boundary*2);
    }

    public void ball_display(int i) {
        if(balls[i] == null) {
            if(i >= 1 && i <= 15) {
                balls[i] = new Ball(random(width/10,width*9/10), random(height/10,height*9/10), width/25);
            }else if(i >= 16 && i <= 30) {
                balls[i] = new Ball(random(width/10,width*9/10), random(height/10,height*9/10), width/30);
            }else if(i >= 31 && i <= 45) {
                balls[i] = new Ball(random(width/10,width*9/10), random(height/10,height*9/10), width/25);
            }else if(i >= 46 && i <= 60) {
                balls[i] = new Ball(random(width/10,width*9/10), random(height/10,height*9/10), width/20);
            }else if(i >= 61 && i <= 75) {
                balls[i] = new Ball(random(width/10,width*9/10), random(height/10,height*9/10), width/25);
            }else if(i >= 76 && i <= 90) {
                balls[i] = new Ball(random(width/10,width*9/10), random(height/10,height*9/10), width/25);
            }
        }
        if(i >= 1 && i <= 15) {
            balls[i].display(img_country[0]);
        }else if(i >= 16 && i <= 30) {
            balls[i].display(img_country[1]);
        }else if(i >= 31 && i <= 45) {
            balls[i].display(img_country[2]);
        }else if(i >= 46 && i <= 60) {
            balls[i].display(img_country[3]);
        }else if(i >= 61 && i <= 75) {
            balls[i].display(img_country[4]);
        }else if(i >= 76 && i <= 90) {
            balls[i].display(img_country[5]);
        }
    }

    public void north_america(int i) {
        if(balls_clear[i] != true) {
            balls_clear[i] = true;
            if(i >= 1 && i <= 15) {
                score(true);
            }else{
                score(false);
            }
            clear_judge++;
        }
    }

    public void oceania(int i) {
        if(balls_clear[i] != true) {
            balls_clear[i] = true;
            if(i >= 16 && i <= 30) {
                score(true);
            }else{
                score(false);
            }
            clear_judge++;
        }
    }

    public void south_america(int i) {
        if(balls_clear[i] != true) {
            balls_clear[i] = true;
            if(i >= 31 && i <= 45) {
                score(true);
            }else{
                score(false);
            }
            clear_judge++;
        }
    }

    public void asia(int i) {
        if(balls_clear[i] != true) {
            balls_clear[i] = true;
            if(i >= 46 && i <= 60) {
                score(true);
            }else{
                score(false);
            }
            clear_judge++;
        }
    }

    public void africa(int i) {
        if(balls_clear[i] != true) {
            balls_clear[i] = true;
            if(i >= 61 && i <= 75) {
                score(true);
            }else{
                score(false);
            }
            clear_judge++;
        }
    }

    public void europe(int i) {
        if(balls_clear[i] != true) {
            balls_clear[i] = true;
            if(i >= 76 && i <= 90) {
                score(true);
            }else{
                score(false);
            }
            clear_judge++;
        }
    }

    public void score(boolean b) {
        if(b == true) {
            combo += 1;
            score += combo;
        }else if(b == false) {
            combo = 0;
        }
    }

    public void gameover() {
        billiards();
        textAlign(CENTER);
        textSize(displayWidth/10);
        fill(0);
        text("GAME OVER", width/2, height/2);
    }

    public void gameclear() {
        billiards();;
        textAlign(CENTER);
        fill(255, 255, 0);
        textSize(displayHeight / 10);
        text("CLEAR!!", width / 2, height / 3);
        text("SCORE: " + score, width / 2, height / 2);
        text("RETRY", width / 2, height * 2 / 3);

        stroke(10);
        //line(width/2*0.8f, height/2*0.8f, width/2*1.2f, height/2);
        //line(width/2*0.6f, height/3*1.7f, width/2*1.4f, height/3*2);

        if(mouseX > width/2*0.6 && mouseX < width/2*1.4 && mouseY > height/3*1.7 && mouseY < height/3*2) {
            score = 0;
            combo = 0;
            time = 0;
            clear_judge = 0;
            for(int i = 0; i <= 90; i++) balls[i] = null;
            billiards();
            if(mousePressed) flag = 0;
        }
    }

    public void mouseReleased() {
        spring.destroy();
    }

    public void mousePressed() {
        if(balls[0].contains(mouseX, mouseY)) {
            spring.bind(mouseX, mouseY, balls[0]);
        }
    }

    class Ball {
        Body body;
        float rad;

        Ball(float x, float y, float r) {
            rad = r;

            BodyDef bd = new BodyDef();
            bd.type = BodyType.DYNAMIC;

            bd.position = box2d.coordPixelsToWorld(x, y);
            body = box2d.world.createBody(bd);

            CircleShape cs = new CircleShape();
            cs.m_radius = box2d.scalarPixelsToWorld(r/2);

            FixtureDef fd = new FixtureDef();
            fd.shape = cs;
            fd.density = 1;
            fd.friction = 0.3f;
            fd.restitution = 0.5f;

            body.createFixture(fd);
        }

        public void killBody() {
            box2d.destroyBody(body);
        }

        public boolean contains(float x, float y) {
            Vec2 worldPoint = box2d.coordPixelsToWorld(x, y);
            Fixture f = body.getFixtureList();
            boolean inside = f.testPoint(worldPoint);
            return inside;
        }

        public void display(PImage img) {
            Vec2 pos = box2d.getBodyPixelCoord(body);
            float a = body.getAngle();
            pushMatrix();
            translate(pos.x, pos.y);
            rotate(a);
            fill(150);
            stroke(0);
            strokeWeight(1);
            image(img, -rad/2, -rad/2, rad, rad);
            popMatrix();
        }
    }

    class Boundary {
        float x;
        float y;
        float w;
        float h;

        Body b;

        Boundary(float x1, float y1, float w1, float h1) {
            x = x1;
            y = y1;
            w = w1;
            h = h1;

            PolygonShape sd = new PolygonShape();

            float box2dW = box2d.scalarPixelsToWorld(w/2);
            float box2dH = box2d.scalarPixelsToWorld(h/2);

            sd.setAsBox(box2dW, box2dH);

            BodyDef bd = new BodyDef();
            bd.type = BodyType.STATIC;
            bd.position.set(box2d.coordPixelsToWorld(x,y));
            b = box2d.createBody(bd);

            b.createFixture(sd, 1);
        }

        public void display() {
            fill(188, 132, 88);
            stroke(0);
            strokeWeight(0);
            rectMode(CENTER);
            rect(x, y, w, h);
        }
    }

    class Spring {
        MouseJoint mouseJoint;

        Spring() {
            mouseJoint = null;
        }

        public void update(float x, float y) {
            if(mouseJoint != null) {
                Vec2 mouseWorld = box2d.coordPixelsToWorld(x, y);
                mouseJoint.setTarget(mouseWorld);
            }
        }

        public void display() {
            if(mouseJoint != null) {
                Vec2 v1 = new Vec2(0, 0);
                mouseJoint.getAnchorA(v1);
                Vec2 v2 = new Vec2(0, 0);
                mouseJoint.getAnchorB(v2);
                v1 = box2d.coordWorldToPixels(v1);
                v2 = box2d.coordWorldToPixels(v2);

                stroke(0);
                strokeWeight(1);
                line(v1.x, v1.y, v2.x, v2.y);
            }
        }

        public void bind(float x, float y, Ball balls) {
            MouseJointDef md = new MouseJointDef();
            md.bodyA = box2d.getGroundBody();
            md.bodyB = balls.body;
            Vec2 mp = box2d.coordPixelsToWorld(x, y);
            md.target.set(mp);
            md.maxForce = 1000.0f * balls.body.m_mass;
            md.frequencyHz = 5.0f;
            md.dampingRatio = 0.9f;

            mouseJoint = (MouseJoint)box2d.world.createJoint(md);
        }

        public void destroy() {
            if(mouseJoint != null) {
                box2d.world.destroyJoint(mouseJoint);
                mouseJoint = null;
            }
        }
    }
}

