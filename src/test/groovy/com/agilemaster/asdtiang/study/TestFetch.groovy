package com.agilemaster.asdtiang.study

@Grab(group='org.ccil.cowan.tagsoup', module='tagsoup', version='1.2' )
def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()  
def slurper = new XmlSlurper(tagsoupParser) 
def start=System.currentTimeMillis()
def storePath = "/tmp/mm/"
def urlPrefix = "https://matterport.com/gallery/"
def htmlParser = slurper.parse(urlPrefix)
println "startFetch........."
def pagesLink = []
def mmLink = []
htmlParser.'**'.findAll{ it.@class == 'image-wrap'}.each {//抓取分页地址   
    pagesLink.add(it.'@href'.text())
}
def linkTemp
def str = "https://my.matterport.com/show/?m=mmQifKW36kQ"
def subIndex = str.length()-1
pagesLink.each{//抓取每个分页mm图片链接 
     htmlParser = slurper.parse(it)
     htmlParser.'**'.findAll{ it.@class == 'wp3d-embed-wrap'}.each {   
		 linkTemp = it.iframe.'@src'.text()
		 linkTemp = linkTemp.substring(0, str)
		 println linkTemp
         mmLink.add(linkTemp)
     }
}
println mmLink

