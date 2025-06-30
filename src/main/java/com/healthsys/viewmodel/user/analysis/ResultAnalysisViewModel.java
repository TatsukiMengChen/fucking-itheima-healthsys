package com.healthsys.viewmodel.user.analysis;

import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.model.entity.Appointment;
import com.healthsys.model.entity.CheckItem;
import com.healthsys.service.IExaminationResultService;
import com.healthsys.service.IAppointmentService;
import com.healthsys.service.ICheckItemService;
import com.healthsys.service.impl.ExaminationResultServiceImpl;
import com.healthsys.service.impl.AppointmentServiceImpl;
import com.healthsys.service.impl.CheckItemServiceImpl;
import com.healthsys.viewmodel.base.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 体检结果分析ViewModel
 * 管理选定的体检结果详情，并包含生成分析和建议的逻辑
 * 
 * @author AI Assistant
 */
public class ResultAnalysisViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(ResultAnalysisViewModel.class);

  private final IExaminationResultService examinationResultService;
  private final IAppointmentService appointmentService;
  private final ICheckItemService checkItemService;

  // 当前选定的结果
  private ExaminationResult selectedResult;
  private Appointment selectedAppointment;
  private List<ExaminationResult> currentResults;

  // 分析结果
  private String analysisText;
  private String suggestionText;
  private String healthScore;
  private String riskLevel;

  // 用户预约列表
  private List<Appointment> userAppointments;

  // UI状态
  private boolean isLoading;
  private String statusMessage;

  public ResultAnalysisViewModel() {
    this.examinationResultService = new ExaminationResultServiceImpl();
    this.appointmentService = new AppointmentServiceImpl();
    this.checkItemService = new CheckItemServiceImpl();
    this.currentResults = new ArrayList<>();
    this.userAppointments = new ArrayList<>();
    this.analysisText = "";
    this.suggestionText = "";

    // 初始化
    initializeData();
  }

  /**
   * 初始化数据
   */
  private void initializeData() {
    loadUserAppointments();
  }

  /**
   * 加载用户预约列表
   */
  public CompletableFuture<Void> loadUserAppointments() {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载预约记录...");

        List<Appointment> appointments = appointmentService.getAppointmentsByUserId(getCurrentUserId());
        if (appointments != null) {
          // 只显示已完成的预约
          List<Appointment> completedAppointments = new ArrayList<>();
          for (Appointment appointment : appointments) {
            if ("已完成".equals(appointment.getStatus())) {
              completedAppointments.add(appointment);
            }
          }
          setUserAppointments(completedAppointments);
          setStatusMessage("预约记录加载完成");
        } else {
          setStatusMessage("加载预约记录失败");
        }

      } catch (Exception e) {
        logger.error("加载用户预约时发生错误", e);
        setStatusMessage("加载预约记录时发生错误：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 加载结果详情命令
   */
  public CompletableFuture<Void> loadResultDetailCommand(Integer appointmentId) {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载体检结果...");

        // 加载预约详情
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        setSelectedAppointment(appointment);

        // 加载该预约的所有体检结果
        List<ExaminationResult> results = examinationResultService.getExaminationResultsByAppointmentId(appointmentId);
        setCurrentResults(results);

        if (results != null && !results.isEmpty()) {
          // 生成分析和建议
          generateAnalysisAndSuggestions(results);
          setStatusMessage("体检结果加载完成");
        } else {
          setStatusMessage("该预约暂无体检结果");
          clearAnalysis();
        }

      } catch (Exception e) {
        logger.error("加载体检结果详情时发生错误", e);
        setStatusMessage("加载体检结果时发生错误：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 生成分析和建议
   */
  private void generateAnalysisAndSuggestions(List<ExaminationResult> results) {
    try {
      StringBuilder analysisBuilder = new StringBuilder();
      StringBuilder suggestionBuilder = new StringBuilder();

      int totalItems = results.size();
      int normalItems = 0;
      int abnormalItems = 0;

      analysisBuilder.append("=== 体检结果分析报告 ===\n\n");
      analysisBuilder.append("本次体检共检查 ").append(totalItems).append(" 个项目：\n\n");

      for (ExaminationResult result : results) {
        // 获取检查项信息
        CheckItem checkItem = checkItemService.getCheckItemById(result.getItemId());
        String itemName = checkItem != null ? checkItem.getItemName() : "检查项_" + result.getItemId();
        String referenceVal = checkItem != null ? checkItem.getReferenceVal() : "未知";

        analysisBuilder.append("• ").append(itemName).append("：")
            .append(result.getMeasuredValue())
            .append(" (参考值：").append(referenceVal).append(")\n");

        // 简单的异常判断逻辑
        boolean isNormal = isValueNormal(result.getMeasuredValue(), referenceVal);
        if (isNormal) {
          normalItems++;
          analysisBuilder.append("  ✓ 正常范围内\n");
        } else {
          abnormalItems++;
          analysisBuilder.append("  ⚠ 需要关注\n");
        }

        analysisBuilder.append("\n");
      }

      // 生成总体评价
      double normalRate = (double) normalItems / totalItems * 100;
      analysisBuilder.append("=== 总体评价 ===\n");
      analysisBuilder.append("正常项目：").append(normalItems).append(" 个 (").append(String.format("%.1f", normalRate))
          .append("%)\n");
      analysisBuilder.append("异常项目：").append(abnormalItems).append(" 个 (")
          .append(String.format("%.1f", 100 - normalRate)).append("%)\n\n");

      // 健康评分
      String score;
      String risk;
      if (normalRate >= 90) {
        score = "优秀 (90-100分)";
        risk = "低风险";
      } else if (normalRate >= 80) {
        score = "良好 (80-89分)";
        risk = "低风险";
      } else if (normalRate >= 70) {
        score = "一般 (70-79分)";
        risk = "中等风险";
      } else {
        score = "需改善 (60-69分)";
        risk = "高风险";
      }

      setHealthScore(score);
      setRiskLevel(risk);

      // 生成建议
      suggestionBuilder.append("=== 健康建议 ===\n\n");

      if (abnormalItems == 0) {
        suggestionBuilder.append("🎉 恭喜！您的体检结果全部正常。\n\n")
            .append("建议：\n")
            .append("• 保持现有的健康生活方式\n")
            .append("• 定期进行体检，建议每年至少一次\n")
            .append("• 注意均衡饮食和适量运动\n");
      } else if (abnormalItems <= 2) {
        suggestionBuilder.append("📊 您的体检结果大部分正常，但有少数项目需要关注。\n\n")
            .append("建议：\n")
            .append("• 针对异常项目咨询医生，制定改善计划\n")
            .append("• 调整生活习惯，重点关注异常指标\n")
            .append("• 3-6个月后复查相关项目\n");
      } else {
        suggestionBuilder.append("⚠️ 您的体检结果显示多个项目异常，建议及时就医。\n\n")
            .append("建议：\n")
            .append("• 尽快咨询专科医生，进行进一步检查\n")
            .append("• 严格按医嘱执行治疗方案\n")
            .append("• 改善生活方式，戒烟限酒，规律作息\n")
            .append("• 定期复查，密切监测健康状况\n");
      }

      setAnalysisText(analysisBuilder.toString());
      setSuggestionText(suggestionBuilder.toString());

    } catch (Exception e) {
      logger.error("生成分析和建议时发生错误", e);
      setAnalysisText("分析生成失败");
      setSuggestionText("建议生成失败");
    }
  }

  /**
   * 简单的数值正常性判断
   */
  private boolean isValueNormal(String measuredValue, String referenceValue) {
    try {
      // 这里实现简单的数值比较逻辑
      // 实际项目中应该有更复杂的判断规则
      if (referenceValue == null || referenceValue.isEmpty() || "未知".equals(referenceValue)) {
        return true; // 无参考值时默认正常
      }

      // 处理范围格式，如 "3.5-5.0"
      if (referenceValue.contains("-")) {
        String[] range = referenceValue.split("-");
        if (range.length == 2) {
          double min = Double.parseDouble(range[0].trim());
          double max = Double.parseDouble(range[1].trim());
          double value = Double.parseDouble(measuredValue.trim());
          return value >= min && value <= max;
        }
      }

      // 其他情况默认正常
      return true;

    } catch (NumberFormatException e) {
      // 非数值类型，默认正常
      return true;
    }
  }

  /**
   * 清空分析结果
   */
  private void clearAnalysis() {
    setAnalysisText("");
    setSuggestionText("");
    setHealthScore("");
    setRiskLevel("");
  }

  /**
   * 获取当前登录用户ID
   */
  private Integer getCurrentUserId() {
    // TODO: 从全局状态获取
    return 1;
  }

  // Getters and Setters
  public ExaminationResult getSelectedResult() {
    return selectedResult;
  }

  public void setSelectedResult(ExaminationResult selectedResult) {
    ExaminationResult oldValue = this.selectedResult;
    this.selectedResult = selectedResult;
    firePropertyChange("selectedResult", oldValue, selectedResult);
  }

  public Appointment getSelectedAppointment() {
    return selectedAppointment;
  }

  public void setSelectedAppointment(Appointment selectedAppointment) {
    Appointment oldValue = this.selectedAppointment;
    this.selectedAppointment = selectedAppointment;
    firePropertyChange("selectedAppointment", oldValue, selectedAppointment);
  }

  public List<ExaminationResult> getCurrentResults() {
    return currentResults;
  }

  public void setCurrentResults(List<ExaminationResult> currentResults) {
    List<ExaminationResult> oldValue = this.currentResults;
    this.currentResults = currentResults;
    firePropertyChange("currentResults", oldValue, currentResults);
  }

  public String getAnalysisText() {
    return analysisText;
  }

  public void setAnalysisText(String analysisText) {
    String oldValue = this.analysisText;
    this.analysisText = analysisText;
    firePropertyChange("analysisText", oldValue, analysisText);
  }

  public String getSuggestionText() {
    return suggestionText;
  }

  public void setSuggestionText(String suggestionText) {
    String oldValue = this.suggestionText;
    this.suggestionText = suggestionText;
    firePropertyChange("suggestionText", oldValue, suggestionText);
  }

  public String getHealthScore() {
    return healthScore;
  }

  public void setHealthScore(String healthScore) {
    String oldValue = this.healthScore;
    this.healthScore = healthScore;
    firePropertyChange("healthScore", oldValue, healthScore);
  }

  public String getRiskLevel() {
    return riskLevel;
  }

  public void setRiskLevel(String riskLevel) {
    String oldValue = this.riskLevel;
    this.riskLevel = riskLevel;
    firePropertyChange("riskLevel", oldValue, riskLevel);
  }

  public List<Appointment> getUserAppointments() {
    return userAppointments;
  }

  public void setUserAppointments(List<Appointment> userAppointments) {
    List<Appointment> oldValue = this.userAppointments;
    this.userAppointments = userAppointments;
    firePropertyChange("userAppointments", oldValue, userAppointments);
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
}