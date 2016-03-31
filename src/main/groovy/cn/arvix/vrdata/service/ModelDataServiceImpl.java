package cn.arvix.vrdata.service;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.arvix.vrdata.consants.ArvixMatterportConstants;
import cn.arvix.vrdata.domain.FileInfo;
import cn.arvix.vrdata.domain.ModelData;
import cn.arvix.vrdata.domain.ModelData.FetchStatus;
import cn.arvix.vrdata.repository.ModelDataRepository;
import cn.arvix.vrdata.util.AntZipUtil;
import cn.arvix.vrdata.util.JSONResult;
import cn.arvix.vrdata.util.StaticMethod;

@Service
public class ModelDataServiceImpl implements ModelDataService{
	private static final Logger log = LoggerFactory
			.getLogger(ModelDataServiceImpl.class);
	
	@Autowired
	ModelDataRepository modelDataRepository;
	@Autowired
	ConfigDomainService configDomainService;
	@Autowired
	JpaShareService jpaShareService;
	@Autowired
	FileService fileService;
	private String rootPath = "";
	@PostConstruct
	public void init(){
		Resource resource = new ClassPathResource("application.properties");
		try {
			rootPath=resource.getFile().getParentFile().getAbsolutePath()+"/files/upload";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("upload file store root path:--->>>>>>>>>>>>>>>\n"+rootPath);
	}
	@Override
	public Object getJsonFileDesc(String caseId) {
		String fileJson = modelDataRepository.findFileJsonByCaseId(caseId);
		Object result = null;
		if(fileJson!=null){
			JSONObject jsonObject = JSON.parseObject(fileJson);
			String baseUrl = configDomainService.getConfigString(ArvixMatterportConstants.SITE_URL)+"files/"+caseId+"/{{filename}}";
			jsonObject.put(BASE_URL,baseUrl );
			result = jsonObject;
		}
		return result;
	}
	@Override
	public ModelData findByCaseId(String caseId) {
		return modelDataRepository.findByCaseId(caseId);
	}
	@Override
	public JdbcPage list(int max, int offset) {
		Map<String,Object>  map = new HashMap<String,Object>();
		map.put("fetchStatus",FetchStatus.FINISH);
		String hql = "select m.title,m.caseId,f.storePath,m.description From ModelData m left join m.fileInfo f  where m.fetchStatus=:fetchStatus " ;
		String countHql = "select count(*) from ModelData where fetchStatus=:fetchStatus " ;
		JdbcPage jdbcPage = jpaShareService.queryForHql(hql, countHql, max,
				offset, map);
		return jdbcPage;
	}
	@Override
	public JdbcPage listAdmin(int max, int offset) {
		Map<String,Object>  map = new HashMap<String,Object>();
		map.put("fetchStatus",FetchStatus.FINISH);
		String hql = "select m.title,m.caseId,m.id,m.description,m.sourceUrl,m.fileTotalSize,m.fileSumCount,m.modelData,f.storePath,m.logoShow From "
				+ " ModelData m left join m.fileInfo f where m.fetchStatus=:fetchStatus " ;
		String countHql = "select count(*) from ModelData where fetchStatus=:fetchStatus " ;
		JdbcPage jdbcPage = jpaShareService.queryForHql(hql, countHql, max,
				offset, map);
		return jdbcPage;
	}
	@Override
	public Map<String, Object> update(String fieldName, Long id, String value) {
		Map<String, Object> result = StaticMethod.getResult();
		if("logoShow".equals(fieldName)){
			jpaShareService.executeForHql("update ModelData set "+fieldName+"=? where id=?", Boolean.valueOf(value),id);
		}else{
			jpaShareService.executeForHql("update ModelData set "+fieldName+"=? where id=?", value,id);
		}
		
		result.put(ArvixMatterportConstants.SUCCESS, true);
		return result;
	}
	@Override
	public JSONResult uploadModelData(ModelData modelData, MultipartFile zipFileData) {
		JSONResult jsonResult = new JSONResult();
		if(modelData.getCaseId()!=null){
			ModelData modelDataFromDb  = modelDataRepository.findByCaseId(modelData.getCaseId());
			if(modelDataFromDb!=null){
				jsonResult.setErrorCode(ArvixMatterportConstants.ERROR_CODE_EXIST);
			}else{
				Calendar now = Calendar.getInstance();
				modelData.setDateCreated(now);
				modelData.setLastUpdated(now);
				modelData.setFetchStatus(FetchStatus.FINISH);
				String saveDir =  configDomainService.getConfigString(ArvixMatterportConstants.FILE_STORE_PATH);
				log.info("unzip file ......."+"  saveDir : {}",saveDir);
				String uploadFilePath =saveDir+"upload/"
						+System.currentTimeMillis()+".zip";
				File fileDir = new File(saveDir+"upload/");
				if(!fileDir.exists()){
					fileDir.mkdirs();
				}
				File file = new File(uploadFilePath);
				try {
					zipFileData.transferTo(file);
					String unzipResult = AntZipUtil.readByApacheZipFile(uploadFilePath, saveDir);
					File fileDest = new File(rootPath+"/playerImages/"+modelData.getCaseId());
					FileUtils.deleteDirectory(fileDest);
					File srcFileDir = new File(saveDir+modelData.getCaseId()+"/playerImages/");
					if(srcFileDir.exists()){
						FileUtils.moveDirectoryToDirectory(srcFileDir
								,fileDest , true);
					}
					log.info("unzipResult {}",unzipResult);
					modelDataRepository.save(modelData);
					jsonResult.setSuccess(true);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("unzip file error", e);
				} 
			}
		}else{
			jsonResult.setErrorCode(CASE_ID_NULL);
		}
		log.info("jsonResult {}",jsonResult.toString());
		return jsonResult;
	}
	@Override
	public Map<String, Object> updatePhoto(Long id, MultipartFile photoData) {
		ModelData modelData = modelDataRepository.findOne(id);
		Map<String,Object> result = StaticMethod.getResult();
		if(modelData!=null){
			 FileInfo fileInfo = modelData.getFileInfo();
			 FileInfo fileInfoNew = fileService.saveFile(photoData, "/modelDataPhoto/");
			 if(null!=fileInfoNew){
				 jpaShareService.executeForHql("update ModelData set fileInfo = ? where id=?",fileInfoNew,id);
				 if(null!=fileInfo){
					 fileService.delFile(fileInfo);
				 }
				 result.put(ArvixMatterportConstants.DATA, fileInfoNew.getStorePath());
			 }
			 result.put(ArvixMatterportConstants.SUCCESS,true);
		}else{
			result.put(ArvixMatterportConstants.ERROR_MSG, "没有找到相关数据！");
			result.put(ArvixMatterportConstants.ERROR_CODE,404);
		}
		return result;
	}
	@Override
	public String getActiveReel(String caseId) {
		return modelDataRepository.findActiveReelByCaseId(caseId);
	}
	@Override
	public Map<String, Object> delete(Long id) {
		ModelData modelData = modelDataRepository.findOne(id);
		Map<String,Object> result = StaticMethod.getResult();
		if(modelData!=null){
			 FileInfo fileInfo = modelData.getFileInfo();
			 String caseId = modelData.getCaseId();
			 String saveDir =  configDomainService.getConfigString(ArvixMatterportConstants.FILE_STORE_PATH);
			 File delDir = new File(saveDir+caseId);
			 if(delDir.exists()){
				 try {
					FileUtils.deleteDirectory(delDir);
				} catch (IOException e) {
					e.printStackTrace();
					log.error("del file dir error {}",delDir.getAbsolutePath(), e);
				}
			 }
			 modelDataRepository.delete(modelData);
			 fileService.delFile(fileInfo);
			 result.put(ArvixMatterportConstants.SUCCESS,true);
		}else{
			result.put(ArvixMatterportConstants.ERROR_MSG, "没有找到相关数据！");
			result.put(ArvixMatterportConstants.ERROR_CODE,404);
		}
		return result;
	}

}
