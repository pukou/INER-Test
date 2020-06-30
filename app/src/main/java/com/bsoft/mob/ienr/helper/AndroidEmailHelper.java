package com.bsoft.mob.ienr.helper;

import android.text.TextUtils;

import com.bsoft.mob.ienr.components.email.netease.NeteaseEmailFactory;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.louisgeek.javamail.EmailMessage;
import com.louisgeek.javamail.interfaces.IEmailFactory;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Created by classichu on 2018/3/12.
 */

public class AndroidEmailHelper {
    private static final String TO_EMAIL = "louisgeek@163.com";
    private static final String TO_EMAIL_2 = "louisgeek@126.com";
    private static final String CC_EMAIL = "classichu@qq.com";
    private static final String BCC_EMAIL = "louisgeek@126.com";


    public static void sendByNeteaseSmtp(String title, String content) {
        if (EmptyTool.isBlank(title) || EmptyTool.isBlank(content)) {
            return;
        }
        ExecutorServiceHelper.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    IEmailFactory emailFactory = new NeteaseEmailFactory();

                    EmailMessage emailMessage = EmailMessage.newBuilder()
                            .setTitle(title)
                            .setContent(content)
                            .setTOAddresses(new Address[]{new InternetAddress(TO_EMAIL)})
                            .build();

                    emailFactory.getProtocolSmtp().send(emailMessage);

                } catch (AddressException e) {
                    e.printStackTrace();
                }

            }
        });

    }


}
