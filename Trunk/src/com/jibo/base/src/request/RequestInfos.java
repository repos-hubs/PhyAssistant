package com.jibo.base.src.request;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.jibo.base.src.request.config.DescInfo;

public class RequestInfos {
	public RequestInfos() {
	}

	public RequestInfos(Map<DescInfo, Integer> requests) {
		super();
		this.requests = requests;
	}

	public Comparator<DescInfo> intKey = new Comparator<DescInfo>() {

		public int compare(DescInfo arg0, DescInfo arg1) {
			return arg0.idx - arg1.idx;
		}
	};
	public Map<DescInfo, Integer> requests = new TreeMap<DescInfo, Integer>(
			intKey);

	public <T extends DescInfo> void putSrc(T soap, int value) {
		soap.idx = requests.size();
		requests.put(soap, value);
	}

}