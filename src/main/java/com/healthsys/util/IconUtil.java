package com.healthsys.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 图标工具类。
 * 提供应用程序中图标的加载和管理功能。
 * 
 * @author 梦辰
 * @since 1.0
 */
public class IconUtil {

  private static final Map<String, Icon> iconCache = new HashMap<>();

  // 默认图标大小
  public static final int SMALL_SIZE = 16;
  public static final int MEDIUM_SIZE = 24;
  public static final int LARGE_SIZE = 32;

  /**
   * 加载图标
   * 
   * @param iconName 图标名称
   * @param size     图标大小
   * @return 图标对象，如果加载失败返回默认图标
   */
  public static Icon loadIcon(String iconName, int size) {
    String key = iconName + "_" + size;

    if (iconCache.containsKey(key)) {
      return iconCache.get(key);
    }

    Icon icon = createIcon(iconName, size);
    iconCache.put(key, icon);
    return icon;
  }

  /**
   * 加载小尺寸图标 (16x16)
   */
  public static Icon loadSmallIcon(String iconName) {
    return loadIcon(iconName, SMALL_SIZE);
  }

  /**
   * 加载中等尺寸图标 (24x24)
   */
  public static Icon loadMediumIcon(String iconName) {
    return loadIcon(iconName, MEDIUM_SIZE);
  }

  /**
   * 加载大尺寸图标 (32x32)
   */
  public static Icon loadLargeIcon(String iconName) {
    return loadIcon(iconName, LARGE_SIZE);
  }

  /**
   * 创建图标
   */
  private static Icon createIcon(String iconName, int size) {
    try {
      // 尝试从资源目录加载图标
      URL iconUrl = IconUtil.class.getResource("/images/" + iconName + ".png");
      if (iconUrl != null) {
        ImageIcon originalIcon = new ImageIcon(iconUrl);
        Image img = originalIcon.getImage();
        Image scaledImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
      }
    } catch (Exception e) {
      // 忽略异常，使用默认图标
    }

    // 如果无法加载图标，创建默认图标
    return createDefaultIcon(iconName, size);
  }

