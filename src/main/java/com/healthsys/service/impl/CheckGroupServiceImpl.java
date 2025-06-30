package com.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.config.DataAccessManager;
import com.healthsys.dao.CheckGroupMapper;
import com.healthsys.dao.CheckItemMapper;
import com.healthsys.model.entity.CheckGroup;
import com.healthsys.model.entity.CheckItem;
import com.healthsys.service.ICheckGroupService;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查组服务实现。
 * 实现体检分组相关的业务逻辑。
 * 
 * @author 梦辰
 */
public class CheckGroupServiceImpl implements ICheckGroupService {

  private static final Logger logger = LoggerFactory.getLogger(CheckGroupServiceImpl.class);

  private CheckGroupMapper checkGroupMapper;
  private CheckItemMapper checkItemMapper;

  /**
   * 构造函数
   */
  public CheckGroupServiceImpl() {
    this.checkGroupMapper = DataAccessManager.getCheckGroupMapperStatic();
    this.checkItemMapper = DataAccessManager.getCheckItemMapperStatic();
  }

  @Override
  public Page<CheckGroup> queryCheckGroups(String groupName, String groupCode, int current, int size) {
    try {
      Page<CheckGroup> page = new Page<>(current, size);
      QueryWrapper<CheckGroup> queryWrapper = new QueryWrapper<>();

      // 构建查询条件
      if (StrUtil.isNotBlank(groupName)) {
        queryWrapper.like("group_name", groupName.trim());
      }
      if (StrUtil.isNotBlank(groupCode)) {
        queryWrapper.like("group_code", groupCode.trim());
      }

      // 只查询启用的检查组
      queryWrapper.eq("is_active", true);
      queryWrapper.orderByDesc("created_at");

      return checkGroupMapper.selectPage(page, queryWrapper);

    } catch (Exception e) {
      logger.error("查询检查组列表失败", e);
      throw new RuntimeException("查询检查组列表失败: " + e.getMessage(), e);
    }
  }

  @Override
  public CheckGroup getCheckGroupById(Integer groupId) {
    try {
      if (groupId == null) {
        return null;
      }
      return checkGroupMapper.selectById(groupId);
    } catch (Exception e) {
      logger.error("根据ID查询检查组失败: groupId={}", groupId, e);
      return null;
    }
  }

  @Override
  public boolean addCheckGroup(CheckGroup checkGroup, List<Integer> checkItemIds) {
    try {
      // 参数验证
      if (checkGroup == null) {
        logger.warn("添加检查组失败：检查组信息为空");
        return false;
      }

      // 验证必填字段
      if (StrUtil.isBlank(checkGroup.getGroupCode()) || StrUtil.isBlank(checkGroup.getGroupName())) {
        logger.warn("添加检查组失败：代码或名称为空");
        return false;
      }

      // 检查代码重复
      if (isGroupCodeExists(checkGroup.getGroupCode(), null)) {
        logger.warn("添加检查组失败：代码已存在 - {}", checkGroup.getGroupCode());
        return false;
      }

      // 检查名称重复
      if (isGroupNameExists(checkGroup.getGroupName(), null)) {
        logger.warn("添加检查组失败：名称已存在 - {}", checkGroup.getGroupName());
        return false;
      }

      // 设置创建时间
      checkGroup.setCreatedAt(LocalDateTime.now());
      checkGroup.setUpdatedAt(LocalDateTime.now());

      // 默认启用
      if (checkGroup.getIsActive() == null) {
        checkGroup.setIsActive(true);
      }

      // 插入检查组
      int insertResult = checkGroupMapper.insert(checkGroup);
      if (insertResult <= 0) {
        logger.error("添加检查组失败：数据库插入失败");
        return false;
      }

      // 添加检查项关联
      if (checkItemIds != null && !checkItemIds.isEmpty()) {
        for (Integer itemId : checkItemIds) {
          if (itemId != null) {
            checkGroupMapper.addCheckItemToGroup(checkGroup.getGroupId(), itemId);
          }
        }
      }

      logger.info("成功添加检查组：{}", checkGroup.getGroupName());
      return true;

    } catch (Exception e) {
      logger.error("添加检查组失败", e);
      return false;
    }
  }

