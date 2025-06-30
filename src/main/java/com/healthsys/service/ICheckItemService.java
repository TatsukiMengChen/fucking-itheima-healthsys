package com.healthsys.service;

import com.healthsys.model.entity.CheckItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 检查项服务接口。
 * 定义体检项目相关的业务操作。
 * 
 * @author 梦辰
 */
public interface ICheckItemService {

  /**
   * 添加检查项
   * 
   * @param checkItem 检查项实体
   * @return 是否添加成功
   * @throws Exception 如果检查项代码已存在或其他错误
   */
  boolean addCheckItem(CheckItem checkItem) throws Exception;

  /**
   * 删除检查项
   * 
   * @param itemId 检查项ID
   * @return 是否删除成功
   * @throws Exception 如果检查项不存在或被使用中
   */
  boolean deleteCheckItem(Integer itemId) throws Exception;

  /**
   * 批量删除检查项
   * 
   * @param itemIds 检查项ID列表
   * @return 成功删除的数量
   * @throws Exception 如果有检查项被使用中
   */
  int deleteCheckItems(List<Integer> itemIds) throws Exception;

  /**
   * 更新检查项
   * 
   * @param checkItem 检查项实体
   * @return 是否更新成功
   * @throws Exception 如果检查项不存在或代码重复
   */
  boolean updateCheckItem(CheckItem checkItem) throws Exception;

  /**
   * 根据ID查询检查项
   * 
   * @param itemId 检查项ID
   * @return 检查项实体，如果不存在返回null
   */
  CheckItem getCheckItemById(Integer itemId);

  /**
   * 根据代码查询检查项
   * 
   * @param itemCode 检查项代码
   * @return 检查项实体，如果不存在返回null
   */
  CheckItem getCheckItemByCode(String itemCode);

  /**
   * 查询所有检查项（分页）
   * 
   * @param currentPage 当前页码（从1开始）
   * @param pageSize    每页大小
   * @return 分页结果
   */
  Page<CheckItem> queryAllCheckItems(int currentPage, int pageSize);

  /**
   * 根据名称搜索检查项（分页）
   * 
   * @param itemName    检查项名称（支持模糊搜索）
   * @param currentPage 当前页码（从1开始）
   * @param pageSize    每页大小
   * @return 分页结果
   */
  Page<CheckItem> queryCheckItemsByName(String itemName, int currentPage, int pageSize);

  /**
   * 根据代码搜索检查项（分页）
   * 
   * @param itemCode    检查项代码（支持模糊搜索）
   * @param currentPage 当前页码（从1开始）
   * @param pageSize    每页大小
   * @return 分页结果
   */
  Page<CheckItem> queryCheckItemsByCode(String itemCode, int currentPage, int pageSize);

  /**
   * 多条件搜索检查项（分页）
   * 
   * @param itemName    检查项名称（可为空）
   * @param itemCode    检查项代码（可为空）
   * @param currentPage 当前页码（从1开始）
   * @param pageSize    每页大小
   * @return 分页结果
   */
  Page<CheckItem> queryCheckItems(String itemName, String itemCode, int currentPage, int pageSize);

  /**
   * 获取所有活跃的检查项（不分页）
   * 用于下拉框等场景
   * 
   * @return 活跃检查项列表
   */
  List<CheckItem> getAllActiveCheckItems();

  /**
   * 检查检查项代码是否已存在
   * 
   * @param itemCode  检查项代码
   * @param excludeId 排除的ID（用于更新时检查）
   * @return 如果代码已存在返回true
   */
  boolean isItemCodeExists(String itemCode, Integer excludeId);

  /**
   * 检查检查项是否被检查组使用
   * 
   * @param itemId 检查项ID
   * @return 如果被使用返回true
   */
  boolean isItemInUse(Integer itemId);

  /**
   * 切换检查项的活跃状态
   * 
   * @param itemId   检查项ID
   * @param isActive 是否活跃
   * @return 是否操作成功
   */
  boolean toggleItemActive(Integer itemId, boolean isActive);
}