  /**
   * 创建默认图标
   */
  private static Icon createDefaultIcon(String iconName, int size) {
    return new Icon() {
      @Override
      public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 根据图标名称绘制不同的默认图标
        switch (iconName.toLowerCase()) {
          case "add":
          case "plus":
            drawPlusIcon(g2d, x, y, size);
            break;
          case "edit":
          case "pencil":
            drawEditIcon(g2d, x, y, size);
            break;
          case "delete":
          case "trash":
            drawDeleteIcon(g2d, x, y, size);
            break;
          case "search":
            drawSearchIcon(g2d, x, y, size);
            break;
          case "refresh":
            drawRefreshIcon(g2d, x, y, size);
            break;
          case "user":
            drawUserIcon(g2d, x, y, size);
            break;
          case "settings":
            drawSettingsIcon(g2d, x, y, size);
            break;
          case "logout":
            drawLogoutIcon(g2d, x, y, size);
            break;
          default:
            drawDefaultIcon(g2d, x, y, size);
            break;
        }

        g2d.dispose();
      }

      @Override
      public int getIconWidth() {
        return size;
      }

      @Override
      public int getIconHeight() {
        return size;
      }
    };
  }

  /**
   * 绘制加号图标
   */
  private static void drawPlusIcon(Graphics2D g2d, int x, int y, int size) {
    g2d.setColor(new Color(34, 139, 34)); // 绿色
    g2d.setStroke(new BasicStroke(2));

    int center = size / 2;
    int margin = size / 4;

    // 画横线
    g2d.drawLine(x + margin, y + center, x + size - margin, y + center);
    // 画竖线
    g2d.drawLine(x + center, y + margin, x + center, y + size - margin);
  }

  /**
   * 绘制编辑图标
   */
  private static void drawEditIcon(Graphics2D g2d, int x, int y, int size) {
    g2d.setColor(new Color(255, 165, 0)); // 橙色
    g2d.setStroke(new BasicStroke(1.5f));

    int[] xPoints = { x + size - 4, x + size - 8, x + 4, x + 2, x + 6 };
    int[] yPoints = { y + 4, y + 2, y + size - 6, y + size - 2, y + size - 4 };

    g2d.drawPolyline(xPoints, yPoints, 5);
  }

  /**
   * 绘制删除图标
   */
  private static void drawDeleteIcon(Graphics2D g2d, int x, int y, int size) {
    g2d.setColor(new Color(220, 20, 60)); // 红色
    g2d.setStroke(new BasicStroke(2));

    int margin = size / 4;

    // 画X
    g2d.drawLine(x + margin, y + margin, x + size - margin, y + size - margin);
    g2d.drawLine(x + size - margin, y + margin, x + margin, y + size - margin);
  }

  /**
   * 绘制搜索图标
   */
  private static void drawSearchIcon(Graphics2D g2d, int x, int y, int size) {
    g2d.setColor(new Color(70, 130, 180)); // 蓝色
    g2d.setStroke(new BasicStroke(2));

    int radius = size / 3;
    int centerX = x + size / 2 - 2;
    int centerY = y + size / 2 - 2;

    // 画圆
    g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    // 画手柄
    g2d.drawLine(centerX + radius - 2, centerY + radius - 2,
        x + size - 3, y + size - 3);
  }

  /**
   * 绘制刷新图标
   */
  private static void drawRefreshIcon(Graphics2D g2d, int x, int y, int size) {
    g2d.setColor(new Color(32, 178, 170)); // 青色
    g2d.setStroke(new BasicStroke(2));

    int radius = size / 3;
    int centerX = x + size / 2;
    int centerY = y + size / 2;

    // 画圆弧
    g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 45, 270);

    // 画箭头
    int arrowX = centerX + radius - 2;
    int arrowY = centerY - radius + 2;
    g2d.drawLine(arrowX, arrowY, arrowX - 4, arrowY - 2);
    g2d.drawLine(arrowX, arrowY, arrowX - 2, arrowY + 4);
  }

  /**
   * 绘制用户图标
   */
  private static void drawUserIcon(Graphics2D g2d, int x, int y, int size) {
    g2d.setColor(new Color(75, 0, 130)); // 紫色
    g2d.setStroke(new BasicStroke(1.5f));

    int centerX = x + size / 2;
    int headY = y + size / 4;
    int headRadius = size / 6;

    // 画头
    g2d.drawOval(centerX - headRadius, headY - headRadius, headRadius * 2, headRadius * 2);

    // 画身体
    g2d.drawArc(x + size / 4, y + size / 2, size / 2, size / 2, 0, 180);
  }

  /**
   * 绘制设置图标
   */
  private static void drawSettingsIcon(Graphics2D g2d, int x, int y, int size) {
    g2d.setColor(new Color(105, 105, 105)); // 灰色
    g2d.setStroke(new BasicStroke(1.5f));

    int centerX = x + size / 2;
    int centerY = y + size / 2;
    int radius = size / 4;

    // 画中心圆
    g2d.drawOval(centerX - radius / 2, centerY - radius / 2, radius, radius);

    // 画齿轮外圈
    for (int i = 0; i < 8; i++) {
      double angle = i * Math.PI / 4;
      int x1 = centerX + (int) (radius * 1.5 * Math.cos(angle));
      int y1 = centerY + (int) (radius * 1.5 * Math.sin(angle));
      int x2 = centerX + (int) (radius * 0.8 * Math.cos(angle));
      int y2 = centerY + (int) (radius * 0.8 * Math.sin(angle));
      g2d.drawLine(x1, y1, x2, y2);
    }
  }

  /**
   * 绘制退出图标
   */
  private static void drawLogoutIcon(Graphics2D g2d, int x, int y, int size) {
    g2d.setColor(new Color(178, 34, 34)); // 暗红色
    g2d.setStroke(new BasicStroke(2));

    // 画门框
    g2d.drawRect(x + 2, y + 2, size - 8, size - 4);

    // 画箭头
    int arrowX = x + size - 6;
    int arrowY = y + size / 2;
    g2d.drawLine(x + size / 2, arrowY, arrowX, arrowY);
    g2d.drawLine(arrowX - 3, arrowY - 2, arrowX, arrowY);
    g2d.drawLine(arrowX - 3, arrowY + 2, arrowX, arrowY);
  }

  /**
   * 绘制默认图标
   */
  private static void drawDefaultIcon(Graphics2D g2d, int x, int y, int size) {
    g2d.setColor(new Color(128, 128, 128)); // 灰色
    g2d.fillRect(x + 2, y + 2, size - 4, size - 4);
    g2d.setColor(Color.WHITE);
    g2d.drawString("?", x + size / 2 - 3, y + size / 2 + 3);
  }

  /**
   * 清空图标缓存
   */
  public static void clearCache() {
    iconCache.clear();
  }

  /**
   * 获取缓存大小
   */
  public static int getCacheSize() {
    return iconCache.size();
  }
}