  @Override
  public boolean updateCheckGroup(CheckGroup checkGroup, List<Integer> checkItemIds) {
    try {
      // 参数验证
      if (checkGroup == null || checkGroup.getGroupId() == null) {
        logger.warn("更新检查组失败：检查组信息或ID为空");
        return false;
      }

      // 验证必填字段
      if (StrUtil.isBlank(checkGroup.getGroupCode()) || StrUtil.isBlank(checkGroup.getGroupName())) {
        logger.warn("更新检查组失败：代码或名称为空");
        return false;
      }

      // 检查代码重复（排除自己）
      if (isGroupCodeExists(checkGroup.getGroupCode(), checkGroup.getGroupId())) {
        logger.warn("更新检查组失败：代码已存在 - {}", checkGroup.getGroupCode());
        return false;
      }

      // 检查名称重复（排除自己）
      if (isGroupNameExists(checkGroup.getGroupName(), checkGroup.getGroupId())) {
        logger.warn("更新检查组失败：名称已存在 - {}", checkGroup.getGroupName());
        return false;
      }

      // 设置更新时间
      checkGroup.setUpdatedAt(LocalDateTime.now());

      // 更新检查组
      int updateResult = checkGroupMapper.updateById(checkGroup);
      if (updateResult <= 0) {
        logger.error("更新检查组失败：数据库更新失败");
        return false;
      }

      // 更新检查项关联：先删除所有关联，再添加新的关联
      checkGroupMapper.removeAllCheckItemsFromGroup(checkGroup.getGroupId());

      if (checkItemIds != null && !checkItemIds.isEmpty()) {
        for (Integer itemId : checkItemIds) {
          if (itemId != null) {
            checkGroupMapper.addCheckItemToGroup(checkGroup.getGroupId(), itemId);
          }
        }
      }

      logger.info("成功更新检查组：{}", checkGroup.getGroupName());
      return true;

    } catch (Exception e) {
      logger.error("更新检查组失败", e);
      return false;
    }
  }

  @Override
  public boolean deleteCheckGroup(Integer groupId) {
    try {
      if (groupId == null) {
        logger.warn("删除检查组失败：ID为空");
        return false;
      }

      // 检查是否正在被使用
      if (isGroupInUse(groupId)) {
        logger.warn("删除检查组失败：检查组正在被使用 - ID: {}", groupId);
        return false;
      }

      // 删除检查项关联
      checkGroupMapper.removeAllCheckItemsFromGroup(groupId);

      // 删除检查组（逻辑删除：设置为非活动状态）
      CheckGroup checkGroup = new CheckGroup();
      checkGroup.setGroupId(groupId);
      checkGroup.setIsActive(false);
      checkGroup.setUpdatedAt(LocalDateTime.now());

      int deleteResult = checkGroupMapper.updateById(checkGroup);
      if (deleteResult <= 0) {
        logger.error("删除检查组失败：数据库更新失败");
        return false;
      }

      logger.info("成功删除检查组：ID={}", groupId);
      return true;

    } catch (Exception e) {
      logger.error("删除检查组失败：ID={}", groupId, e);
      return false;
    }
  }

  @Override
  public int deleteCheckGroups(List<Integer> groupIds) {
    if (groupIds == null || groupIds.isEmpty()) {
      return 0;
    }

    int successCount = 0;
    for (Integer groupId : groupIds) {
      if (deleteCheckGroup(groupId)) {
        successCount++;
      }
    }

    return successCount;
  }

  @Override
  public boolean isGroupCodeExists(String groupCode, Integer excludeId) {
    try {
      if (StrUtil.isBlank(groupCode)) {
        return false;
      }
      return checkGroupMapper.existsByGroupCodeExcludeId(groupCode.trim(), excludeId);
    } catch (Exception e) {
      logger.error("检查检查组代码是否存在失败", e);
      return false;
    }
  }

