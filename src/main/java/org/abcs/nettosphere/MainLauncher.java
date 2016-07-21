package org.abcs.nettosphere;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.nettosphere.Config;
import org.atmosphere.nettosphere.Handler;
import org.atmosphere.nettosphere.Nettosphere;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @作者 Mitkey
 * @时间 2016年7月21日 下午2:40:31
 * @类说明:
 * @版本 xx
 */
public class MainLauncher {

	public static void main(String[] args) {
		Nettosphere nettosphere = new Nettosphere.Builder().config(//
				new Config.Builder().//
						host("localhost").//
						port(8080).//
						resource("/test", new Handler() {
							@Override
							public void handle(AtmosphereResource resource) {
								try {
									AtmosphereRequest request = resource.getRequest();
									AtmosphereResponse response = resource.getResponse();
									request.setCharacterEncoding("UTF-8");
									response.setContentType("text/json;charset=UTF-8");

									JSONObject object = new JSONObject();
									object.put("query", request.getQueryString());
									object.put("method", request.getMethod());
									object.put("parames", request.getParameterMap());
									object.put("host", request.getRemoteHost());
									object.put("cookies", request.getCookies());

									try (InputStream inputStream = request.getInputStream()) {
										char[] cbuf = new char[inputStream.available()];
										InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
										inputStreamReader.read(cbuf);
										inputStream.close();
										object.put("ioContent", JSON.parse(String.valueOf(cbuf).trim()));
									} catch (Exception e) {
										e.printStackTrace();
									}

									object.put("time", new Date());
									response.write(JSON.toJSONString(object, true).getBytes()).flushBuffer();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}).//
						build())
				.build();
		nettosphere.start();
	}

}
