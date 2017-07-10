package cx.com.flowlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private FlowLayout flowLayout;
    private Button btn;
    private Button btnRm;
    private Button btnAddLast;
    private Button btnAddFirst;
    private ImageView addTv;

    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.flow_item, null, false);
        flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
        flowLayout.setOnItemClickListener(new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int index) {
                if(v.isSelected()) {
                    v.setBackgroundResource(android.R.color.white);
                    v.setSelected(false);
                }else{
                    v.setBackgroundResource(R.color.colorPrimary);
                    v.setSelected(true);
                }
                Toast.makeText(MainActivity.this, "第"+index+"项被点击了", Toast.LENGTH_SHORT).show();
            }
        });
        
        flowLayout.setDefaultSelect(R.color.colorPrimary,0,2,4,6,8,10);

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"选中了"+flowLayout.getSelectList()+"个item",Toast.LENGTH_SHORT).show();
            }
        });

        final int rmChild = 3;
        btnRm = (Button) findViewById(R.id.btnRm);
        btnRm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flowLayout.removeChildView(rmChild);
            }
        });

        btnAddLast = (Button) findViewById(R.id.addLast);
        addTv = (ImageView)inflate.findViewById(R.id.addTv);
//        btnAddLast.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                flowLayout.addLastChildView(addTv);
//            }
//        });
//        tv = (TextView)inflate.findViewById(R.id.tv);
//        btnAddFirst = (Button) findViewById(R.id.addFirst);
//        btnAddFirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                flowLayout.addLastChildView(tv);
//            }
//        });
    }

}
