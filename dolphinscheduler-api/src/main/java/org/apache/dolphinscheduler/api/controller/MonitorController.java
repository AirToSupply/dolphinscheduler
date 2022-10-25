/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.api.controller;

import static org.apache.dolphinscheduler.api.enums.Status.LIST_MASTERS_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.LIST_WORKERS_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.QUERY_DATABASE_STATE_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.QUERY_HOST_LIST_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.QUERY_HOST_METRICS_ERROR;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.dolphinscheduler.api.aspect.AccessLogAnnotation;
import org.apache.dolphinscheduler.api.exceptions.ApiException;
import org.apache.dolphinscheduler.api.service.MonitorService;
import org.apache.dolphinscheduler.api.service.NodeDiskService;
import org.apache.dolphinscheduler.api.service.NodeMetricsService;
import org.apache.dolphinscheduler.api.service.NodeMetricsSnapshotService;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.api.vo.metrics.CpuUsageVo;
import org.apache.dolphinscheduler.api.vo.metrics.DiskUsageVo;
import org.apache.dolphinscheduler.api.vo.metrics.LoadAverageVo;
import org.apache.dolphinscheduler.api.vo.metrics.MemoryUsageVo;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.utils.DateUtils;
import org.apache.dolphinscheduler.dao.entity.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * monitor controller
 */
@Api(tags = "MONITOR_TAG")
@RestController
@RequestMapping("/monitor")
public class MonitorController extends BaseController {

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private NodeMetricsService nodeMetricsService;

    @Autowired
    private NodeMetricsSnapshotService nodeMetricsSnapshotService;

    @Autowired
    private NodeDiskService nodeDiskService;

