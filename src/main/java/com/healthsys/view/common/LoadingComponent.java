package com.healthsys.view.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 加载状态组件
 * 提供美观的加载动画和提示信息
 * 
 * @author HealthSys Team
 * @since 1.0
 */
public class LoadingComponent extends JPanel {

  private Timer animationTimer;
  private int animationAngle = 0;
  private String loadingText = "加载中...";
  private boolean isVisible = false;

  /**
   * 构造函数
   */
  public LoadingComponent() {
    initComponent();
    setupAnimation();
  }

  /**
   * 构造函数（带自定义文本）
   */
  public LoadingComponent(String text) {
    this.loadingText = text;
    initComponent();
    setupAnimation();
  }

  /**
   * 初始化组件
   */
  private void initComponent() {
    setOpaque(true);
    setBackground(new Color(0, 0, 0, 100)); // 半透明背景
    setVisible(false);
  }

  /**
   * 设置动画
   */
  private void setupAnimation() {
    animationTimer = new Timer(50, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        animationAngle += 10;
        if (animationAngle >= 360) {
          animationAngle = 0;
        }
        repaint();
      }
    });
  }

  /**
   * 显示加载状态
   */
  public void showLoading() {
    showLoading("加载中...");
  }

  /**
   * 显示加载状态（带自定义文本）
   */
  public void showLoading(String text) {
    this.loadingText = text;
    isVisible = true;
    setVisible(true);
    animationTimer.start();

    // 确保组件在最顶层
    if (getParent() != null) {
      getParent().setComponentZOrder(this, 0);
    }
  }

  /**
   * 隐藏加载状态
   */
  public void hideLoading() {
    isVisible = false;
    animationTimer.stop();
    setVisible(false);
  }

  /**
   * 检查是否正在显示加载状态
   */
  public boolean isLoading() {
    return isVisible;
  }

  /**
   * 设置加载文本
   */
  public void setLoadingText(String text) {
    this.loadingText = text;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (!isVisible) {
      return;
    }

    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // 绘制半透明背景
    g2d.setColor(new Color(0, 0, 0, 80));
    g2d.fillRect(0, 0, getWidth(), getHeight());

    // 计算中心位置
    int centerX = getWidth() / 2;
    int centerY = getHeight() / 2;

    // 绘制加载圆环
    drawLoadingSpinner(g2d, centerX, centerY - 20);

    // 绘制加载文本
    drawLoadingText(g2d, centerX, centerY + 30);

    g2d.dispose();
  }

  /**
   * 绘制加载旋转器
   */
  private void drawLoadingSpinner(Graphics2D g2d, int centerX, int centerY) {
    int radius = 15;
    int strokeWidth = 3;

    g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

    // 绘制外圆（背景）
    g2d.setColor(new Color(200, 200, 200, 100));
    g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

    // 绘制旋转的弧（前景）
    g2d.setColor(new Color(70, 130, 180)); // 蓝色
    g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
        animationAngle, 90);

    // 绘制旋转的点
    double angle = Math.toRadians(animationAngle + 45);
    int dotX = centerX + (int) (radius * Math.cos(angle));
    int dotY = centerY + (int) (radius * Math.sin(angle));

    g2d.setColor(new Color(70, 130, 180));
    g2d.fillOval(dotX - 2, dotY - 2, 4, 4);
  }

  /**
   * 绘制加载文本
   */
  private void drawLoadingText(Graphics2D g2d, int centerX, int centerY) {
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(loadingText);
    int textHeight = fm.getHeight();

    // 绘制文本阴影
    g2d.setColor(new Color(0, 0, 0, 100));
    g2d.drawString(loadingText, centerX - textWidth / 2 + 1, centerY + textHeight / 2 + 1);

    // 绘制文本
    g2d.setColor(Color.WHITE);
    g2d.drawString(loadingText, centerX - textWidth / 2, centerY + textHeight / 2);
  }

  /**
   * 创建覆盖层加载组件
   * 可以覆盖在任何父组件上显示加载状态
   */
  public static LoadingComponent createOverlay(JComponent parent) {
    LoadingComponent loadingComponent = new LoadingComponent();

    if (parent.getLayout() instanceof BorderLayout ||
        parent.getLayout() instanceof OverlayLayout ||
        parent.getLayout() == null) {
      // 直接添加到父组件
      parent.add(loadingComponent, 0);
    } else {
      // 创建一个覆盖层
      JPanel overlay = new JPanel();
      overlay.setLayout(new OverlayLayout(overlay));
      overlay.setOpaque(false);
      overlay.add(loadingComponent);

      parent.removeAll();
      parent.setLayout(new BorderLayout());
      parent.add(overlay, BorderLayout.CENTER);
    }

    return loadingComponent;
  }

  /**
   * 显示模态加载对话框
   */
  public static JDialog showModalLoading(Component parent, String message) {
    JDialog dialog = new JDialog();
    dialog.setUndecorated(true);
    dialog.setModal(true);
    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

    LoadingComponent loadingComponent = new LoadingComponent(message);
    loadingComponent.setPreferredSize(new Dimension(200, 100));
    loadingComponent.showLoading(message);

    dialog.add(loadingComponent);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);

    // 在后台线程中显示对话框
    SwingUtilities.invokeLater(() -> dialog.setVisible(true));

    return dialog;
  }

  /**
   * 清理资源
   */
  public void dispose() {
    if (animationTimer != null && animationTimer.isRunning()) {
      animationTimer.stop();
    }
  }
}