package opt.jmetal.problem.oil.canvas.gante;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ChartFrame {
    // 窗口大小
    private final static int width = 800;
    private final static int height = 480;

    public JFrame frame;
    private CanvasGante canvas;

    public ChartFrame() {
        frame = new JFrame("原油调度甘特图");
        canvas = new CanvasGante();

        // 将窗口定位到屏幕中间
        int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setBounds(screenWidth / 2 - width / 2, screenHeight / 2 - height / 2, width, height);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.add(canvas);

        // 监听到JFrame窗口大小改变事件时，自动重绘canvas
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                canvas.repaint();
            }
        });
    }

    public void updateCanvas(double[][] data) {
        canvas.setData(data);
        canvas.repaint();
    }
}
