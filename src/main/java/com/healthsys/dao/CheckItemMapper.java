package com.healthsys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.model.entity.CheckItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 检查项数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI健康管理系统开发团队
 */
@Mapper
public interface CheckItemMapper extends BaseMapper<CheckItem> {

  /**
   * 根据检查项代码查找检查项
   * 
   * @param itemCode 检查项代码
   * @return 检查项实体，如果未找到返回null
   */
  @Select("SELECT * FROM check_items WHERE item_code = #{itemCode}")
  CheckItem findByItemCode(@Param("itemCode") String itemCode);

  /**
   * 检查检查项代码是否存在
   * 
   * @param itemCode 检查项代码
   * @return 存在返回true，否则返回false
   */
  @Select("SELECT COUNT(*) > 0 FROM check_items WHERE item_code = #{itemCode}")
  boolean existsByItemCode(@Param("itemCode") String itemCode);

  /**
   * 查询所有活动状态的检查项
   * 
   * @return 活动检查项列表
   */
  @Select("SELECT * FROM check_items WHERE is_active = true ORDER BY created_at DESC")
  List<CheckItem> findAllActive();

  /**
   * 根据名称模糊查询检查项（分页）
   * 
   * @param page     分页参数
   * @param itemName 检查项名称（支持模糊查询）
   * @return 分页查询结果
   */
  @Select("SELECT * FROM check_items WHERE item_name LIKE CONCAT('%', #{itemName}, '%') AND is_active = true ORDER BY created_at DESC")
  Page<CheckItem> findByItemNameLike(Page<CheckItem> page, @Param("itemName") String itemName);

  /**
   * 根据代码模糊查询检查项（分页）
   * 
   * @param page     分页参数
   * @param itemCode 检查项代码（支持模糊查询）
   * @return 分页查询结果
   */
  @Select("SELECT * FROM check_items WHERE item_code LIKE CONCAT('%', #{itemCode}, '%') AND is_active = true ORDER BY created_at DESC")
  Page<CheckItem> findByItemCodeLike(Page<CheckItem> page, @Param("itemCode") String itemCode);

  /**
   * 根据创建者查询检查项
   * 
   * @param createdBy 创建者用户ID
   * @return 检查项列表
   */
  @Select("SELECT * FROM check_items WHERE created_by = #{createdBy} AND is_active = true ORDER BY created_at DESC")
  List<CheckItem> findByCreatedBy(@Param("createdBy") Integer createdBy);

  /**
   * 查询指定检查组中的所有检查项
   * 
   * @param groupId 检查组ID
   * @return 检查项列表
   */
  @Select("SELECT ci.* FROM check_items ci " +
      "INNER JOIN group_check_item gci ON ci.item_id = gci.item_id " +
      "WHERE gci.group_id = #{groupId} AND ci.is_active = true " +
      "ORDER BY ci.created_at DESC")
  List<CheckItem> findByGroupId(@Param("groupId") Integer groupId);
}