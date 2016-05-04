package cn.arvix.vrdata.controller

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

import cn.arvix.vrdata.consants.ArvixDataConstants
import cn.arvix.vrdata.service.ConfigDomainService
import cn.arvix.vrdata.service.SimpleStaService


@Controller
@RequestMapping("/files")
class FilesController {
	private static final Logger log = LoggerFactory
	.getLogger(FilesController.class);
	@Autowired
	ConfigDomainService configDomainService;
	@Autowired
	SimpleStaService simpleStaService;
	@RequestMapping("/**")
	public String checkUsername(HttpServletRequest request,HttpServletResponse response) {
		response.setHeader("Accept-Ranges", "bytes");
		response.setHeader("Access-Control-Allow-Credentials",
			"true");
		response.setHeader("Access-Control-Allow-Methods","GET");
		response.setDateHeader("Age", 0);
		response.setHeader("Connection", "keep-alive");
		response.setHeader("Vary", "Origin, Access-Control-Request-Headers, Access-Control-Request-Method");
		response.setHeader("Via", "1.1 varnish, 1.1 varnish");
		response.setHeader("X-Cache", "MISS, MISS");
		response.setHeader("X-Cache-Hits", "0, 0");		
		response.setHeader("access-control-allow-origin", "*");
		
		
		String contentPath = request.getRequestURI() 
		if(contentPath.endsWith("&")){
			contentPath = contentPath.substring(0,contentPath.length()-1);
		}
		String downLoadPath = configDomainService.getConfig(ArvixDataConstants.FILE_STORE_PATH)+contentPath.replace("/files/","")
		//log.info("downLoadPath {}",downLoadPath)
		int startIndex = contentPath.indexOf("/files/")+7
		String caseId = contentPath.subSequence(startIndex,startIndex+11)
		log.info("caseId-->"+caseId)
		response.setContentType("text/html;charset=utf-8");
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;
		try {
			File file =  new File(downLoadPath);
			if(file.exists()){
				long fileLength = file.length();//application/octet-stream
				log.info("filename:"+contentPath+"    length:"+fileLength);
				if(downLoadPath.endsWith(".jpg")){
					response.setContentType("image/jpeg;");
				}else{
					response.setContentType("application/octet-stream");
					
				}
				response.setHeader("Content-Length", String.valueOf(fileLength));
				bis = new BufferedInputStream(new FileInputStream(downLoadPath));
				bos = new BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048];
				int bytesRead;
				int countRead = 0;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
					countRead = countRead +bytesRead;
				}
				bos.flush();
				log.info ("bytesRead--->"+countRead)
				simpleStaService.addDownloadSize(countRead)
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
		return null;
	}
}
