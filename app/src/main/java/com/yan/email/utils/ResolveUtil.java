package com.yan.email.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

/**
 * Created by yinzhiyu on 2018-3-29.
 */

public class ResolveUtil {
    private MimeMessage mimeMessage = null;
    private StringBuffer mailContent = new StringBuffer();// 邮件内容
    private String charset;
    private boolean html;
    private ArrayList<String> attachments = new ArrayList<String>();
    private ArrayList<InputStream> attachmentsInputStreams = new ArrayList<InputStream>();

    public ResolveUtil(String charset) {
        this.charset = parseCharset(charset);
    }

    /**
     * 取得邮件标题
     *
     * @return String
     */
    public String getSubject(String subject) {
        try {
            if (subject.indexOf("=?gb18030?") != -1) {
                subject = subject.replace("gb18030", "gb2312");
            }
            subject = MimeUtility.decodeText(subject);
            if (this.charset == null) {
                subject = TranCharsetUtil.TranEncodeTOGB(subject);
            }
        } catch (Exception e) {
        }
        return subject;
    }

    /**
     * 解析字符集编码
     *
     * @param contentType
     * @return
     */
    private String parseCharset(String contentType) {
        if (!contentType.contains("charset")) {
            return null;
        }
        if (contentType.contains("gbk")) {
            return "GBK";
        } else if (contentType.contains("GB2312") || contentType.contains("gb18030")) {
            return "gb2312";
        } else {
            String sub = contentType.substring(contentType.indexOf("charset") + 8).replace("\"", "");
            if (sub.contains(";")) {
                return sub.substring(0, sub.indexOf(";"));
            } else {
                return sub;
            }
        }
    }

    /**
     * 取得邮件内容
     *
     * @throws Exception
     */
    public String getMailContent() throws Exception {
        compileMailContent((Part) mimeMessage);
        String content = mailContent.toString();
        if (content.indexOf("<html>") != -1) {
            html = true;
        }
        mailContent.setLength(0);
        return content;
    }

    /**
     * 解析邮件内容
     *
     * @param part
     * @throws Exception
     */
    private void compileMailContent(Part part) throws Exception {
        String contentType = part.getContentType();
        boolean connName = false;
        if (contentType.indexOf("name") != -1) {
            connName = true;
        }
        if (part.isMimeType("text/plain") && !connName) {
            String content = parseInputStream((InputStream) part.getContent());
            mailContent.append(content);
        } else if (part.isMimeType("text/html") && !connName) {
            html = true;
            String content = parseInputStream((InputStream) part.getContent());
            mailContent.append(content);
        } else if (part.isMimeType("multipart/*") || part.isMimeType("message/rfc822")) {
            if (part.getContent() instanceof Multipart) {
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContent(multipart.getBodyPart(i));
                }
            } else {
                Multipart multipart = new MimeMultipart(new ByteArrayDataSource(part.getInputStream(), "multipart/*"));
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContent(multipart.getBodyPart(i));
                }
            }
        } else if (part.getDisposition() != null && part.getDisposition().equals(Part.ATTACHMENT)) {
            // 获取附件
            String filename = part.getFileName();
            if (filename != null) {
                if (filename.indexOf("=?gb18030?") != -1) {
                    filename = filename.replace("gb18030", "gb2312");
                }
                filename = MimeUtility.decodeText(filename);
                attachments.add(filename);
                attachmentsInputStreams.add(part.getInputStream());
            }
            // Log.e("content", "附件：" + filename);
        }
    }

    /**
     * 解析流格式
     *
     * @param is
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    private String parseInputStream(InputStream is) throws IOException, MessagingException {
        StringBuffer str = new StringBuffer();
        byte[] readByte = new byte[1024];
        int count;
        try {
            while ((count = is.read(readByte)) != -1) {
                if (charset == null) {
                    str.append(new String(readByte, 0, count, "GBK"));
                } else {
                    str.append(new String(readByte, 0, count, charset));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }

}
