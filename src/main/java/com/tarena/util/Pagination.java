package com.tarena.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * 分页类
 * @param <T>
 */
public class Pagination<T> {
  
  /**
   * 一页数据默认10条
   */
  private int pageSize = 10;
  
  /**
   * 当前页
   */
  private int pageNo;
  
  /**
   * 上一页
   */
  private int upPage;
  
  /**
   * 下一页
   */
  private int nextPage;
  
  /**
   * 一共多少条数据
   */
  private long total;
  
  /**
   * 一共有多少页
   */
  private long totalPage;
  
  /**
   * 数据集合
   */
  private List<T> rows = new ArrayList<T>();
  
  /**
   * 分页url
   */
  private String pageUrl;
  
  /**
   * 获取第一条记录位置
   * @return
   */
  public int getFirstResult() {
    return (this.getPageNo() - 1 ) * this.getPageSize();
  }
  
  /**
   * 获取最后一条记录位置
   * @return
   */
  public int getLastResult() {
    return this.getPageNo() * this.getPageSize();
  }
  
  

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getPageNo() {
    return pageNo;
  }

  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
  }

  public int getUpPage() {
    return upPage;
  }
  

  public long getTotalPage() {
    return totalPage;
  }

  public void setTotalPage() {
    this.totalPage = (this.total % this.pageSize > 0) ? (this.total / this.pageSize + 1) : (this.total / this.pageSize);
  }

  /**
   * 设置上一页
   * @param upPage
   */
  public void setUpPage() {
    this.upPage = (this.pageNo > 1)?(this.pageNo - 1) : this.pageNo;
  }

  public int getNextPage() {
    return nextPage;
  }

  public void setNextPage() {
    this.nextPage = (this.pageNo == this.totalPage) ? (this.pageNo) : (this.pageNo + 1);
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public List<T> getRows() {
    return rows;
  }

  public void setRows(List<T> rows) {
    this.rows = rows;
  }

  public String getPageUrl() {
    return pageUrl;
  }

  public void setPageUrl(String pageUrl) {
    this.pageUrl = pageUrl;
  }

  public Pagination(int pageNo, int pageSize, long total) {
    super();
    this.pageSize = pageSize;
    this.pageNo = pageNo;
    this.total = total;
    this.init();
  }

  /**
   * 初始化计算分页
   */
  private void init() {
     this.setTotalPage();//设置一共多少页
     this.setUpPage();//设置上一页
     this.setNextPage();//设置下一页
  }

  @Override
  public String toString() {
    return this.getPageUrl() + ":" + this.getFirstResult() + ":" + this.getLastResult() +":" + this.getPageNo() +":" + this.getPageSize();
  }
  
  
}
