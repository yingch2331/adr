package com.liyuan3210.adr.dto;

import java.io.Serializable;
import java.util.List;

public class RegionMemberDto implements Serializable {
	private static final long serialVersionUID = -531146497L;
	  private List<RegionMember> regions;

	  public void setRegions(List<RegionMember> regions)
	  {
	    this.regions = regions;
	  }

	  public List<RegionMember> getRegions()
	  {
	    return this.regions; } 
	  public static class RegionMember
	  implements Serializable { private static final long serialVersionUID = 1583471395L;
	    private String regionId;
	    private String parent;
	    private String name;
	    private String allName;
	    private String code;
	    private String rank;
	    private String gbCode;
	    private List<RegionMember> children;

	    public void setRegionId(String regionId) { this.regionId = regionId; } 
	    public void setParent(String parent) { this.parent = parent; } 
	    public void setName(String name) { this.name = name; } 
	    public void setAllName(String allName) { this.allName = allName; } 
	    public void setCode(String code) { this.code = code; } 
	    public void setRank(String rank) { this.rank = rank; } 
	    public void setGbCode(String gbCode) { this.gbCode = gbCode; } 
	    public void setChildren(List<RegionMember> children) { this.children = children;
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
}
