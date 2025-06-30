package com.healthsys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.model.entity.CheckGroup;
import com.healthsys.model.entity.CheckItem;

import java.util.List;

/**
 * 检查组服务接口。
 * 定义体检分组相关的业务操作。
 * 
 * @author 梦辰
 */
public interface ICheckGroupService {

  /**
   * 分页查询检查组
   * 
   * @param groupName 检查组名称（模糊查询，可为null）
   * @param groupCode 检查组代码（模糊查询，可为null）
   * @param current   当前页码
   * @param size      每页大小
   * @return 分页结果
   */
  Page<CheckGroup> queryCheckGroups(String groupName, String groupCode, int current, int size);

  /**
   * 根据ID查询检查组详情
   * 
   * @param groupId 检查组ID
   * @return 检查组实体，不存在则返回null
   */
  CheckGroup getCheckGroupById(Integer groupId);

  /**
   * 添加检查组
   * 
   * @param checkGroup   检查组信息
   * @param checkItemIds 关联的检查项ID列表
   * @return 添加成功返回true，失败返回false
   */
  boolean addCheckGroup(CheckGroup checkGroup, List<Integer> checkItemIds);

  /**
   * 更新检查组
   * 
   * @param checkGroup   检查组信息
   * @param checkItemIds 关联的检查项ID列表
   * @return 更新成功返回true，失败返回false
   */
  boolean updateCheckGroup(CheckGroup checkGroup, List<Integer> checkItemIds);

  /**
   * 删除检查组
   * 
   * @param groupId 检查组ID
   * @return 删除成功返回true，失败返回false
   */
  boolean deleteCheckGroup(Integer groupId);

  /**
   * 批量删除检查组
   * 
   * @param groupIds 检查组ID列表
   * @return 成功删除的数量
   */
  int deleteCheckGroups(List<Integer> groupIds);

  /**
   * 检查检查组代码是否已存在
   * 
   * @param groupCode 检查组代码
   * @param excludeId 排除的ID（用于更新时检查，可为null）
   * @return 存在返回true，不存在返回false
   */
  boolean isGroupCodeExists(String groupCode, Integer excludeId);

  /**
   * 检查检查组名称是否已存在
   * 
   * @param groupName 检查组名称
   * @param excludeId 排除的ID（用于更新时检查，可为null）
   * @return 存在返回true，不存在返回false
   */
  boolean isGroupNameExists(String groupName, Integer excludeId);

  /**
   * 获取检查组关联的所有检查项
   * 
   * @param groupId 检查组ID
   * @return 检查项列表
   */
  List<CheckItem> getCheckItemsByGroupId(Integer groupId);

  /**
   * 获取检查组关联的检查项ID列表
   * 
   * @param groupId 检查组ID
   * @return 检查项ID列表
   */
  List<Integer> getCheckItemIdsByGroupId(Integer groupId);

  /**
   * 检查检查组是否正在被使用（如在预约中）
   * 
   * @param groupId 检查组ID
   * @return 正在被使用返回true，否则返回false
   */
  boolean isGroupInUse(Integer groupId);

  /**
   * 获取所有启用的检查组
   * 
   * @return 启用的检查组列表
   */
  List<CheckGroup> getAllActiveCheckGroups();

  /**
   * 更新检查组状态
   * 
   * @param groupId  棉查组ID
   * @param isActive 是否启用
   * @return 更新成功返回true，失败返回false
   */
  boolean updateCheckGroupStatus(Integer groupId, boolean isActive);

  /**
   * 统计检查组总数
   * 
   * @return 检查组总数
   */
  int countCheckGroups();

  /**
   * 统计启用的检查组数量
   * 
   * @return 启用的检查组数量
   */
  int countActiveCheckGroups();

  /**
   * 根据检查组ID获取检查组名称
   * 
   * @param groupId 检查组ID
   * @return 检查组名称
   */
  String getCheckGroupNameById(Integer groupId);
}