package com.sizzler.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sizzler.common.MediaType;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.vo.ConnectionTimezoneVo;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.annotation.MethodRemark;
import com.sizzler.system.api.annotation.ApiVersion;
import com.sizzler.system.api.common.ResponseResult;
import com.sizzler.system.api.common.RestResultGenerator;

/**
 * @ClassName: ConnectionsController
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2017/1/19
 * @author: zhangli
 */
@RestController("connectionsController")
@RequestMapping("{version}/connections")
@Scope("prototype")
@ApiVersion(Constants.API_VERSION_1)
public class ConnectionsController extends BaseController {

	/**
	 * 从编辑器中保远端存数据到本地.
	 */
	@RequestMapping(value = "/sources", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@MethodRemark(remark = OpreateConstants.Datasource.SAVE_DATA_SOURCE,
					domain = OpreateConstants.BusinessDomain.DATASOURCE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseResult saveDataSource(@RequestBody UserConnectionSourceVo sourceVo, @RequestHeader(value = "token",
					required = false) String token) {
		UserConnectionSourceVo uiAcceptTable =
						serviceFactory.getDataSourceManagerService().saveOrUpdateEditorDataToFile(sourceVo);
		return RestResultGenerator.genResult(uiAcceptTable);
	}

	/**
	 * @Description: 获取编辑所选文件的数据.
	 */
	@RequestMapping(value = "/{connectionId}/sources/{sourceId}/editor", method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON)
	@MethodRemark(remark = OpreateConstants.Datasource.EDIT_TABLE, domain = OpreateConstants.BusinessDomain.DATASOURCE)
	public ResponseResult getDataSourceEditView(@PathVariable("connectionId") String connectionId,
																							@PathVariable("sourceId") String sourceId, @RequestHeader(value = "token", required = false) String token) {
		UserConnectionSourceVo uIAcceptTable =
						serviceFactory.getDataSourceManagerService().getEditorDataByConnectionId(connectionId, sourceId, false, false,
										false);
		return RestResultGenerator.genResult(uIAcceptTable);
	}

	/**
	 * @Description: 获取连接信息的时区信息.
	 */
	@RequestMapping(value = "{dsId}/{connectionId}/{sourceId}/timezone", method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON)
	public ResponseResult getConnectionTimezone(@PathVariable("dsId") long dsId,
																							@PathVariable("connectionId") String connectionId,
																							@PathVariable("sourceId") String sourceId,
																							@RequestHeader(value = "token", required = false) String token) {
		ConnectionTimezoneVo vo = serviceFactory.getPtoneUserConnectionService().getConnectionTimezone(dsId, connectionId, sourceId);
		return RestResultGenerator.genResult(vo);
	}

	/**
	 * @Description: 获取当前连接下某个文件的远端数据.
	 */
	@RequestMapping(value = "/{connectionId}/sources/{folderId}/{fileId:.+}/editor/remote", method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON)
	@MethodRemark(remark = OpreateConstants.Datasource.PULL_REMOTE_DATA,
					domain = OpreateConstants.BusinessDomain.DATASOURCE)
	public ResponseResult pullRemoteData(@PathVariable("connectionId") String connectionId,
																			 @PathVariable("folderId") String folderId, @PathVariable("fileId") String fileId, @RequestHeader(value = "token",
					required = false) String token) {
		UserConnectionSourceVo uiAcceptTable =
						serviceFactory.getDataSourceManagerService().pullRemoteData(connectionId, folderId, fileId);
		return RestResultGenerator.genResult(uiAcceptTable);
	}

	/**
	 * @Description: 获取当前数据源连接下的文件树结构.
	 */
	@RequestMapping(value = "/{connectionId}/folders/{folderId:.+}", method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	public ResponseResult getDataSourceAccountSchema(@PathVariable("connectionId") String connectionId, @RequestHeader(
					value = "token", required = false) String token, @PathVariable("folderId") String folderId, @RequestParam(
					value = "refresh", required = false) boolean refresh) {
		String json =
						serviceFactory.getDataSourceManagerService().getDataSourceAccountSchema(connectionId, folderId, refresh);
		return RestResultGenerator.genResult(json);
	}

	/**
	 * 根据tableId更新远端文件数据.
	 */
	@RequestMapping(value = "/sources/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseResult updateRemoteSourceDataByTableId(@PathVariable("id") String id, @RequestHeader(value = "token",
					required = false) String token, @RequestParam(value = "type", required = true) String type) {
		UserConnectionSourceVo uiAcceptTable = null;
		// todo
		if (type.equals("tables")) {
			UserConnectionSource source = serviceFactory.getDataSourceManagerService().getUserConnectionSourceByTableId(id);
			uiAcceptTable = serviceFactory.getDataSourceManagerService().updateRemoteSourceData(source);
		}
		return RestResultGenerator.genResult(uiAcceptTable);
	}

	/**
	 * @Description: 更新连接信息的时区信息.
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "{dsId}/{connectionId}/{sourceId}/timezone", method = RequestMethod.PUT,
					produces = MediaType.APPLICATION_JSON)
	public void updateConnectionTimezone(@PathVariable("dsId") long dsId, @PathVariable("connectionId") String connectionId,
																			 @PathVariable("sourceId") String sourceId, @RequestBody ConnectionTimezoneVo vo,
																			 @RequestHeader(value = "token", required = false) String token) {
		serviceFactory.getPtoneUserConnectionService().updateConnectionTimezone(dsId, connectionId, sourceId, vo);
	}

}
