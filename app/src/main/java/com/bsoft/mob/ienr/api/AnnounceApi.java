package com.bsoft.mob.ienr.api;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.ParserModel;
import com.bsoft.mob.ienr.model.Statue;
import com.bsoft.mob.ienr.model.announce.AnnnouceSecondIdx;
import com.bsoft.mob.ienr.model.announce.AnnnouceThirdIdx;
import com.bsoft.mob.ienr.model.announce.AnnnouceTopIdx;
import com.bsoft.mob.ienr.model.announce.AnnounceItem;
import com.bsoft.mob.ienr.reflect.ReflectVo;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;

public class AnnounceApi extends BaseApi {

	public String url;

	public AnnounceApi(AppHttpClient httpClient, Context mContext) {
		super(httpClient, mContext);
	}

	public AnnounceApi(AppHttpClient httpClient, Context mContext, String url) {
		super(httpClient, mContext);
		this.url = url;
	}

	public static AnnounceApi getInstance(Context mContext)
			throws IllegalStateException {
		// Context localContext = AppContext.getContext();
		AnnounceApi api = (AnnounceApi) mContext
				.getSystemService("com.bsoft.mob.ienr.api.announce");
		if (api == null)
			api = (AnnounceApi) mContext.getApplicationContext()
					.getSystemService("com.bsoft.mob.ienr.api.announce");
		if (api == null)
			throw new IllegalStateException("api not available");
		return api;
	}

