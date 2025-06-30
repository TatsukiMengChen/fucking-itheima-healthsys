package com.healthsys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.model.entity.CheckGroup;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 检查组数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI健康管理系统开发团队
 */
@Mapper
public interface CheckGroupMapper extends BaseMapper<CheckGroup> {

  /**
   * 根据检查组代码查找检查组
   * 
   * @param groupCode 检查组代码
   * @return 检查组实体，如果未找到返回null
   */
  @Select("SELECT * FROM check_groups WHERE group_code = #{groupCode}")
  CheckGroup findByGroupCode(@Param("groupCode") String groupCode);

  /**
   * 检查检查组代码是否存在
   * 
   * @param groupCode 检查组代码
   * @return 存在返回true，否则返回false
   */
  @Select("SELECT COUNT(*) > 0 FROM check_groups WHERE group_code = #{groupCode}")
  boolean existsByGroupCode(@Param("groupCode") String groupCode);

  /**
   * 查询所有活动状态的检查组
   * 
   * @return 活动检查组列表
   */
  @Select("SELECT * FROM check_groups WHERE is_active = true ORDER BY created_at DESC")
  List<CheckGroup> findAllActive();

  /**
   * 根据名称模糊查询检查组（分页）
   * 
   * @param page      分页参数
   * @param groupName 检查组名称（支持模糊查询）
   * @return 分页查询结果
   */
  @Select("SELECT * FROM check_groups WHERE group_name LIKE CONCAT('%', #{groupName}, '%') AND is_active = true ORDER BY created_at DESC")
  Page<CheckGroup> findByGroupNameLike(Page<CheckGroup> page, @Param("groupName") String groupName);

  /**
   * 根据代码模糊查询检查组（分页）
   * 
   * @param page      分页参数
   * @param groupCode 检查组代码（支持模糊查询）
   * @return 分页查询结果
   */
  @Select("SELECT * FROM check_groups WHERE group_code LIKE CONCAT('%', #{groupCode}, '%') AND is_active = true ORDER BY created_at DESC")
  Page<CheckGroup> findByGroupCodeLike(Page<CheckGroup> page, @Param("groupCode") String groupCode);

  /**
   * 根据创建者查询检查组
   * 
   * @param createdBy 创建者用户ID
   * @return 检查组列表
   */
  @Select("SELECT * FROM check_groups WHERE created_by = #{createdBy} AND is_active = true ORDER BY created_at DESC")
  List<CheckGroup> findByCreatedBy(@Param("createdBy") Integer createdBy);

  /**
   * 为检查组添加检查项关联
   * 
   * @param groupId 检查组ID
   * @param itemId  检查项ID
   */
  @Insert("INSERT INTO group_check_item (group_id, item_id) VALUES (#{groupId}, #{itemId})")
  void addCheckItemToGroup(@Param("groupId") Integer groupId, @Param("itemId") Integer itemId);

  /**
   * 删除检查组的所有检查项关联
   * 
   * @param groupId 检查组ID
   */
  @Delete("DELETE FROM group_check_item WHERE group_id = #{groupId}")
  void removeAllCheckItemsFromGroup(@Param("groupId") Integer groupId);

  /**
   * 删除检查组的指定检查项关联
   * 
   * @param groupId 检查组ID
   * @param itemId  检查项ID
   */
  @Delete("DELETE FROM group_check_item WHERE group_id = #{groupId} AND item_id = #{itemId}")
  void removeCheckItemFromGroup(@Param("groupId") Integer groupId, @Param("itemId") Integer itemId);

  /**
   * 查询检查组包含的检查项ID列表
   * 
   * @param groupId 检查组ID
   * @return 检查项ID列表
   */
  @Select("SELECT item_id FROM group_check_item WHERE group_id = #{groupId}")
  List<Integer> getCheckItemIdsByGroupId(@Param("groupId") Integer groupId);

  /**
   * 检查检查组名称是否存在（排除指定ID）
   * 
   * @param groupName 检查组名称
   * @param excludeId 排除的ID（可为null）
   * @return 存在返回true，否则返回false
   */
  @Select("<script>" +
      "SELECT COUNT(*) > 0 FROM check_groups WHERE group_name = #{groupName}" +
      "<if test='excludeId != null'> AND group_id != #{excludeId}</if>" +
      "</script>")
  boolean existsByGroupNameExcludeId(@Param("groupName") String groupName, @Param("excludeId") Integer excludeId);

  /**
   * 检查检查组代码是否存在（排除指定ID）
   * 
   * @param groupCode 检查组代码
   * @param excludeId 排除的ID（可为null）
   * @return 存在返回true，否则返回false
   */
  @Select("<script>" +
      "SELECT COUNT(*) > 0 FROM check_groups WHERE group_code = #{groupCode}" +
      "<if test='excludeId != null'> AND group_id != #{excludeId}</if>" +
      "</script>")
  boolean existsByGroupCodeExcludeId(@Param("groupCode") String groupCode, @Param("excludeId") Integer excludeId);

  /**
   * 检查检查组是否正在被使用（在预约表中）
   * 
   * @param groupId 检查组ID
   * @return 正在被使用返回true，否则返回false
   */
  @Select("SELECT COUNT(*) > 0 FROM appointments WHERE group_id = #{groupId}")
  boolean isGroupInUse(@Param("groupId") Integer groupId);

  /**
   * 统计检查组总数
   * 
   * @return 检查组总数
   */
  @Select("SELECT COUNT(*) FROM check_groups")
  int countCheckGroups();

  /**
   * 统计启用的检查组数量
   * 
   * @return 启用的检查组数量
   */
  @Select("SELECT COUNT(*) FROM check_groups WHERE is_active = true")
  int countActiveCheckGroups();
}