  @Override
  public boolean isGroupNameExists(String groupName, Integer excludeId) {
    try {
      if (StrUtil.isBlank(groupName)) {
        return false;
      }
      return checkGroupMapper.existsByGroupNameExcludeId(groupName.trim(), excludeId);
    } catch (Exception e) {
      logger.error("检查检查组名称是否存在失败", e);
      return false;
    }
  }

  @Override
  public List<CheckItem> getCheckItemsByGroupId(Integer groupId) {
    try {
      if (groupId == null) {
        return new ArrayList<>();
      }

      List<Integer> itemIds = getCheckItemIdsByGroupId(groupId);
      if (itemIds.isEmpty()) {
        return new ArrayList<>();
      }

      List<CheckItem> checkItems = new ArrayList<>();
      for (Integer itemId : itemIds) {
        CheckItem item = checkItemMapper.selectById(itemId);
        if (item != null && Boolean.TRUE.equals(item.getIsActive())) {
          checkItems.add(item);
        }
      }

      return checkItems;

    } catch (Exception e) {
      logger.error("获取检查组关联的检查项失败：groupId={}", groupId, e);
      return new ArrayList<>();
    }
  }

  @Override
  public List<Integer> getCheckItemIdsByGroupId(Integer groupId) {
    try {
      if (groupId == null) {
        return new ArrayList<>();
      }

      List<Integer> itemIds = checkGroupMapper.getCheckItemIdsByGroupId(groupId);
      return itemIds != null ? itemIds : new ArrayList<>();

    } catch (Exception e) {
      logger.error("获取检查组关联的检查项ID失败：groupId={}", groupId, e);
      return new ArrayList<>();
    }
  }

  @Override
  public boolean isGroupInUse(Integer groupId) {
    try {
      if (groupId == null) {
        return false;
      }
      return checkGroupMapper.isGroupInUse(groupId);
    } catch (Exception e) {
      logger.error("检查检查组是否被使用失败：groupId={}", groupId, e);
      return false;
    }
  }

  @Override
  public List<CheckGroup> getAllActiveCheckGroups() {
    try {
      return checkGroupMapper.findAllActive();
    } catch (Exception e) {
      logger.error("获取所有启用检查组失败", e);
      return new ArrayList<>();
    }
  }

  @Override
  public boolean updateCheckGroupStatus(Integer groupId, boolean isActive) {
    try {
      if (groupId == null) {
        logger.warn("更新检查组状态失败：ID为空");
        return false;
      }

      CheckGroup checkGroup = new CheckGroup();
      checkGroup.setGroupId(groupId);
      checkGroup.setIsActive(isActive);
      checkGroup.setUpdatedAt(LocalDateTime.now());

      int updateResult = checkGroupMapper.updateById(checkGroup);
      if (updateResult > 0) {
        logger.info("成功更新检查组状态：ID={}, isActive={}", groupId, isActive);
        return true;
      } else {
        logger.error("更新检查组状态失败：数据库更新失败");
        return false;
      }

    } catch (Exception e) {
      logger.error("更新检查组状态失败：groupId={}", groupId, e);
      return false;
    }
  }

  @Override
  public int countCheckGroups() {
    try {
      return checkGroupMapper.countCheckGroups();
    } catch (Exception e) {
      logger.error("统计检查组总数失败", e);
      return 0;
    }
  }

  @Override
  public int countActiveCheckGroups() {
    try {
      return checkGroupMapper.countActiveCheckGroups();
    } catch (Exception e) {
      logger.error("统计启用检查组数量失败", e);
      return 0;
    }
  }

  @Override
  public String getCheckGroupNameById(Integer groupId) {
    try {
      if (groupId == null) {
        return null;
      }

      CheckGroup checkGroup = checkGroupMapper.selectById(groupId);
      return checkGroup != null ? checkGroup.getGroupName() : null;

    } catch (Exception e) {
      logger.error("根据ID获取检查组名称失败：groupId={}", groupId, e);
      return null;
    }
  }
}