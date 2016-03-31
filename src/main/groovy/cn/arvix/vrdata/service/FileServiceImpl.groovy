package cn.arvix.vrdata.service
import java.awt.image.BufferedImage

import javax.annotation.PostConstruct
import javax.imageio.ImageIO
import javax.servlet.ServletContext

import org.apache.commons.io.FileUtils
import org.imgscalr.Scalr
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import cn.arvix.vrdata.been.ImageTargetSize
import cn.arvix.vrdata.domain.FileInfo
import cn.arvix.vrdata.repository.FileInfoRepository
import cn.arvix.vrdata.util.StaticMethod
import cn.arvix.vrdata.util.StaticMethod.JunjieFileType

import com.alibaba.druid.stat.TableStat.Mode
import com.sun.java.util.jar.pack.Package.Class.Method

@Service
class FileServiceImpl implements FileService{
	private static final Logger log = LoggerFactory
	.getLogger(FileServiceImpl.class);
	@Autowired
	ServletContext context;
	@Autowired
	FileInfoRepository fileInfoRepository;
	@Autowired
	JpaShareService japShareService;
	@Autowired
	UserService userService;

	private String rootPath = "";
	@PostConstruct
	public void init(){
		Resource resource = new ClassPathResource("application.properties");
		rootPath=resource.getFile().getParentFile().getAbsolutePath()+"/files/upload";
		log.info("upload file store root path:--->>>>>>>>>>>>>>>\n"+rootPath)
	}
	@Override
	public FileInfo saveFile(MultipartFile file,String path ) {
		//	WebApplicationContextUtils.
		FileInfo fileInfo = null;
		if(!file.isEmpty()){
			fileInfo =new FileInfo()
			log.info("  path->"+path)
			log.info(" rootPath->"+rootPath)
			def user = userService.currentUser()
			fileInfo.setAuthor(user)
			def filePath = rootPath+(path.startsWith("/")?path:("/"+path))
			log.info("file storePath"+filePath)
			def fileName = file.getOriginalFilename()
			fileInfo.setOriginalName(fileName)
			fileInfo.setFileSize(file.getSize())
			fileInfo.setFileType(StaticMethod.genFileType(fileName))
			// unique the file name
			int dotPos = fileName.lastIndexOf(".")
			def extension = (dotPos) ? fileName[dotPos + 1..fileName.length() - 1] : ""
			if(extension=="blob"){
				extension="jpg"
			}
			fileName = System.currentTimeMillis()+"." + extension
			def newFile = new File(filePath, fileName)
			fileInfo.setStorePath(path+"/"+fileName)
			fileInfo.setStoreFileName(fileName)
			def dir = new File(filePath)
			if (!dir.exists()) {
				try {
					dir.mkdirs()
				} catch (SecurityException se) {
					log.error("Creating Folders in: ${path} was failed")
					log.error(se.message)
					return null
				}
			}
			def freeSpace = dir.getUsableSpace()
			if (file.size > freeSpace) {
				log.error("File size exceeds the usable size: ${freeSpace.toString()}")
				return null
			}
			if (!dir.canWrite()) {
				// no, try to make it writable
				if (!dir.setWritable(true)) {
					log.error("The Folder ${filePath} is not writable, and can not set to writable!")
					return null
				}
			}
			file.transferTo(newFile)
			fileInfo = fileInfoRepository.save(fileInfo)
		}
		return fileInfo
	}
	@Override
	public void delFile(FileInfo fileInfo) {
		if(fileInfo){
			String path = fileInfo.getStorePath()
			def filePath = rootPath+(path.startsWith("/")?path:("/"+path))
			File file = new File(filePath);
			log.info("del file-->"+file.exists()+"--"+file.getAbsolutePath())
			file.delete()
			if(JunjieFileType.PHOTO ==fileInfo.fileType){
				def otherInfo = fileInfo.otherInfo
				if(otherInfo){
					def widths = otherInfo.split (",")
					def fileDir = file.getParentFile().getAbsolutePath()
					def fileTemp = null
					widths.each {
						fileTemp = new File(fileDir,it+"-"+fileInfo.storeFileName);
						log.info("del file-->"+fileTemp.exists()+"--"+fileTemp.getAbsolutePath())
						fileTemp.delete()
					}
				}
			}
			japShareService.delete(fileInfo)
		}

	}
	@Override
	public void delFile(Long fileInfoId) {
		delFile(fileInfoRepository.getOne(fileInfoId))
	}

