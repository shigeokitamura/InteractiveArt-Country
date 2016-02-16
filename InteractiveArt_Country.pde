import shiffman.box2d.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.joints.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import java.util.Collections;
//import ddf.minim.*;

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
PFont font;
//Minim minim;
//AudioSample sound;

void setup() {
  size(660, 375);
  smooth();
  frameRate(60);
  box2d = new Box2DProcessing(this);
  box2d.createWorld();
  box2d.setGravity(0, 0);
  spring = new Spring();
  
  boundaries = new ArrayList<Boundary>();
  boundaries.add(new Boundary(width/4+15,height-10,width/2-90,20));
  boundaries.add(new Boundary(width*3/4-15,height-10,width/2-90,20));
  boundaries.add(new Boundary(width-10,height/2,20,height-120));
  boundaries.add(new Boundary(10,height/2,20,height-120));
  boundaries.add(new Boundary(width/4+15,10,width/2-90,20));
  boundaries.add(new Boundary(width*3/4-15,10,width/2-90,20));
  balls[0] = new Ball(random(50,610),random(50,325),30);
  balls_add[0] = 0;
  for(int i = 1; i < 100; i++) {
    if(i < 10) {
      balls_add[i] = balls_add[i-1] + 10;
      balls_add[i] -= i-1;
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
  font = createFont("MS-Gothic-24.vlw",24);
  //minim = new Minim(this);
  //sound = minim.loadSample("spo_ge_biriyado_ball01.mp3");
}

void draw() {
  //println(mouseX, mouseY);
  //println(frameRate);
  billiards();
  box2d.step();
  
  //Display all the boundaries
  for (Boundary wall: boundaries) {
    wall.display();
  }
  
  if(flag == 0) {
    textAlign(CENTER);
    textFont(font,50);
    text("課題（仮題）", width/2, height/2 - 100);
    textSize(50);
    if(mouseX > 255 && mouseX < 410 && mouseY > 153 && mouseY < 190) {
      fill(255, 255, 0);
      if(mousePressed) {
        flag = 2;
      }
    }else{
      fill(0);
    }
    text("START", width/2, height/2);
    if(mouseX > 165 && mouseX < 500 && mouseY > 253 && mouseY < 290) {
      fill(255, 255, 0);
      if(mousePressed) {
        flag = 1;
      }
    }else{
      fill(0);
    }
    text("HOW TO PLAY", width/2, height/2 + 100);
    textAlign(RIGHT);
    textSize(10);
    fill(0);
    text("Powered by Box2D for Processing", width-100, height-5);
  }
  if(flag == 1) {
    //println(mouseX, mouseY);
    textAlign(LEFT);
    textFont(font);
    text("↑ヨーロッパ", 40, 70);
    text("↑アジア", 320, 50);
    text("北アメリカ↑", 500, 70);
    text("↓アフリカ", 40, 320);
    text("↓オセアニア", 320, 340);
    text("南アメリカ↓", 500, 320);
    textAlign(CENTER);
    text("各地域で人口1位の国の国旗の球を、", width/2, 125);
    text("白い球を操作してそれぞれの穴に入れましょう。",width/2, 150);
    text("連続で正しい穴に入れると高得点です。",width/2, 175);
    textSize(50);
    if(mouseX > 256 && mouseX < 410 && mouseY > 217 && mouseY < 255) {
      fill(255, 255, 0);
      if(mousePressed) {
        flag = 2;
      }
    }else{
      fill(0);
    }
    text("START", width/2, 250);
  }
  
  if(flag == 2) {
    //println((int)time);
    time = time + (1/frameRate);
    
    spring.update(mouseX, mouseY);
    
    balls[0].display(img_main);
    spring.display();
    
    textAlign(CENTER);
    textSize(20);
    fill(255,255,0);
    text("SCORE: " + score, width*3/4, 18);
    if(combo > 1) {
      text(combo + " COMBO", width/4, 18);
    }
    fill(0);
    text("FPS: " + (int)frameRate, width/4, height-2);
    for(int i = 1; i <= 90; i++) {
      if(time >= balls_add[i]) {
        ball_display(list.get(i-1));
      }
    }
    
    //判定
    //println(balls[0].body.getPosition().x*10, balls[0].body.getPosition().y*10);
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
        if(ball_x > -30 && ball_x < 30 && ball_y > height/2){
          asia(i);
        }else if(ball_x > -30 && ball_x < 30 && ball_y < -height/2) {
          oceania(i);
        }else if(ball_x < (-width/2+60) && ball_y > height/2-20 || ball_x < (-width/2+20) && ball_y > height/2-60) {
          europe(i);
        }else if(ball_x > width/2-60 && ball_y >height/2-20 || ball_x > width/2-20 && ball_y > height/2-60) {
          north_america(i);
        }else if(ball_x > width/2-60 && ball_y < (-height/2+20) || ball_x > width/2-20 && ball_y < (-height/2+60)) {
          south_america(i);
        }else if(ball_x < (-width/2+60) && ball_y < (-height/2+20) || ball_x < (-width/2+20) && ball_y > (-height/2+60)) {
          africa(i);
        }
      }
    }
    //println(score);
    
    //クリア判定
    println(clear_judge);
    if(clear_judge >= 89) {
      gameclear();
    }
  }
}

