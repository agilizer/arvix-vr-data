package cn.arvix.vrdata.service;

public interface ModelDataListService {
	
	JdbcPage list(int max,int offset,String search);

}
