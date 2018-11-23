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

    TextView p1tv, p2tv;
    Button start_btn;


    public HandlerUI handlerUI;
    public Worker1Thread worker1Thread;
    public Worker2Thread worker2Thread;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        p1tv=findViewById(R.id.p1tv);
        p2tv=findViewById(R.id.p2tv);
        start_btn=findViewById(R.id.start_btn);


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
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        while(!win) {
                            runGame();
                        }
                    }
                },1000);
            }
        });


    }

    public void makeMoveP1(){
        String guess1=Integer.toString(generateNum());
        if(checkWin(Integer.parseInt(player1),Integer.parseInt(guess1))){
            Toast.makeText(getApplicationContext(), "Player 1 wins", Toast.LENGTH_SHORT).show();
        }
        turn=0;

      //  Toast.makeText(getApplicationContext(), "Turn is "+Integer.toString(turn), Toast.LENGTH_SHORT).show();
        p2tv.setText("ACTUAL: "+player1+"\n"+"Oppnenent guess: "+guess1);
    }


    public void makeMoveP2(){
        String guess2=Integer.toString(generateNum());
        if(checkWin(Integer.parseInt(player2),Integer.parseInt(guess2))){
            Toast.makeText(getApplicationContext(), "Player 2 wins", Toast.LENGTH_SHORT).show();
        }
        turn=1;
      //  Toast.makeText(getApplicationContext(), "Turn is "+Integer.toString(turn), Toast.LENGTH_SHORT).show();

        p1tv.setText("ACTUAL: "+player2+"\n"+"Oppnenent guess: "+guess2);
    }


    public void runGame(){


            if (turn == 0) {
                makeMoveP2();
                //  worker1Thread.run();
            } else if (turn == 1) {
                makeMoveP1();
            }


    }


    public boolean checkWin(int a, int b){

        if(a==b){
            win=true;
            return true;
        }

        else
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

    private class HandlerUI extends Handler {
        @Override
        public void handleMessage(Message msg) {


            if(turn == 1){
                //  m = worker1Thread.worker1Handler.obtainMessage(MAKE_MOVE);
                //  worker1Thread.worker1Handler.sendMessage(m);
            }
            else if(turn == 0){
                // m = worker2Thread.worker2Handler.obtainMessage(MAKE_MOVE);
                //   worker2Thread.worker2Handler.sendMessage(m);
            }

        }
    }



    private class Worker1Thread extends Thread{
        public Handler worker1Handler;
        private HandlerUI callbackHandler;

        public Worker1Thread(HandlerUI h){
            callbackHandler = h;
        }

        public void run(){
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }
            worker1Handler = new Handler(){
                @Override
                public void handleMessage(Message msg){

                            worker1Handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }

                                          catch (InterruptedException e) {}


                                    if(turn==0){
                                        makeMoveP2();
                                     }
                                    else if (turn==1){
                                        makeMoveP1();
                                    }
                                }


                            });

                }
            };

            Looper.loop();
        }//end run()
    }//end Worker1Thread class




    private class Worker2Thread extends Thread{
        public Handler worker2Handler;
        private HandlerUI callbackHandler;

        public Worker2Thread(HandlerUI h){
            callbackHandler = h;
        }

        public void run(){
            Looper.prepare();

            worker2Handler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    int what = msg.what ;

                            worker2Handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try { Thread.sleep(1000); }
                                    catch (InterruptedException e) {}


                                    if(turn==0){
                                        makeMoveP2();
                                   //     worker1Thread.run();
                                    }
                                    else if (turn==1){
                                        makeMoveP1();
                                    }

                                }
                            });

                }
            };

            Looper.loop();
        }
    }



}//endd Main
