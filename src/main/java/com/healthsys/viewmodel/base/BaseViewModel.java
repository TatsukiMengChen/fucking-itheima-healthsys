package com.healthsys.viewmodel.base;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel基类
 * 提供属性变更通知机制，支持数据绑定
 */
public abstract class BaseViewModel {

  /**
   * 属性变更支持器
   */
  protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  /**
   * 添加属性变更监听器
   * 
   * @param listener 监听器
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  /**
   * 移除属性变更监听器
   * 
   * @param listener 监听器
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  /**
   * 为特定属性添加监听器
   * 
   * @param propertyName 属性名
   * @param listener     监听器
   */
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * 为特定属性移除监听器
   * 
   * @param propertyName 属性名
   * @param listener     监听器
   */
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
  }

  /**
   * 触发属性变更事件
   * 
   * @param propertyName 属性名
   * @param oldValue     旧值
   * @param newValue     新值
   */
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * 触发属性变更事件（布尔类型）
   * 
   * @param propertyName 属性名
   * @param oldValue     旧值
   * @param newValue     新值
   */
  protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * 触发属性变更事件（整型）
   * 
   * @param propertyName 属性名
   * @param oldValue     旧值
   * @param newValue     新值
   */
  protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
    propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * 设置字符串属性值并触发变更事件
   * 
   * @param currentValue 当前值的引用
   * @param newValue     新值
   * @param propertyName 属性名
   * @return 新值
   */
  protected String setProperty(String currentValue, String newValue, String propertyName) {
    String oldValue = currentValue;
    currentValue = newValue;
    firePropertyChange(propertyName, oldValue, newValue);
    return currentValue;
  }

  /**
   * 设置布尔属性值并触发变更事件
   * 
   * @param currentValue 当前值
   * @param newValue     新值
   * @param propertyName 属性名
   * @return 新值
   */
  protected boolean setProperty(boolean currentValue, boolean newValue, String propertyName) {
    boolean oldValue = currentValue;
    firePropertyChange(propertyName, oldValue, newValue);
    return newValue;
  }

  /**
   * 设置整型属性值并触发变更事件
   * 
   * @param currentValue 当前值
   * @param newValue     新值
   * @param propertyName 属性名
   * @return 新值
   */
  protected int setProperty(int currentValue, int newValue, String propertyName) {
    int oldValue = currentValue;
    firePropertyChange(propertyName, oldValue, newValue);
    return newValue;
  }

  /**
   * 设置对象属性值并触发变更事件
   * 
   * @param currentValue 当前值
   * @param newValue     新值
   * @param propertyName 属性名
   * @param <T>          对象类型
   * @return 新值
   */
  protected <T> T setProperty(T currentValue, T newValue, String propertyName) {
    T oldValue = currentValue;
    firePropertyChange(propertyName, oldValue, newValue);
    return newValue;
  }
}