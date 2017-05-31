package com.sizzler.controller.rest;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sizzler.common.MediaType;
import com.sizzler.common.SourceType;
import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.log.LogMessageUtil;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.FileType;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.sizzler.UserConnectionConfig;
import com.sizzler.common.store.file.FileStoreStrategy;
import com.sizzler.common.utils.CodecUtil;
import com.sizzler.common.utils.FtpClientUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.common.utils.UuidUtil;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.annotation.MethodRemark;

@Controller
@Scope("prototype")
@RequestMapping("/file")
public class FileController {

  private static Logger logger = LoggerFactory.getLogger(FileController.class);

  /** 文件上传的操作标识 */
  private static final String OPERATE_UPLOAD_FILE = "uploadFile";
  /** 文件更新的操作标识 */
  private static final String OPERATE_UPDATE_FILE = "updateFile";
  /** 操作内容常量-userConnection中的配置文件 */
  private static final String OPERATE_INFO_KEY_CONFIG_MAP = "configMap";
  /** 操作内容常量-userEmail */
  private static final String OPERATE_INFO_KEY_USER_EMAIL = "userEmail";
  /** 操作内容常量-uploadFileToHdfsUsedTimes */
  private static final String OPERATE_INFO_KEY_UPLOAD_FILE_TO_HDFS_USED_TIMES = "uploadFileToHdfsUsedTimes";
  /** 操作内容常量-fileId */
  private static final String OPERATE_INFO_KEY_FILE_ID = "fileId";
  /** 操作内容常量-fileName */
  private static final String OPERATE_INFO_KEY_FILE_NAME = "fileName";
  /** 操作内容常量-fileInfoMap */
  private static final String OPERATE_INFO_KEY_FILE_INFO_MAP = "fileInfoMap";
  /** 操作内容常量-fileUploadPath */
  private static final String OPERATE_INFO_KEY_FILE_UPLOAD_PATH = "fileUploadPath";
  /** 操作内容常量-fileSize */
  private static final String OPERATE_INFO_KEY_FILE_SIZE = "fileSize";

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  @Autowired
  private ServiceFactory serviceFactory;

//  @Autowired // 暂时禁用ftp
  private FtpClientUtil ftpClient;

  @Autowired
  private FileStoreStrategy fileStoreStrategy;

  public FtpClientUtil getFtpClient() {
    return ftpClient;
  }

  public void setFtpClient(FtpClientUtil ftpClient) {
    this.ftpClient = ftpClient;
  }

