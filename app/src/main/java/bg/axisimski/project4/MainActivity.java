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
    public HandlerUI handlerUI;
    public Worker1Thread worker1Thread;
    public Worker2Thread worker2Thread;

    public static final int WAIT = 1;               //used by worker threads
    public static final int MAKE_MOVE = 2;          //used by worker threads
    public static final int GAME_OVER = 3;          //used by worker threads

    public static final int DONE_WAITING = 5;       //used by ui thread
    public static final int UPDATE_BOARD = 6;       //used by ui thread
    public static final int NEXT_MOVE = 7;          //used by ui thread



    private class HandlerUI extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            Message m;

            switch (what) {
                //updates the game board, and checks if the game needs to be continued (if there is a winner or not)
                case UPDATE_BOARD:
                    boolean winner = checkWin();

                    //continue playing the game if the game is no worker has won
                    if(winner == false){
                        m = handlerUI.obtainMessage(NEXT_MOVE);
                        handlerUI.sendMessage(m);

                    }
                    //if either has one, stop both threads and display winner
                    else if(winner =true){
                        //gameInProgress = false; #############################################
                        m = worker1Thread.worker1Handler.obtainMessage(GAME_OVER);
                        worker1Thread.worker1Handler.sendMessage(m);
                        m = worker2Thread.worker2Handler.obtainMessage(GAME_OVER);
                        worker2Thread.worker2Handler.sendMessage(m);
                    }

                    break;


                case NEXT_MOVE:

                    if(turn  == 0){
                        m = worker1Thread.worker1Handler.obtainMessage(MAKE_MOVE);
                        worker1Thread.worker1Handler.sendMessage(m);
                         p2tv.setText("ACTUAL: "+player2+"\n"+"Oppnenent guess: "+guess1);

                    }
                    else if(turn  == 1){
                        m = worker2Thread.worker2Handler.obtainMessage(MAKE_MOVE);
                        worker2Thread.worker2Handler.sendMessage(m);
                        p1tv.setText("ACTUAL: "+player1+"\n"+"Oppnenent guess: "+guess2);

                    }
                    break;
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

        handlerUI=new HandlerUI();
        worker1Thread = new Worker1Thread(handlerUI);
        worker2Thread = new Worker2Thread(handlerUI);
        worker1Thread.start();
        worker2Thread.start();

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


        Message m = worker1Thread.worker1Handler.obtainMessage(MAKE_MOVE);
        //send message to worker1 thread, and have worker1 handler take care of message
        worker1Thread.worker1Handler.sendMessage(m);


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





    private class Worker1Thread extends Thread{
        public Handler worker1Handler;
        private HandlerUI callbackHandler;

        public Worker1Thread(HandlerUI h){
            callbackHandler = h;
        }//end constructor

        public void run(){
            Looper.prepare();

            worker1Handler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    int what = msg.what ;
                    switch (what) {
                        case WAIT:
                            worker1Handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }

                                    //sending message back to UI handler
                                    Message message;
                                    message = callbackHandler.obtainMessage(DONE_WAITING);
                                    callbackHandler.sendMessage(message);
                                }
                            });

                        case MAKE_MOVE:
                            worker1Handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }

                                    makeMoveP1();


                                    //sending message back to UI handler
                                    Message message;
                                    message = callbackHandler.obtainMessage(UPDATE_BOARD);
                                    callbackHandler.sendMessage(message);
                                }
                            });
                            break;

                        case GAME_OVER:
                            worker1Handler.post(new Runnable() {
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
    }//end Worker1Thread class



    private class Worker2Thread extends Thread{
        public Handler worker2Handler;
        private HandlerUI callbackHandler;

        public Worker2Thread(HandlerUI h){
            callbackHandler = h;
        }//end constructor

        public void run(){
            Looper.prepare();

            worker2Handler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    int what = msg.what ;
                    switch (what) {
                        case WAIT:
                            worker2Handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }

                                    //sending message back to UI handler
                                    Message message;
                                    message = callbackHandler.obtainMessage(DONE_WAITING);
                                    callbackHandler.sendMessage(message);
                                }
                            });

                        case MAKE_MOVE:
                            worker2Handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) { System.out.println("Thread interrupted!"); }

                                    makeMoveP2();

                                    //sending message back to UI handler
                                    Message message;
                                    message = callbackHandler.obtainMessage(UPDATE_BOARD);
                                    callbackHandler.sendMessage(message);
                                }
                            });
                            break;

                        case GAME_OVER:
                            worker2Handler.post(new Runnable() {
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
    }//end Worker2Thread class




}//endd Main
