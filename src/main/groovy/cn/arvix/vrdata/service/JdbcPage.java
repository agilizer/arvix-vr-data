package cn.arvix.vrdata.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 分页信息bean，页面标识从0开始。
 * @author abel.lee
 *
 */
@SuppressWarnings("rawtypes")
public class JdbcPage  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7882299080501080103L;
	private long pageNumber;
    private long pagesAvailable;
    private long sumItem;
    private int max;
    private int offset;
    
	private List<?> pageItems = new ArrayList();

    public void setPageNumber(long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPagesAvailable(long pagesAvailable) {
        this.pagesAvailable = pagesAvailable;
    }


    public long getPageNumber() {
        return pageNumber;
    }

    public long getPagesAvailable() {
        return pagesAvailable;
    }

    public List getPageItems() {
        return pageItems;
    }

	public long getSumItem() {
		return sumItem;
	}

	public void setSumItem(long sumItem) {
		this.sumItem = sumItem;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setPageItems(List<?> pageItems) {
		this.pageItems = pageItems;
	}

	
	
	
    
}