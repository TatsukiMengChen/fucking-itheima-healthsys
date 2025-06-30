package com.healthsys.view.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * 通用分页组件
 * 用于表格的分页功能
 * 
 * @author HealthSys Team
 */
public class PagingComponent extends JPanel {

  /**
   * 分页监听器接口
   */
  public interface PagingListener {
    /**
     * 页码改变时调用
     * 
     * @param currentPage 当前页码（从1开始）
     * @param pageSize    每页大小
     */
    void onPageChanged(int currentPage, int pageSize);
  }

  private int currentPage = 1; // 当前页码（从1开始）
  private int totalPages = 1; // 总页数
  private int totalRecords = 0; // 总记录数
  private int pageSize = 10; // 每页大小

  private JLabel infoLabel; // 信息标签
  private JButton firstButton; // 首页按钮
  private JButton prevButton; // 上一页按钮
  private JButton nextButton; // 下一页按钮
  private JButton lastButton; // 末页按钮
  private JTextField pageField; // 页码输入框
  private JButton goButton; // 跳转按钮
  private JComboBox<Integer> sizeComboBox; // 每页大小选择框

  private PagingListener pagingListener;

  /**
   * 构造函数
   */
  public PagingComponent() {
    initializeComponents();
    setupLayout();
    setupEventListeners();
    updatePagingUI();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    infoLabel = new JLabel();
    infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

    firstButton = new JButton("首页");
    prevButton = new JButton("上一页");
    nextButton = new JButton("下一页");
    lastButton = new JButton("末页");

    // 设置按钮样式
    JButton[] buttons = { firstButton, prevButton, nextButton, lastButton };
    for (JButton button : buttons) {
      button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
      button.setPreferredSize(new Dimension(60, 25));
    }

    pageField = new JTextField(3);
    pageField.setHorizontalAlignment(JTextField.CENTER);
    pageField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

    goButton = new JButton("跳转");
    goButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    goButton.setPreferredSize(new Dimension(50, 25));

    sizeComboBox = new JComboBox<>(new Integer[] { 10, 20, 50, 100 });
    sizeComboBox.setSelectedItem(pageSize);
    sizeComboBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

    setBorder(new EmptyBorder(8, 0, 8, 0));
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));

    add(infoLabel);
    add(new JSeparator(JSeparator.VERTICAL));
    add(firstButton);
    add(prevButton);
    add(nextButton);
    add(lastButton);
    add(new JSeparator(JSeparator.VERTICAL));
    add(new JLabel("跳转到:"));
    add(pageField);
    add(new JLabel("页"));
    add(goButton);
    add(new JSeparator(JSeparator.VERTICAL));
    add(new JLabel("每页:"));
    add(sizeComboBox);
    add(new JLabel("条"));
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    firstButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        goToPage(1);
      }
    });

    prevButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (currentPage > 1) {
          goToPage(currentPage - 1);
        }
      }
    });

    nextButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (currentPage < totalPages) {
          goToPage(currentPage + 1);
        }
      }
    });

    lastButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        goToPage(totalPages);
      }
    });

    goButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jumpToPage();
      }
    });

    // 在页码输入框中按回车也可以跳转
    pageField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jumpToPage();
      }
    });

    sizeComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          Integer newSize = (Integer) e.getItem();
          if (newSize != null && newSize != pageSize) {
            changePageSize(newSize);
          }
        }
      }
    });
  }

  /**
   * 跳转到指定页码
   */
  private void jumpToPage() {
    try {
      String pageText = pageField.getText().trim();
      if (pageText.isEmpty()) {
        return;
      }

      int targetPage = Integer.parseInt(pageText);
      if (targetPage >= 1 && targetPage <= totalPages) {
        goToPage(targetPage);
      } else {
        JOptionPane.showMessageDialog(this,
            "页码必须在 1 到 " + totalPages + " 之间",
            "无效页码",
            JOptionPane.WARNING_MESSAGE);
        pageField.setText(String.valueOf(currentPage));
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this,
          "请输入有效的页码数字",
          "无效输入",
          JOptionPane.WARNING_MESSAGE);
      pageField.setText(String.valueOf(currentPage));
    }
  }

  /**
   * 跳转到指定页码
   * 
   * @param page 目标页码
   */
  private void goToPage(int page) {
    if (page >= 1 && page <= totalPages && page != currentPage) {
      currentPage = page;
      updatePagingUI();
      if (pagingListener != null) {
        pagingListener.onPageChanged(currentPage, pageSize);
      }
    }
  }

  /**
   * 改变每页大小
   * 
   * @param newSize 新的每页大小
   */
  private void changePageSize(int newSize) {
    pageSize = newSize;
    // 重新计算当前页，保持尽可能接近原来的位置
    int currentFirstRecord = (currentPage - 1) * pageSize + 1;
    currentPage = (currentFirstRecord - 1) / newSize + 1;
    totalPages = (totalRecords + newSize - 1) / newSize;
    if (totalPages == 0)
      totalPages = 1;
    if (currentPage > totalPages)
      currentPage = totalPages;

    updatePagingUI();
    if (pagingListener != null) {
      pagingListener.onPageChanged(currentPage, pageSize);
    }
  }

  /**
   * 更新UI状态
   */
  private void updatePagingUI() {
    // 更新信息标签
    int startRecord = totalRecords > 0 ? (currentPage - 1) * pageSize + 1 : 0;
    int endRecord = Math.min(currentPage * pageSize, totalRecords);
    infoLabel.setText(String.format("显示 %d-%d 条，共 %d 条记录",
        startRecord, endRecord, totalRecords));

    // 更新页码输入框
    pageField.setText(String.valueOf(currentPage));

    // 更新按钮状态
    firstButton.setEnabled(currentPage > 1);
    prevButton.setEnabled(currentPage > 1);
    nextButton.setEnabled(currentPage < totalPages);
    lastButton.setEnabled(currentPage < totalPages);

    // 更新每页大小选择框
    sizeComboBox.setSelectedItem(pageSize);
  }

  /**
   * 设置分页数据
   * 
   * @param totalRecords 总记录数
   * @param currentPage  当前页码
   * @param pageSize     每页大小
   */
  public void setPagingData(int totalRecords, int currentPage, int pageSize) {
    this.totalRecords = Math.max(0, totalRecords);
    this.pageSize = Math.max(1, pageSize);
    this.totalPages = this.totalRecords > 0 ? (this.totalRecords + this.pageSize - 1) / this.pageSize : 1;
    this.currentPage = Math.max(1, Math.min(currentPage, this.totalPages));

    updatePagingUI();
  }

  /**
   * 设置分页监听器
   * 
   * @param listener 分页监听器
   */
  public void setPagingListener(PagingListener listener) {
    this.pagingListener = listener;
  }

  /**
   * 获取当前页码
   * 
   * @return 当前页码
   */
  public int getCurrentPage() {
    return currentPage;
  }

  /**
   * 获取每页大小
   * 
   * @return 每页大小
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * 获取总页数
   * 
   * @return 总页数
   */
  public int getTotalPages() {
    return totalPages;
  }

  /**
   * 获取总记录数
   * 
   * @return 总记录数
   */
  public int getTotalRecords() {
    return totalRecords;
  }
}