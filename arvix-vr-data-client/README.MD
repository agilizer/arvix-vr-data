#项目说明

本项目为jar项目，可以发布为exe。main类为MainJFrame，布局由netbean完成。
实现功能为根据提交的信息抓取matterprot服务器文件数据，并同步到指定的arvix-matterport服务器。


#程序说明

抓取时多个url为并发抓取，不能多次提交抓取事件。先会抓取db存储时需要的信息，然后抓取文件数据，最后压缩并上传到服务器。

继续httpclient-mime的FileBody类，实现上传进度展示。


#发布

eclipse export即可。
