package com.agilemaster.asdtiang.study

import cn.arvix.vrdata.util.AntZipUtil


long start = System.currentTimeMillis()
println AntZipUtil.readByApacheZipFile("D:/home/abel/arvix-test-files/YXkDVj4ungu.zip","D:/home/abel/arvix-test-files/");
long end = System.currentTimeMillis()
println "use time:"+(end-start)

println UUID.randomUUID().toString()