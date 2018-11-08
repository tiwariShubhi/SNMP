package com.joy.app.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author joy
 * DTO
 */

public class SpaceUtilDrilldownObject {
	
	List<Map<String, SpaceUtilDrilldownMonthObject>> drilldown ;
	
	
	
	public SpaceUtilDrilldownObject() {
		
		this.drilldown = new ArrayList<Map<String, SpaceUtilDrilldownMonthObject>>();
	}

	public List<Map<String, SpaceUtilDrilldownMonthObject>> getDrilldown() {
		return drilldown;
	}

	public void setDrilldown(
			List<Map<String, SpaceUtilDrilldownMonthObject>> drilldown) {
		this.drilldown = drilldown;
	}

	@Override
	public String toString() {
		return "SpaceUtilDrilldownObject [drilldown=" + drilldown + "]";
	}
	
	
	
}