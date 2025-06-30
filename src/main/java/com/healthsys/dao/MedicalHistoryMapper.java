package com.healthsys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.model.entity.MedicalHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 病史记录数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI健康管理系统开发团队
 */
@Mapper
public interface MedicalHistoryMapper extends BaseMapper<MedicalHistory> {

  /**
   * 根据用户ID查询病史记录
   * 
   * @param userId 用户ID
   * @return 病史记录列表
   */
  @Select("SELECT * FROM medical_history WHERE user_id = #{userId} ORDER BY diagnosis_date DESC")
  List<MedicalHistory> findByUserId(@Param("userId") Integer userId);

  /**
   * 根据用户ID分页查询病史记录
   * 
   * @param page   分页参数
   * @param userId 用户ID
   * @return 分页查询结果
   */
  @Select("SELECT * FROM medical_history WHERE user_id = #{userId} ORDER BY diagnosis_date DESC")
  Page<MedicalHistory> findByUserIdWithPage(Page<MedicalHistory> page, @Param("userId") Integer userId);

  /**
   * 根据诊断关键词模糊查询病史记录
   * 
   * @param userId    用户ID
   * @param diagnosis 诊断关键词
   * @return 病史记录列表
   */
  @Select("SELECT * FROM medical_history WHERE user_id = #{userId} " +
      "AND diagnosis LIKE CONCAT('%', #{diagnosis}, '%') " +
      "ORDER BY diagnosis_date DESC")
  List<MedicalHistory> findByUserIdAndDiagnosisLike(@Param("userId") Integer userId,
      @Param("diagnosis") String diagnosis);

  /**
   * 根据医生姓名查询病史记录
   * 
   * @param userId     用户ID
   * @param doctorName 医生姓名
   * @return 病史记录列表
   */
  @Select("SELECT * FROM medical_history WHERE user_id = #{userId} AND doctor_name = #{doctorName} " +
      "ORDER BY diagnosis_date DESC")
  List<MedicalHistory> findByUserIdAndDoctorName(@Param("userId") Integer userId,
      @Param("doctorName") String doctorName);

  /**
   * 查询指定用户在指定日期范围内的病史记录
   * 
   * @param userId    用户ID
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return 病史记录列表
   */
  @Select("SELECT * FROM medical_history WHERE user_id = #{userId} " +
      "AND diagnosis_date BETWEEN #{startDate} AND #{endDate} " +
      "ORDER BY diagnosis_date DESC")
  List<MedicalHistory> findByUserIdAndDateRange(@Param("userId") Integer userId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  /**
   * 查询指定用户的最近病史记录
   * 
   * @param userId 用户ID
   * @param limit  数量限制
   * @return 病史记录列表
   */
  @Select("SELECT * FROM medical_history WHERE user_id = #{userId} " +
      "ORDER BY diagnosis_date DESC LIMIT #{limit}")
  List<MedicalHistory> findRecentByUserId(@Param("userId") Integer userId, @Param("limit") Integer limit);

  /**
   * 根据诊断日期查询病史记录
   * 
   * @param diagnosisDate 诊断日期
   * @return 病史记录列表
   */
  @Select("SELECT * FROM medical_history WHERE diagnosis_date = #{diagnosisDate} " +
      "ORDER BY created_at DESC")
  List<MedicalHistory> findByDiagnosisDate(@Param("diagnosisDate") LocalDate diagnosisDate);

  /**
   * 统计用户的病史记录数量
   * 
   * @param userId 用户ID
   * @return 病史记录总数
   */
  @Select("SELECT COUNT(*) FROM medical_history WHERE user_id = #{userId}")
  Integer countByUserId(@Param("userId") Integer userId);

  /**
   * 统计某个医生的诊断记录数量
   * 
   * @param doctorName 医生姓名
   * @return 诊断记录数量
   */
  @Select("SELECT COUNT(*) FROM medical_history WHERE doctor_name = #{doctorName}")
  Integer countByDoctorName(@Param("doctorName") String doctorName);

  /**
   * 查询所有不同的医生姓名
   * 
   * @return 医生姓名列表
   */
  @Select("SELECT DISTINCT doctor_name FROM medical_history WHERE doctor_name IS NOT NULL " +
      "ORDER BY doctor_name")
  List<String> findAllDoctorNames();
}