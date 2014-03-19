package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.Constant;
import com.jibo.data.entity.DownloadPacketEntity;
import com.jibo.data.entity.PacketEntity;



/**
 * 版本信息数据
 */
public class VersionDataParser extends SoapDataPaser {

	public ArrayList<DownloadPacketEntity> list;
	public String action;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		list = new ArrayList<DownloadPacketEntity>();

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("getUpdateResult");
//				.getProperty("getUpdateNewResult");
		
		if (!"Null".equals(detail.getProperty(1).toString())) {
			action = Constant.FLAG_APP;
			String apkVersion = detail.getProperty(0).toString();
			String apkURL = detail.getProperty(1).toString();
			DownloadPacketEntity model = new DownloadPacketEntity();
			model.setAppVersion(apkVersion);
			model.setAppURL(apkURL);
			list.add(model);
		} else {
			DownloadPacketEntity model = null;
			System.out.println("detail.getProperty(2)   "+detail.getProperty(2));
			if (!"Null".equals(detail.getProperty(2).toString())) {
				if (Constant.FLAG_DIFF.equals(detail.getProperty(2)
						.toString())) {
					action = Constant.FLAG_DIFF;
					SoapObject lstSO = (SoapObject) detail.getProperty("LstDataPacket");
					ArrayList<PacketEntity> packetList = new ArrayList<PacketEntity>();
					model = new DownloadPacketEntity();
					for (int i = 0; i < lstSO.getPropertyCount(); i++) {
						SoapObject dataPacket = (SoapObject) lstSO
								.getProperty(i);
						PacketEntity packet = new PacketEntity();
						packet.setVersionCode(dataPacket.getProperty(0).toString());
						packet.setDownloadURL(dataPacket.getProperty(1) .toString());
						packetList.add(packet);
					}
					model.setDataPacket(packetList);
					model.setUpdateType(Constant.FLAG_DIFF);
				} else {
					SoapObject lstSO = (SoapObject) detail.getProperty("LstDataPacket");
					ArrayList<PacketEntity> packetList = new ArrayList<PacketEntity>();
					model = new DownloadPacketEntity();
					for (int i = 0; i < lstSO.getPropertyCount(); i++) {
						SoapObject dataPacket = (SoapObject) lstSO.getProperty(i);
						PacketEntity packet = new PacketEntity();
						packet.setVersionCode(dataPacket.getProperty(0) + "");
						packet.setDownloadURL(dataPacket.getProperty(1) + "");
						packetList.add(packet);
					}
					model.setDataPacket(packetList);
					model.setUpdateType(Constant.FLAG_FULL);
					action = Constant.FLAG_FULL;
				}
			} 
			list.add(model);
		}

		bSuccess =true;
	}

}