	@Override
	public FileInfo saveImageFile(MultipartFile file, String path,
			ImageTargetSize... imageTargetSizes) {
		FileInfo fileInfo = saveFile(file,path);
		if(fileInfo){
			def filePath = rootPath+(path.startsWith("/")?path:("/"+path))
			def newFile = new File(filePath,fileInfo.storeFileName)
			def otherInfo = ""
			if (imageTargetSizes != null && imageTargetSizes.length > 0){
				BufferedImage source = ImageIO.read(newFile);
				for(ImageTargetSize t:imageTargetSizes){
					otherInfo = otherInfo+","+t.width
					BufferedImage target = Scalr.resize(source,Method.QUALITY,Mode.FIT_TO_WIDTH,t.width,t.height)
					FileOutputStream fileOut = null
					try{
						fileOut = new FileOutputStream(new File(filePath,t.width+'-'+fileInfo.storeFileName))
						ImageIO.write(target,"PNG", fileOut);
						fileOut.flush();
					}catch(e){
						log.error("compress error",e)
					}finally{
						if(null!=fileOut){
							fileOut.close()
						}
					}
				}
			}
			if(otherInfo.length()>1){
				otherInfo = otherInfo.substring(1)
			}
			japShareService.executeForHql("update FileInfo set fileType=?,otherInfo=? where id =?", JunjieFileType.PHOTO,otherInfo,fileInfo.getId())
		}
		return fileInfo;
	}
	@Override
	public String getRootPath() {
		return rootPath;
	}
	public FileInfo saveFileData(String data, String path) {
		FileInfo fileInfo = null;
		try {
			if(data){
				fileInfo =new FileInfo()
				data = data.replace("data:image/png;base64,", "");
				//data = data.replace("data%3Aimage%2Fpng%3Bbase64%", "")
				def user = userService.currentUser()
				fileInfo.setAuthor(user)
				def filePath = rootPath+(path.startsWith("/")?path:("/"+path))
				log.info("file storePath"+filePath)
				log.info("data size--->"+data.length())
				byte[] fileData = Base64.decodeBase64(data.getBytes())
				
				def fileName =System.currentTimeMillis() +".jpg"
				fileInfo.setOriginalName(fileName)
				fileInfo.setFileSize(fileData.length)
				fileInfo.setFileType(JunjieFileType.PHOTO)
				def newFile = new File(filePath, fileName)
				fileInfo.setStorePath(path+"/"+fileName)
				fileInfo.setStoreFileName(fileName)
				def dir = new File(filePath)
				if (!dir.exists()) {
					try {
						dir.mkdirs()
					} catch (SecurityException se) {
						log.error("Creating Folders in: ${path} was failed")
						log.error(se.message)
						return null
					}
				}
				def freeSpace = dir.getUsableSpace()
				if (fileData.size()> freeSpace) {
					log.error("File size exceeds the usable size: ${freeSpace.toString()}")
					return null
				}
				if (!dir.canWrite()) {
					// no, try to make it writable
					if (!dir.setWritable(true)) {
						log.error("The Folder ${filePath} is not writable, and can not set to writable!")
						return null
					}
				}
				FileUtils.writeByteArrayToFile(newFile, fileData)
				//BufferedImage source = ImageIO.read(newFile);
				//CompressJPEGFileUtil.compress(filePath+"/"+fileInfo.storeFileName, filePath+'compress-'+fileInfo.storeFileName)
				BufferedImage source = ImageIO.read(newFile);
				BufferedImage target = Scalr.resize(source,Method.QUALITY,Mode.FIT_TO_WIDTH,300,600)
				FileOutputStream fileOut = null
				try{
					fileOut = new FileOutputStream(new File(filePath,'compress-'+fileInfo.storeFileName))
					ImageIO.write(target,"PNG", fileOut);
					fileOut.flush();
				}catch(e){
					log.error("compress error",e)
				}finally{
					if(null!=fileOut){
						fileOut.close()
					}
				}
				
				fileInfo.setStoreFileName('compress-'+fileInfo.storeFileName)
				fileInfo.setStorePath(path+"/"+fileInfo.storeFileName)
				fileInfo = fileInfoRepository.save(fileInfo)
				log.info("1id------->"+fileInfo.getId())
			}
		} catch (Exception e) {
			e.printStackTrace();
			return fileInfo
		}
		return fileInfo
	}
	@Override
	public FileInfo saveImageFile(MultipartFile file, String path, long maxSize, int scaleWidth) {
		FileInfo fileInfo = saveFile(file,path);
		if(fileInfo){
			if (file.size>maxSize){
				def filePath = rootPath+(path.startsWith("/")?path:("/"+path))
				def newFile = new File(filePath,fileInfo.storeFileName)
				def otherInfo = ""
				if(scaleWidth==0){
					scaleWidth = 500;
				}
				BufferedImage source = ImageIO.read(newFile);
				int targetHeight =(int) source.getHeight()/(source.getWidth()*1.0/scaleWidth);
				otherInfo =scaleWidth +""
				BufferedImage target = Scalr.resize(source,Method.QUALITY,Mode.FIT_TO_WIDTH,scaleWidth,targetHeight)
				FileOutputStream fileOut = null
				try{
					fileOut = new FileOutputStream(newFile)
					ImageIO.write(target,"PNG", fileOut);
					fileOut.flush();
				}catch(e){
					log.error("compress error",e)
				}finally{
					if(null!=fileOut){
						fileOut.close()
					}
				}
				japShareService.executeForHql("update FileInfo set fileType=?,otherInfo=?,fileSize=? where id =?", JunjieFileType.PHOTO,otherInfo,
					newFile.length(),fileInfo.getId())
			}
			
		}
		return fileInfo;
	}
}
