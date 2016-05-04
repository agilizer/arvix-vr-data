package cn.arvix.vrdata.service;


import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class SimpleStaServiceImpl implements SimpleStaService{
	
	private AtomicLong  downloadSize =  new AtomicLong();

	@Override
	public long getDownloadSize(){
		return downloadSize.get();
	}

	@Override
	public void addDownloadSize(int size) {
		downloadSize.addAndGet(size);
	}
	
	

}
