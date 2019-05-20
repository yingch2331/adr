package com.liyuan3210.adr.dto;

import java.io.Serializable;
import java.util.List;

public class RegionMember implements Serializable {
	private static final long serialVersionUID = 1583471395L;
	  private String regionId;
	  private String parent;
	  private String name;
	  private String allName;
	  private String code;
	  private String rank;
	  private String gbCode;
	  private List<RegionMember> children;

	  public void setRegionId()
	  {
	    this.regionId = regionId; } 
	  public void setParent() { this.parent = parent; } 
	  public void setName() { this.name = name; } 
	  public void setAllName() { this.allName = allName; } 
	  public void setCode() { this.code = code; } 
	  public void setRank() { this.rank = rank; } 
	  public void setGbCode() { this.gbCode = gbCode; } 
	  public void setChildren() { this.children = children;
	  }

	  public String getRegionId()
	  {
	    return this.regionId; } 
	  public String getParent() { return this.parent; } 
	  public String getName() { return this.name; } 
	  public String getAllName() { return this.allName; } 
	  public String getCode() { return this.code; } 
	  public String getRank() { return this.rank; } 
	  public String getGbCode() { return this.gbCode; } 
	  public List<RegionMember> getChildren() { return this.children;
	  }
}
