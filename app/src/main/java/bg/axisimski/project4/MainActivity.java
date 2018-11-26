package bg.axisimski.project4;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public int p1guess;
    public int p2guess;
    public static String player1, player2;
    private static int turn=1;
    private boolean win=false;
    private static String guess1, guess2;

    TextView p1tv, p2tv;
    Button start_btn;


    //threads and handlers
    public UIT UIT;
    public ThreadPlayer1 ThreadPlayer1;
    public ThreadPlayer2 ThreadPlayer2;

    public static final int WAIT = 1;               //used by worker threads
    public static final int MAKE_MOVE = 2;          //used by worker threads
    public static final int GAME_OVER = 3;          //used by worker threads

    public static final int DONE_WAITING = 5;       //used by ui thread
    public static final int UPDATE_BOARD = 6;       //used by ui thread
    public static final int NEXT_MOVE = 7;          //used by ui thread



    private class UIT extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;

            if(what==6){
                boolean winner = checkWin();

                //continue playing the game if the game is no worker has won
                if(winner == false){
                    msg = UIT.obtainMessage(NEXT_MOVE);
                    UIT.sendMessage(msg);

                }
                //if either has one, stop both threads and display winner
                else if(winner =true){
                    //gameInProgress = false; #############################################
                    msg = ThreadPlayer1.hp1.obtainMessage(GAME_OVER);
                    ThreadPlayer1.hp1.sendMessage(msg);
                    msg = ThreadPlayer2.hp2.obtainMessage(GAME_OVER);
                    ThreadPlayer2.hp2.sendMessage(msg);
                }



            }

            else if(what==7){
                if(turn  == 0){
                    msg = ThreadPlayer1.hp1.obtainMessage(MAKE_MOVE);
                    ThreadPlayer1.hp1.sendMessage(msg);
                    p2tv.setText("ACTUAL: "+player2+"\n"+"Oppnenent guess: "+guess1);

                }
                else if(turn  == 1){
                    msg = ThreadPlayer2.hp2.obtainMessage(MAKE_MOVE);
                    ThreadPlayer2.hp2.sendMessage(msg);
                    p1tv.setText("ACTUAL: "+player1+"\n"+"Oppnenent guess: "+guess2);

                }
            }

     

        }
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        p1tv=findViewById(R.id.p1tv);
        p2tv=findViewById(R.id.p2tv);
        start_btn=findViewById(R.id.start_btn);

        UIT=new UIT();
        ThreadPlayer1 = new ThreadPlayer1(UIT);
        ThreadPlayer2 = new ThreadPlayer2(UIT);
        ThreadPlayer1.start();
        ThreadPlayer2.start();

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  runGame();
                player1= Integer.toString(generateNum());
                player2= Integer.toString(generateNum());
                runGame();
            }
        });


    }

    public void makeMoveP1(){
        guess1=Integer.toString(generateNum());
        turn=1;

        Toast.makeText(getApplicationContext(), "Turn is "+Integer.toString(turn), Toast.LENGTH_SHORT).show();
//        p2tv.setText("ACTUAL: "+player1+"\n"+"Oppnenent guess: "+guess1);
    }


    public void makeMoveP2(){
        guess2=Integer.toString(generateNum());
        turn=0;
        Toast.makeText(getApplicationContext(), "Turn is "+Integer.toString(turn), Toast.LENGTH_SHORT).show();

       // p1tv.setText("ACTUAL: "+player2+"\n"+"Oppnenent guess: "+guess2);
    }


    public void runGame(){
        
        Message msg = ThreadPlayer1.hp1.obtainMessage(MAKE_MOVE);
        ThreadPlayer1.hp1.sendMessage(msg);

    }


    public boolean checkWin(){

       /* if(a==b){
            win=true;
            return true;
        }

        else
           return false;*/

        if(Integer.toString(p1guess)==player2){
           // Toast.makeText(getApplicationContext(), "Player I wins", Toast.LENGTH_SHORT).show();
            win=true;
            return true;
        }

        else if(Integer.toString(p2guess)==player1){
         //   Toast.makeText(getApplicationContext(), "Player II wins", Toast.LENGTH_SHORT).show();
            win=true;
            return true;
        }


       return false;
    }

    public int generateNum(){


        Random r=new Random();
        int ret=r.nextInt(10000);

        while(hasRepeatedDigit(ret)==true){

              ret=r.nextInt(10000);
        }


        return ret;
    }



      boolean hasRepeatedDigit(int num){
        int i =  num;
        boolean[] flagArr = new boolean[10];
        while(i > 0){
            int r = i%10;
            if(flagArr[r])
                return true;
            else flagArr[r] = true;
            i = i/10;
        }
        return false;
    }


    //##############################################################################################INCLASSES





    private class ThreadPlayer1 extends Thread{
        public Handler hp1;
        private UIT cbh;

        public ThreadPlayer1(UIT h){
            cbh = h;
        }//end constructor

        public void run(){
            Looper.prepare();

            hp1 = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    int what = msg.what ;
                    switch (what) {
                        case WAIT:
                            hp1.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }

                                    //sending message back to UI handler
                                    Message message;
                                    message = cbh.obtainMessage(DONE_WAITING);
                                    cbh.sendMessage(message);
                                }
                            });

                        case MAKE_MOVE:
                            hp1.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }

                                    makeMoveP1();


                                    //sending message back to UI handler
                                    Message message;
                                    message = cbh.obtainMessage(UPDATE_BOARD);
                                    cbh.sendMessage(message);
                                }
                            });
                            break;

                        case GAME_OVER:
                            hp1.post(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("Game over");
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }
                                    //stopThreads();
                                    //resetGame(0);
                                    //gameOver = true;
                                    Toast.makeText(MainActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }//end switch(...)
                }//end handleMessage()
            };//end handler

            Looper.loop();
        }//end run()
    }//end ThreadPlayer1 class



    private class ThreadPlayer2 extends Thread{
        public Handler hp2;
        private UIT cbh;

        public ThreadPlayer2(UIT h){
            cbh = h;
        }//end constructor

        public void run(){
            Looper.prepare();

            hp2 = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    int what = msg.what ;
                    switch (what) {
                        case WAIT:
                            hp2.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }

                                    //sending message back to UI handler
                                    Message message;
                                    message = cbh.obtainMessage(DONE_WAITING);
                                    cbh.sendMessage(message);
                                }
                            });

                        case MAKE_MOVE:
                            hp2.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }

                                    makeMoveP2();

                                    //sending message back to UI handler
                                    Message message;
                                    message = cbh.obtainMessage(UPDATE_BOARD);
                                    cbh.sendMessage(message);
                                }
                            });
                            break;

                        case GAME_OVER:
                            hp2.post(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("Game over");
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }

                                }
                            });
                            break;
                    }//end switch(...)
                }//end handleMessage()
            };//end handler

            Looper.loop();
        }//end run()
    }//end ThreadPlayer2 class




}//endd Main
