/**
 *
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.apdplat.search.paper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * 今日早报
 * @author 杨尚川
 */
public class JRZBPaperCollector  extends AbstractPaperCollector{
    private static final String paperName = "今日早报";
    private static final String paperPath = "http://jrzb.zjol.com.cn/";
    private static final String url = paperPath+"html/";
    private static final String hrefPrefix = paperPath+"images/";
    private static final String start = "node_142.htm";
    private static final String pdfCssQuery = "html body div.main div.main-ednav div.main-ednav-nav dl dd img";
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM/dd/"); 
    
    @Override
    public List<File> collect(Date date) {
        List<String> hrefs = new ArrayList<>();
        try {
            LOG.debug("url: "+url);
            String paper = url + sf.format(date) + start;
            LOG.debug("paper: "+paper);
            Document document = Jsoup.connect(paper).get();
            
            LOG.debug("pdfCssQuery: " + pdfCssQuery);
            Elements elements = document.select(pdfCssQuery);
            for(Element element : elements){
                String href = element.attr("filepath");
                if(href != null && href.endsWith(".jpg")){
                    LOG.debug("报纸链接："+href);
                    href = href.replace("../../../", "");
                    LOG.debug("报纸链接："+href);
                    hrefs.add(paperPath+href);
                }else{
                    LOG.debug("不是报纸链接："+href);
                }
            }            
        } catch (IOException ex) {
            LOG.error("采集出错",ex);
        }
        return downloadPaper(hrefs);
    }
    @Override
    protected String getPath(String href) {
        String path = href.replace(hrefPrefix, "");
        String[] attrs = path.split("/");
        StringBuilder str = new StringBuilder();
        str.append(paperName)
            .append(File.separator)
            .append(attrs[0])
            .append(File.separator)
            .append(attrs[1]);
        return str.toString();
    }
    @Override
    protected String getFile(String href) {
        String path = href.replace(hrefPrefix, "");
        String[] attrs = path.split("/");
        String file = attrs[2];
        return file;
    }
    public static void main(String[] args) {
        new JRZBPaperCollector().run();
    }
}