package com.healthsys.service.impl;

import com.healthsys.service.ICheckItemService;
import com.healthsys.model.entity.CheckItem;
import com.healthsys.dao.CheckItemMapper;
import com.healthsys.util.ValidationUtil;
import com.healthsys.config.DataAccessManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.util.StrUtil;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检查项服务实现。
 * 实现体检项目相关的业务逻辑。
 * 
 * @author 梦辰
 */
public class CheckItemServiceImpl implements ICheckItemService {

  private CheckItemMapper checkItemMapper;

  /**
   * 构造函数
   */
  public CheckItemServiceImpl() {
    this.checkItemMapper = DataAccessManager.getInstance().getCheckItemMapper();
  }

  @Override
  public boolean addCheckItem(CheckItem checkItem) throws Exception {
    // 参数验证
    validateCheckItem(checkItem, true);

    // 检查代码是否已存在
    if (isItemCodeExists(checkItem.getItemCode(), null)) {
      throw new Exception("检查项代码已存在：" + checkItem.getItemCode());
    }

    // 设置创建时间和更新时间
    LocalDateTime now = LocalDateTime.now();
    checkItem.setCreatedAt(now);
    checkItem.setUpdatedAt(now);
    checkItem.setIsActive(true); // 默认为活跃状态

    int result = checkItemMapper.insert(checkItem);
    return result > 0;
  }

  @Override
  public boolean deleteCheckItem(Integer itemId) throws Exception {
    if (itemId == null || itemId <= 0) {
      throw new Exception("检查项ID不能为空");
    }

    // 检查是否存在
    CheckItem checkItem = checkItemMapper.selectById(itemId);
    if (checkItem == null) {
      throw new Exception("检查项不存在");
    }

    // 检查是否被使用
    if (isItemInUse(itemId)) {
      throw new Exception("该检查项正在被检查组使用，无法删除");
    }

    int result = checkItemMapper.deleteById(itemId);
    return result > 0;
  }

  @Override
  public int deleteCheckItems(List<Integer> itemIds) throws Exception {
    if (itemIds == null || itemIds.isEmpty()) {
      throw new Exception("检查项ID列表不能为空");
    }

    int successCount = 0;
    StringBuilder errorMessages = new StringBuilder();

    for (Integer itemId : itemIds) {
      try {
        if (deleteCheckItem(itemId)) {
          successCount++;
        }
      } catch (Exception e) {
        errorMessages.append("ID[").append(itemId).append("]: ").append(e.getMessage()).append("; ");
      }
    }

    if (errorMessages.length() > 0 && successCount == 0) {
      throw new Exception("批量删除失败：" + errorMessages.toString());
    }

    return successCount;
  }

  @Override
  public boolean updateCheckItem(CheckItem checkItem) throws Exception {
    // 参数验证
    validateCheckItem(checkItem, false);

    if (checkItem.getItemId() == null || checkItem.getItemId() <= 0) {
      throw new Exception("检查项ID不能为空");
    }

    // 检查是否存在
    CheckItem existingItem = checkItemMapper.selectById(checkItem.getItemId());
    if (existingItem == null) {
      throw new Exception("检查项不存在");
    }

    // 检查代码是否重复（排除自身）
    if (isItemCodeExists(checkItem.getItemCode(), checkItem.getItemId())) {
      throw new Exception("检查项代码已存在：" + checkItem.getItemCode());
    }

    // 设置更新时间
    checkItem.setUpdatedAt(LocalDateTime.now());

    int result = checkItemMapper.updateById(checkItem);
    return result > 0;
  }

  @Override
  public CheckItem getCheckItemById(Integer itemId) {
    if (itemId == null || itemId <= 0) {
      return null;
    }
    return checkItemMapper.selectById(itemId);
  }

  @Override
  public CheckItem getCheckItemByCode(String itemCode) {
    if (StrUtil.isBlank(itemCode)) {
      return null;
    }

    QueryWrapper<CheckItem> wrapper = new QueryWrapper<>();
    wrapper.eq("item_code", itemCode.trim())
        .eq("is_active", true)
        .last("LIMIT 1");

    return checkItemMapper.selectOne(wrapper);
  }

  @Override
  public Page<CheckItem> queryAllCheckItems(int currentPage, int pageSize) {
    Page<CheckItem> page = new Page<>(currentPage, pageSize);

    QueryWrapper<CheckItem> wrapper = new QueryWrapper<>();
    wrapper.orderByAsc("item_id");

    return checkItemMapper.selectPage(page, wrapper);
  }

  @Override
  public Page<CheckItem> queryCheckItemsByName(String itemName, int currentPage, int pageSize) {
    Page<CheckItem> page = new Page<>(currentPage, pageSize);

    QueryWrapper<CheckItem> wrapper = new QueryWrapper<>();
    if (StrUtil.isNotBlank(itemName)) {
      wrapper.like("item_name", itemName.trim());
    }
    wrapper.orderByAsc("item_id");

    return checkItemMapper.selectPage(page, wrapper);
  }