	/**
	 * 获取宣教访视大类,即宣教表单
	 */
	public ParserModel getXjDl(String areaId, String jgid, int sysType) {

		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			xml = getLocalXml("GetXjDl.xml");
		} else {
			String uri = new StringBuffer(url).append("GetXjDl?bqid=")
					.append(areaId).append("&jgid=").append(jgid)
					.append("&sysType=").append(sysType).toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
			if (!isRequestSuccess(httpString.first)) {
				ParserModel executVo = new ParserModel(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			 xml = httpString.second;

		}
		if (null != xml && xml.length() > 0) {
			return parser.parserTable(xml, new ReflectVo(AnnnouceTopIdx.class,
					"Table1"));
		} else {
			// 网络失败
			return new ParserModel(Statue.NET_ERROR);
		}
	}

	/**
	 * 获取所有级别宣教表单
	 * 
	 * @param brbq
	 *            病区
	 * @param zyh
	 *            住院号
	 * @param jgid
	 *            机构ID
	 * @param sysType
	 *            类型
	 * @return
	 */
	public ParserModel GetPatientTeacherInfo(String brbq, String zyh,
			String jgid, int sysType) {

		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			xml = getLocalXml("GetPatientTeacherInfo.xml");
		} else {
			String uri = new StringBuffer(url)
					.append("GetPatientTeacherInfo?brbq=").append(brbq)
					.append("&zyh=").append(zyh).append("&jgid=").append(jgid)
					.append("&sysType=").append(sysType).toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
			if (!isRequestSuccess(httpString.first)) {
				ParserModel executVo = new ParserModel(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserTable(xml, new ReflectVo(AnnnouceTopIdx.class,
					"Table1"),
					new ReflectVo(AnnnouceSecondIdx.class, "Table2"),
					new ReflectVo(AnnnouceThirdIdx.class, "Table3"));
		} else {
			// 网络失败
			return new ParserModel(Statue.NET_ERROR);
		}
	}

	/**
	 * 
	 * @param zyh
	 *            住院号
	 * @param brbq
	 *            病人病区
	 * 
	 * @param xjgh
	 *            宣教单号
	 * @param xjsj
	 *            宣教时间
	 * @param mxList
	 *            三级单号列表
	 * @param xjdx
	 *            宣教对象
	 * @param jgid
	 *            机构
	 * @param sysType
	 *            应用类型
	 * @return
	 */
	public ParserModel SavePatientTeacherInfo(String zyh, String brbq,
			String xjgh, String xjsj, String mxList, int xjdx, String jgid,
			int sysType) {

		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			xml = getLocalXml("SavePatientTeacherInfo.xml");
		} else {
			try {
				mxList = URLEncoder.encode(mxList, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return new ParserModel(Statue.PARSER_ERROR);
			}
			String uri = new StringBuffer(url)
					.append("SavePatientTeacherInfo?zyh=").append(zyh)
					.append("&brbq=").append(brbq).append("&xjdh=")
					.append("&ysbs=").append("&lbbs=").append("&xmbs=")
					.append("&xmbs=").append("&xjgh=").append(xjgh)
					.append("&xjsj=").append(xjsj).append("&mxList=")
					.append(mxList).append("&xjdx=").append(xjdx)
					.append("&jgid=").append(jgid).append("&sysType=")
					.append(sysType).toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
			if (!isRequestSuccess(httpString.first)) {
				ParserModel executVo = new ParserModel(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			 xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			try {
				boolean ok = perserSavePatientResponse(xml);
				if (ok) {
					return new ParserModel(Statue.SUCCESS);
				} else {
					return new ParserModel(Statue.ERROR);
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				return new ParserModel(Statue.PARSER_ERROR);
			} catch (IOException e) {
				e.printStackTrace();
				return new ParserModel(Statue.PARSER_ERROR);
			}
		} else {
			// 网络失败
			return new ParserModel(Statue.NET_ERROR);
		}
	}

	public boolean perserSavePatientResponse(String xmlStr)
			throws XmlPullParserException, IOException {

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new StringReader(xmlStr));
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if ("string".equals(parser.getName())) {

					String value = parser.nextText();
					if (!EmptyTool.isBlank(value)) {
						return true;
					} else {
						return false;
					}
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			event = parser.next();
		}
		return false;
	}

	/**
	 * 获取宣教二级表单
	 * 
	 * @param glxh
	 *            宣教序号
	 * @return
	 */
	public ParserModel getXjMx(String glxh, String jgid, int sysType) {

		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			xml = getLocalXml("GetXjMx.xml");
		} else {
			String uri = new StringBuffer(url).append("GetXjMx?glxh=")
					.append(glxh).append("&jgid=").append(jgid)
					.append("&sysType=").append(sysType).toString();
			HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
			if (!isRequestSuccess(httpString.first)) {
				ParserModel executVo = new ParserModel(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			 xml = httpString.second;
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserTable(xml, new ReflectVo(
					AnnnouceSecondIdx.class, "Table1"));
		} else {
			// 网络失败
			return new ParserModel(Statue.NET_ERROR);
		}
	}

	/**
	 * 获取宣教三级表单
	 * 
	 * @param glxh
	 *            宣教序号
	 * @return
	 */
	public ParserModel getXjMxInfo(String glxh, String lbxh, String jgid,
			int sysType) {

		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			xml = getLocalXml("GetXjMxInfo.xml");
		} else {
			String uri = new StringBuffer(url).append("GetXjMxInfo?glxh=")
					.append(glxh).append("&lbxh=").append(lbxh)
					.append("&jgid=").append(jgid).append("&sysType=")
					.append(sysType).toString();
			HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
			if (!isRequestSuccess(httpString.first)) {
				ParserModel executVo = new ParserModel(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			 xml = httpString.second;
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserTable(xml, new ReflectVo(
					AnnnouceThirdIdx.class, "Table1"));
		} else {
			// 网络失败
			return new ParserModel(Statue.NET_ERROR);
		}
	}

	/**
	 * 获取宣教记录
	 * 
	 * @param glxh
	 *            宣教序号
	 * @return
	 */
	public ParserModel GetXJDLinfo(String zyh, String date, String end,
			String jgid, int sysType) {

		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			xml = getLocalXml("GetXJDLinfo.xml");
		} else {
			String uri = new StringBuffer(url).append("GetXJDLinfo?zyh=")
					.append(zyh).append("&date=").append(date).append("&end=")
					.append(end).append("&jgid=").append(jgid)
					.append("&sysType=").append(sysType).toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
			if (!isRequestSuccess(httpString.first)) {
				ParserModel parserModel = new ParserModel(Statue.ERROR);
				parserModel.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return parserModel;
			}
			 xml = httpString.second;

		}
		if (null != xml && xml.length() > 0) {
			return parser.parserTable(xml, new ReflectVo(
					AnnnouceThirdIdx.class, "Table1"));
		} else {
			// 网络失败
			return new ParserModel(Statue.NET_ERROR);
		}
	}

	public ParserModel GetPatientTeacherQuery(String zyh, String brbq,
			String start, String end, String jgid, int sysType) {

		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			xml = getLocalXml("GetPatientTeacherQuery.xml");
		} else {

			String uri = new StringBuffer(url)
					.append("GetPatientTeacherQuery?zyh=").append(zyh)
					.append("&brbq=").append(brbq).append("&start=")
					.append(start).append("&end=").append(end).append("&jgid=")
					.append(jgid).append("&sysType=").append(sysType)
					.toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
			if (!isRequestSuccess(httpString.first)) {
				ParserModel executVo = new ParserModel(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			 xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserTable(xml, new ReflectVo(AnnounceItem.class,
					"Table1"));
		} else {
			// 网络失败
			return new ParserModel(Statue.NET_ERROR);
		}
	}
}
