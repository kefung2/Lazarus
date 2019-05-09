package Lazarus;

import Lazarus.GameObj.*;
import Lazarus.GameObj.Button;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

public class LazarusWorld extends JComponent {
    //values

    //Frame Values
    private int fWidth = 660;  //640
    private int fHeight = 560; // 480

    //Resource
    private String player;
    private String background;
    private String jumpleft;
    private String jumpright;
    private String squished;
    private String cardboardbox;    // all box dimension 40x40
    private String metalbox;
    private String woodbox;
    private String stonebox;
    private String button;
    private String wall;
    private String level1;
    private String level2;
    private String level3;
    private int maxlevel;

    //Game Element
    private int currLevel;
    private static Player p1;
    private LazaKey p1Key;
    private ArrayList<Wall> mapWall;
    private ArrayList<CardBoardBox> mapC;
    private ArrayList<WoodBox> mapW;
    private ArrayList<MetalBox> mapM;
    private ArrayList<StoneBox> mapS;
    private ArrayList<Button> levelButton;
    private Scanner scanner;
    private CardBoardBox droppingCBox;
    private WoodBox droppingWBox;
    private MetalBox droppingMBox;
    private StoneBox droppingSBox;
    private Random rng;
    private boolean firstTime;
    private boolean dropping;
    private ArrayList<String> DropboxList;
    private String boxType;
    private ArrayList<Boxes> AllBoxOnMap;
    private ArrayList<CardBoardBox> CboxInAir;
    private ArrayList<WoodBox> WboxInAir;
    private ArrayList<MetalBox> MboxInAir;
    private ArrayList<StoneBox> SboxInAir;
    private int nextBox;
    private int currDropping;
    private int dropClock, levelClock;

    //Draw
    private DrawPanel drawpanel;

    /**********************************************************************************************************/
    //main function

    public void LazarusWorld(){}

    public void init(){
        setResourcePath();
        this.drawpanel = new DrawPanel(fWidth,fHeight, background, this);
        currLevel = 1;
        maxlevel = 3;
        firstTime = true;

        setMapObj();
        setLevelClock();
        setFrame();
    }

    public void startGame(){
        init();
        try{
            while(true) {
                update();
                this.drawpanel.repaint();

                if(levelButton.get(0).getLevelup() || levelButton.get(1).getLevelup()){
                    if(currLevel < maxlevel){
                        levelUp();
                    }
                }

                Thread.sleep(1000/144); //1000/144
            }

        } catch (InterruptedException ignored){

        }
    }

    public void stop(){}

    public void levelUp() {
        currLevel++;
        setLevelClock();
    }

    public void update(){
        this.p1.update();

//        AllBoxOnMap.forEach((curr) -> {
//            curr.update();
//        });

        boxQueue();
        dropClock--;
        System.out.println("dropping: " + dropping);
        if(!(dropping) && dropClock <= 0){
            this.drawpanel.DropABox();
            if (DropboxList.get(0) == "Cbox") {

                droppingCBox = new CardBoardBox(p1.getX()+2, 0 , 1, this.getCBoximg(), this, p1);
                droppingCBox.setDroping();
                CboxInAir.add(droppingCBox);
                dropping = true;
                DropboxList.remove(0);
                this.drawpanel.setCBoxInAir(CboxInAir);
                currDropping = 1;

            } else if (DropboxList.get(0) == "Wbox") {

                droppingWBox = new WoodBox(p1.getX()+2, 0 , 2, this.getWBoximg(), this, p1);
                droppingWBox.setDroping();
                WboxInAir.add(droppingWBox);
                DropboxList.remove(0);
                this.drawpanel.setWBoxInAir(WboxInAir);
                currDropping = 2;

            } else if (DropboxList.get(0) == "Mbox") {

                droppingMBox = new MetalBox(p1.getX()+2, 0 , 3, this.getMBoximg(), this, p1);
                droppingMBox.setDroping();
                MboxInAir.add(droppingMBox);
                DropboxList.remove(0);
                this.drawpanel.setMBoxInAir(MboxInAir);
                currDropping = 3;


            } else if (DropboxList.get(0) == "Sbox") {

                droppingSBox = new StoneBox(p1.getX()+2, 0 , 4, this.getSBoximg(), this, p1);
                droppingSBox.setDroping();
                SboxInAir.add(droppingSBox);
                DropboxList.remove(0);
                this.drawpanel.setSBoxInAir(SboxInAir);
                currDropping = 4;

            }
            System.out.println("Now dropping: " + currDropping);
            dropClock = levelClock;
        }
        this.drawpanel.setBox(DropboxList); // display next 3 box

    }

    /*********************************************************************************************************/
    //set up
     public void setResourcePath(){
         background = "Resource/Background.bmp";
         player = "Resource/Lazarus_stand.gif";
         jumpleft = "Resource/Lazarus_left.gif";
         jumpright = "Resource/Lazarus_right.gif";
         squished = "Resource/Lazarus_squished.gif";
         cardboardbox = "Resource/CardBox.gif";
         woodbox = "Resource/WoodBox.gif";
         metalbox = "Resource/MetalBox.gif";
         stonebox = "Resource/StoneBox.gif";
         button = "Resource/Button.gif";
         wall = "Resource/Wall.gif";
         level1 = "Resource/LazarusMap_Level1.csv";
         level2 = "Resource/LazarusMap_Level2.csv";
         level3 = "Resource/LazarusMap_Level3.csv";
     }

     public void setFrame(){
         JFrame jFrame = new JFrame();
         jFrame.setTitle("Lazarus");
         jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         jFrame.add(this.drawpanel);
         jFrame.setSize(fWidth,fHeight);
         jFrame.addKeyListener(p1Key);

         jFrame.setVisible(true);
     }