  @Override
  public Page<CheckItem> queryCheckItemsByCode(String itemCode, int currentPage, int pageSize) {
    Page<CheckItem> page = new Page<>(currentPage, pageSize);

    QueryWrapper<CheckItem> wrapper = new QueryWrapper<>();
    if (StrUtil.isNotBlank(itemCode)) {
      wrapper.like("item_code", itemCode.trim());
    }
    wrapper.orderByAsc("item_id");

    return checkItemMapper.selectPage(page, wrapper);
  }

  @Override
  public Page<CheckItem> queryCheckItems(String itemName, String itemCode, int currentPage, int pageSize) {
    Page<CheckItem> page = new Page<>(currentPage, pageSize);

    QueryWrapper<CheckItem> wrapper = new QueryWrapper<>();
    if (StrUtil.isNotBlank(itemName)) {
      wrapper.like("item_name", itemName.trim());
    }
    if (StrUtil.isNotBlank(itemCode)) {
      wrapper.like("item_code", itemCode.trim());
    }
    wrapper.orderByAsc("item_id");

    return checkItemMapper.selectPage(page, wrapper);
  }

  @Override
  public List<CheckItem> getAllActiveCheckItems() {
    QueryWrapper<CheckItem> wrapper = new QueryWrapper<>();
    wrapper.eq("is_active", true)
        .orderByAsc("item_id");

    return checkItemMapper.selectList(wrapper);
  }

  @Override
  public boolean isItemCodeExists(String itemCode, Integer excludeId) {
    if (StrUtil.isBlank(itemCode)) {
      return false;
    }

    QueryWrapper<CheckItem> wrapper = new QueryWrapper<>();
    wrapper.eq("item_code", itemCode.trim());

    if (excludeId != null && excludeId > 0) {
      wrapper.ne("item_id", excludeId);
    }

    Long count = checkItemMapper.selectCount(wrapper);
    return count != null && count > 0;
  }

  @Override
  public boolean isItemInUse(Integer itemId) {
    if (itemId == null || itemId <= 0) {
      return false;
    }

    // 查询group_check_item表，检查是否有关联的检查组
    // 这里需要通过自定义SQL或者连表查询来实现
    // 暂时先返回false，后续在完善Mapper时实现
    return checkItemMapper.isItemInUse(itemId);
  }

  @Override
  public boolean toggleItemActive(Integer itemId, boolean isActive) {
    if (itemId == null || itemId <= 0) {
      return false;
    }

    CheckItem checkItem = new CheckItem();
    checkItem.setItemId(itemId);
    checkItem.setIsActive(isActive);
    checkItem.setUpdatedAt(LocalDateTime.now());

    int result = checkItemMapper.updateById(checkItem);
    return result > 0;
  }

  /**
   * 验证检查项数据
   * 
   * @param checkItem 检查项实体
   * @param isNew     是否为新增操作
   * @throws Exception 验证失败时抛出异常
   */
  private void validateCheckItem(CheckItem checkItem, boolean isNew) throws Exception {
    if (checkItem == null) {
      throw new Exception("检查项信息不能为空");
    }

    // 验证检查项代码
    if (StrUtil.isBlank(checkItem.getItemCode())) {
      throw new Exception("检查项代码不能为空");
    }

    String itemCode = checkItem.getItemCode().trim();
    if (!ValidationUtil.isValidCode(itemCode)) {
      throw new Exception("检查项代码格式不正确，只能包含字母、数字和下划线");
    }

    if (itemCode.length() > 50) {
      throw new Exception("检查项代码长度不能超过50个字符");
    }

    // 验证检查项名称
    if (StrUtil.isBlank(checkItem.getItemName())) {
      throw new Exception("检查项名称不能为空");
    }

    String itemName = checkItem.getItemName().trim();
    if (itemName.length() > 100) {
      throw new Exception("检查项名称长度不能超过100个字符");
    }

    // 验证参考值（可选）
    if (StrUtil.isNotBlank(checkItem.getReferenceVal()) &&
        checkItem.getReferenceVal().length() > 255) {
      throw new Exception("参考值长度不能超过255个字符");
    }

    // 验证单位（可选）
    if (StrUtil.isNotBlank(checkItem.getUnit()) &&
        checkItem.getUnit().length() > 50) {
      throw new Exception("单位长度不能超过50个字符");
    }

    // 设置处理后的值
    checkItem.setItemCode(itemCode);
    checkItem.setItemName(itemName);
    if (StrUtil.isNotBlank(checkItem.getReferenceVal())) {
      checkItem.setReferenceVal(checkItem.getReferenceVal().trim());
    }
    if (StrUtil.isNotBlank(checkItem.getUnit())) {
      checkItem.setUnit(checkItem.getUnit().trim());
    }
  }
}