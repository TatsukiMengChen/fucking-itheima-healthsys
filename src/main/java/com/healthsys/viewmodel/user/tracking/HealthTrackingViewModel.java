package com.healthsys.viewmodel.user.tracking;

import com.healthsys.model.entity.MedicalHistory;
import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.service.IMedicalHistoryService;
import com.healthsys.service.IExaminationResultService;
import com.healthsys.service.impl.MedicalHistoryServiceImpl;
import com.healthsys.service.impl.ExaminationResultServiceImpl;
import com.healthsys.viewmodel.base.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 用户健康跟踪视图模型。
 * 负责健康跟踪数据的加载与交互。
 * 
 * @author 梦辰
 */
public class HealthTrackingViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(HealthTrackingViewModel.class);

  private final IMedicalHistoryService medicalHistoryService;
  private final IExaminationResultService examinationResultService;

  // 病史列表
  private List<MedicalHistory> medicalHistoryList;

  // 体检结果历史（用于图表对比）
  private List<ExaminationResult> examinationResults;

  // 图表数据
  private Map<String, List<Object[]>> chartData; // 检查项名称 -> [(日期, 数值), ...]

  // 筛选条件
  private LocalDate startDate;
  private LocalDate endDate;
  private String selectedCheckItem;
  private String diagnosisKeyword;

  // UI状态
  private boolean isLoading;
  private String statusMessage;

  // 图表配置
  private String selectedChartType; // 折线图、柱状图等
  private final String[] chartTypes = { "折线图", "柱状图", "饼图" };

  public HealthTrackingViewModel() {
    this.medicalHistoryService = new MedicalHistoryServiceImpl();
    this.examinationResultService = new ExaminationResultServiceImpl();
    this.medicalHistoryList = new ArrayList<>();
    this.examinationResults = new ArrayList<>();
    this.chartData = new HashMap<>();
    this.selectedChartType = chartTypes[0]; // 默认折线图

    // 初始化日期范围为最近一年
    this.endDate = LocalDate.now();
    this.startDate = this.endDate.minusYears(1);
  }

  /**
   * 加载病史列表命令
   */
  public CompletableFuture<Void> loadMedicalHistoryCommand() {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载病史记录...");

        List<MedicalHistory> histories;

        if (diagnosisKeyword != null && !diagnosisKeyword.trim().isEmpty()) {
          // 按诊断关键词搜索
          histories = medicalHistoryService.getMedicalHistoryByDiagnosis(
              getCurrentUserId(), diagnosisKeyword.trim());
        } else if (startDate != null && endDate != null) {
          // 按日期范围搜索
          histories = medicalHistoryService.getMedicalHistoryByDateRange(
              getCurrentUserId(), startDate.toString(), endDate.toString());
        } else {
          // 加载所有病史
          histories = medicalHistoryService.getMedicalHistoryByUserId(getCurrentUserId());
        }

        if (histories != null) {
          setMedicalHistoryList(histories);
          setStatusMessage("病史记录加载完成，共 " + histories.size() + " 条记录");
        } else {
          setStatusMessage("加载病史记录失败");
        }

      } catch (Exception e) {
        logger.error("加载病史记录时发生错误", e);
        setStatusMessage("加载病史记录时发生错误：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 加载体检结果用于图表对比
   */
  public CompletableFuture<Void> loadComparisonDataCommand() {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载对比数据...");

        // 获取用户的体检结果
        List<ExaminationResult> results = examinationResultService
            .getExaminationResultsByUserId(getCurrentUserId());

        if (results != null && !results.isEmpty()) {
          setExaminationResults(results);

          // 处理图表数据
          processChartData(results);

          setStatusMessage("对比数据加载完成");
        } else {
          setStatusMessage("暂无体检数据用于对比");
        }

      } catch (Exception e) {
        logger.error("加载对比数据时发生错误", e);
        setStatusMessage("加载对比数据时发生错误：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 加载最近病史命令
   */
  public CompletableFuture<Void> loadRecentMedicalHistoryCommand(int limit) {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载最近病史...");

        List<MedicalHistory> recentHistories = medicalHistoryService
            .getRecentMedicalHistory(getCurrentUserId(), limit);

        if (recentHistories != null) {
          setMedicalHistoryList(recentHistories);
          setStatusMessage("最近病史加载完成，共 " + recentHistories.size() + " 条记录");
        } else {
          setStatusMessage("加载最近病史失败");
        }

      } catch (Exception e) {
        logger.error("加载最近病史时发生错误", e);
        setStatusMessage("加载最近病史时发生错误：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 搜索病史命令
   */
  public CompletableFuture<Void> searchMedicalHistoryCommand(String keyword) {
    this.diagnosisKeyword = keyword;
    return loadMedicalHistoryCommand();
  }

  /**
   * 按日期范围筛选命令
   */
  public CompletableFuture<Void> filterByDateRangeCommand(LocalDate start, LocalDate end) {
    this.startDate = start;
    this.endDate = end;
    return loadMedicalHistoryCommand();
  }

  /**
   * 处理图表数据
   */
  private void processChartData(List<ExaminationResult> results) {
    Map<String, List<Object[]>> processedData = new HashMap<>();

    // 按检查项分组处理数据
    for (ExaminationResult result : results) {
      String itemName = getCheckItemName(result.getItemId());

      if (itemName != null) {
        processedData.computeIfAbsent(itemName, k -> new ArrayList<>())
            .add(new Object[] { result.getRecordedAt(), result.getMeasuredValue() });
      }
    }

    // 对每个检查项的数据按日期排序
    processedData.forEach((itemName, dataList) -> {
      dataList.sort((o1, o2) -> ((java.time.LocalDateTime) o1[0])
          .compareTo((java.time.LocalDateTime) o2[0]));
    });

    setChartData(processedData);
  }

  /**
   * 获取检查项名称
   * TODO: 实现从CheckItem服务获取检查项名称的逻辑
   */
  private String getCheckItemName(Integer itemId) {
    // 这里需要从CheckItemService获取检查项名称
    // 暂时返回一个默认值
    return "检查项_" + itemId;
  }

  /**
   * 获取当前登录用户ID
   * TODO: 实现从全局状态获取当前用户ID的逻辑
   */
  private Integer getCurrentUserId() {
    // 这里需要从全局应用状态或会话中获取当前登录用户的ID
    // 暂时返回一个测试值
    return 1;
  }

  /**
   * 清空筛选条件
   */
  public void clearFilters() {
    setStartDate(LocalDate.now().minusYears(1));
    setEndDate(LocalDate.now());
    setSelectedCheckItem(null);
    setDiagnosisKeyword(null);
  }

  // Getters and Setters
  public List<MedicalHistory> getMedicalHistoryList() {
    return medicalHistoryList;
  }

  public void setMedicalHistoryList(List<MedicalHistory> medicalHistoryList) {
    List<MedicalHistory> oldValue = this.medicalHistoryList;
    this.medicalHistoryList = medicalHistoryList;
    firePropertyChange("medicalHistoryList", oldValue, medicalHistoryList);
  }

  public List<ExaminationResult> getExaminationResults() {
    return examinationResults;
  }

  public void setExaminationResults(List<ExaminationResult> examinationResults) {
    List<ExaminationResult> oldValue = this.examinationResults;
    this.examinationResults = examinationResults;
    firePropertyChange("examinationResults", oldValue, examinationResults);
  }

  public Map<String, List<Object[]>> getChartData() {
    return chartData;
  }

  public void setChartData(Map<String, List<Object[]>> chartData) {
    Map<String, List<Object[]>> oldValue = this.chartData;
    this.chartData = chartData;
    firePropertyChange("chartData", oldValue, chartData);
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    LocalDate oldValue = this.startDate;
    this.startDate = startDate;
    firePropertyChange("startDate", oldValue, startDate);
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    LocalDate oldValue = this.endDate;
    this.endDate = endDate;
    firePropertyChange("endDate", oldValue, endDate);
  }

  public String getSelectedCheckItem() {
    return selectedCheckItem;
  }

  public void setSelectedCheckItem(String selectedCheckItem) {
    String oldValue = this.selectedCheckItem;
    this.selectedCheckItem = selectedCheckItem;
    firePropertyChange("selectedCheckItem", oldValue, selectedCheckItem);
  }

  public String getDiagnosisKeyword() {
    return diagnosisKeyword;
  }

  public void setDiagnosisKeyword(String diagnosisKeyword) {
    String oldValue = this.diagnosisKeyword;
    this.diagnosisKeyword = diagnosisKeyword;
    firePropertyChange("diagnosisKeyword", oldValue, diagnosisKeyword);
  }

  public String getSelectedChartType() {
    return selectedChartType;
  }

  public void setSelectedChartType(String selectedChartType) {
    String oldValue = this.selectedChartType;
    this.selectedChartType = selectedChartType;
    firePropertyChange("selectedChartType", oldValue, selectedChartType);
  }

  public String[] getChartTypes() {
    return chartTypes;
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

  /**
   * 初始化加载数据
   */
  public void initialize() {
    loadMedicalHistoryCommand();
    loadComparisonDataCommand();
  }
}