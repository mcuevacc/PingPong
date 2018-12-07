package pe.edu.uni.www.pingpong;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PingPongActivity extends AppCompatActivity implements View.OnTouchListener {

    private static boolean normalMode;
    public boolean responde;
    private static float vel=15;
    private static float border;
    float velX,velY;
    private boolean startPlay, playing, humanTurn;

    private int maxPoint=5;
    private int pointHuman,pointPc;

    private RelativeLayout layout;
    private float anchoPantalla;
    private boolean initLayout;

    private ImageView imageBarPc;
    private float anchoImageBarPc,altoImageBarPc;
    private boolean initBarPc=false;

    private ImageView imageBarHuman;
    private float anchoImageBarHuman,altoImageBarHuman;
    private boolean initBarHuman=false;

    private ImageView imageBall;
    private float anchoImageBall,altoImageBall;
    private boolean initBall;

    private TextView textPointPc;
    private TextView textPointHuman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_pong);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            normalMode = bundle.getBoolean("normal");
        }

        initUI();
    }

    public void initUI(){
        if(normalMode){
            Toast.makeText(getApplicationContext(), "Modo Normal", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Modo Experto", Toast.LENGTH_SHORT).show();
        }

        border = 10*(this.getResources().getDisplayMetrics().density);

        layout=(RelativeLayout) findViewById(R.id.layout);
        layout.post(new Runnable() {
            @Override
            public void run() {
                anchoPantalla = layout.getWidth();
                initLayout=true;
                initApp();
            }
        });

        imageBarPc=(ImageView)findViewById(R.id.imageBarPc);
        imageBarPc.post(new Runnable() {
            @Override
            public void run() {
                anchoImageBarPc = imageBarPc.getMeasuredWidth();
                altoImageBarPc = imageBarPc.getMeasuredHeight();
                initBarPc=true;
                initApp();
            }
        });

        imageBarHuman=(ImageView)findViewById(R.id.imageBarHuman);
        imageBarHuman.post(new Runnable() {
            @Override
            public void run() {
                anchoImageBarHuman = imageBarHuman.getMeasuredWidth();
                altoImageBarHuman = imageBarHuman.getMeasuredHeight();
                initBarHuman=true;
                initApp();
            }
        });

        imageBall=(ImageView)findViewById(R.id.imageBall);
        imageBall.post(new Runnable() {
            @Override
            public void run() {
                anchoImageBall = imageBall.getMeasuredWidth();
                altoImageBall = imageBall.getMeasuredHeight();
                initBall=true;
                initApp();
            }
        });

        textPointPc = this.findViewById(R.id.textPointPc);
        textPointHuman = this.findViewById(R.id.textPointHuman);
    }

    private void initApp(){
        if( !initLayout || !initBarPc || !initBarHuman || !initBall )
            return;

        restart();

        layout.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int evento=event.getAction();
        switch (evento) {
            case MotionEvent.ACTION_DOWN:
                if(playing)
                    moveWhilePlaying(event.getX());
                else{
                    if(humanTurn)
                        moveBeforePlaying(event.getX());
                }
                break;
            case MotionEvent.ACTION_UP:
                if(playing)
                    moveWhilePlaying(event.getX());
                else{
                    if(humanTurn){
                        startPlay=true;
                        moveBeforePlaying(event.getX());
                        play();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(playing)
                    moveWhilePlaying(event.getX());
                else{
                    if(humanTurn)
                        moveBeforePlaying(event.getX());
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void play(){
        playing = true;
        if(humanTurn){
            if(imageBarHuman.getX()>(anchoPantalla-anchoImageBarHuman)/2){
                velX=-1*vel;
            }else{
                velX=vel;
            }
            velY=-1*vel;
        }else{
            int random = (int)(Math.random() * 2);
            if(random==0){
                velX=-1*vel;
            }else{
                velX=vel;
            }
            velY=vel;
        }

        final Handler actualize=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                imageBall.setX(imageBall.getX()+velX);
                imageBall.setY(imageBall.getY()+velY);

                float posX = imageBall.getX();
                float posY = imageBall.getY();

                if(posX<= border){
                    if(velX<0)
                        velX=-1*velX;
                }else if( posX>=(anchoPantalla-(border +anchoImageBall)) ){
                    if(velX>0)
                        velX=-1*velX;
                }

                if( (posY+altoImageBall)>=imageBarHuman.getY() && posY<=(imageBarHuman.getY()+altoImageBarHuman) &&
                        (posX+anchoImageBall)>=imageBarHuman.getX() && posX<=(imageBarHuman.getX()+anchoImageBarHuman) ){
                    if(velY>0){
                        velY=-1*velY;
                        changeResponse();
                    }
                }else if( posY<=(imageBarPc.getY()+altoImageBarPc) && (posY+altoImageBall)>=imageBarPc.getY() &&
                        (posX+anchoImageBall)>=imageBarPc.getX() && posX<=(imageBarPc.getX()+anchoImageBarPc) ){
                    if(velY<0){
                        velY=-1*velY;
                    }
                }else if( posY>(imageBarHuman.getY()+altoImageBarHuman) ){
                    playing=false;
                    checkWinner(false);
                }else if( (posY+altoImageBall)<imageBarPc.getY() ){
                    playing=false;
                    checkWinner(true);
                }

                if (playing)
                    actualize.postDelayed(this, 30);
            }
        };
        actualize.postDelayed(runnable,0000);
    }

    public void playPc(){
        final Handler playPc=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                if(startPlay && !playing && !humanTurn ){
                    play();
                }
                if(playing){
                    if(velY<0){
                        float pointBall=imageBall.getX()+anchoImageBall/2;
                        float pointBarPc=imageBarPc.getX()+anchoImageBarPc/2;
                        if(responde || imageBall.getY()>(imageBarPc.getY()+anchoImageBarPc/2+altoImageBarPc)){
                            if(velX>0){
                                if( (pointBarPc+anchoImageBarPc/2)<(anchoPantalla- border) && pointBarPc<pointBall && pointBarPc>pointBall-anchoImageBall ){
                                    imageBarPc.setX(pointBall-anchoImageBarPc/2);
                                }
                            }else{
                                if( (pointBarPc-anchoImageBarPc/2)> border && pointBarPc>pointBall && pointBarPc<(pointBall+anchoImageBall)){
                                    imageBarPc.setX(pointBall-anchoImageBarPc/2);
                                }
                            }
                        }
                    }
                }

                if(!startPlay || playing){
                    playPc.postDelayed(this, 60);
                }
            }
        };
        playPc.postDelayed(runnable,0000);
    }

    public void changeResponse(){
        if(normalMode){
            int random = (int)(Math.random() * 3);
            if(random==0)
                 responde=false;
        }
    }

    public void checkWinner(boolean newPointHuman){
        if(newPointHuman){
            pointHuman++;
            if(pointHuman==maxPoint)
                Toast.makeText(getApplicationContext(), "¡Ganastes!", Toast.LENGTH_SHORT).show();
            else{
                humanTurn=true;
                Toast.makeText(getApplicationContext(), "¡Bien!", Toast.LENGTH_SHORT).show();
            }
            textPointHuman.setText("0"+pointHuman);
        }else{
            pointPc++;
            if(pointPc==maxPoint)
                Toast.makeText(getApplicationContext(), "¡Perdistes!", Toast.LENGTH_SHORT).show();
            else{
                humanTurn=false;
                Toast.makeText(getApplicationContext(), "¡Mal!", Toast.LENGTH_SHORT).show();
            }
            textPointPc.setText("0"+pointPc);
        }

        if( pointPc==maxPoint || pointHuman==maxPoint )
            restart();
        else
            changeTurn();
    }

    public void moveBeforePlaying(float posx){
        moveHumanBall(posx);

        moveHumanBar(posx);
    }

    public void moveWhilePlaying(float posx){
        moveHumanBar(posx);
    }

    public void moveHumanBall(float posx){
        if(posx>= border +anchoImageBall/2 && posx+anchoImageBall/2<=anchoPantalla- border){
            imageBall.setX(posx-anchoImageBall/2);
        }
    }

    public void moveHumanBar(float posx){
        if(posx>= border +anchoImageBarHuman/2 && posx+anchoImageBarHuman/2<=anchoPantalla- border){
            imageBarHuman.setX(posx-anchoImageBarHuman/2);
        }
    }

    public void restart(){
        startPlay=false;
        humanTurn=true;
        playing=false;

        pointPc=0;
        pointHuman=0;
        textPointPc.setText("0"+pointPc);
        textPointHuman.setText("0"+pointHuman);

        changeTurn();
    }

    public void changeTurn(){
        responde=true;

        playPc();

        imageBarPc.setX((anchoPantalla-anchoImageBarPc)/2);
        imageBarHuman.setX((anchoPantalla-anchoImageBarHuman)/2);

        imageBall.setX((anchoPantalla-anchoImageBall)/2);
        if(humanTurn){
            imageBall.setY(imageBarHuman.getY()-altoImageBall-1);
        }else{
            imageBall.setY(imageBarPc.getY()+altoImageBarPc+1);
      }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
