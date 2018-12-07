package pe.edu.uni.www.pingpong;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button buttonNormal;
    private Button buttonExperto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("by Miguel Cueva");

        initUI();
    }

    public void initUI(){
        buttonNormal = this.findViewById(R.id.buttonNormal);
        buttonExperto = this.findViewById(R.id.buttonExperto);

        buttonNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PingPongActivity.class);
                intent.putExtra("normal", true);
                startActivity(intent);
            }
        });

        buttonExperto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PingPongActivity.class);
                intent.putExtra("normal", false);
                startActivity(intent);
            }
        });
    }
}