    /**
     * master list
     *
     * @param loginUser login user
     * @return master list
     */
    @ApiOperation(value = "listMaster", notes = "MASTER_LIST_NOTES")
    @GetMapping(value = "/masters")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(LIST_MASTERS_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result listMaster(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Map<String, Object> result = monitorService.queryMaster(loginUser);
        return returnDataList(result);
    }

    /**
     * worker list
     *
     * @param loginUser login user
     * @return worker information list
     */
    @ApiOperation(value = "listWorker", notes = "WORKER_LIST_NOTES")
    @GetMapping(value = "/workers")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(LIST_WORKERS_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result listWorker(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Map<String, Object> result = monitorService.queryWorker(loginUser);
        return returnDataList(result);
    }

    /**
     * query database state
     *
     * @param loginUser login user
     * @return data base state
     */
    @ApiOperation(value = "queryDatabaseState", notes = "QUERY_DATABASE_STATE_NOTES")
    @GetMapping(value = "/databases")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_DATABASE_STATE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryDatabaseState(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Map<String, Object> result = monitorService.queryDatabaseState(loginUser);
        return returnDataList(result);
    }

    /**
     * query monitor hosts, include master and worker
     *
     * @param loginUser login user
     * @param pageNo the process definition version list current page number
     * @param pageSize the process definition version list page size
     * @param ip host ip
     * @param state host state
     * @return host list
     */
    @ApiOperation(value = "queryHosts", notes = "QUERY_HOST_LIST_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNo", value = "PAGE_NO", required = true, dataType = "Int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "PAGE_SIZE", required = true, dataType = "Int", example = "10"),
        @ApiImplicitParam(name = "ip", value = "MONITOR_HOST_IP", required = false, dataType = "String", example = "127.0.0.1"),
        @ApiImplicitParam(name = "state", value = "MONITOR_HOST_STATE", required = false, dataType = "Int", example = "0")
    })
    @GetMapping(value = "/hosts")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_HOST_LIST_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryHosts(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                             @RequestParam(value = "pageNo") int pageNo,
                             @RequestParam(value = "pageSize") int pageSize,
                             @RequestParam(value = "ip", required = false) String ip,
                             @RequestParam(value = "state", required = false) Integer state) {
        Result result = checkPageParams(pageNo, pageSize);
        if (!result.checkResult()) {
            return result;
        }
        return nodeMetricsService.queryNodeMetricsListPaging(pageNo, pageSize, ip, state);
    }

    /**
     * query host cpu usage
     *
     * @param loginUser login user
     * @param ip host ip
     * @param startDate host metrics start date
     * @param endDate host metrics end date
     * @return host cpu usage
     */
    @ApiOperation(value = "cpuUsage", notes = "QUERY_HOST_METRICS_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ip", value = "MONITOR_HOST_IP", dataType = "String", example = "127.0.0.1"),
        @ApiImplicitParam(name = "startDate", value = "START_DATE", required = true, dataType = "String"),
        @ApiImplicitParam(name = "endDate", value = "END_DATE", required = true, dataType = "String")
    })
    @GetMapping(value = "/cpu-usage")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_HOST_METRICS_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result cpuUsage(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                           @RequestParam(value = "ip") String ip,
                           @RequestParam(value = "startDate") String startDate,
                           @RequestParam(value = "endDate") String endDate) {
        List<CpuUsageVo> result = nodeMetricsSnapshotService.queryNodeMetricsSnapshot(ip,
            DateUtils.stringToDate(startDate), DateUtils.stringToDate(endDate))
                .stream()
                .map(point -> CpuUsageVo.of(point))
                .collect(Collectors.toList());
        return success(result);
    }

    /**
     * query host memory usage
     *
     * @param loginUser login user
     * @param ip host ip
     * @param startDate host metrics start date
     * @param endDate host metrics end date
     * @return host memory usage
     */
    @ApiOperation(value = "memoryUsage", notes = "QUERY_HOST_METRICS_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ip", value = "MONITOR_HOST_IP", dataType = "String", example = "127.0.0.1"),
        @ApiImplicitParam(name = "startDate", value = "START_DATE", required = true, dataType = "String"),
        @ApiImplicitParam(name = "endDate", value = "END_DATE", required = true, dataType = "String")
    })
    @GetMapping(value = "/memory-usage")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_HOST_METRICS_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result memoryUsage(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                           @RequestParam(value = "ip") String ip,
                           @RequestParam(value = "startDate") String startDate,
                           @RequestParam(value = "endDate") String endDate) {
        List<MemoryUsageVo> result = nodeMetricsSnapshotService.queryNodeMetricsSnapshot(ip,
            DateUtils.stringToDate(startDate), DateUtils.stringToDate(endDate))
                .stream()
                .map(point -> MemoryUsageVo.of(point))
                .collect(Collectors.toList());
        return success(result);
    }

    /**
     * query host load average
     *
     * @param loginUser login user
     * @param ip host ip
     * @param startDate host metrics start date
     * @param endDate host metrics end date
     * @return host load average
     */
    @ApiOperation(value = "loadAverage", notes = "QUERY_HOST_METRICS_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ip", value = "MONITOR_HOST_IP", dataType = "String", example = "127.0.0.1"),
        @ApiImplicitParam(name = "startDate", value = "START_DATE", required = true, dataType = "String"),
        @ApiImplicitParam(name = "endDate", value = "END_DATE", required = true, dataType = "String")
    })
    @GetMapping(value = "/load-average")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_HOST_METRICS_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result loadAverage(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                              @RequestParam(value = "ip") String ip,
                              @RequestParam(value = "startDate") String startDate,
                              @RequestParam(value = "endDate") String endDate) {
        List<LoadAverageVo> result = nodeMetricsSnapshotService.queryNodeMetricsSnapshot(ip,
            DateUtils.stringToDate(startDate), DateUtils.stringToDate(endDate))
                .stream()
                .map(point -> LoadAverageVo.of(point))
                .collect(Collectors.toList());
        return success(result);
    }

    /**
     * query host disk usage
     *
     * @param loginUser login user
     * @param ip host ip
     * @param startDate host metrics start date
     * @param endDate host metrics end date
     * @return host disk usage
     */
    @ApiOperation(value = "diskUsage", notes = "QUERY_HOST_METRICS_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ip", value = "MONITOR_HOST_IP", dataType = "String", example = "127.0.0.1"),
        @ApiImplicitParam(name = "startDate", value = "START_DATE", required = true, dataType = "String"),
        @ApiImplicitParam(name = "endDate", value = "END_DATE", required = true, dataType = "String")
    })
    @GetMapping(value = "/disk-usage")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_HOST_METRICS_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result diskUsage(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                            @RequestParam(value = "ip") String ip,
                            @RequestParam(value = "startDate") String startDate,
                            @RequestParam(value = "endDate") String endDate) {
        List<DiskUsageVo> result = nodeMetricsSnapshotService.queryNodeMetricsSnapshot(ip,
            DateUtils.stringToDate(startDate), DateUtils.stringToDate(endDate))
                .stream()
                .map(point -> DiskUsageVo.of(point))
                .collect(Collectors.toList());
        return success(result);
    }

    /**
     * query host file store
     *
     * @param loginUser login user
     * @param ip host ip
     * @return host file store
     */
    @ApiOperation(value = "fileStores", notes = "QUERY_HOST_METRICS_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ip", value = "MONITOR_HOST_IP", dataType = "String", example = "127.0.0.1")
    })
    @GetMapping(value = "/file-stores")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_HOST_METRICS_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result fileStores(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                             @RequestParam(value = "ip") String ip) {
        return success(nodeDiskService.queryNodeDisk(ip));
    }
}
