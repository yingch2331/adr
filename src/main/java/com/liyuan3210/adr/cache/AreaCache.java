package com.liyuan3210.adr.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import  com.liyuan3210.adr.util.KnownChannelException;
import com.liyuan3210.adr.dto.RegionMemberDto;
import com.liyuan3210.adr.dto.RegionMemberDto.RegionMember;
import lombok.Getter;
import lombok.Setter;

@Component
public class AreaCache {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AreaCache.class);

	/**
	 * 缓存省市区信息，key=区（县）名称  value=省市区对象
	 */
	private Map<String, Area> areaCache = new HashMap<String, Area>();
	
	/**
	 * 区（县）对应多个省市区信息
	 */
	private Map<String, Set<Area>> areaMultiCache = new HashMap<String, Set<Area>>();
	
	/**
	 * 省市名称缓存（省、市）
	 */
	private Set<String> areaNameCache = new HashSet<String>();
	
	/**
	 * 省市区名称缓存（省+市+区）对应区（县）
	 */
	private Map<String, Area> areaDetailCache = new HashMap<String, Area>();
	
	/**
	 * 区（县）名称缓存
	 */
	private Set<String> districtNameSet = new HashSet<String>();
	
	/**
	 * 市名称缓存
	 */
	private Set<String> cityNameSet = new HashSet<String>();
	
	/**
	 * 省名称缓存
	 */
	private Set<String> provNameSet = new HashSet<String>();
	
	/**
	 * 省+市+区名称 - regionId缓存
	 */
	private Map<String, RegionId> regionIdMap = new HashMap<String, RegionId>();
	
	/**
	 * 城市-区（县）缓存
	 */
	private Map<String, Set<String>> cityDistrictMap = new HashMap<String, Set<String>>(); 
	
//	@Autowired
//	private RegionMemberRpcService regionRpcService;
	@Value("${city.path}")
	private String cityPath;
	private void initAreaCache() {
		System.out.println("######初始化省市区");
		System.out.println("######获取文件地址:"+cityPath);
//		String regionJson = regionRpcService.getResion();
		String regionJson = getTemplate(cityPath);
		System.out.println("######省市区:"+regionJson);
		if (regionJson == null) {
			throw new KnownChannelException("500", "缓存中没有找到省市区信息");
		}
		
		RegionMemberDto regionMemberDto = JSON.parseObject(regionJson, RegionMemberDto.class);
		List<RegionMember> provinceDtos = regionMemberDto.getRegions();
		// 省
		for (RegionMember provinceDto : provinceDtos) {
			areaNameCache.add(provinceDto.getName());
			provNameSet.add(provinceDto.getName());
			List<RegionMember> cityDtos = provinceDto.getChildren();
			
			// 市
			if (!CollectionUtils.isEmpty(cityDtos)) {
				for (RegionMember cityDto : cityDtos) {
					areaNameCache.add(cityDto.getName());
					cityNameSet.add(cityDto.getName());
					List<RegionMember> districtDtos = cityDto.getChildren();
					
					// 省市区元数据对象
					Area area = new Area();
					area.setProvince(provinceDto.getName());
					area.setCity(cityDto.getName());
					
					// 区（县）名称集合
					Set<String> districtSet = new HashSet<String>();
					cityDistrictMap.put(cityDto.getName(), districtSet);
					
					// 区（县）
					if (!CollectionUtils.isEmpty(districtDtos)) {
						for (RegionMember districtDto : districtDtos) {
							String districtName = districtDto.getName();
							// 省+市+区
							areaDetailCache.put(provinceDto.getName() + cityDto.getName() + districtName, new Area(provinceDto.getName(), cityDto.getName(), districtName));
							districtNameSet.add(districtName);
							districtSet.add(districtName);
							
							RegionId regionId = new RegionId();
							regionId.setProvinceId(provinceDto.getRegionId());
							regionId.setCityId(cityDto.getRegionId());
							regionId.setCountyId(districtDto.getRegionId());
							String key = provinceDto.getName() + cityDto.getName() + districtDto.getName();
							regionIdMap.put(key, regionId);
							
							// 设置区（县）
							area.setCounty(districtName);
							
							// 仅有两个城市有重复区（县）
							if (areaCache.containsKey(districtName)) {
								Set<Area> areas = new HashSet<Area>();
								areas.add(areaCache.get(districtName));
								areas.add(area);
								areaMultiCache.put(districtName, areas);
								// 移除原有的
								areaCache.remove(districtName);
								
							// 有多个城市有重复区（县）
							} else if (areaMultiCache.containsKey(districtName)) {
								Set<Area> areas = areaMultiCache.get(districtName);
								areas.add(area);
								
							// 没有重复区（县）
							} else {
								areaCache.put(districtDto.getName(), area);
							}
							
							// 如果是县级市
							if (districtName.endsWith("市")) {
								districtName = districtName.substring(0, districtName.length() - 1) + "区";
								
								districtNameSet.add(districtName);
								
								Area areaCopy = new Area();
								BeanUtils.copyProperties(area, areaCopy);
								areaCopy.setCounty(districtName);
								
								// 仅有两个城市有重复区（县）
								if (areaCache.containsKey(districtName)) {
									Set<Area> areas = new HashSet<Area>();
									areas.add(areaCache.get(districtName));
									areas.add(area);
									areaMultiCache.put(districtName, areas);
									// 移除原有的
									areaCache.remove(districtName);
									
								// 有多个城市有重复区（县）
								} else if (areaMultiCache.containsKey(districtName)) {
									Set<Area> areas = areaMultiCache.get(districtName);
									areas.add(area);
									
								// 没有重复区（县）
								} else {
									areaCache.put(districtDto.getName(), area);
								}
							}
						}
					} else {
						area.setProvince(provinceDto.getName());
						area.setCity(cityDto.getName());
						area.setCounty(cityDto.getName());
						
						// 直辖市，则区（县）设置为当前城市名称
						areaCache.put(cityDto.getName(), area);
						districtNameSet.add(cityDto.getName());
						districtSet.add(cityDto.getName());
						areaDetailCache.put(provinceDto.getName() + cityDto.getName(), new Area(provinceDto.getName(), cityDto.getName(), cityDto.getName()));
						
						RegionId regionId = new RegionId();
						regionId.setProvinceId(provinceDto.getRegionId());
						regionId.setCityId(cityDto.getRegionId());
						regionId.setCountyId(cityDto.getRegionId());
						String key = provinceDto.getName() + cityDto.getName() + cityDto.getName();
						regionIdMap.put(key, regionId);
					}
				}
			}
		}
		
		LOGGER.info("加载区（县）-省市区映射关系【" + areaCache.size() + "条】");
		LOGGER.info("加载省市【" + areaNameCache.size() + "条】");
	}
