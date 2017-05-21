package com.sizzler.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sizzler.common.MediaType;
import com.sizzler.common.SourceType;
import com.sizzler.common.exception.ErrorCode;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.common.sizzler.MetaContentNode;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.dto.DsContentView;
import com.sizzler.domain.ds.dto.UIDataBaseConnection;
import com.sizzler.domain.ds.dto.UiAccountConnection;
import com.sizzler.domain.ds.dto.UserAccountSource;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.provider.common.UpdateDataResponse;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.annotation.MethodRemark;

/**
 * @ClassName: SourceManagerController
 * @Description:各种数据源管理api.
 */
@Controller
@Scope("prototype")
@RequestMapping("/sourceManager")
public class SourceManagerController {
  
  private Logger logger = LoggerFactory.getLogger(SourceManagerController.class);

  @Autowired
  private ServiceFactory serviceFactory;

  /**
   * @Description: 从编辑器中保远端存数据到本地.（在外面update已经连接的GD文件时，也走该接口）
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "saveDataSource", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark= OpreateConstants.Datasource.SAVE_DATA_SOURCE,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView saveDataSource(@RequestBody UserConnectionSourceVo acceptTable,@RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      /*
       * String fileName = acceptTable.getName();
       * if(StringUtil.endsWithIgnoreCase(fileName,UserConnectionConfig.UploadConfig.SUFFIX_XLSX)){
       * fileName = fileName.replace(UserConnectionConfig.UploadConfig.SUFFIX_XLSX, "_edit" +
       * UserConnectionConfig.UploadConfig.SUFFIX_XLSX); }else
       * if(StringUtil.endsWithIgnoreCase(fileName, UserConnectionConfig.UploadConfig.SUFFIX_XLS)){
       * fileName = fileName.replace(UserConnectionConfig.UploadConfig.SUFFIX_XLS, "_edit" +
       * UserConnectionConfig.UploadConfig.SUFFIX_XLS); }
       */
      // String fileUploadPath = Constants.hdfsClusterPath + "/ptone/" + acceptTable.getUid() +
      // "/upload/" + fileName;
      UserConnectionSourceVo uiAcceptTable =
          serviceFactory.getDataSourceManagerService().saveOrUpdateEditorDataToFile(acceptTable);
      /*
       * if(null != acceptTable && null != acceptTable.getDsId() &&
       * acceptTable.getDsId().equals(4l)){ uiAcceptTable =
       * serviceFactory.getDataSourceManagerService
       * ().saveOrUpdateEditorDataOfLocalFile(acceptTable); }else{ uiAcceptTable =
       * serviceFactory.getDataSourceManagerService().saveOrUpdateEditorDataToFile(acceptTable); }
       */
      jsonView.successPack(uiAcceptTable);
    } catch (Exception e) {
      jsonView.errorPack(JSON.toJSONString(acceptTable) + " | save Excel error.", e);
    }
    return jsonView;
  }

  /**
   * 根据tableId更新远端文件数据.
   * @return
   * @date: 2016年6月3日
   * @author peng.xu
   */
  @RequestMapping(value = "updateRemoteSourceData/{tableId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView updateRemoteSourceDataByTableId(@PathVariable("tableId") String tableId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      UserConnectionSource source =
          serviceFactory.getDataSourceManagerService().getUserConnectionSourceByTableId(tableId);
      if (source != null) {
        UserConnectionSourceVo uiAcceptTable =
            serviceFactory.getDataSourceManagerService().updateRemoteSourceData(source);
        jsonView.successPack(uiAcceptTable);
      } else {
        jsonView.failedPack("update remote source <tableId=" + tableId
            + "> data failed, source not exists.");
      }
    } catch (Exception e) {
      jsonView.errorPack("update remote source <tableId=" + tableId + "> data error.", e);
    }
    return jsonView;
  }


  /**
   * @Description: 获取当前用户下当前dsId下的所有有效数据源连接.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "getDataSourceView/{dsId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Deprecated
  public JsonView getDataSourceView(@PathVariable("dsId") String dsId, HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("dsId", new Object[] {dsId});
      paramMap.put("uid", new Object[] {loginUser.getPtId()});
      paramMap.put("status", new Object[] {Constants.validate});

      Map<String, String> orderMap = new HashMap<>();
      orderMap.put("updateTime", "desc");
      List<UserConnection> connectionsResults =
          serviceFactory.getPtoneUserConnectionService().findByWhere(paramMap, orderMap);
      jsonView.successPack(connectionsResults);
    } catch (Exception e) {
      jsonView.errorPack("get Data Source View error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取当前空间下当前dsId下的所有有效数据源连接.
   * @author: peng.xu
   */
  @RequestMapping(value = "getSpaceDataSourceView/{spaceId}/{dsId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSpaceDataSourceView(@PathVariable("spaceId") String spaceId,
      @PathVariable("dsId") Long dsId, HttpServletRequest request, @RequestParam(value = "sid",
          required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      // Map<String,Object[]> paramMap = new HashMap<>();
      // paramMap.put("dsId",new Object[]{dsId});
      // paramMap.put("spaceId",new Object[]{spaceId});
      // paramMap.put("status",new Object[]{Constants.validate});
      //
      // Map<String,String> orderMap = new HashMap<>();
      // orderMap.put("updateTime","desc");
      // List<UserConnection> connectionsResults =
      // serviceFactory.getPtoneUserConnectionService().findByWhere(paramMap,orderMap);

      List<UserConnection> connectionsResults =
          serviceFactory.getPtoneUserConnectionService().findSpaceUserConnectionList(spaceId, dsId);

      jsonView.successPack(connectionsResults);
    } catch (Exception e) {
      jsonView.errorPack("get Data Source View error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取当前用户下当前dsId下的所有保存到ptone的文件列表.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @Deprecated
  @RequestMapping(value = "getAuthAccount/{connectionId}/{dsId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getAuthAccount(@PathVariable("dsId") String dsId,
      @PathVariable("connectionId") String connectionId, HttpServletRequest request, @RequestParam(
          value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      List<UserAccountSource> userConnectionResults =
          serviceFactory.getDataSourceManagerService().getAuthAccount(connectionId,
              loginUser.getPtId(), dsId);
      jsonView.successPack(userConnectionResults);
    } catch (Exception e) {
      jsonView.errorPack("get Auth Account error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取当前空间下当前dsId下的所有保存到ptone的文件列表.
   * @author: peng.xu
   */
  @RequestMapping(value = "getSpaceAuthAccount/{spaceId}/{connectionId}/{dsId}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getAuthAccount(@PathVariable("spaceId") String spaceId,
      @PathVariable("dsId") String dsId, @PathVariable("connectionId") String connectionId,
      HttpServletRequest request, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      List<UserAccountSource> userConnectionResults =
          serviceFactory.getDataSourceManagerService().getSpaceAuthAccount(spaceId, connectionId,
              dsId);
      jsonView.successPack(userConnectionResults);
    } catch (Exception e) {
      jsonView.errorPack("get Auth Account error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取widget的档案列表.
   * @date: 2015/12/25
   * @author: zhangli
   */
  /*@RequestMapping(value = "getWidgetAuthAccount/{connectionId}/{dsId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Deprecated
  public JsonView getWidgetAuthAccount(@PathVariable("dsId") String dsId,
      @PathVariable("connectionId") String connectionId, HttpServletRequest request, @RequestParam(
          value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      List<MetaContentNode> metaContentNodeList =
          serviceFactory.getDataSourceManagerService().getWidgetAuthAccount(connectionId,
              loginUser.getPtId(), dsId);
      jsonView.successPack(metaContentNodeList);
    } catch (Exception e) {
      jsonView.errorPack("get Widget Auth Account error", e);
    }
    return jsonView;
  }*/

  /**
   * @Description: 获取widget的档案列表(当前space下).
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "getSpaceWidgetAuthAccount/{spaceId}/{connectionId}/{dsId}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSpaceWidgetAuthAccount(@PathVariable("spaceId") String spaceId,
      @PathVariable("dsId") String dsId, @PathVariable("connectionId") String connectionId,
      HttpServletRequest request, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      List<MetaContentNode> metaContentNodeList =
          serviceFactory.getDataSourceManagerService().getSpaceWidgetAuthAccount(spaceId,
              connectionId, dsId);
      jsonView.successPack(metaContentNodeList);
    } catch (Exception e) {
      jsonView.errorPack("get Widget Auth Account error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取编辑所选文件的数据.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "getDataSourceEditView/{connectionId}/{sourceId}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark= OpreateConstants.Datasource.EDIT_TABLE,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView getDataSourceEditView(@PathVariable("connectionId") String connectionId,
      @PathVariable("sourceId") String sourceId,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      UserConnectionSourceVo uIAcceptTable =
          serviceFactory.getDataSourceManagerService().getEditorDataByConnectionId(connectionId,
              sourceId, false,false, false);
      jsonView.successPack(uIAcceptTable);
    } catch (Exception e) {
      jsonView.errorPack("get Data Source View error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取当前数据源连接下的文件树结构.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "getDataSourceAccountSchema/{connectionId}/{folderId:.+}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getDataSourceAccountSchema(@PathVariable("connectionId") String connectionId,
      @PathVariable("folderId") String folderId,
      @RequestParam(value = "refresh", required = false) boolean refresh) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      String json =
          serviceFactory.getDataSourceManagerService().getDataSourceAccountSchema(connectionId,
              folderId, refresh);
      jsonView.successPack(json);
    } catch (Exception e) {
      jsonView.errorPack("get Data Source Account Schema error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取当前连接下某个文件的远端数据.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "pullRemoteData/{connectionId}/{folderId}/{fileId:.+}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark= OpreateConstants.Datasource.PULL_REMOTE_DATA,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView pullRemoteData(@PathVariable("connectionId") String connectionId,
      @PathVariable("folderId") String folderId, @PathVariable("fileId") String fileId,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      UserConnectionSourceVo uiAcceptTable =
          serviceFactory.getDataSourceManagerService().pullRemoteData(connectionId, folderId,
              fileId);
      jsonView.successPack(uiAcceptTable);
    } catch (Exception e) {
      jsonView.errorPack("pull Remote Data error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取当前用户下所有授过权的账户个数和文件个数.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "getUserDsContentView", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Deprecated
  public JsonView getUserDsContentView(@RequestParam(value = "sid", required = false) String sid,
      HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      List<DsContentView> dsContentViews =
          serviceFactory.getDataSourceManagerService().getUserDsContentView(loginUser.getPtId());
      jsonView.successPack(dsContentViews);
    } catch (Exception e) {
      jsonView.errorPack("get User Ds Content View error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取当前空间下所有授过权的账户个数和文件个数.
   * @author: peng.xu
   */
  @RequestMapping(value = "getSpaceDsContentView/{spaceId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSpaceDsContentView(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("spaceId") String spaceId, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      List<DsContentView> dsContentViews =
          serviceFactory.getDataSourceManagerService().getSpaceDsContentView(spaceId,loginUser.getSource());
//      List<PtoneSysPermission> sysPermissions = PermissionUtil.getUserPermissionBySpaceId(serviceFactory,spaceId,sid);
//      dsContentViews =
//          PermissionUtil.validateDataSourcePermission(dsContentViews, sysPermissions, "dsCode","reverse");
//      PermissionUtil.processDsInfoPlus(dsContentViews);
      jsonView.successPack(dsContentViews);
    } catch (Exception e) {
      jsonView.errorPack("get Space Ds Content View error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 获取当前连接下的config信息.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "getUserConnectionConfig/{connectionId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getUserConnectionConfig(
      @RequestParam(value = "sid", required = false) String sid, HttpServletRequest request,
      @PathVariable("connectionId") String connectionId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      UserConnection userConnection =
          serviceFactory.getPtoneUserConnectionService().get(connectionId);
      //为了避免前端有过多的修改，所以在这里解析出config内容，然后往里面添加dsCode信息
      String config = userConnection.getConfig();
      Map<String, String> configMap = JSON.parseObject(config, Map.class);
      configMap.put("dsCode", userConnection.getDsCode());
      jsonView.successPack(JSON.toJSONString(configMap));
    } catch (Exception e) {
      jsonView.errorPack("get User Connection Config error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 某个连接下所有widget数.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "getAccountWidgetCount/{connectionId}/{dsId}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getAccountWidgetCount(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("connectionId") String connectionId, @PathVariable("dsId") String dsId,
      HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      long count =
          serviceFactory.getDataSourceManagerService().getAccountWidgetCount(connectionId,
              loginUser.getPtId(), dsId);
      jsonView.successPack(count);
    } catch (Exception e) {
      jsonView.errorPack("get Account Widget Count error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 某个文件下所有widget数.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "getSourceWidgetCount/{sourceId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSourceWidgetCount(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("sourceId") String sourceId, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      long count =
          serviceFactory.getDataSourceManagerService().getSourceWidgetCount(sourceId,
              loginUser.getPtId());
      jsonView.successPack(count);
    } catch (Exception e) {
      jsonView.errorPack("get Source Widget Count error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 断开某个连接.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "delUserDsConnection/{connectionId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark= OpreateConstants.Datasource.DEL_USER_DS_CONNECTION,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView delUserDsConnection(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("connectionId") String connectionId, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      UserConnection userConnection =
          serviceFactory.getPtoneUserConnectionService().get(connectionId);
      // userConnection.setId(Long.parseLong(connectionId));
      // userConnection.setStatus(Constants.inValidate);
      // serviceFactory.getPtoneUserConnectionService().update(userConnection);
      /*
       * serviceFactory.getPtoneUserConnectionService().delete(userConnection); Map<String,Object[]>
       * paramMap = new HashMap<>(); paramMap.put("connectionId",new Object[]{connectionId});
       */
      serviceFactory.getDataSourceManagerService().deleteConnectionInfo(userConnection);
      jsonView.messagePack("delUserDsConnection success");
    } catch (Exception e) {
      jsonView.errorPack("delUserDsConnection error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 删除某个连接下保存的文件.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "delSavedFile/{sourceId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark= OpreateConstants.Datasource.DEL_SAVED_FILE,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView delSavedFile(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("sourceId") String sourceId, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      serviceFactory.getDataSourceManagerService().delSavedFile(sourceId);
      jsonView.messagePack("delSavedFile success");
    } catch (Exception e) {
      jsonView.errorPack("delSavedFile error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 从远端刷新文件. 该方法已经不再使用
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "refreshFileFromRemote", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark= OpreateConstants.Datasource.REFRESH_FILE_FROM_REMOTE,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView refreshFileFromRemote(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody UserConnectionSourceVo acceptTable, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      UpdateDataResponse updateDataResponse =
          serviceFactory.getDataSourceManagerService().refreshFileFromRemote(acceptTable);
      boolean updateStatus = updateDataResponse.getUpdateStatus();
      if (updateStatus) {
        boolean hasDeleted = updateDataResponse.hasDeleted();
        boolean hasChanged = updateDataResponse.hasChanged();
        if (hasDeleted) {
          jsonView.messagePack("file has deleted in the remote.");
          return jsonView;
        }
        if (hasChanged) {
          jsonView.messagePack("文件已和远端同步.");
          return jsonView;
        } else {
          jsonView.messagePack("远端文件无变化.");
          return jsonView;
        }
      } else {
        jsonView.messagePack("refresh error.");
        return jsonView;
      }
    } catch (Exception e) {
      jsonView.errorPack("refresh File From Remote error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 保存数据库连接.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "saveDBConnection", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Transactional
  @MethodRemark(remark= OpreateConstants.Datasource.SAVE_DB_CONNECTION,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView saveDBConnection(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody UIDataBaseConnection uIDataBaseConnection, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      uIDataBaseConnection = processConnection(uIDataBaseConnection);
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      String operateType = uIDataBaseConnection.getOperateType();

      UserConnection userConnection = new UserConnection();
      userConnection.setName(uIDataBaseConnection.getConnectionName());
      userConnection.setUid(loginUser.getPtId());
      userConnection.setDsCode(uIDataBaseConnection.getDataBaseType());
      userConnection.setStatus(Constants.validate);
      userConnection.setConfig(JSON.toJSONString(uIDataBaseConnection));
      userConnection.setUpdateTime(System.currentTimeMillis());
      userConnection.setDsId(uIDataBaseConnection.getDsId());
      userConnection.setDsCode(uIDataBaseConnection.getDsCode());
      userConnection.setSpaceId(uIDataBaseConnection.getSpaceId());
      userConnection.setUserName(loginUser.getUserName());
      serviceFactory.getDataSourceManagerService().testRemoteConnection(userConnection);

      if (operateType.equalsIgnoreCase(Constants.UI_OPERATE_SAVE)) {
        Map<String, Object[]> paramMap = new HashMap<>();
        paramMap.put("name", new Object[] {uIDataBaseConnection.getConnectionName()});
        // paramMap.put("uid",new Object[]{loginUser.getPtId()});
        paramMap.put("spaceId", new Object[] {uIDataBaseConnection.getSpaceId()});
        paramMap.put("dsId", new Object[] {uIDataBaseConnection.getDsId()});
        paramMap.put("status", new Object[] {Constants.validate});
        int count = serviceFactory.getPtoneUserConnectionService().queryCount(paramMap);
        if (count != 0) {
          jsonView.failedPack("CONNECTION_NAME_EXISTS | connection name has already exists.");
          return jsonView;
        }
        userConnection.setConnectionId(UUID.randomUUID().toString());
        userConnection.setSourceType(SourceType.UserConnection.USER_CREATED);
        serviceFactory.getPtoneUserConnectionService().save(userConnection);
      } else if (operateType.equalsIgnoreCase(Constants.UI_OPERATE_EDIT_SAVE)) {
        List<UserConnectionSource> sourceList =
            serviceFactory.getDataSourceManagerService().getConnectionSourceList(
                uIDataBaseConnection.getConnectionId());
        if (!sourceList.isEmpty()) {
          String remotePath = sourceList.get(0).getRemotePath();
          if (!sourceList.isEmpty() && StringUtil.hasText(remotePath)
              && !remotePath.split("@#\\*")[0].equals(uIDataBaseConnection.getConnectionName())) {
            remotePath =
                uIDataBaseConnection.getConnectionName() + "@#*" + remotePath.split("@#\\*")[1];
            serviceFactory.getDataSourceManagerService().updateConnectionSourcePath(remotePath,
                uIDataBaseConnection.getConnectionId());
          }
        }
        userConnection.setConnectionId(uIDataBaseConnection.getConnectionId());
        serviceFactory.getPtoneUserConnectionService().update(userConnection);
      }
      jsonView.successPack(userConnection);
    } catch (ServiceException e) {
      jsonView.failedPack(e.getErrorCode() + " | " + e.getErrorMsg());
    } catch (Exception e) {
      jsonView.errorPack("save db connection source error", e);
    }
    return jsonView;
  }

  /**
   * @Description: 测试数据库连接.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "testDBConnection", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark= OpreateConstants.Datasource.TEST_DB_CONNECTION,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView testDBConnection(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody UIDataBaseConnection uIDataBaseConnection, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      uIDataBaseConnection = processConnection(uIDataBaseConnection);
      // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
      // PtoneUser loginUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      UserConnection userConnection = new UserConnection();
      userConnection.setName(uIDataBaseConnection.getConnectionName());
      userConnection.setUid(loginUser.getPtId());
      userConnection.setDsCode(uIDataBaseConnection.getDataBaseType());
      userConnection.setStatus(Constants.validate);
      userConnection.setConfig(JSON.toJSONString(uIDataBaseConnection));
      userConnection.setUpdateTime(System.currentTimeMillis());

      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("name", new Object[] {uIDataBaseConnection.getConnectionName()});
      // paramMap.put("uid",new Object[]{loginUser.getPtId()});
      paramMap.put("spaceId", new Object[] {uIDataBaseConnection.getSpaceId()});
      paramMap.put("dsId", new Object[] {uIDataBaseConnection.getDsId()});
      paramMap.put("status", new Object[] {Constants.validate});
      UserConnection us = serviceFactory.getPtoneUserConnectionService().getByWhere(paramMap);
      if (us != null && !us.getConnectionId().equals(uIDataBaseConnection.getConnectionId())) {
        jsonView.failedPack("CONNECTION_NAME_EXISTS | connection name has already exists.");
        return jsonView;
      }

      serviceFactory.getDataSourceManagerService().testRemoteConnection(userConnection);
      jsonView.successPack(userConnection);
    } catch (ServiceException e) {
      jsonView.failedPack(e.getErrorCode() + " | " + e.getErrorMsg());
    } catch (Exception e) {
      jsonView.errorPack("test db connection source error", e);
    }
    return jsonView;
  }

  /**
   * 更新 userConnectionSource.
   */
  @RequestMapping(value = "updateConnectionSource", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark= OpreateConstants.Datasource.UPDATE_CONNECTION_SOURCE,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView updateConnectionSource(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody UserConnectionSource connectionSource, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      UserConnectionSource newConnectionSource = new UserConnectionSource();
      newConnectionSource.setSourceId(connectionSource.getSourceId());
      newConnectionSource.setName(connectionSource.getName());
      newConnectionSource.setUpdateFrequency(connectionSource.getUpdateFrequency());
      newConnectionSource.setUpdateHour(connectionSource.getUpdateHour());
      newConnectionSource.setTimezone(connectionSource.getTimezone());
      
      UserConnectionSource dbConnectionSource =
          serviceFactory.getDataSourceManagerService().updateConnectionSource(newConnectionSource);

      jsonView.successPack(dbConnectionSource);
    } catch (Exception e) {
      jsonView.errorPack("update UserConnectionSource error", e);
    }
    return jsonView;
  }

  /**
   * 用户自己输入授权信息，比如secretKey，目前在yahoo广告中使用
   * 保存Api链接信息<br>
   * 为避免与DBConnection冲突，所以新写一个api专用的接口，保证代码逻辑简洁
   * @param sid
   * @param connectionInfo 数据源连接信息，包含数据源的基本信息，操作类型，配置信息
   * @param request
   * @return
   */
  @RequestMapping(value = "saveApiConnection", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Transactional
  @MethodRemark(remark= OpreateConstants.Datasource.SAVE_API_CONNECTION,domain=OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView saveApiConnection(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody String connectionInfo, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      UserConnection userConnection = buildUserConnection(connectionInfo);

      // 组装剩余的conncetion信息
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      userConnection.setUpdateTime(System.currentTimeMillis());
      // 判断connectionId是否为空，不为空是修改，为空是新增
      if (StringUtil.isBlank(userConnection.getConnectionId())) {
        userConnection.setUid(loginUser.getPtId());
        userConnection.setStatus(Constants.validate);
        userConnection.setUserName(loginUser.getUserName());
        // 保存
        userConnection.setConnectionId(UUID.randomUUID().toString());
        serviceFactory.getPtoneUserConnectionService().save(userConnection);
      } else {
        // 修改
        serviceFactory.getPtoneUserConnectionService().update(userConnection);
      }
      jsonView.successPack(userConnection);
    } catch (ServiceException e) {
      jsonView.failedPack(e.getErrorCode());
    } catch (Exception e) {
      jsonView.errorPack("save api connection source error", e);
    }
    return jsonView;
  }

  /**
   * 检查链接信息是否存在
   * @param userConnection
   * @return
   */
  private boolean checkConnectionInfoExist(UserConnection userConnection) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("name", new Object[] {userConnection.getName()});
    paramMap.put("spaceId", new Object[] {userConnection.getSpaceId()});
    paramMap.put("dsId", new Object[] {userConnection.getDsId()});
    paramMap.put("status", new Object[] {Constants.validate});
    int count = serviceFactory.getPtoneUserConnectionService().queryCount(paramMap);
    return count > 0;
  }

  /**
   * 获取数据源code的配置
   * @param dsCode
   * @return
   */
  @SuppressWarnings("unchecked")
  private Map<String, String> getDsAuthInfoByDsCode(String dsCode) {
    Map<String, String> configMap = null;
    // 获取到数据源的配置信息
    PtoneDsInfo dsInfo = serviceFactory.getPtoneDsService().getDsInfoByDsCode(dsCode);
    String dsConfig = dsInfo.getConfig();
    if (StringUtil.isNotBlank(dsConfig)) {
      // 组装配置信息
      try {
        JSONObject jsonObj = JSON.parseObject(dsConfig);
        if (!CollectionUtil.isEmpty(jsonObj)) {
          configMap = jsonObj.getObject("authInfo", Map.class);
        }
      } catch (Exception e) {
        // 转换出现异常时不需要处理，方法返回空即可
        e.printStackTrace();
      }
    }
    return configMap;
  }

  /**
   * 保存数据源链接前的检查以及构建参数
   * @param connectionInfo
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private UserConnection buildUserConnection(String connectionInfo) throws ServiceException {
    if (StringUtil.isBlank(connectionInfo)) {
      // 如果链接信息是空，则抛出badRequest异常
      throw ServiceException.buildServiceException(
          ErrorCode.MSG_BAD_REQUEST, ErrorCode.CODE_BAD_REQUEST);
    }
    JSONObject jsonObj = JSON.parseObject(connectionInfo);
    if (CollectionUtil.isEmpty(jsonObj)) {
      throw ServiceException.buildServiceException(
          ErrorCode.MSG_BAD_REQUEST, ErrorCode.CODE_BAD_REQUEST);
    }
    // 取出链接信息转换成userConnection
    UserConnection userConnection = jsonObj.getObject("connectionInfo", UserConnection.class);
    String newName = userConnection.getName();
    String connectionId = userConnection.getConnectionId();
    boolean editSave = false;//是否是编辑保存
    boolean isNeedCheckName = true;//是否需要检查链接名
    if(StringUtil.isNotBlank(connectionId)){
      //本次操作是编辑保存，通过connectionId重新获取数据库中的链接信息
      userConnection = serviceFactory.getPtoneUserConnectionService().get(connectionId);
      editSave = true;
    }
    if (userConnection == null) {
      // 如果链接信息是空，则抛出badRequest异常
      throw ServiceException.buildServiceException(
          ErrorCode.MSG_BAD_REQUEST, ErrorCode.CODE_BAD_REQUEST);
    }
    if(editSave){
      if(userConnection.getName().equalsIgnoreCase(newName)){
        //不需要检查链接名了
        isNeedCheckName = false;
      }else{
        //新旧链接名不一致，需要把连接改为新链接名
        userConnection.setName(newName);
      }
    }
    // 验证当前的链接信息是否已存在
    if (isNeedCheckName && checkConnectionInfoExist(userConnection)) {
      // 抛出链接信息已存在的异常
      throw ServiceException.buildServiceException(ErrorCode.MSG_CONNECTION_NAME_EXISTS, 
          ErrorCode.CODE_CONNECTION_NAME_EXISTS);
    }

    String dsCode = userConnection.getDsCode();

    // 取出授权信息
    Map<String, String> authInfo = jsonObj.getObject("authInfo", Map.class);
    Map<String, String> dsAuthInfo = getDsAuthInfoByDsCode(dsCode);
    // 组装时数据源的配置与授权信息，合并然后放到userConnection的config中
    if (!CollectionUtil.isEmpty(dsAuthInfo)) {
      authInfo.putAll(dsAuthInfo);
    }
    userConnection.setConfig(JSON.toJSONString(authInfo));

    // 调用测试接口，不抛出异常则证明测试通过
//    Map<String, String> configMap =
//        serviceFactory.getPtoneApiRemoteService().testProfileApiRemote(userConnection);
//    if (!CollectionUtil.isEmpty(configMap)) {
//      // 如果有返回config信息，则加入到config中
//      authInfo.putAll(configMap);
//      userConnection.setConfig(JSON.toJSONString(authInfo));
//    }

    return userConnection;
  }

  public UIDataBaseConnection processConnection(UIDataBaseConnection uIDataBaseConnection) {
    String http = "http://";
    String https = "https://";
    String host = uIDataBaseConnection.getHost();
    String sshHost = uIDataBaseConnection.getSshHost();
    if (StringUtil.hasText(host)) {
      if (host.indexOf(http) > -1) {
        uIDataBaseConnection.setHost(host.replace(http, ""));
      }
      if (host.indexOf(https) > -1) {
        uIDataBaseConnection.setHost(host.replace(https, ""));
      }
    }
    if (StringUtil.hasText(sshHost)) {
      if (sshHost.indexOf(http) > -1) {
        uIDataBaseConnection.setSshHost(sshHost.replace(http, ""));
      }
      if (sshHost.indexOf(https) > -1) {
        uIDataBaseConnection.setSshHost(sshHost.replace(https, ""));
      }
    }
    return uIDataBaseConnection;
  }
  
  
  /**
   * @Description: 保存新增的授权账号信息的UserConnection，如果是同一人在同一空间下的授权则不新增记录
   * 
   * @date: 2016年10月10日
   * @author peng.xu
   */
  @RequestMapping(value = "addAccountConnection/{sign}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Transactional
  @MethodRemark(remark = OpreateConstants.Datasource.ADD_DB_CONNECTION,
      domain = OpreateConstants.BusinessDomain.DATASOURCE)
  public JsonView addAccountConnection(@RequestParam(value = "sid", required = true) String sid,
      @PathVariable("sign") String sign, @RequestBody UiAccountConnection uiAccountConnection,
      HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginUser = serviceFactory.getSessionContext().getLoginUser(sid);
      if (uiAccountConnection != null) {
        uiAccountConnection.setUid(loginUser.getPtId());
        uiAccountConnection.setUserName(loginUser.getUserName());
        jsonView =
            serviceFactory.getDataSourceManagerService().addAccountConnection(uiAccountConnection,
                sign);
      } else {
        jsonView.failedPack("uiAccountConnection is null");
      }
    } catch (Exception e) {
      jsonView.errorPack("add account auth connection error.", e);
    }
    return jsonView;
  }

}