void mouseReleased() {
  spring.destroy();
}

void mousePressed() {
  if(balls[0].contains(mouseX, mouseY)) {
    spring.bind(mouseX, mouseY, balls[0]);
  }
}

void ball_display(int i) {
  if(balls[i] == null) {
    if(i >= 1 && i <= 15) {
      balls[i] = new Ball(random(50,610), random(50,325), 40);
    }else if(i >= 16 && i <= 30) {
      balls[i] = new Ball(random(50,610), random(50,325), 20);
    }else if(i >= 31 && i <= 45) {
      balls[i] = new Ball(random(50,610), random(50,325), 36);
    }else if(i >= 46 && i <= 60) {
      balls[i] = new Ball(random(50,610), random(50,325), 60);
    }else if(i >= 61 && i <= 75) {
      balls[i] = new Ball(random(50,610), random(50,325), 34);
    }else if(i >= 76 && i <= 90) {
      balls[i] = new Ball(random(50,610), random(50,325), 30);
    }
    //balls[i] = new Ball(random(50,610), random(50,325), random(20,60));
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
  //balls[i].display(img_main); 
}

void billiards(){
  background(#C0C0C0);
  rectMode(LEFT);
  strokeWeight(0);
  fill(#2F6459);
  rect(20, 20, width-20, height-20);
  image(img_hole, 0, 0, 60, 60);
  image(img_hole, width-60, 0, 60, 60);
  image(img_hole, 0, height-60, 60, 60);
  image(img_hole, width-60, height-60, 60, 60);
  image(img_hole, width/2-30, -30, 60, 60);
  image(img_hole, width/2-30, height-30, 60, 60);
}

void north_america(int i) {
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
void oceania(int i) {
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
void south_america(int i) {
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
void asia(int i) {
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
void africa(int i) {
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
void europe(int i) {
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

void score(boolean b) {
  if(b == true) {
    combo += 1;
    score += combo;
  }else if(b == false) {
    combo = 0;
  }
}

void gameover() {
  billiards();
  textAlign(CENTER);
  textSize(100);
  fill(0);
  text("GAME OVER", width/2, height/2);
}

void gameclear() {
  billiards();
  billiards();
  textAlign(CENTER);
  textSize(64);
  fill(255,255,0);
  text("CLEAR!!", width/2, height/2-100);
  text("SCORE: " + score, width/2, height/2);
  if(mouseX > 239 && mouseX < 429 && mouseY > 242 && mouseY < 290) {
    fill(255,255,0);
    if(mousePressed) {
      flag = 0;
    }
  }else{
    fill(0);
  }
  text("RETRY", width/2, height/2+100);
}

/*void stop() {
  sound.close();
  minim.stop();
  super.stop();
}*/



//Copyright(C) 2015 Shigeo Kitamura All Rights Reserved.
/* 謝辞
shiffman/Box2D-for-Processing(https://github.com/shiffman/Box2D-for-Processing)
Sankakukei, Inoue Keisuke/無料で使えるEPSフリー素材集(http://freesozai.jp/)
Abysse Corporation/世界地図・世界の国旗(http://www.abysse.co.jp/world/index.html)
*/
