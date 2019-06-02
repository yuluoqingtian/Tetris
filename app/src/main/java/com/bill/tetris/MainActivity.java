package com.bill.tetris;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements View.OnClickListener {

    //游戏区域控件
    View view;

    //游戏区域宽高
    int xWidth, xHeight;

    //地图用一个二位数组表示 布尔型
    boolean[][] maps;
    //方块
    Point[] boxs = new Point[]{};
    //方块大小
    int boxSize;

    //辅助线画笔
    Paint linePaint;
    //方块画笔
    Paint boxPaint;
    //地图画笔
    Paint mapPaint;


    //方块装类数
    final int TYPE = 7;

    //方块的类型
    int boxType;

    //游戏计时器
    Timer gameTimer;

    //游戏暂停状态
    boolean isPause = false;

    //游戏结束状态
    boolean isOver;


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
        //初始化方块大小
        boxSize = xWidth / maps.length;


    }

    /**
     * 初始化视图
     */
    private void initView() {

        mapPaint = new Paint();
        mapPaint.setColor(0x50000000);
        //打开抗锯齿
        mapPaint.setAntiAlias(true);

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

//                Log.w(TAG, "onDraw: 调用onDraw");

                //绘制地图
                for (int i = 0; i < maps.length; i++) {
                    for (int j = 0; j < maps[0].length; j++) {
                        if (maps[i][j]) {
                            canvas.drawRect(i * boxSize, j * boxSize, (i + 1) * boxSize, (j + 1) * boxSize, mapPaint);
                        }
                    }
                }


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
     * 新的方块
     */
    public void newBoxs() {

//        Log.w("TAG", "调用了newBox");

        Random random = new Random();
        boxType = random.nextInt(TYPE);

        switch (boxType) {
            //田
            case 0:
                boxs = new Point[]{new Point(4, 0), new Point(4, 1), new Point(5, 0), new Point(5, 1)};
                break;
            //L
            case 1:
                boxs = new Point[]{new Point(4, 1), new Point(5, 0), new Point(3, 1), new Point(5, 1)};
                break;
            //反L
            case 2:
                boxs = new Point[]{new Point(4, 1), new Point(3, 0), new Point(3, 1), new Point(5, 1)};
                break;
            case 3 :
                boxs = new Point[]{new Point(4, 1), new Point(4, 0), new Point(5, 1), new Point(5, 2)};
                break;
            case 4:
                boxs = new Point[]{new Point(5, 1), new Point(5, 0), new Point(4, 1), new Point(4, 2)};
                break;
            case 5:
                boxs = new Point[]{new Point(4, 0), new Point(4, 1), new Point(3, 0), new Point(5, 0)};
                break;
            case 6:
                boxs = new Point[]{new Point(4, 0), new Point(3, 0), new Point(5, 0), new Point(6, 0)};
                break;
        }

    }

    /**
     * 移动方法
     */
    public boolean move(int x, int y) {

        //把方块预移动后的坐标传入边界判断
        for (Point box : boxs) {
            if (checkBoundary(box.x + x, box.y + y)) {
                return false;
            }
        }

        for (Point box : boxs) {
            box.x += x;
            box.y += y;
        }
        return true;
    }

    /**
     * 旋转
     */
    public boolean rotate() {
        //田字型不能旋转
        if (boxType == 0) {
            return false;
        }

        //将预旋转后的方块坐标传入边界判断
        for (Point box : boxs) {
            if (checkBoundary(-box.y + boxs[0].x + boxs[0].y, box.x - boxs[0].x + boxs[0].y)) {
                return false;
            }
        }

        for (Point box : boxs) {
            int checkX = -box.y + boxs[0].x + boxs[0].y;
            int checkY = box.x - boxs[0].x + boxs[0].y;
            box.x = checkX;
            box.y = checkY;
        }
        return true;
    }


    /***
     * 边界判断
     * @return true说明出界
     */
    public boolean checkBoundary(int x, int y) {
        return (x < 0 || y < 0 || x >= maps.length || y >= maps[0].length || maps[x][y]);
    }

    /**
     * 捕捉点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                if (isPause || isOver) {
                    return;
                }
                move(-1, 0);
                break;
            case R.id.btnTop:
                if (isPause || isOver) {
                    return;
                }
                rotate();
                break;
            case R.id.btnRight:
                if (isPause || isOver) {
                    return;
                }
                move(1, 0);
                break;
            case R.id.btnBottom:
                if (isPause || isOver) {
                    return;
                }
                //按下下键直接落到底
                while (true) {
                    if (!moveDown()) {
                        break;
                    }
                }
                break;
            case R.id.btnStart:
                startGame();
                break;
            case R.id.btnPause:
                setPause();
                break;
        }

        //调用重绘view
        this.view.invalidate();
    }

    /**
     * 下落
     */
    public boolean moveDown() {
        //1.下落
        if (move(0, 1)) {
//            Log.w("TAG", "进入IF");
            return true;
        } else {
//            Log.w("TAG", "进入else");
            //移动失败，堆积处理
            addBoxs();
            //消除处理
            remove();
            //生成新的方块
            newBoxs();
            isOver = checkOver();
            return false;
        }

    }

    /**
     * 堆积
     */
    public void addBoxs() {
//        Log.w("TAG", "调用了addBox");
        for (int i = 0; i < boxs.length; i++) {
            maps[boxs[i].x][boxs[i].y] = true;
        }
    }

    /**
     * 开始游戏
     */
    public void startGame() {
        newBoxs();
        //开始游戏
        if (gameTimer!=null){
            gameTimer.cancel();
        }
        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isOver) {
                    this.cancel();
                }
                if (isPause) {
                    return;
                }
                moveDown();
                view.invalidate();
            }
        }, 500, 500);

        for (int i=0;i<maps.length;i++){
            for (int j=0;j<maps[0].length;j++){
                maps[i][j] = false;
            }
        }
    }

    /**
     * 设置暂停
     */
    public void setPause() {
        isPause = !isPause;
        Button btnPause = findViewById(R.id.btnPause);
        if (isPause) {

            btnPause.setText("continue");
        } else {
            btnPause.setText("pause");
        }
    }

    /**
     * 游戏结束判断
     */
    public boolean checkOver() {
        for (Point box : boxs) {
            if (maps[box.x][box.y]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 消除处理
     */
    public void remove() {

        //记录要消去的行
        ArrayList<Integer> removeArr = new ArrayList<>();
        boolean isRemove;

        //1.某一行填满就让他消去
        for (int i = 0; i < maps[0].length; i++) {
            isRemove = true;
            for (int j = 0; j < maps.length && isRemove; j++) {
                if (!maps[j][i]) {
                    isRemove = false;
                }
            }
            if (isRemove) {
                removeArr.add(i);
            }
        }

//        System.out.println(removeArr);
        //得到该消去的行数removeArr
        //将该行上面的所有行向下平移一行

        for (Integer removeLine : removeArr) {
            //1.如果是最顶上一行则直接消去
            if (removeLine == 0) {
                for (int i =0;i<maps.length;i++){
                    maps[i][0] = false;
                }
                this.view.invalidate();
            }else {
                //2.不是最顶上一行，将移除行上面的行向下平移一行后，再将顶上一行删除
                for (int i=removeLine;i>0;i--){
                    for (int j=0;j<maps.length;j++){
                        maps[j][i] = maps[j][i-1];
                    }
                }
                for (int i =0;i<maps.length;i++){
                    maps[i][0] = false;
                }
                this.view.invalidate();
            }
        }

    }


}
