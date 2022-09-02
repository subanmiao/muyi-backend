package com.mysl.api.controller.admin;

import com.github.pagehelper.PageInfo;
import com.mysl.api.common.OperateType;
import com.mysl.api.common.lang.ResponseData;
import com.mysl.api.entity.dto.ClassFullDTO;
import com.mysl.api.entity.dto.ClassUpdateDTO;
import com.mysl.api.service.ClassService;
import io.github.flyhero.easylog.annotation.EasyLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ivan Su
 * @date 2022/9/1
 */
@Api(tags = "班级管理接口")
@RestController("adminClassController")
@RequestMapping("/admin/classes")
@Secured({"ROLE_ADMIN"})
public class ClassController {

    @Autowired
    ClassService classService;

    @ApiOperation("查询班级列表")
    @EasyLog(module = "Admin-查询班级列表", type = OperateType.SELECT, success = "", fail = "{{#_errMsg}}")
    @GetMapping
    public ResponseData<PageInfo<ClassFullDTO>> list(@ApiParam(value = "页数，默认 1")
                                                     @RequestParam(name = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                                     @ApiParam(value = "每页记录，默认 20")
                                                     @RequestParam(name = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                                     @ApiParam(value = "搜索关键字")
                                                     @RequestParam(name = "key_word", required = false) String keyWord) {
        return ResponseData.ok(classService.getClasses(pageNum, pageSize, null, null, keyWord));
    }

    @ApiOperation("查询班级详情")
    @EasyLog(module = "Admin-查询班级详情", type = OperateType.SELECT, bizNo = "{{#id}}", success = "", fail = "{{#_errMsg}}")
    @GetMapping("/{id}")
    public ResponseData<ClassFullDTO> get(@PathVariable Long id) {
        return ResponseData.ok(classService.getClassById(id, null));
    }

    @ApiOperation("修改班级信息")
    @EasyLog(module = "Admin-修改班级信息", type = OperateType.UPDATE, bizNo = "{{#id}}", success = "", fail = "{{#_errMsg}}", detail = "{{#dto}}")
    @PutMapping("/{id}")
    public ResponseData update(@PathVariable Long id, @Validated @RequestBody ClassUpdateDTO dto) {
        return ResponseData.ok(classService.update(id, dto));
    }
}
