package bg.axisimski.project4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public int p1guess;
    public int p2guess;
    public static String player1, player2;

    TextView p1tv, p2tv;
    Button start_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        p1tv=findViewById(R.id.p1tv);
        p2tv=findViewById(R.id.p2tv);
        start_btn=findViewById(R.id.start_btn);

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runGame();
            }
        });


    }



    public void runGame(){

        player1= "ACTUAL: "+Integer.toString(generateNum());
        player2=Integer.toString(generateNum());
        String guess1="Opponent guess: "+Integer.toString(generateNum());
        String guess2="Opponent guess: "+Integer.toString(generateNum());

        p1tv.setText(player1+"\n"+guess2);
        p2tv.setText(player2+"\n"+guess1);


    }


    public boolean checkWin(int a, int b){

        if(a==b){
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


}
