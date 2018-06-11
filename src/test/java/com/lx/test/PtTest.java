package com.lx.test;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.golaxy.main.SingleScoreRequest;
import com.golaxy.util.ConfigData;
import com.golaxy.util.FileUtils;
import com.golaxy.util.SqlSessionUtil;

import cn.eversec.amc.utils.CertificateEncrypt;

public class PtTest {
	public static void main(String[] args) {
		FileUtils.writeFile(ConfigData.cronFile+"cron.txt", false, "≤‚ ‘crontab");
	}
}
