package com.agilemaster.asdtiang.study

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

Document doc = Jsoup.parse(new URL("http://localhost:8888/api/v1/isExist/sadfasdfads"), 30000);

println doc.body().text();