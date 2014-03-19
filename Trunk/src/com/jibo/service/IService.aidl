package com.jibo.service;

import com.jibo.service.ICallback;

interface IService {
	void registerCallback(ICallback cb);
	void unregisterCallback(ICallback cb);
}