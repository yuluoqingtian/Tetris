package com.bill.tetris;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements View.OnClickListener {

    //游戏区域控件
    View view;

    //游戏区域宽高
    int xWidth, xHeight;

    //地图用一个二位数组表示 布尔型
    boolean[][] maps;
    //方块
    Point[] boxs;
    //方块大小
    int boxSize;

    //辅助线画笔
    Paint linePaint;
    //方块画笔
    Paint boxPaint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        initData();
        initView();
        intListener();
    }

    private void intListener() {
        findViewById(R.id.btnLeft).setOnClickListener(this);
        findViewById(R.id.btnTop).setOnClickListener(this);
        findViewById(R.id.btnRight).setOnClickListener(this);
        findViewById(R.id.btnBottom).setOnClickListener(this);
        findViewById(R.id.btnStart).setOnClickListener(this);
        findViewById(R.id.btnPause).setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获得屏幕宽度
        int width = getScreenWidth(this);

        //设置游戏区域宽度 = 屏幕宽度*2/3
        xWidth = width * 2 / 3;

        //设置游戏区域的高度为宽度的2倍
        xHeight = xWidth * 2;
        //初始化地图
        maps = new boolean[10][20];
        //初始化方块
        boxs = new Point[]{new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)};
        //初始化方块大小
        boxSize = xWidth / maps.length;


    }

    /**
     * 初始化视图
     */
    private void initView() {
        //初始化画笔
        linePaint = new Paint();
        linePaint.setColor(0xff666666);
        //打开抗锯齿
        linePaint.setAntiAlias(true);

        boxPaint = new Paint();
        boxPaint.setColor(0xff000000);
        //打开抗锯齿
        boxPaint.setAntiAlias(true);

        /*1.得到父容器*/
        FrameLayout layoutGame = findViewById(R.id.layoutGame);
        /*2.实例化游戏区域*/
        view = new View(this) {
            //重写游戏区域的绘制方法

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                Log.w(TAG, "onDraw: 调用onDraw");

                //绘制方块
                for (Point box : boxs) {
                    canvas.drawRect(
                            box.x * boxSize,
                            box.y * boxSize,
                            box.x * boxSize + boxSize,
                            box.y * boxSize + boxSize, boxPaint);
                }

                //画纵向辅助线
                for (int i = 0; i < maps.length; i++) {
                    canvas.drawLine(i * boxSize, 0, i * boxSize, view.getHeight(), linePaint);
                }

                //画横向辅助线
                for (int j = 0; j < maps[0].length; j++) {
                    canvas.drawLine(0, j * boxSize, view.getWidth(), j * boxSize, linePaint);
                }
            }
        };
        /*3.设置游戏区域大小*/
        view.setLayoutParams(new ViewGroup.LayoutParams(xWidth, xHeight));
        view.setBackgroundColor(0x10000000);
        view.setWillNotDraw(false);
        /*4.将游戏区域添加进父容器*/
        layoutGame.addView(view);
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 移动方法
     */
    public boolean move(int x, int y) {
//        Log.d(TAG, "移动前: "+boxs.toString());
        for (Point box : boxs) {
            box.x += x;
            box.y += y;
        }
//        Log.d(TAG, "移动后: "+boxs[0].x+"  "+boxs[0].y);
        return true;
    }

    /**
     * 捕捉点击事件
     */
    @Override
    public void onClick( View view) {
        switch (view.getId()) {
            case R.id.btnLeft:
                move(-1, 0);
                break;
            case R.id.btnTop:
                move(0, -1);
                break;
            case R.id.btnRight:
                move(1, 0);
                break;
            case R.id.btnBottom:
                move(0, 1);
                break;
            case R.id.btnStart:
                break;
            case R.id.btnPause:
                break;
        }

        //调用重绘view
        this.view.invalidate();
    }


}
