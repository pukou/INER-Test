package com.bsoft.mob.ienr.util;

import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.model.ParserModel;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.Statue;
import com.bsoft.mob.ienr.model.advice.execut.ExecutVo;
import com.bsoft.mob.ienr.model.advice.execut.ExecutVo.ExecutType;
import com.bsoft.mob.ienr.model.advice.execut.ResponseBodyInfo;
import com.bsoft.mob.ienr.reflect.ReflectMode;
import com.bsoft.mob.ienr.reflect.ReflectVo;
import com.fasterxml.jackson.core.type.TypeReference;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:14:25
 * @类说明 XML解析器
 */
@SuppressWarnings("all")
public class XmlParser {

    public ParserModel parserTable(String xml, ReflectVo... arrs) {
        ParserModel model = new ParserModel();
        Document document = null;
        try {
            document = DocumentHelper.parseText(xml);
        } catch (DocumentException e1) {
            e1.printStackTrace();
            model.statue = Statue.PARSER_ERROR;
            return model;
        }
        Element element1 = document.getRootElement();
        for (Iterator<Element> iterator2 = element1.elementIterator(); iterator2
                .hasNext(); ) {
            // 需要解析的element，第一个是状态，第二个是具体数据
            Element element3 = iterator2.next();
            if ("schema".equals(element3.getName())) {
                Iterator<Element> schemaIterator = element3.elementIterator();
                if (schemaIterator.hasNext()) {
                    Element schemaElement = schemaIterator.next();
                    model.setIsFalse(schemaElement.attributeValue("IsFalse"));
                    model.ExceptionMessage = schemaElement
                            .attributeValue("ExceptionMessage");
                    model.Message = schemaElement.attributeValue("Message");
                }
            } else if (null != arrs && arrs.length > 0
                    && "diffgram".equals(element3.getName())) {
                Iterator<Element> diffgramIterator = element3.elementIterator();
                if (diffgramIterator.hasNext()) {
                    Element diffgramElement = diffgramIterator.next();
                    if ("NewDataSet".equals(diffgramElement.getName())) {
                        for (Iterator<Element> tableIterator = diffgramElement
                                .elementIterator(); tableIterator.hasNext(); ) {
                            Element tableElement = tableIterator.next();
                            for (int i = 0; i < arrs.length; i++) {
                                if (arrs[i].tableName.equals(tableElement
                                        .getName())) {
                                    if (!model.tableMap
                                            .containsKey((arrs[i].tableName))) {
                                        ArrayList list = new ArrayList();
                                        model.tableMap.put(arrs[i].tableName,
                                                list);
                                    }
                                    try {

                                        model.tableMap.get(arrs[i].tableName)
                                                .add(ReflectMode.reflect(
                                                        arrs[i].className,
                                                        tableElement));
                                        tableElement.getText();
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InstantiationException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return model;
    }

    public ExecutVo parserExecutTable(String json) {
        ExecutVo model = new ExecutVo();
        Response<ResponseBodyInfo> response = null;
        if (null != json && json.length() > 2) {
            try {
                response = JsonUtil.fromJson(json,
                        new TypeReference<Response<ResponseBodyInfo>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                model.statue = Statue.PARSER_ERROR;
                return model;
            }
        }
        if (response != null) {
            if (response.ReType != 0 && response.ReType != -777) {
                model.statue = Statue.ERROR;
                model.ExceptionMessage = response.Msg;
                return model;
            }
            ResponseBodyInfo info = response.Data;
            ExecutType ty = ExecutType.valueOf(info.TableName);
            model.executType = ty;
            switch (ty) {
                case SHOW:
                    model.statue = Statue.SUCCESS;
                    model.ExceptionMessage = info.Message;
                    break;
                case ERROR:
                    model.setIsFalse("true");
                    model.ExceptionMessage = info.Message;
                    break;
                case CORE:
                    model.statue = Statue.SHOW_CORE;
                    model.ExceptionMessage = info.Message;//条码,前缀
                    break;
                case RE:
                    if (info.REModelList != null && !info.REModelList.isEmpty()) {
                        for (Object obj : info.REModelList) {
                            model.add(obj);
                        }
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    //FJXH 客户化
                    model.inArgument = info.inArgument;
                    model.selectResult = info.SyncData;
                    model.selectResultCode = response.ReType;
                    break;
                case SJ:
                    for (Object obj : info.SJModelList) {
                        model.add(obj);
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    break;
                case KF:
                    for (Object obj : info.KFModelList) {
                        model.add(obj);
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    break;
                case KFQX:
                    for (Object obj : info.KFModelList) {
                        model.add(obj);
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    break;
                case SYZT:
                    for (Object obj : info.SYZTModelList) {
                        model.add(obj);
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    break;
                case SYQX:
                    for (Object obj : info.SYZTModelList) {
                        model.add(obj);
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    break;
                case SYJS:
                    for (Object obj : info.SYZTModelList) {
                        model.add(obj);
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    break;
                case SY:
                    for (Object obj : info.SYModelList) {
                        model.add(obj);
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    break;
                case SQ:
                    for (Object obj : info.SQModelList) {
                        model.add(obj);
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    break;
                case ZSQX:
                    for (Object obj : info.ZSModelList) {
                        model.add(obj);
                    }
                    model.statue = Statue.SUCCESS;
                    //ADD 2018-4-17 20:08:17
                    model.ExceptionMessage = response.Msg;
                    break;
                default:
                    model.setIsFalse("true");
                    model.ExceptionMessage = info.Message;
                    break;
            }
        }
        return model;
    }

}