  /**
   * 文件上传接口，规则如下：
   * <ul>
   * <li>1、文件大小不能超过50MB</li>
   * <li>2、不能上传空文件</li>
   * <li>3、只能上传Excel、CSV两种格式文件</li>
   * </ul>
   * 
   * @author you.zou
   * @date 2016年11月9日 上午10:14:53
   * @param file
   * @param sid
   * @param spaceId
   * @param nowFileName
   * @param updateFileName
   * @param request
   * @return
   */
  @RequestMapping(value = "excelFileUpload/{spaceId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark = OpreateConstants.Datasource.UPLOAD_FILE, domain = OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView uploadFile(@RequestBody MultipartFile file,
      @RequestParam(value = "sid", required = true) String sid,
      @PathVariable("spaceId") String spaceId,
      @RequestParam(value = "nowFileName", required = false) String nowFileName,
      @RequestParam(value = "updateFileName", required = false) String updateFileName,
      HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    LogMessage logMessage = new LogMessage();
    try {
      PtoneUser user = serviceFactory.getSessionContext().getLoginUser(sid);
      String uid = user.getPtId();
      String userEmail = user.getUserEmail();
      addOperateInfo(logMessage, OPERATE_UPLOAD_FILE, uid, userEmail);
      String fileName = URLDecoder.decode(CodecUtil.base64decode(nowFileName), "utf8");
      Map<String, String> fileInfoMap = this.getFileInfoByFileName(fileName);
      String fileType = fileInfoMap.get("fileType");

      // 每次上传都作为一个新增数据源
      String fileId = UuidUtil.generateUuid();
      String filePath = "/" + uid + "/" + DsConstants.DS_CODE_UPLOAD;
      String fileUploadPath = fileStoreStrategy.buildFilePath(filePath, fileId);
      logMessage.addOperateInfo(OPERATE_INFO_KEY_FILE_ID, fileId)
          .addOperateInfo(OPERATE_INFO_KEY_FILE_NAME, fileName)
          .addOperateInfo(OPERATE_INFO_KEY_FILE_INFO_MAP, fileInfoMap)
          .addOperateInfo(OPERATE_INFO_KEY_FILE_UPLOAD_PATH, fileUploadPath);

      uploadFileAndLogTimes(file, fileUploadPath, logMessage);

      UserConnection userConnection = new UserConnection();
      Map<String, Object> configMap = new HashMap<>();
      configMap.put(UserConnectionConfig.UploadConfig.PATH, fileUploadPath);
      configMap.put(UserConnectionConfig.UploadConfig.TYPE, fileType);
      configMap.put(UserConnectionConfig.UploadConfig.SPLITER, "");
      configMap.put(UserConnectionConfig.UploadConfig.FILE_ID, fileId);
      configMap.put(UserConnectionConfig.UploadConfig.FILE_NAME, fileName);

      addConfigMapToInfo(logMessage, configMap);

      userConnection.setUid(uid);
      userConnection.setSpaceId(spaceId);
      userConnection.setName(fileName + "-" + fileId);
      userConnection.setDsId(DsConstants.DS_ID_UPLOAD);
      userConnection.setDsCode(DsConstants.DS_CODE_UPLOAD);
      userConnection.setConfig(JSON.toJSONString(configMap));
      userConnection.setStatus(Constants.inReady);
      userConnection.setUpdateTime(System.currentTimeMillis());
      userConnection.setConnectionId(UuidUtil.generateUuid());
      userConnection.setUserName(user.getUserName());
      userConnection.setSourceType(SourceType.UserConnection.USER_CREATED);
      serviceFactory.getPtoneUserConnectionService().save(userConnection);

      jsonView.successPack(serviceFactory.getUploadService().getSourceVo(userConnection));
    } catch (Exception e) {
      LogMessageUtil.addErrorExceptionMessage(logMessage, e.getMessage());
      jsonView.errorPack("file upload error", e);
    } finally {
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return jsonView;
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value = "excelFileUpdate/{connectionId}/{sourceId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Transactional
  @MethodRemark(remark = OpreateConstants.Datasource.UPDATE_UPLOAD_FILE, domain = OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView updateUploadFile(@RequestBody MultipartFile file,
      @RequestParam(value = "sid", required = true) String sid,
      @PathVariable("connectionId") String connectionId, @PathVariable("sourceId") String sourceId,
      @RequestParam(value = "nowFileName", required = false) String nowFileName,
      @RequestParam(value = "updateFileName", required = false) String updateFileName,
      HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    LogMessage logMessage = new LogMessage();
    try {
      PtoneUser user = serviceFactory.getSessionContext().getLoginUser(sid);
      String uid = user.getPtId();
      String userEmail = user.getUserEmail();
      addOperateInfo(logMessage, OPERATE_UPDATE_FILE, uid, userEmail);
      UserConnection dbUserConnection = serviceFactory.getPtoneUserConnectionService().get(
          connectionId);
      UserConnectionSource source = serviceFactory.getDataSourceManagerService()
          .getConnectionSourceById(sourceId);
      if (dbUserConnection != null && source != null) {
        Map<String, Object> config = (Map<String, Object>) JSON.parse(dbUserConnection.getConfig());
        addConfigMapToInfo(logMessage, config);
        String path = (String) config.get(UserConnectionConfig.UploadConfig.PATH);

        uploadFileAndLogTimes(file, path, logMessage);

        // 更新数据
        long startUpadateDataTime = System.currentTimeMillis();
        UserConnectionSourceVo uiAcceptTable = serviceFactory.getDataSourceManagerService()
            .updateRemoteSourceData(source);
        logMessage.addOperateInfo("updateDataUsedTimes",
            (System.currentTimeMillis() - startUpadateDataTime));
        jsonView.successPack(uiAcceptTable);
      } else {
        logMessage.addOperateInfo("info", "update remote source <sourceId=" + sourceId
            + ", connectionId=" + connectionId + "> data failed, connection or source not exists.");
        jsonView.failedPack("update remote source <sourceId=" + sourceId + ", connectionId="
            + connectionId + "> data failed, connection or source not exists.");
      }

    } catch (Exception e) {
      LogMessageUtil.addErrorExceptionMessage(logMessage, e.getMessage());
      jsonView.errorPack("file update error", e);
    } finally {
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return jsonView;
  }

  /**
   * 检测该用户是否已上传了该文件
   * 
   * @param fileName
   * @param sid
   * @param request
   * @return
   */
  @RequestMapping(value = "existFile/{spaceId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView existFile(@RequestBody String fileName, @PathVariable("spaceId") String spaceId,
      @RequestParam(value = "sid", required = false) String sid, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      if (StringUtil.isBlank(fileName)) {
        jsonView.successPack(false);
      } else {
        JSONObject json = JSON.parseObject(fileName);
        String _fileName = json.getString("fileName");
        PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
        UserConnection dbUserConnection = serviceFactory.getPtoneUserConnectionService().get(
            _fileName, loginPtoneUser.getPtId(), DsConstants.DS_ID_UPLOAD, spaceId);
        if (dbUserConnection != null && Constants.validate.equals(dbUserConnection.getStatus())) {
          jsonView.successPack(true);
        } else {
          jsonView.successPack(false);
        }
      }
    } catch (Exception e) {
      jsonView.errorPack(" exist File error.", e);
    }
    return jsonView;
  }

  /**
   * 富文本上传图片接口
   * 
   * @date: 2016年6月20日
   * @author peng.xu
   */
  @RequestMapping(value = "imgUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView imgUpload(@RequestBody MultipartFile file,
      @RequestParam(value = "sid", required = true) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      Map<String, String> resultMap = new HashMap<String, String>();
      String imgUrl = null;
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      String uid = loginUser != null ? loginUser.getPtId() : "";
      String fileType = file.getContentType();
      String fileName = file.getOriginalFilename();
      if (fileType.contains("image/")) {
        String fileExt = "";
        if (fileName.contains(".")) {
          fileExt = fileName.substring(fileName.lastIndexOf("."));
        }
        String remotePath = dateFormat.format(System.currentTimeMillis());
        String remoteFileName = uid + "-" + UuidUtil.generateUuid() + fileExt;
        ftpClient.setBasePath(Constants.FTP_IMAGES_PATH);
        boolean result = ftpClient.upload(file.getInputStream(), remoteFileName, remotePath);
        if (result) {
          imgUrl = Constants.ftpServerDomain + "/" + Constants.FTP_IMAGES_PATH + "/" + remotePath
              + "/" + remoteFileName;
          resultMap.put("imgUrl", imgUrl);
          resultMap.put("contentType", file.getContentType());
          resultMap.put("size", file.getSize() + "");
          resultMap.put("originalFileName", fileName);
          jsonView.successPack(resultMap);
          logger.info("img<" + fileName + "> upload success: " + imgUrl);
        } else {
          logger.error("upload img<" + fileName + "> failed");
          jsonView.failedPack("upload img<" + fileName + "> failed");
        }
      } else {
        logger.error("img<" + fileName + "> type not support" + fileType);
        jsonView.failedPack("img<" + fileName + "> type not support" + fileType);
      }
    } catch (Exception e) {
      jsonView.errorPack("img upload error", e);
      logger.error("upload img failed");
    }
    return jsonView;
  }

  /**
   * 通过文件名获取fileType、fileExt<br>
   * 例如：fileName=测试.csv，返回：fileType=CSV, fileExt=.csv
   * 
   * @author you.zou
   * @date 2016年11月9日 上午10:51:15
   * @param fileName
   * @return
   */
  private Map<String, String> getFileInfoByFileName(String fileName) {
    Map<String, String> fileInfoMap = new HashMap<String, String>();
    String fileExt = "";
    String fileType = "";
    if (StringUtil.endsWithIgnoreCase(fileName, UserConnectionConfig.UploadConfig.SUFFIX_CSV)) {
      fileType = FileType.CSV.name();
      fileExt = UserConnectionConfig.UploadConfig.SUFFIX_CSV;
    } else if (StringUtil.endsWithIgnoreCase(fileName,
        UserConnectionConfig.UploadConfig.SUFFIX_XLSX)) {
      fileType = FileType.EXCEL.name();
      fileExt = UserConnectionConfig.UploadConfig.SUFFIX_XLSX;
    } else if (StringUtil
        .endsWithIgnoreCase(fileName, UserConnectionConfig.UploadConfig.SUFFIX_XLS)) {
      fileType = FileType.EXCEL.name();
      fileExt = UserConnectionConfig.UploadConfig.SUFFIX_XLS;
    } else if (StringUtil
        .endsWithIgnoreCase(fileName, UserConnectionConfig.UploadConfig.SUFFIX_TXT)) {
      fileType = FileType.TXT.name();
      fileExt = UserConnectionConfig.UploadConfig.SUFFIX_TXT;
    }
    fileInfoMap.put("fileExt", fileExt);
    fileInfoMap.put("fileType", fileType);
    return fileInfoMap;
  }

  /**
   * 将操作内容，UID，UserEmail放到LogMessage中
   * 
   * @author you.zou
   * @date 2016年9月21日 下午3:32:28
   * @param logMessage
   * @param operate
   * @param uid
   * @param userEmail
   */
  private void addOperateInfo(LogMessage logMessage, String operate, String uid, String userEmail) {
    logMessage.setOperate(operate);
    logMessage.setUid(uid);
    logMessage.addOperateInfo(OPERATE_INFO_KEY_USER_EMAIL, userEmail);
  }

  /**
   * 添加configMap到LogMessage中
   * 
   * @author you.zou
   * @date 2016年9月21日 下午3:46:07
   * @param logMessage
   * @param configMap
   */
  private void addConfigMapToInfo(LogMessage logMessage, Map<String, Object> configMap) {
    logMessage.addOperateInfo(OPERATE_INFO_KEY_CONFIG_MAP, configMap);
  }

  /**
   * 上传文件到hdfs<br>
   * 记录上传时间、文件大小
   * 
   * @author you.zou
   * @date 2016年9月21日 下午3:52:35
   * @param file
   * @param path
   * @param logMessage
   * @throws IOException
   */
  private void uploadFileAndLogTimes(MultipartFile file, String path, LogMessage logMessage)
      throws Exception {
    logMessage.addOperateInfo(OPERATE_INFO_KEY_FILE_SIZE, file.getSize());
    long startUploadTimes = System.currentTimeMillis();
    fileStoreStrategy.uploadFile(file.getInputStream(), path);
    logMessage.addOperateInfo(OPERATE_INFO_KEY_UPLOAD_FILE_TO_HDFS_USED_TIMES,
        (System.currentTimeMillis() - startUploadTimes));
  }

}