public static String getTemplate(String fileName){
        
        File templateFile = new File(fileName);
        //如果文件不存，则返回空
        if(!templateFile.exists()){
            return null;
        }
         
        BufferedReader bis = null;
        InputStream is = null;
        Reader fr = null;
        try {
             
            is = new FileInputStream(templateFile);
            fr = new InputStreamReader(is, "utf-8");
            bis = new BufferedReader(fr);
            StringBuffer xmlTemplate = new StringBuffer();
             
            int count;
            char[] buf = new char[1024];
            while((count = bis.read(buf)) > 0){
                xmlTemplate.append(buf, 0, count);
            }
            return xmlTemplate.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
             
            if(is != null) {
                 
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(fr != null) {
                 
                try {
                    fr.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                if(bis != null){
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
	/**
	 * 根据区（县）名称查找对应的省市区信息
	 * @param area
	 * @return
	 */
	public Area getArea(String county, List<String> words) {
		// 如果省市区缓存为空，则初始化缓存
		synchronized (areaCache) {
			if (CollectionUtils.isEmpty(areaCache)) {
				initAreaCache();
			}
		}
		
		if (areaCache.containsKey(county)) {
			Area area = areaCache.get(county);
			if (area != null) {
				// 文本是否包含省或市名称
				boolean hasProvCityName = false;
				for (String cityName : cityNameSet) {
					for (int i=words.size()-1; i>=0; i--) {
						if (cityName.startsWith(words.get(i))) {
							hasProvCityName = true;
						}
					}
				}
				if (!hasProvCityName) {
					for (String provName : provNameSet) {
						for (int i=words.size()-1; i>=0; i--) {
							if (provName.startsWith(words.get(i))) {
								hasProvCityName = true;
							}
						}
					}
				}
				
				if (hasProvCityName) {
					// 如果区（县）已经过时，则可能匹配到其他省和市，因此需要额外校验省和市
					String provCity = area.getProvince() + area.getCity();
					for (int i=words.size()-1; i>=0; i--) {
						if (provCity.contains(words.get(i))) {
							return area;
						}
					}
				} else {
					return area;
				}
			}
		}
		
		// 一个区（县）对应多个省市区信息
		if (areaMultiCache.containsKey(county)) {
			Set<Area> areas = areaMultiCache.get(county);
			// 优先匹配城市
			for (Area area : areas) {
				String city = area.getCity();
				for (int i=words.size()-1; i>=0; i--) {
					if (city.contains(words.get(i))) {
						return area;
					}
				}
			}
			// 城市未找到，则匹配省份
			for (Area area : areas) {
				String prov = area.getProvince();
				for (int i=words.size()-1; i>=0; i--) {
					if (prov.contains(words.get(i))) {
						return area;
					}
				}
			}
			
			LOGGER.error("【" + county + "】区（县）找到多个省市区地址，请输入省、市定位具体地址");
			throw new KnownChannelException("500", "【" + county + "】区（县）找到多个省市区地址，请输入省、市定位具体地址");
		}
		return null;
	}
	
	/**
	 * 判断是否包含省份或城市
	 * @param provCity
	 * @return
	 */
	public boolean containProvCity(String provCity) {
		for (String key : areaNameCache) {
			if (key.startsWith(provCity)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是否包含省份
	 * @param prov
	 * @return
	 */
	public boolean containProv(String prov) {
		for (String key : provNameSet) {
			if (key.startsWith(prov)) {
				return true;
			}
		}
		return false;
	}
	
	public Map<String, Area> getAreaCache() {
		return areaCache;
	}

	public Set<String> getAreaNameCache() {
		return areaNameCache;
	}

	public Map<String, Area> getAreaDetailCache() {
		return areaDetailCache;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public Set<String> getCityNameSet() {
		return cityNameSet;
	}

	public Map<String, Set<Area>> getAreaMultiCache() {
		return areaMultiCache;
	}

	public Set<String> getProvNameSet() {
		return provNameSet;
	}

	public Set<String> getDistrictNameSet() {
		return districtNameSet;
	}

	public Map<String, Set<String>> getCityDistrictMap() {
		return cityDistrictMap;
	}

	public Map<String, RegionId> getRegionIdMap() {
		return regionIdMap;
	}

	@Setter
	@Getter
	public static class Area {
		
		private String province;
		private String city;
		private String county;
		
		public Area() {}
		
		public Area(String province, String city, String county) {
			this.province = province;
			this.city = city;
			this.county = county;
		}
	}
	
	@Setter
	@Getter
	public static class RegionId {
		private String provinceId;
		private String cityId;
		private String countyId;
	}
	
}