    public void setMapObj(){
        BufferedImage img;
        String mapLevel = null;
        AllBoxOnMap = new ArrayList<>();
        levelButton = new ArrayList<>();

        DropboxList = new ArrayList<>();
        CboxInAir = new ArrayList<>();
        WboxInAir = new ArrayList<>();
        MboxInAir = new ArrayList<>();
        SboxInAir = new ArrayList<>();

//        mapWall = new ArrayList<>();
//        mapC = new ArrayList<>();
//        mapW = new ArrayList<>();
//        mapM = new ArrayList<>();
//        mapS = new ArrayList<>();

        if(currLevel == 1){
            mapLevel = level1;
        }else if(currLevel == 2){
            mapLevel = level2;
        }else if(currLevel == 3){
            mapLevel = level3;
        }


        try {
            scanner = new Scanner(new File(mapLevel));
        }catch (IOException e){
            System.out.println("File not found");
        }

        while(scanner.hasNext()) {

            for (int row = 0; row < 12; row++) {
                String data = scanner.next();
                String[] value = data.split(",");
                for (int col = 0; col < 16; col++) {
                    int mapCode = Integer.parseInt(value[col]);
                    if (mapCode == 1) {
                        img = stringToBuffer(wall);
                        //mapWall.add(new Wall(col*40, row*40, img));
                        AllBoxOnMap.add(new Boxes(col*40, row*40, img));
                    }
                    if (mapCode == 2) {
                        img = stringToBuffer(player);
                        if(currLevel == 1){
                        p1 = new Player(col*40,row*40,img,this);
                        p1Key = new LazaKey(p1, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
                        }else if(currLevel > 1){
                            p1.setX(col*40);
                            p1.setY(row*40);
                        }
                    }
                    if (mapCode == 3) {
                        img = stringToBuffer(button);
                        levelButton.add(new Button(col*40, row*40, img));
                    }

                }
            }
        }
//        this.drawpanel.setCBoxonMap(mapC);
//        this.drawpanel.setWBoxonMap(mapW);
//        this.drawpanel.setMBoxonMap(mapM);
//        this.drawpanel.setSBoxonMap(mapS);
        this.drawpanel.setBoxonMap(AllBoxOnMap);
//        this.drawpanel.setWallBoxonMap(mapWall);
        this.drawpanel.setButtons(levelButton);
        this.drawpanel.setP1(p1);
    }


    public void setLevelClock(){
        if (currLevel == 1) {
            levelClock = 1000;
        } else if (currLevel == 2) {
            levelClock = 700;
        } else if (currLevel == 3) {
            levelClock = 500;
        }
    }

    public void boxQueue(){
        rng = new Random();
        if(firstTime) {
            for(int i = 0; i < 3; i++ ) {
                GoBoxIChooseYou();
            }
            firstTime = false;
        }else if(DropboxList.size() < 3){
                GoBoxIChooseYou();

        }

//        for(int i = 0; i < DropboxList.size(); i++){
//            System.out.println(DropboxList.get(i));
//        }


    }


    public BufferedImage stringToBuffer(String path){
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Image not found");
        }
        return img;
    }

    public void grenateBox(int n){
        if (n + 1 == 1) {
            boxType = "Cbox";
            DropboxList.add(boxType);
        } else if (n + 1 == 2) {
            boxType = "Wbox";
            DropboxList.add(boxType);
        } else if (n + 1 == 3) {
            boxType = "Mbox";
            DropboxList.add(boxType);
        } else if (n + 1 == 4) {
            boxType = "Sbox";
            DropboxList.add(boxType);
        }
    }

    public void GoBoxIChooseYou(){
        nextBox = rng.nextInt(4);
        grenateBox(nextBox);

        for(int j= 0;j <DropboxList.size(); j++){
            System.out.println(DropboxList.get(j));
        }
    }


    /*********************************************************************************************************/
    // get/set

    public ArrayList<String> getBoxList(){
        return DropboxList;
    }

    public ArrayList<Boxes> getAllBoxOnMap(){return AllBoxOnMap;}

//    public ArrayList<CardBoardBox> getmapC(){ return mapC;}
//
//    public ArrayList<WoodBox> getmapW(){ return mapW;}
//    public ArrayList<MetalBox> getmapM(){ return mapM;}
//    public ArrayList<StoneBox> getmapS(){ return mapS;}

    public ArrayList<CardBoardBox> getCboxInAir(){return CboxInAir;}
    public ArrayList<WoodBox> getWboxInAir(){return WboxInAir;}
    public ArrayList<MetalBox> getMboxInAir(){return MboxInAir;}
    public ArrayList<StoneBox> getSboxInAir(){return SboxInAir;}

    public static Player getP1(){return p1;}


    public ArrayList<Wall> getMapWall() {return mapWall;}

    public BufferedImage getCBoximg(){
        return stringToBuffer(cardboardbox);
    }

    public BufferedImage getWBoximg(){
        return stringToBuffer(woodbox);
    }

    public BufferedImage getMBoximg(){
        return stringToBuffer(metalbox);
    }

    public BufferedImage getSBoximg(){
        return stringToBuffer(stonebox);
    }

    public BufferedImage getJumpLeft() {return stringToBuffer(jumpleft);}

    public BufferedImage getJumpRight() {return stringToBuffer(jumpright);}

    public BufferedImage getSquished() {return stringToBuffer(squished);}

    public int getCurrLevel(){
        return  currLevel;
    }

    public void setDropping(){
        dropping = false;
    }

    public void setAllBoxOnMap(Boxes box){
        AllBoxOnMap.add(box);
    }





}
