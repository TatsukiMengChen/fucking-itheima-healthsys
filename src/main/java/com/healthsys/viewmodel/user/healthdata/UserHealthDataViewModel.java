package com.healthsys.viewmodel.user.healthdata;

import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.service.IExaminationResultService;
import com.healthsys.service.impl.ExaminationResultServiceImpl;
import com.healthsys.viewmodel.base.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 用户健康数据管理ViewModel
 * 管理用户自主录入的健康数据
 * 
 * @author AI Assistant
 */
public class UserHealthDataViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(UserHealthDataViewModel.class);

  private final IExaminationResultService examinationResultService;

  // 健康数据列表
  private List<ExaminationResult> healthDataList;

  // 搜索和筛选条件
  private String searchKeyword;
  private String selectedDateRange;

  // UI状态
  private boolean isLoading;
  private String statusMessage;
  private ExaminationResult selectedHealthData;

  public UserHealthDataViewModel() {
    this.examinationResultService = new ExaminationResultServiceImpl();
    this.healthDataList = new ArrayList<>();
    this.searchKeyword = "";
    this.selectedDateRange = "全部";

    // 初始化数据
    initialize();
  }

  /**
   * 初始化
   */
  public void initialize() {
    loadHealthDataCommand();
  }

  /**
   * 加载健康数据命令
   */
  public CompletableFuture<Void> loadHealthDataCommand() {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载健康数据...");

        // 获取当前用户的健康数据
        List<ExaminationResult> results = examinationResultService.getExaminationResultsByUserId(getCurrentUserId());

        if (results != null) {
          setHealthDataList(results);
          setStatusMessage("健康数据加载完成，共 " + results.size() + " 条记录");
        } else {
          setHealthDataList(new ArrayList<>());
          setStatusMessage("暂无健康数据");
        }

      } catch (Exception e) {
        logger.error("加载健康数据时发生错误", e);
        setStatusMessage("加载健康数据失败：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 搜索健康数据命令
   */
  public CompletableFuture<Void> searchHealthDataCommand(String keyword) {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setSearchKeyword(keyword);
        setStatusMessage("正在搜索...");

        // 这里可以实现搜索逻辑
        List<ExaminationResult> allResults = examinationResultService.getExaminationResultsByUserId(getCurrentUserId());
        List<ExaminationResult> filteredResults = new ArrayList<>();

        if (allResults != null) {
          for (ExaminationResult result : allResults) {
            if (keyword.isEmpty() ||
                (result.getResultNotes() != null && result.getResultNotes().contains(keyword)) ||
                (result.getMeasuredValue() != null && result.getMeasuredValue().contains(keyword))) {
              filteredResults.add(result);
            }
          }
        }

        setHealthDataList(filteredResults);
        setStatusMessage("搜索完成，找到 " + filteredResults.size() + " 条记录");

      } catch (Exception e) {
        logger.error("搜索健康数据时发生错误", e);
        setStatusMessage("搜索失败：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 删除健康数据命令
   */
  public CompletableFuture<Void> deleteHealthDataCommand(Integer resultId) {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在删除健康数据...");

        boolean success = examinationResultService.deleteExaminationResult(resultId);

        if (success) {
          setStatusMessage("健康数据删除成功");
          // 重新加载数据
          loadHealthDataCommand();
        } else {
          setStatusMessage("健康数据删除失败");
        }

      } catch (Exception e) {
        logger.error("删除健康数据时发生错误", e);
        setStatusMessage("删除失败：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    loadHealthDataCommand();
  }

  /**
   * 清空搜索条件
   */
  public void clearSearchConditions() {
    setSearchKeyword("");
    setSelectedDateRange("全部");
    loadHealthDataCommand();
  }

  /**
   * 获取当前登录用户ID
   * TODO: 从全局状态获取
   */
  private Integer getCurrentUserId() {
    return 1;
  }

  // Getters and Setters
  public List<ExaminationResult> getHealthDataList() {
    return healthDataList;
  }

  public void setHealthDataList(List<ExaminationResult> healthDataList) {
    List<ExaminationResult> oldValue = this.healthDataList;
    this.healthDataList = healthDataList;
    firePropertyChange("healthDataList", oldValue, healthDataList);
  }

  public String getSearchKeyword() {
    return searchKeyword;
  }

  public void setSearchKeyword(String searchKeyword) {
    String oldValue = this.searchKeyword;
    this.searchKeyword = searchKeyword;
    firePropertyChange("searchKeyword", oldValue, searchKeyword);
  }

  public String getSelectedDateRange() {
    return selectedDateRange;
  }

  public void setSelectedDateRange(String selectedDateRange) {
    String oldValue = this.selectedDateRange;
    this.selectedDateRange = selectedDateRange;
    firePropertyChange("selectedDateRange", oldValue, selectedDateRange);
  }

  public boolean isLoading() {
    return isLoading;
  }

  public void setLoading(boolean loading) {
    boolean oldValue = this.isLoading;
    this.isLoading = loading;
    firePropertyChange("loading", oldValue, loading);
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    String oldValue = this.statusMessage;
    this.statusMessage = statusMessage;
    firePropertyChange("statusMessage", oldValue, statusMessage);
  }

  public ExaminationResult getSelectedHealthData() {
    return selectedHealthData;
  }

  public void setSelectedHealthData(ExaminationResult selectedHealthData) {
    ExaminationResult oldValue = this.selectedHealthData;
    this.selectedHealthData = selectedHealthData;
    firePropertyChange("selectedHealthData", oldValue, selectedHealthData);
  }
}